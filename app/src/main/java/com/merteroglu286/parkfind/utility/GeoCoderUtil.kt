package com.merteroglu286.parkfind.utility

import android.content.Context
import android.location.Address
import android.location.Geocoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.util.Locale

object GeocoderUtil {

    /**
     * Enlem ve boylam değerlerinden açık adres bilgisini almak için kullanılır.
     *
     * @param context Context nesnesi
     * @param latitude Enlem değeri
     * @param longitude Boylam değeri
     * @return Açık adres bilgisi veya hata durumunda null
     */
    suspend fun getAddressFromLatLng(context: Context, latitude: Double, longitude: Double): String? {
        return withContext(Dispatchers.IO) {
            try {
                val geocoder = Geocoder(context, Locale.getDefault())

                // Android sürüm kontrolü
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                    // Android 13 ve üzeri için
                    var result: String? = null
                    geocoder.getFromLocation(latitude, longitude, 1) { addresses ->
                        if (addresses.isNotEmpty()) {
                            result = formatAddress(addresses[0])
                        }
                    }
                    return@withContext result
                } else {
                    // Android 12 ve altı için
                    val addresses = geocoder.getFromLocation(latitude, longitude, 1)
                    if (!addresses.isNullOrEmpty()) {
                        return@withContext formatAddress(addresses[0])
                    }
                }

                return@withContext null
            } catch (e: IOException) {
                e.printStackTrace()
                return@withContext null
            }
        }
    }

    /**
     * Address nesnesini formatlar ve okunabilir adres bilgisini döndürür
     */
    private fun formatAddress(address: Address): String {
        val addressParts = mutableListOf<String>()

        // Sokak adı
        if (!address.thoroughfare.isNullOrEmpty()) {
            addressParts.add(address.thoroughfare)
        }

        // Bina no
        if (!address.subThoroughfare.isNullOrEmpty()) {
            addressParts.add(address.subThoroughfare)
        }

        // Mahalle
        if (!address.subLocality.isNullOrEmpty()) {
            addressParts.add(address.subLocality)
        }

        // İlçe
        if (!address.subAdminArea.isNullOrEmpty()) {
            addressParts.add(address.subAdminArea)
        }

        // İl
        if (!address.adminArea.isNullOrEmpty()) {
            addressParts.add(address.adminArea)
        }

        // Posta kodu
        if (!address.postalCode.isNullOrEmpty()) {
            addressParts.add(address.postalCode)
        }

        // Ülke
        if (!address.countryName.isNullOrEmpty()) {
            addressParts.add(address.countryName)
        }

        return addressParts.joinToString(", ")
    }
}