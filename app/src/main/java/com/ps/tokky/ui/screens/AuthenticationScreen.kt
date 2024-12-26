package com.ps.tokky.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ps.tokky.R
import com.ps.tokky.ui.components.KeypadLayout
import com.ps.tokky.ui.components.PinField
import com.ps.tokky.ui.components.TokkyScaffold
import com.ps.tokky.ui.viewmodels.AuthenticationViewModel

@Composable
fun AuthenticationScreen(authViewModel: AuthenticationViewModel) {

    TokkyScaffold { contentPadding ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding),
            contentAlignment = Alignment.Center
        ) {
            val split = when {
                maxWidth > 800.dp -> true
                maxWidth > maxHeight -> true
                else -> false
            }

            if (!split) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 30.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Section1(authViewModel)

                    Spacer(modifier = Modifier.weight(1f))

                    Section2(authViewModel)
                    Section3(
                        authViewModel,
                        Modifier.widthIn(min = 100.dp, max = 320.dp)
                    )
                }
            } else {
                Box(
                    Modifier
                        .fillMaxHeight()
                        .widthIn(max = 800.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp, horizontal = 30.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Section1(authViewModel)
                            Section2(authViewModel)
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Box(
                            Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            Section3(
                                authViewModel,
                                Modifier
                                    .fillMaxHeight()
                                    .align(Alignment.Center)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Section1(authViewModel: AuthenticationViewModel, modifier: Modifier = Modifier) {
    Column(modifier) {
        Image(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier
                .padding(20.dp)
                .size(60.dp)
                .clip(MaterialTheme.shapes.small)
                .background(colorResource(R.color.launcher_background_color))
                .scale(1.25f)
                .align(Alignment.CenterHorizontally)
        )
        Text(
            text = stringResource(id = R.string.unlock_vault),
            style = MaterialTheme.typography.titleLarge.copy(color = MaterialTheme.colorScheme.primary),
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Text(
            text = stringResource(id = R.string.unlock_vault_message),
            style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(vertical = 10.dp)
        )

        PinField(
            modifier = Modifier
                .padding(vertical = 30.dp)
                .align(Alignment.CenterHorizontally),
            pinLength = authViewModel.passcode.value.size,
        )
    }
}

@Composable
private fun Section2(authViewModel: AuthenticationViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    if (authViewModel.areBiometricsEnabled(context)) {
        Button(
            onClick = {
                authViewModel.promptForBiometricsIfAvailable(context)
            },
            shape = RoundedCornerShape(4.dp),
            modifier = modifier
                .padding(vertical = 15.dp)
        ) {
            Text(text = stringResource(R.string.unlock_with_biometrics))
        }
    }
}

@Composable
private fun Section3(authViewModel: AuthenticationViewModel, modifier: Modifier) {
    val context = LocalContext.current

    KeypadLayout(
        onKeyClick = {
            authViewModel.appendCharacter(it)
        },
        onBackspaceClick = {
            authViewModel.deleteCharacter()
        },
        onSubmit = {
            authViewModel.verifyPasscode {
                Toast.makeText(context, R.string.incorrect_password, Toast.LENGTH_SHORT).show()
            }
        },
        modifier = modifier
    )
}