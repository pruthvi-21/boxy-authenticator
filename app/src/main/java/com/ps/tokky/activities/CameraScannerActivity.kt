package com.ps.tokky.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.*
import com.google.zxing.BarcodeFormat
import com.ps.tokky.databinding.ActivityCameraScannerBinding

class CameraScannerActivity : AppCompatActivity() {

    private val binding by lazy { ActivityCameraScannerBinding.inflate(layoutInflater) }

    private val codeScanner by lazy { CodeScanner(this, binding.scannerView) }

    private var cameraPermissionGranted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        requestCameraPermission()

        // Parameters (default values)
        codeScanner.apply {
            camera = CodeScanner.CAMERA_BACK
            formats = listOf(BarcodeFormat.QR_CODE)
            autoFocusMode = AutoFocusMode.SAFE
            scanMode = ScanMode.SINGLE
            isAutoFocusEnabled = true
            isFlashEnabled = false
        }


        // Callbacks
        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread {
                try {
                    setResult(Activity.RESULT_OK)
                    addNewActivityLauncher.launch(
                        Intent(this, EnterKeyDetailsActivity::class.java)
                            .putExtra("otpAuth", it.text)
                    )
                } catch (exception: Exception) {
                    exception.printStackTrace()
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

        binding.scannerView.setOnClickListener {
            codeScanner.startPreview()
        }
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_CODE
            )
        } else {
            cameraPermissionGranted = true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_CODE) {
            cameraPermissionGranted =
                grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onResume() {
        super.onResume()
        if (cameraPermissionGranted)
            codeScanner.startPreview()
    }

    override fun onPause() {
        codeScanner.releaseResources()
        super.onPause()
    }

    private val addNewActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val extras = it.data?.extras
            if (it.resultCode == Activity.RESULT_OK && extras != null) {
                setResult(Activity.RESULT_OK, Intent().putExtra("id", extras.getString("id")))
            }
            finish()
        }

    companion object {
        private const val CAMERA_PERMISSION_CODE = 9890
    }
}