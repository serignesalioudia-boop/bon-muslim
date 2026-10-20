package com.sourate.app

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object QuranRepository {

    private var cached: List<Surah>? = null

    fun getSurahs(context: Context): List<Surah> {
        cached?.let { return it }
        val json = context.assets.open("quran.json")
            .bufferedReader(Charsets.UTF_8)
            .use { it.readText() }
        val type = object : TypeToken<List<Surah>>() {}.type
        val list: List<Surah> = Gson().fromJson(json, type)
        cached = list
        return list
    }

    fun getSurah(context: Context, number: Int): Surah? =
        getSurahs(context).find { it.n == number }
}
