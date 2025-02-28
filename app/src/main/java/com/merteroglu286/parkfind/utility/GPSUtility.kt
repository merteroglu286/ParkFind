package com.merteroglu286.parkfind.utility

import android.app.Activity
import android.content.IntentSender
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest

class GPSUtility {
    companion object {
        fun enableLocationSettings(
            activity: Activity,
            launcher: ActivityResultLauncher<IntentSenderRequest>,
            successCallback: (() -> Unit?) = {},
            errorCallback: (() -> Unit?) = {},
            notFoundLocationService: (() -> Unit?) = {}
        ) {
            val locationRequest = LocationRequest.create().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

            val task = LocationServices.getSettingsClient(activity)
                .checkLocationSettings(builder.build())

            task.addOnSuccessListener { response ->
                val states = response.locationSettingsStates
                successCallback()
            }

            task.addOnFailureListener { e ->
                if (e is ResolvableApiException) {
                    try {
                        errorCallback()
                        val intentSenderRequest =
                            IntentSenderRequest.Builder(e.resolution).build()
                        launcher.launch(intentSenderRequest)
                    } catch (sendEx: IntentSender.SendIntentException) {
                        errorCallback()
                    }
                } else {
                    notFoundLocationService()
                }
            }
        }
    }
}