package com.merteroglu286.parkfind.utility.extension

import android.net.Uri

fun Uri?.toDatabasePath(): String? = this?.toString()

fun String?.toUri(): Uri? = this?.let { Uri.parse(it) }
