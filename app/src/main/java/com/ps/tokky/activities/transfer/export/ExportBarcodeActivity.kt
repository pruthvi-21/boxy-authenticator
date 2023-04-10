package com.ps.tokky.activities.transfer.export

import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.ps.tokky.activities.BaseActivity
import com.ps.tokky.activities.transfer.export.ExportActivity.Companion.INTENT_EXTRA_KEY_EXPORT_SELECTION
import com.ps.tokky.databinding.ActivityExportBarcodeBinding
import java.util.*

class ExportBarcodeActivity : BaseActivity() {

    private val binding by lazy { ActivityExportBarcodeBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbarLayout.toolbar)

        val qrData = intent.extras?.getString(INTENT_EXTRA_KEY_EXPORT_SELECTION)
        if (qrData == null) {
            binding.qrCodeViewer.visibility = View.GONE
            Log.e(TAG, "onCreate: null or no data is provided")
            return
        }
        val size = 512 // Size of the QR code image in pixels

        // Configure the QR code writer
        val hints: MutableMap<EncodeHintType, Any> = EnumMap(EncodeHintType::class.java)
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
        hints[EncodeHintType.CHARACTER_SET] = "UTF-8"

        val qrCodeWriter = QRCodeWriter()

        try {
            // Generate the QR code image
            val bitMatrix = qrCodeWriter.encode(qrData, BarcodeFormat.QR_CODE, size, size, hints)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

            // Convert the BitMatrix to a Bitmap image
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }

            binding.qrCodeViewer.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressedDispatcher.onBackPressed()
            else -> return false
        }
        return true
    }

    companion object {
        private const val TAG = "ExportBarcodeActivity"
    }
}