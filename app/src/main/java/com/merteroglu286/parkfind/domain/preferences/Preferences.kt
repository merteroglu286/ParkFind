package com.merteroglu286.parkfind.domain.preferences

interface Preferences {
    fun setLastLocation(lat: Double, lng: Double)
    fun getLastLocation(): Pair<Double, Double>
}