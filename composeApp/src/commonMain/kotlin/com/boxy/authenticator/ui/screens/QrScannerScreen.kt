package com.boxy.authenticator.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.FlashOff
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import boxy_authenticator.composeapp.generated.resources.Res
import boxy_authenticator.composeapp.generated.resources.close
import boxy_authenticator.composeapp.generated.resources.invalid_qr_code
import boxy_authenticator.composeapp.generated.resources.retry
import com.arkivanov.decompose.router.stack.pop
import com.boxy.authenticator.helpers.TokenEntryBuilder
import com.boxy.authenticator.navigation.components.QrScannerScreenComponent
import com.boxy.authenticator.ui.components.Toolbar
import com.boxy.authenticator.ui.components.dialogs.PlatformAlertDialog
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import qrscanner.CameraLens
import qrscanner.OverlayShape
import qrscanner.QrScanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QrScannerScreen(component: QrScannerScreenComponent) {
    val scope = rememberCoroutineScope()

    var isFlashOn by remember { mutableStateOf(false) }
    var showPlatformAlertDialog by remember { mutableStateOf(false) }

    Scaffold { contentPadding ->
        Box {
            if (!showPlatformAlertDialog) {
                QrScanner(
                    modifier = Modifier,
                    flashlightOn = isFlashOn,
                    cameraLens = CameraLens.Back,
                    openImagePicker = false,
                    onCompletion = { result ->
                        scope.launch {
                            try {
                                TokenEntryBuilder.buildFromUrl(result)
                                component.navigateToTokenSetupScreen(authUrl = result)
                            } catch (e: Exception) {
                                println(e)
                                showPlatformAlertDialog = true
                            }
                        }
                    },
                    imagePickerHandler = { },
                    onFailure = { },
                    overlayShape = OverlayShape.Square,
                    overlayColor = Color(0x88000000),
                    overlayBorderColor = MaterialTheme.colorScheme.outline,
                )
            }
            Toolbar(
                title = "",
                navigationIcon = {
                    IconButton(onClick = { component.navigation.pop() }) {
                        Icon(Icons.Default.Close, stringResource(Res.string.close))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                )
            )

            if (showPlatformAlertDialog) {
                PlatformAlertDialog(
                    title = stringResource(Res.string.invalid_qr_code),
                    confirmText = stringResource(Res.string.retry),
                    dismissText = stringResource(Res.string.close),
                    onDismissRequest = {
                        showPlatformAlertDialog = false
                        component.navigateUp()
                    },
                    onConfirmation = {
                        showPlatformAlertDialog = false
                    }
                )
            }

            CameraControls(
                isFlashOn = isFlashOn,
                onFlashChange = { isFlashOn = it },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = contentPadding.calculateBottomPadding()),
            )
        }
    }
}

@Composable
private fun CameraControls(
    modifier: Modifier = Modifier,
    isFlashOn: Boolean = false,
    onFlashChange: (Boolean) -> Unit = {},
) {
    Row(
        modifier = modifier
            .padding(20.dp)
    ) {
        IconToggleButton(
            isFlashOn,
            onCheckedChange = {
                onFlashChange(it)
            },
            modifier = Modifier.size(66.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = .05f))
        ) {
            Icon(
                if (isFlashOn) Icons.Outlined.FlashOn else Icons.Outlined.FlashOff,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.padding(10.dp),
            )
        }
    }
}