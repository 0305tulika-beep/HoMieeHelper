package com.homiee.helper.ui.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.homiee.helper.ui.components.AuthScaffold
import com.homiee.helper.ui.components.AuthTitle
import com.homiee.helper.ui.components.PrimaryButton
import com.homiee.helper.ui.theme.BorderGray
import com.homiee.helper.ui.theme.TealPrimary
import com.homiee.helper.ui.theme.TextSecondary
import com.homiee.helper.viewmodel.OtpUiState
import com.homiee.helper.viewmodel.OtpViewModel
import kotlinx.coroutines.delay

private const val RESEND_SECONDS = 28
private val ErrorRed = Color(0xFFFF6B6B)

@Composable
fun OtpScreen(
    email: String,
    onBackClick: () -> Unit,
    onVerifyClick: () -> Unit,
    // Skipped entirely in preview — OtpViewModel builds a repository that
    // touches Retrofit + EncryptedSharedPreferences.
    viewModel: OtpViewModel? = if (LocalInspectionMode.current) null
    else viewModel(factory = OtpViewModel.Factory(LocalContext.current))
) {
    val otpValues = remember { mutableStateListOf("", "", "", "", "", "") }
    val focusRequesters = remember { List(6) { FocusRequester() } }
    var secondsLeft by remember { mutableIntStateOf(RESEND_SECONDS) }

    val uiState by viewModel?.uiState?.collectAsState()
        ?: remember { mutableStateOf(OtpUiState()) }

    LaunchedEffect(Unit) {
        focusRequesters[0].requestFocus()
    }

    LaunchedEffect(secondsLeft) {
        if (secondsLeft > 0) {
            delay(1000)
            secondsLeft--
        }
    }

    // OTP verified on the server → move into onboarding.
    LaunchedEffect(uiState.isVerified) {
        if (uiState.isVerified) {
            onVerifyClick()
            viewModel?.resetState()
        }
    }

    // Content uses a bottom-pinned button (weight), so this screen isn't scrollable.
    AuthScaffold(onBackClick = onBackClick, scrollable = false) {
        AuthTitle(title = "Verify OTP", subtitle = "Enter the 6-digit code sent to")
        Text(
            text = email.ifBlank { "your email" },
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = TealPrimary
        )

        Spacer(modifier = Modifier.height(32.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            for (i in 0 until 6) {
                OutlinedTextField(
                    value = otpValues[i],
                    onValueChange = { newVal ->
                        val digit = newVal.filter { it.isDigit() }.take(1)
                        otpValues[i] = digit
                        if (digit.isNotEmpty() && i < 5) {
                            focusRequesters[i + 1].requestFocus()
                        } else if (digit.isEmpty() && i > 0 && newVal.isEmpty()) {
                            focusRequesters[i - 1].requestFocus()
                        }
                    },
                    modifier = Modifier
                        .width(46.dp)
                        .height(56.dp)
                        .focusRequester(focusRequesters[i]),
                    singleLine = true,
                    textStyle = TextStyle(
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.SemiBold
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TealPrimary,
                        unfocusedBorderColor = BorderGray
                    )
                )
            }
        }

        if (uiState.errorMessage != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Text(uiState.errorMessage ?: "", color = ErrorRed, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(28.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            if (secondsLeft > 0) {
                val m = secondsLeft / 60
                val s = secondsLeft % 60
                Text(
                    text = "Resend OTP in  %02d:%02d".format(m, s),
                    fontSize = 13.sp,
                    color = TextSecondary
                )
            } else {
                Text(
                    text = if (uiState.isResending) "Resending..." else "Resend OTP",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TealPrimary,
                    modifier = Modifier.clickable(enabled = !uiState.isResending) {
                        secondsLeft = RESEND_SECONDS
                        viewModel?.resendOtp(email)
                    }
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        PrimaryButton(
            text = if (uiState.isLoading) "Verifying..." else "Verify & Continue",
            enabled = !uiState.isLoading && otpValues.all { it.isNotEmpty() },
            onClick = {
                viewModel?.verifyOtp(email, otpValues.joinToString(""))
            }
        )
    }
}