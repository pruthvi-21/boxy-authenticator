package com.ps.tokky.utils

import android.content.Context
import android.graphics.*
import android.text.TextPaint
import com.ps.tokky.R
import kotlin.math.abs

class LetterBitmap(context: Context) {

    private val tileWidth = context.resources.getDimension(R.dimen.card_thumbnail_width).toInt()
    private val tileHeight = context.resources.getDimension(R.dimen.card_thumbnail_height).toInt()
    private val tileColors = context.resources.obtainTypedArray(R.array.tile_colors)

    private val paint = TextPaint().apply {
        color = Utils.getThemeColorFromAttr(context, com.google.android.material.R.attr.titleTextColor)
        textAlign = Paint.Align.CENTER
        isAntiAlias = true
        textSize = tileHeight * FONT_SCALE
        typeface = Typeface.create("sans-serif-light", Typeface.NORMAL)
    }

    private val rect = Rect()
    private val canvas = Canvas()

    fun getLetterTile(displayName: String): Bitmap {
        val bitmap = Bitmap.createBitmap(tileWidth, tileHeight, Bitmap.Config.ARGB_8888)
        var firstChar = '?'
        if (displayName.isNotEmpty() && startsWithAlphabeticOrDigit(displayName)) {
            firstChar = displayName[0].uppercaseChar()
        }

        paint.getTextBounds(firstChar.toCharArray(), 0, 1, rect)

        canvas.setBitmap(bitmap)
        canvas.drawColor(pickColor(displayName))
        canvas.drawText(
            firstChar.toCharArray(), 0, 1, (tileWidth / 2).toFloat(), (tileHeight / 2
                    + (rect.bottom - rect.top) / 2).toFloat(), paint
        )
        return bitmap
    }

    private fun pickColor(key: String): Int {
        val color = abs(key.hashCode()) % tileColors.length()
        return try {
            tileColors.getColor(color, Color.BLACK)
        } finally {
            tileColors.recycle()
        }
    }

    companion object {
        private const val FONT_SCALE = 0.55f

        private fun startsWithAlphabeticOrDigit(string: String): Boolean {
            return Character.isAlphabetic(string.codePointAt(0)) ||
                    Character.isDigit(string[0])
        }
    }
}

private fun Char.toCharArray(): CharArray {
    return CharArray(1).also {
        it[0] = this
    }
}
