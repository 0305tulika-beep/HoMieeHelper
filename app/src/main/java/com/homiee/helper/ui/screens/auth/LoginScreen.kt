package com.homiee.helper.ui.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homiee.helper.ui.components.*
import com.homiee.helper.ui.theme.TealPrimary
import com.homiee.helper.ui.theme.TextSecondary

@Composable
fun LoginScreen(
    onLoginClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onSignUpClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

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

        Spacer(modifier = Modifier.height(14.dp))
        HomieePasswordField(
            value = password,
            onValueChange = { password = it },
            placeholder = "Password"
        )

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

        Spacer(modifier = Modifier.height(20.dp))
        PrimaryButton(text = "Login", onClick = onLoginClick)

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
