package com.merteroglu286.parkfind.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.merteroglu286.parkfind.domain.preferences.Preferences
import javax.inject.Inject

class PreferencesImpl @Inject constructor(context: Context) : Preferences {

    private val lastLat = "LAST_LAT"
    private val lastLon = "LAST_LON"
    private val introShowed = "INTRO_SHOWED"

    var mPrefs: SharedPreferences = context.getSharedPreferences("Pref", Context.MODE_PRIVATE)

    override fun setLastLocation(lat: Double, lng: Double) {
        return mPrefs.edit()
            .putString(lastLat, lat.toString())
            .putString(lastLon, lng.toString())
            .apply()
    }

    override fun getLastLocation(): Pair<Double, Double> {
        val lat = mPrefs.getString(lastLat, null)?.toDoubleOrNull() ?: 0.0
        val lng = mPrefs.getString(lastLon, null)?.toDoubleOrNull() ?: 0.0
        return Pair(lat, lng)
    }

    override fun setIntroShow(showed: Boolean) {
        return mPrefs.edit()
            .putBoolean(introShowed,showed)
            .apply()
    }

    override fun getIntroShow(): Boolean {
        return mPrefs.getBoolean(introShowed, false)
    }

}