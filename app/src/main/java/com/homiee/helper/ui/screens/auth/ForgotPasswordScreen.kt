package com.homiee.helper.ui.screens.auth

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Shield
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.homiee.helper.ui.components.AuthScaffold
import com.homiee.helper.ui.components.AuthTitle
import com.homiee.helper.ui.components.HomieeTextField
import com.homiee.helper.ui.components.InfoCard
import com.homiee.helper.ui.components.PrimaryButton

@Composable
fun ForgotPasswordScreen(
    onBackClick: () -> Unit,
    onSendResetLinkClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }

    AuthScaffold(onBackClick = onBackClick) {
        AuthTitle(
            title = "Forgot Password?",
            subtitle = "No worries! Enter your email and we'll send you a link to reset it."
        )

        Spacer(modifier = Modifier.height(28.dp))
        HomieeTextField(
            value = email,
            onValueChange = { email = it },
            placeholder = "Email",
            leadingIcon = Icons.Filled.Email,
            keyboardType = KeyboardType.Email
        )

        Spacer(modifier = Modifier.height(24.dp))
        PrimaryButton(text = "Send Reset Link", onClick = onSendResetLinkClick)

        Spacer(modifier = Modifier.height(24.dp))
        InfoCard(
            text = "We'll send a secure link to your registered email.",
            icon = Icons.Filled.Shield
        )
    }
}
