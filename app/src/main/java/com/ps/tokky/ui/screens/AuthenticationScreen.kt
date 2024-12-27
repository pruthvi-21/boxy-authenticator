package com.ps.tokky.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ps.tokky.R
import com.ps.tokky.ui.components.StyledTextField
import com.ps.tokky.ui.viewmodels.AuthenticationViewModel

@Composable
fun AuthenticationScreen(authViewModel: AuthenticationViewModel) {

    val context = LocalContext.current

    Scaffold { contentPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
                .padding(horizontal = 16.dp, vertical = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 600.dp)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
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

                Spacer(Modifier.height(30.dp))

                StyledTextField(
                    value = authViewModel.password.value,
                    onValueChange = {
                        authViewModel.updatePassword(it)
                    },
                    placeholder = stringResource(R.string.enter_your_password),
                    isPasswordField = true,
                )

                Spacer(Modifier.height(20.dp))

                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (authViewModel.areBiometricsEnabled(context)) {
                        TextButton(
                            onClick = { authViewModel.promptForBiometricsIfAvailable(context) },
                            shape = RoundedCornerShape(4.dp),
                        ) {
                            Text(stringResource(R.string.use_biometrics))
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    Button(
                        onClick = {
                            authViewModel.verifyPassword {
                                Toast.makeText(
                                    context,
                                    R.string.incorrect_password,
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        },
                        enabled = authViewModel.password.value.isNotEmpty(),
                        shape = RoundedCornerShape(4.dp),
                    ) {
                        Text(stringResource(R.string.unlock))
                    }
                }

                Spacer(Modifier.weight(1f))

                Text(
                    "TOKKY v3.1",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(0.5f),
                    modifier = Modifier.padding(10.dp)
                )

            }
        }
    }
}