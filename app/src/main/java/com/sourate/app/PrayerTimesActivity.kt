package com.sourate.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.batoulapps.adhan.CalculationMethod
import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.Madhab
import com.batoulapps.adhan.PrayerTimes
import com.batoulapps.adhan.data.DateComponents
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.sourate.app.databinding.ActivityPrayerTimesBinding
import com.sourate.app.databinding.ItemPrayerBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class PrayerTimesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrayerTimesBinding
    private val fusedClient by lazy { LocationServices.getFusedLocationProviderClient(this) }
    private val REQ_LOCATION = 1001
    private val REQ_NOTIF = 1003
    private var lastLocation: Location? = null

    private val pickAdhan = registerForActivityResult(
        ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            if (AdhanSettings.setCustom(this, uri)) {
                Toast.makeText(this, "Nouveau son d'adhan enregistré", Toast.LENGTH_SHORT).show()
                updateAdhanLabel()
            } else {
                Toast.makeText(this, "Impossible de lire ce fichier", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrayerTimesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Horaires de Prières"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.switchAdhan.isChecked = PrayerNotificationScheduler.isEnabled(this)
        binding.switchAdhan.setOnCheckedChangeListener { _, checked ->
            if (checked) enableAdhan() else {
                PrayerNotificationScheduler.setEnabled(this, false)
            }
        }

        setupAdhanSound()
        setupOffsets()

        if (hasLocationPermission()) {
            fetchLocation()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQ_LOCATION
            )
            useDefaultLocation()
        }
    }

    private fun setupAdhanSound() {
        binding.switchSoundAdhan.isChecked = AdhanSettings.isSoundEnabled(this)
        binding.switchSoundAdhan.setOnCheckedChangeListener { _, checked ->
            AdhanSettings.setSoundEnabled(this, checked)
        }
        binding.btnPickAdhan.setOnClickListener {
            pickAdhan.launch(arrayOf("audio/*"))
        }
        binding.btnTestAdhan.setOnClickListener {
            try {
                ContextCompat.startForegroundService(
                    this,
                    Intent(this, AdhanService::class.java).putExtra("PRAYER", "Test")
                )
            } catch (e: Exception) {
                Toast.makeText(this, "Lecture impossible: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        binding.btnResetAdhan.setOnClickListener {
            AdhanSettings.clearCustom(this)
            updateAdhanLabel()
            Toast.makeText(this, "Son par défaut rétabli", Toast.LENGTH_SHORT).show()
        }
        updateAdhanLabel()
    }

    private fun setupOffsets() {
        bindOffset("Fajr", binding.btnFajrMinus, binding.btnFajrPlus)
        bindOffset("Dhouhr", binding.btnDhouhrMinus, binding.btnDhouhrPlus)
        bindOffset("Asr", binding.btnAsrMinus, binding.btnAsrPlus)
        bindOffset("Maghrib", binding.btnMaghribMinus, binding.btnMaghribPlus)
        bindOffset("Isha", binding.btnIshaMinus, binding.btnIshaPlus)
        binding.btnResetOffsets.setOnClickListener {
            PrayerOffsets.reset(this)
            updateOffsetLabels()
            lastLocation?.let { computeTimes(it) }
            PrayerNotificationScheduler.scheduleNext(this)
            Toast.makeText(this, "Réglages Sénégal (Thiès) rétablis", Toast.LENGTH_SHORT).show()
        }
        updateOffsetLabels()
    }

    private fun bindOffset(
        prayer: String,
        minus: com.google.android.material.button.MaterialButton,
        plus: com.google.android.material.button.MaterialButton
    ) {
        minus.setOnClickListener {
            PrayerOffsets.set(this, prayer, PrayerOffsets.get(this, prayer) - 1)
            updateOffsetLabels()
            lastLocation?.let { computeTimes(it) }
            PrayerNotificationScheduler.scheduleNext(this)
        }
        plus.setOnClickListener {
            PrayerOffsets.set(this, prayer, PrayerOffsets.get(this, prayer) + 1)
            updateOffsetLabels()
            lastLocation?.let { computeTimes(it) }
            PrayerNotificationScheduler.scheduleNext(this)
        }
    }

    private fun updateOffsetLabels() {
        fun txt(v: Int) = (if (v > 0) "+" else "") + v + " min"
        binding.tvFajrOffset.text = txt(PrayerOffsets.get(this, "Fajr"))
        binding.tvDhouhrOffset.text = txt(PrayerOffsets.get(this, "Dhouhr"))
        binding.tvAsrOffset.text = txt(PrayerOffsets.get(this, "Asr"))
        binding.tvMaghribOffset.text = txt(PrayerOffsets.get(this, "Maghrib"))
        binding.tvIshaOffset.text = txt(PrayerOffsets.get(this, "Isha"))
    }

    private fun updateAdhanLabel() {
        val custom = AdhanSettings.hasCustom(this)
        binding.tvAdhanSound.text =
            if (custom) "Son actuel : fichier personnalisé"
            else "Son actuel : adhan par défaut (domaine public)"
        binding.btnResetAdhan.visibility = if (custom) View.VISIBLE else View.GONE
    }

    private fun enableAdhan() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                REQ_NOTIF
            )
        } else {
            PrayerNotificationScheduler.setEnabled(this, true)
        }
    }

    private fun hasLocationPermission(): Boolean =
        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    private fun fetchLocation() {
        binding.tvLocation.text = "Localisation en cours..."
        fusedClient.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) computeTimes(location)
                else useDefaultLocation()
            }
            .addOnFailureListener { useDefaultLocation() }
    }

    private fun useDefaultLocation() {
        val loc = Location("").apply {
            latitude = Defaults.LATITUDE
            longitude = Defaults.LONGITUDE
        }
        computeTimes(loc)
        binding.tvLocation.text = "Position: ${Defaults.LOCATION_NAME} (par défaut)"
    }

    private fun computeTimes(location: Location) {
        lastLocation = location
        val coordinates = Coordinates(location.latitude, location.longitude)
        binding.tvLocation.text = String.format(
            Locale.FRANCE, "Position: %.3f°, %.3f°", location.latitude, location.longitude
        )
        PrayerNotificationScheduler.saveLocation(this, location.latitude, location.longitude)

        val tz = TimeZone.getTimeZone(Defaults.TIMEZONE)
        val cal = Calendar.getInstance(tz)
        val dateComponents = DateComponents(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH))
        val params = CalculationMethod.MUSLIM_WORLD_LEAGUE.parameters
        params.madhab = Madhab.SHAFI
        val times = PrayerTimes(coordinates, dateComponents, params)

        val fmt = SimpleDateFormat("HH:mm", Locale.FRANCE).apply { timeZone = tz }

        fun offset(prayer: String, date: Date): Date {
            val c = Calendar.getInstance(tz)
            c.time = date
            c.add(Calendar.MINUTE, PrayerOffsets.get(this, prayer))
            return c.time
        }

        val fajr = offset("Fajr", times.fajr)
        val dhuhr = offset("Dhouhr", times.dhuhr)
        val asr = offset("Asr", times.asr)
        val maghrib = offset("Maghrib", times.maghrib)
        val isha = offset("Isha", times.isha)

        val rows = listOf(
            PrayerRow("Fajr", fmt.format(fajr), "Prière de l'aube", R.drawable.ic_prayer),
            PrayerRow("Chourouq", fmt.format(times.sunrise), "Lever du soleil", R.drawable.ic_prayer),
            PrayerRow("Dhouhr", fmt.format(dhuhr), "Prière de midi", R.drawable.ic_prayer),
            PrayerRow("Asr", fmt.format(asr), "Prière de l'après-midi", R.drawable.ic_prayer),
            PrayerRow("Maghrib", fmt.format(maghrib), "Coucher du soleil", R.drawable.ic_prayer),
            PrayerRow("Isha", fmt.format(isha), "Prière de la nuit", R.drawable.ic_prayer)
        )
        binding.recyclerView.adapter = PrayerAdapter(rows)

        val next = nextPrayer(fajr, dhuhr, asr, maghrib, isha, fmt, tz)
        binding.tvNext.text = "Prochaine prière: ${next.first} à ${next.second}"
    }

    private fun nextPrayer(
        fajr: Date, dhuhr: Date, asr: Date, maghrib: Date, isha: Date,
        fmt: SimpleDateFormat, tz: TimeZone
    ): Pair<String, String> {
        val now = Calendar.getInstance(tz).time
        val list = listOf(
            "Fajr" to fajr,
            "Dhouhr" to dhuhr,
            "Asr" to asr,
            "Maghrib" to maghrib,
            "Isha" to isha
        )
        list.firstOrNull { it.second.after(now) }?.let { return it.first to fmt.format(it.second) }
        return "Fajr (demain)" to fmt.format(fajr)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocation()
            } else {
                binding.tvLocation.text = "Permission refusée. Horaires pour ${Defaults.LOCATION_NAME}."
                useDefaultLocation()
            }
        } else if (requestCode == REQ_NOTIF) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                PrayerNotificationScheduler.setEnabled(this, true)
            } else {
                binding.switchAdhan.isChecked = false
                PrayerNotificationScheduler.setEnabled(this, false)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}

data class PrayerRow(
    val name: String,
    val time: String,
    val label: String,
    val icon: Int
)

class PrayerAdapter(private val rows: List<PrayerRow>) :
    RecyclerView.Adapter<PrayerAdapter.PrayerViewHolder>() {

    inner class PrayerViewHolder(val binding: ItemPrayerBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PrayerViewHolder {
        val binding = ItemPrayerBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return PrayerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PrayerViewHolder, position: Int) {
        val row = rows[position]
        holder.binding.tvName.text = row.name
        holder.binding.tvLabel.text = row.label
        holder.binding.tvTime.text = row.time
        holder.binding.ivIcon.setImageResource(row.icon)
    }

    override fun getItemCount() = rows.size
}
