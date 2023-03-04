package com.ps.tokky.utils

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.RelativeSizeSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.ps.tokky.R
import com.ps.tokky.models.OTPLength
import java.security.MessageDigest

object Utils {
    fun getThemeColorFromAttr(context: Context, colorAttr: Int): Int {
        val arr = context.theme.obtainStyledAttributes(intArrayOf(colorAttr))
        val colorValue = arr.getColor(0, -1)
        arr.recycle()
        return colorValue
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

fun Int.formatOTP(length: OTPLength): SpannableStringBuilder {
    val res = "$this"
        .padStart(length.value, '0')
        .reversed()
        .replace(".".repeat(length.chunkSize).toRegex(), "$0 ")
        .trim()
        .reversed()

    val split = res.split(" ")
    val spannable = SpannableStringBuilder()
    for (i in split) {
        val span = SpannableString("$i ")
        span.setSpan(RelativeSizeSpan(.5f), i.length, i.length + 1, 0)
        span.setSpan(Typeface.MONOSPACE, 0, i.length, 0)
        spannable.append(span)
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