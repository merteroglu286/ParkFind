package com.merteroglu286.parkfind.utility

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.merteroglu286.parkfind.R

object MapUtils {

    fun openMap(context: Context, lat: Double, lon: Double) {

        val uriYandex = "yandexnavi://build_route_on_map?lat_to=${lat}&lon_to=${lon}"
        val intentYandex = Intent(Intent.ACTION_VIEW, Uri.parse(uriYandex))
        intentYandex.setPackage("ru.yandex.yandexnavi")

        val uriGoogle = Uri.parse("google.navigation:q=${lat},${lon}&mode=c")
        val intentGoogle = Intent(Intent.ACTION_VIEW, uriGoogle)
        intentGoogle.setPackage("com.google.android.apps.maps")

        val chooserIntent = Intent.createChooser(intentYandex, context.getString(R.string.please_select))
        val arr = arrayOfNulls<Intent>(1)
        arr[0] = intentGoogle
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, arr)

        val activities = context.packageManager.queryIntentActivities(chooserIntent, 0)
        if (activities.size > 0) {
            context.startActivity(chooserIntent)
        } else {

        }
    }

}
