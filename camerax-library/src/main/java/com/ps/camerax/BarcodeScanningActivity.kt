package com.ps.camerax
//From https://github.com/maulikhirani/scannerX

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Size
import android.view.OrientationEventListener
import android.view.Surface
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.TorchState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.common.util.concurrent.ListenableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class BarcodeScanningActivity : AppCompatActivity() {

    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>

    private lateinit var previewView: PreviewView
    private lateinit var viewFinderOverlay: ViewFinderOverlay
    private lateinit var flashControlImageView: ImageView

    /** Blocking camera operations are performed using this executor */
    private var cameraExecutor: ExecutorService? = null
    private var flashEnabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_barcode_scanning)
        setTheme(com.google.android.material.R.style.Theme_Material3_DayNight_NoActionBar)

        previewView = findViewById(R.id.camera_preview_view)
        viewFinderOverlay = findViewById(R.id.view_finder_overlay)
        flashControlImageView = findViewById(R.id.flash_control)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            initializeCamera()
        } else {
            requestCameraPermission()
        }
    }

    private fun initializeCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        // Initialize our background executor
        cameraExecutor = Executors.newSingleThreadExecutor()

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            bindPreview(cameraProvider)
        }, ContextCompat.getMainExecutor(this))

        viewFinderOverlay.post {
            viewFinderOverlay.setViewFinder()
        }
    }

    private fun bindPreview(cameraProvider: ProcessCameraProvider?) {

        if (isDestroyed || isFinishing) {
            //This check is to avoid an exception when trying to re-bind use cases but user closes the activity.
            //java.lang.IllegalArgumentException: Trying to create use case mediator with destroyed lifecycle.
            return
        }

        cameraProvider?.unbindAll()

        val preview: Preview = Preview.Builder()
            .build()

        val cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        val imageAnalysis = ImageAnalysis.Builder()
            .setTargetResolution(Size(previewView.width, previewView.height))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()

        val orientationEventListener = object : OrientationEventListener(this as Context) {
            override fun onOrientationChanged(orientation: Int) {
                // Monitors orientation values to determine the target rotation value
                val rotation: Int = when (orientation) {
                    in 45..134 -> Surface.ROTATION_270
                    in 135..224 -> Surface.ROTATION_180
                    in 225..314 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }

                imageAnalysis.targetRotation = rotation
            }
        }
        orientationEventListener.enable()

        val analyzer = ZXingBarcodeAnalyzer {
            runOnUiThread {
                imageAnalysis.clearAnalyzer()
                cameraProvider?.unbindAll()

                setResult(Activity.RESULT_OK, Intent().putExtra(SCAN_RESULT, it))
                finish()
            }
        }
        cameraExecutor?.let { imageAnalysis.setAnalyzer(it, analyzer) }

        preview.surfaceProvider = previewView.surfaceProvider

        val camera = cameraProvider?.bindToLifecycle(this, cameraSelector, imageAnalysis, preview)

        if (camera?.cameraInfo?.hasFlashUnit() == true) {
            flashControlImageView.isVisible = true

            flashControlImageView.setOnClickListener {
                camera.cameraControl.enableTorch(!flashEnabled)
            }

            camera.cameraInfo.torchState.observe(this) {
                it?.let { torchState ->
                    if (torchState == TorchState.ON) {
                        flashEnabled = true
                        flashControlImageView.setImageResource(R.drawable.ic_flash_on)
                    } else {
                        flashEnabled = false
                        flashControlImageView.setImageResource(R.drawable.ic_flash_off)
                    }
                }
            }
        }
    }

    private fun requestCameraPermission() {
        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                initializeCamera()
            } else {
                Toast.makeText(this, R.string.camera_permission_required, Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor?.shutdown()
    }

    companion object {
        const val SCAN_RESULT = "qr_scan_result"
    }
}