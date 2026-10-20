package com.sourate.app

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.hardware.GeomagneticField
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.batoulapps.adhan.Coordinates
import com.batoulapps.adhan.Qibla
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.sourate.app.databinding.ActivityQiblaBinding
import java.util.Locale
import kotlin.math.abs
import kotlin.math.roundToInt

class QiblaActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var binding: ActivityQiblaBinding
    private val fusedClient by lazy { LocationServices.getFusedLocationProviderClient(this) }
    private var sensorManager: SensorManager? = null
    private var rotationSensor: Sensor? = null

    private var currentAzimuth = 0f
    private var displayedRotation = 0f
    private var qiblaDirection = 0.0
    private var declination = 0f
    private val REQ_LOCATION = 1002

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQiblaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = "Boussole Qibla"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        rotationSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        if (rotationSensor == null) {
            binding.tvStatus.text = "Boussole non disponible sur cet appareil."
        }

        if (hasLocationPermission()) fetchLocation() else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQ_LOCATION
            )
            // Par défaut vers Thiès (Sénégal) comme demandé
            useDefaultLocation()
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
                if (location != null) computeQibla(location)
                else useDefaultLocation()
            }
            .addOnFailureListener { useDefaultLocation() }
    }

    private fun useDefaultLocation() {
        val loc = Location("").apply {
            latitude = Defaults.LATITUDE
            longitude = Defaults.LONGITUDE
        }
        computeQibla(loc)
        binding.tvLocation.text = "Position: ${Defaults.LOCATION_NAME} (par défaut)"
    }

    private fun computeQibla(location: Location) {
        val coordinates = Coordinates(location.latitude, location.longitude)
        qiblaDirection = Qibla(coordinates).direction
        try {
            val gf = GeomagneticField(
                location.latitude.toFloat(),
                location.longitude.toFloat(),
                0f,
                System.currentTimeMillis()
            )
            declination = gf.declination
        } catch (e: Exception) {
            declination = 0f
        }
        binding.tvLocation.text = if (location.provider.isNullOrEmpty()) {
            "Position: ${String.format(Locale.FRANCE, "%.3f°, %.3f°", location.latitude, location.longitude)}"
        } else {
            String.format(Locale.FRANCE, "%.3f°, %.3f°", location.latitude, location.longitude)
        }
        binding.tvQiblaAngle.text = String.format(
            Locale.FRANCE, "Qibla: %.1f° du Nord", qiblaDirection
        ).replace('.', ',')
        // Réinitialiser la rotation affichée après changement de position
        displayedRotation = 0f
    }

    override fun onResume() {
        super.onResume()
        rotationSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null || event.sensor.type != Sensor.TYPE_ROTATION_VECTOR) return
        val rotationMatrix = FloatArray(9)
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
        val orientation = FloatArray(3)
        SensorManager.getOrientation(rotationMatrix, orientation)
        var azimuth = Math.toDegrees(orientation[0].toDouble()).toFloat()
        azimuth = (azimuth + 360f) % 360f
        updateCompass(azimuth)
    }

    private fun normalizeDeg(d: Float): Float {
        var v = d % 360f
        if (v < 0f) v += 360f
        return v
    }

    private fun updateCompass(azimuth: Float) {
        val mag = normalizeDeg(azimuth)
        currentAzimuth = mag
        val trueHeading = normalizeDeg(mag + declination)
        val target = normalizeDeg((qiblaDirection - trueHeading).toFloat())
        var delta = target - displayedRotation
        while (delta > 180f) delta -= 360f
        while (delta < -180f) delta += 360f
        val animation = RotateAnimation(
            displayedRotation,
            displayedRotation + delta,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        animation.duration = 250
        animation.fillAfter = true
        binding.ivCompass.startAnimation(animation)
        displayedRotation = normalizeDeg(displayedRotation + delta)

        binding.tvDegrees.text = "${mag.roundToInt()}°"
        val diffToUp = abs(target)
        val aligned = diffToUp < 5f || diffToUp > 355f
        binding.tvStatus.text = if (aligned) {
            "Vous êtes orienté vers la Qibla ✓"
        } else {
            "Tournez jusqu'à aligner la flèche verte vers le haut"
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_LOCATION &&
            grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            fetchLocation()
        } else {
            useDefaultLocation()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
