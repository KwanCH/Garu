package com.example.ak24project

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Base64
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.drawable.toBitmap
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.request.SuccessResult
import java.io.ByteArrayOutputStream

val achievementIconMap = mapOf(
    "Century Club" to R.drawable.century_club,
    "Christmas Grind" to R.drawable.christmast_grind,
    "Collector" to R.drawable.collector,
    "Consistency Champion" to R.drawable.consistency_champion,
    "Early Bird" to R.drawable.early_bird,
    "First Steps" to R.drawable.first_stepts,
    "5K Finisher" to R.drawable.five_k_finisher,
    "Hourglass" to R.drawable.hourglass,
    "Level Up" to R.drawable.level_up,
    "Marathoner" to R.drawable.marathoner,
    "Motivator" to R.drawable.motivator,
    "New Year’s Resolution" to R.drawable.new_years_resolution,
    "Night Owl" to R.drawable.night_owl,
    "Social Butterfly" to R.drawable.social_butterfly,
    "The Long Haul" to R.drawable.the_long_haul,
    "XP Collector" to R.drawable.xp_collector,
)

@SuppressLint("UseCompatLoadingForDrawables")
fun drawableToBitmap(context: Context, drawableId: Int): Bitmap? {
    val drawable = context.getDrawable(drawableId)
    if (drawable is BitmapDrawable) {
        return drawable.bitmap
    }
    // If not a BitmapDrawable, handle according to your drawable type
    return null
}

@Composable
fun AchievementIcon(name:String, iconSize:Dp = 80.dp) {
    return Image(
        painter = rememberAsyncImagePainter(model = achievementIconMap[name]),
        contentDescription = "PNG Icon",
        modifier = Modifier.size(iconSize),
        contentScale = ContentScale.Fit
    )
}


suspend fun uriToBitmap(context: Context, uri: Uri): Bitmap? {
    val imageLoader = ImageLoader(context)
    val request = ImageRequest.Builder(context)
        .data(uri)
        .allowHardware(false) // This is important for decoding
        .build()

    val result = imageLoader.execute(request)
    return if (result is SuccessResult) result.drawable.toBitmap() else null
}

fun bitmapToBase64(bitmap: Bitmap): String {
    ByteArrayOutputStream().use { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}
fun getPicture(profilePicture: String?): Bitmap? {
    if(profilePicture.isNullOrEmpty()){
        Log.d("UserDataViewModel", "picture Is null")

        return null
    }

    val bytes = Base64.decode(profilePicture, Base64.DEFAULT)
    return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
}