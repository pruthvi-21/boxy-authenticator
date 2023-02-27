package com.ps.tokky.activities

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.budiyev.android.codescanner.*
import com.google.zxing.BarcodeFormat
import com.ps.tokky.R
import com.ps.tokky.models.TokenEntry
import com.ps.tokky.utils.DBHelper

class CameraScannerActivity : AppCompatActivity() {
    private lateinit var codeScanner: CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera_scanner)
        val scannerView = findViewById<CodeScannerView>(R.id.scanner_view)

        codeScanner = CodeScanner(this, scannerView)

        // Parameters (default values)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = listOf(BarcodeFormat.QR_CODE)
        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled = true
        codeScanner.isFlashEnabled = false


        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                try {
                    val obj = TokenEntry.Builder()
                        .createFromQR(it.text)
                        .build()

                    DBHelper.getInstance(this).addEntry(obj)
                    Toast.makeText(this, "Added ${obj.issuer} (${obj.label})", Toast.LENGTH_SHORT).show()

                    setResult(Activity.RESULT_OK)
                    finish()
                } catch (exception: Exception) {
                    Toast.makeText(this, "Exception: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread {
                Toast.makeText(
                    this, "Camera initialization error: ${it.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    override fun onResume() {
        super.onResume()
        codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }
}