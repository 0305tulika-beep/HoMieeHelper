package com.homiee.helper.ui.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.homiee.helper.ui.components.*
import com.homiee.helper.ui.theme.TealPrimary
import com.homiee.helper.ui.theme.TextSecondary
import com.homiee.helper.viewmodel.LoginUiState
import com.homiee.helper.viewmodel.LoginViewModel
import com.homiee.helper.viewmodel.LoginViewModelFactory

private val ErrorRed = Color(0xFFFF6B6B)

private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
private fun isValidEmail(email: String): Boolean = EMAIL_REGEX.matches(email.trim())

private fun emailErrorMessage(email: String, touched: Boolean): String? {
    if (touched && email.isBlank()) return "Email is required"
    if (email.isNotBlank() && !isValidEmail(email)) return "Please enter a valid email address"
    return null
}

private fun passwordRequiredError(password: String, touched: Boolean): String? =
    if (touched && password.isEmpty()) "Password is required" else null

// Non-blocking hint only — this checks an existing password, so we don't
// want to lock out someone whose real password predates this rule.
private fun passwordSpaceWarning(password: String): String? =
    if (password.isNotEmpty() && password.any { it.isWhitespace() })
        "Note: your password contains a space" else null

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onSignUpClick: () -> Unit,
    // Skipped entirely in preview — LoginViewModel builds a repository that
    // touches Retrofit + EncryptedSharedPreferences, neither of which work
    // inside the Layout Preview sandbox.
    viewModel: LoginViewModel? = if (LocalInspectionMode.current) null
    else viewModel(factory = LoginViewModelFactory(LocalContext.current))
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    var emailTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }

    val uiState by viewModel?.uiState?.collectAsState()
        ?: remember { mutableStateOf(LoginUiState()) }

    val emailError = emailErrorMessage(email, emailTouched)
    val passwordRequired = passwordRequiredError(password, passwordTouched)
    val passwordHint = passwordRequired ?: passwordSpaceWarning(password)

    val isFormValid = email.isNotBlank() && emailError == null && password.isNotBlank()

    // Login succeeded on the server → tell the nav host, which now routes
    // straight to Home (not the onboarding forms).
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onLoginClick()
            viewModel?.resetState()
        }
    }

    // No back button here - this is a root-level destination.
    AuthScaffold(showBackButton = false) {
        AuthTitle(
            title = "Welcome Back!",
            subtitle = "Login to continue and explore new opportunities."
        )
        Spacer(modifier = Modifier.height(28.dp))

        HomieeTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "Email",
            leadingIcon = Icons.Filled.Email,
            keyboardType = KeyboardType.Email
        )
        if (emailError != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(emailError, color = ErrorRed, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))
        HomieePasswordField(
            value = password,
            onValueChange = { password = it },
            placeholder = "Password"
        )
        if (passwordHint != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                passwordHint,
                color = if (passwordRequired != null) ErrorRed else TextSecondary,
                fontSize = 12.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "Forgot Password?",
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            color = TealPrimary,
            modifier = Modifier
                .align(Alignment.End)
                .clickable { onForgotPasswordClick() }
        )

        if (uiState.errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(uiState.errorMessage ?: "", color = ErrorRed, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))
        PrimaryButton(
            text = if (uiState.isLoading) "Logging In..." else "Login",
            enabled = isFormValid && !uiState.isLoading,
            onClick = {
                emailTouched = true
                passwordTouched = true
                viewModel?.login(email, password)
            }
        )

        Spacer(modifier = Modifier.height(20.dp))
        OrDivider()

        Spacer(modifier = Modifier.height(20.dp))
        GoogleButton(text = "Login with Google", onClick = onGoogleLoginClick)

        Spacer(modifier = Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Text("Don't have an account? ", fontSize = 13.sp, color = TextSecondary)
            Text(
                "Sign Up",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TealPrimary,
                modifier = Modifier.clickable { onSignUpClick() }
            )
        }
    }
}