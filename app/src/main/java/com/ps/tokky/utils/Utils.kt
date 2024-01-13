package com.ps.tokky.utils

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.AssetManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.appcompat.widget.Toolbar
import androidx.core.graphics.drawable.DrawableCompat
import com.ps.tokky.R
import org.json.JSONArray
import org.json.JSONException
import java.security.MessageDigest
import java.util.*

object Utils {
    fun getThemeColorFromAttr(context: Context, colorAttr: Int): Int {
        val arr = context.theme.obtainStyledAttributes(intArrayOf(colorAttr))
        val colorValue = arr.getColor(0, -1)
        arr.recycle()
        return colorValue
    }

    fun getDimenFromAttr(context: Context, dimenAttr: Int): Int {
        val arr = context.theme.obtainStyledAttributes(intArrayOf(dimenAttr))
        val dimen = arr.getDimensionPixelSize(0, -1)
        arr.recycle()
        return dimen
    }

    fun copyToClipboard(context: Context, text: String?) {
        text ?: return
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(context.getString(R.string.label_clipboard_content), text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(context, R.string.toast_copied_to_clipboard, Toast.LENGTH_LONG).show()
    }

    fun isValidTOTPAuthURL(url: String): Boolean {
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

    @ColorInt
    fun getColorFromAttr(context: Context, @AttrRes attr: Int, @ColorInt defaultColor: Int): Int {
        val ta = context.obtainStyledAttributes(intArrayOf(attr))
        val color = ta.getColor(0, defaultColor)
        ta.recycle()
        return color
    }
}

fun View.showKeyboard(context: Context, showForced: Boolean) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    if (showForced) imm.toggleSoftInput(
        InputMethodManager.SHOW_FORCED,
        InputMethodManager.HIDE_IMPLICIT_ONLY
    ) else imm.showSoftInput(this, 0)
}

fun View.hideKeyboard(context: Context) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun Int.formatOTP(length: Int): String {
    return "$this"
        .padStart(length, '0')
        .reversed()
        .replace(".".repeat(3).toRegex(), "$0 ")
        .trim()
        .reversed()
}

fun String.setMonospaceFontToOTP(isMonospace: Boolean = true): SpannableString {
    val spannable = SpannableString(this)
    if (isMonospace) {
        forEachIndexed { index, char ->
            if (char.isWhitespace()) {
                spannable.setSpan(HalfSpaceSpan(), index, index + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
    }
    return spannable
}

@SuppressLint("DefaultLocale")
fun String.cleanSecretKey(): String {
    return this.replace("\\s", "").uppercase()
}

fun String.isValidSecretKey(): Boolean {
    return Regex(Constants.BASE32_CHARS).matches(this)
}

fun String.hash(algorithm: String): String {
    val md = MessageDigest.getInstance(algorithm)
    val digest = md.digest(toByteArray())
    return digest.fold("") { str, it -> str + "%02x".format(it) }
}

fun String.toast(context: Context) {
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

fun String?.isJsonArray(): Boolean {
    this ?: return false
    return try {
        JSONArray(this)
        true
    } catch (e: JSONException) {
        false
    }
}

fun Toolbar.changeOverflowIconColor(color: Int) {
    var drawable: Drawable? = overflowIcon
    if (drawable != null) {
        drawable = DrawableCompat.wrap(drawable)
        DrawableCompat.setTint(drawable.mutate(), color)
        overflowIcon = drawable
    }
    invalidate()
}

fun TextView.applyMonospaceFont() {
    val typeFace = Typeface.createFromAsset(context.assets, "fonts/SourceCodePro-Regular.ttf")
    typeface = typeFace
}