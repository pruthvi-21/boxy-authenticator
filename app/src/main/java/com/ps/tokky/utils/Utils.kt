package com.ps.tokky.utils

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import com.ps.tokky.R
import com.ps.tokky.navigation.Routes

object Utils {
    fun copyToClipboard(context: Context, text: String?) {
        text ?: return
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(context.getString(R.string.label_clipboard_content), text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, R.string.toast_copied_to_clipboard, Toast.LENGTH_LONG).show()
    }

    fun isValidTOTPAuthURL(url: String?): Boolean {
        url ?: return false
        //TODO: need to find the regex
        val regex = Regex("")
        return true
    }

    fun getThumbnailFromAssets(assetManager: AssetManager, fileName: String): Bitmap? {
        return try {
            val inputStream = assetManager.open(fileName)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            bitmap
        } catch (exception: Exception) {
            null
        }
    }
}

fun String.formatOTP(): String {
    return this
        .reversed()
        .replace(".".repeat(3).toRegex(), "$0 ")
        .trim()
        .reversed()
}

@SuppressLint("DefaultLocale")
fun String.cleanSecretKey(): String {
    return this.replace("\\s", "").uppercase()
}

fun String.isValidSecretKey(): Boolean {
    return Regex(Constants.BASE32_CHARS).matches(this)
}

fun String.toast(context: Context?) {
    Toast.makeText(context, this, Toast.LENGTH_SHORT).show()
}

fun String.getInitials(): String {
    if (isEmpty()) return "?"
    val words = trim().split("\\s+".toRegex())
    var initials = ""

    for (i in 0 until minOf(words.size, 2)) {
        initials += words[i][0]
    }

    return initials.uppercase()
}

fun Dp.toPx(context: Context): Int {
    return (value * context.resources.displayMetrics.density).toInt()
}

fun NavController.isInRoute(route: Routes): Boolean {
    return currentDestination?.route
        ?.startsWith(route.base) == true
}

fun NavController.popBackStackIfInRoute(route: Routes) {
    if (isInRoute(route)) {
        popBackStack()
    }
}