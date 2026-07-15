package com.homiee.helper.ui.screens.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.homiee.helper.ui.components.*
import com.homiee.helper.ui.theme.TealPale
import com.homiee.helper.ui.theme.TealPrimary
import com.homiee.helper.ui.theme.TealPrimaryDark
import com.homiee.helper.ui.theme.TextSecondary
import com.homiee.helper.viewmodel.RegisterUiState
import com.homiee.helper.viewmodel.RegisterViewModel

private val ErrorRed = Color(0xFFFF6B6B)

private val EMAIL_REGEX = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
private fun isValidEmail(email: String): Boolean = EMAIL_REGEX.matches(email.trim())

private fun requiredError(value: String, touched: Boolean, fieldLabel: String): String? =
    if (touched && value.isBlank()) "$fieldLabel is required" else null

private fun emailErrorMessage(email: String, touched: Boolean): String? {
    if (touched && email.isBlank()) return "Email is required"
    if (email.isNotBlank() && !isValidEmail(email)) return "Please enter a valid email address"
    return null
}

private fun passwordErrorMessage(password: String, touched: Boolean): String? {
    if (touched && password.isEmpty()) return "Password is required"
    if (password.isEmpty()) return null

    val hasSpace = password.any { it.isWhitespace() }
    val hasMinLength = password.length >= 8
    val hasUpper = password.any { it.isUpperCase() }
    val hasSpecial = password.any { !it.isLetterOrDigit() && !it.isWhitespace() }

    return when {
        hasSpace -> "Password must not contain spaces"
        !hasMinLength -> "Password must be at least 8 characters"
        !hasUpper -> "Password must include at least 1 capital letter"
        !hasSpecial -> "Password must include at least 1 special symbol"
        else -> null
    }
}

@Composable
fun SignUpScreen(
    onBackClick: () -> Unit,
    onSignUpClick: (email: String) -> Unit,
    onGoogleSignUpClick: () -> Unit,
    onLoginClick: () -> Unit,
    // Skipped entirely in preview — RegisterViewModel builds a repository
    // that touches Retrofit + EncryptedSharedPreferences.
    viewModel: RegisterViewModel? = if (LocalInspectionMode.current) null
    else viewModel(factory = RegisterViewModel.Factory(LocalContext.current))
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var termsAccepted by remember { mutableStateOf(true) }

    var firstNameTouched by remember { mutableStateOf(false) }
    var lastNameTouched by remember { mutableStateOf(false) }
    var emailTouched by remember { mutableStateOf(false) }
    var passwordTouched by remember { mutableStateOf(false) }
    var confirmPasswordTouched by remember { mutableStateOf(false) }

    val uiState by viewModel?.uiState?.collectAsState()
        ?: remember { mutableStateOf(RegisterUiState()) }

    val firstNameError = requiredError(firstName, firstNameTouched, "First name")
    val lastNameError = requiredError(lastName, lastNameTouched, "Last name")
    val emailError = emailErrorMessage(email, emailTouched)
    val passwordError = passwordErrorMessage(password, passwordTouched)
    val confirmError = when {
        confirmPasswordTouched && confirmPassword.isEmpty() -> "Please confirm your password"
        confirmPassword.isNotEmpty() && confirmPassword.any { it.isWhitespace() } ->
            "Password must not contain spaces"
        confirmPassword.isNotEmpty() && confirmPassword != password -> "Passwords do not match"
        else -> null
    }

    val isFormValid = firstName.isNotBlank() && lastName.isNotBlank() &&
            email.isNotBlank() && emailError == null &&
            password.isNotBlank() && passwordError == null &&
            confirmPassword.isNotBlank() && confirmError == null

    // Registration succeeded on the server → move to OTP with the email
    // it was actually sent to. Username is generated internally by the
    // ViewModel — there's no username field in this form.
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSignUpClick(viewModel?.registeredEmail ?: email)
            viewModel?.resetState()
        }
    }

    AuthScaffold(onBackClick = onBackClick) {
        AuthTitle(
            title = "Create Account",
            subtitle = "Join Homiee Helper and get more job opportunities."
        )
        Spacer(modifier = Modifier.height(28.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.weight(1f)) {
                HomieeTextField(
                    value = firstName,
                    onValueChange = { firstName = it },
                    placeholder = "First Name",
                    leadingIcon = Icons.Filled.Person
                )
                if (firstNameError != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(firstNameError, color = ErrorRed, fontSize = 11.sp)
                }
            }
            Column(modifier = Modifier.weight(1f)) {
                HomieeTextField(
                    value = lastName,
                    onValueChange = { lastName = it },
                    placeholder = "Last Name",
                    leadingIcon = Icons.Filled.Person
                )
                if (lastNameError != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(lastNameError, color = ErrorRed, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
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
            placeholder = "Create Password"
        )
        if (passwordError != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(passwordError, color = ErrorRed, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))
        HomieePasswordField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = "Confirm Password"
        )
        if (confirmError != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(confirmError, color = ErrorRed, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.Top) {
            Checkbox(
                checked = termsAccepted,
                onCheckedChange = { termsAccepted = it },
                colors = CheckboxDefaults.colors(checkedColor = TealPrimary)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = buildAnnotatedString {
                    append("I agree to the ")
                    withStyle(SpanStyle(color = TealPrimary, fontWeight = FontWeight.SemiBold)) {
                        append("Terms & Conditions")
                    }
                    append(" and ")
                    withStyle(SpanStyle(color = TealPrimary, fontWeight = FontWeight.SemiBold)) {
                        append("Privacy Policy")
                    }
                },
                fontSize = 13.sp,
                color = TextSecondary,
                modifier = Modifier.padding(top = 12.dp)
            )
        }

        if (uiState.errorMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(uiState.errorMessage ?: "", color = ErrorRed, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))
        PrimaryButton(
            text = if (uiState.isLoading) "Signing Up..." else "Sign Up",
            enabled = termsAccepted && isFormValid && !uiState.isLoading,
            onClick = {
                firstNameTouched = true
                lastNameTouched = true
                emailTouched = true
                passwordTouched = true
                confirmPasswordTouched = true
                viewModel?.register(
                    firstName = firstName,
                    lastName = lastName,
                    email = email,
                    password = password,
                    password2 = confirmPassword
                )
            }
        )

        // Nudge for people who already have a resident Homiee account.
        // TODO: not wired up yet - decide where this should actually route
        // (a dedicated resident-login flow?) before re-enabling the click.
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(TealPale)
                .border(1.dp, TealPrimaryDark, RoundedCornerShape(12.dp))
                .clickable { /* TODO: wire up resident login routing */ }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text(
                text = buildAnnotatedString {
                    append("Already a Homiee resident? ")
                    withStyle(SpanStyle(color = TealPrimary, fontWeight = FontWeight.SemiBold)) {
                        append("Login here")
                    }
                },
                fontSize = 12.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        OrDivider()

        Spacer(modifier = Modifier.height(20.dp))
        GoogleButton(text = "Sign up with Google", onClick = onGoogleSignUpClick)

        Spacer(modifier = Modifier.height(20.dp))
        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
            Text("Already have an account? ", fontSize = 13.sp, color = TextSecondary)
            Text(
                "Login",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TealPrimary,
                modifier = Modifier.clickable { onLoginClick() }
            )
        }
    }
}