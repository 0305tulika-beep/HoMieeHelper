package com.homiee.helper.ui.screens.auth

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homiee.helper.ui.components.AuthScaffold
import com.homiee.helper.ui.components.AuthTitle
import com.homiee.helper.ui.components.HomieePasswordField
import com.homiee.helper.ui.components.InfoCard
import com.homiee.helper.ui.components.PrimaryButton
import com.homiee.helper.ui.theme.HintGray
import com.homiee.helper.ui.theme.SuccessGreen
import com.homiee.helper.ui.theme.SuccessGreenBg

@Composable
fun ResetPasswordScreen(
    onBackClick: () -> Unit,
    onUpdatePasswordClick: () -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    // Bottom-pinned info card (weight), so this screen isn't scrollable.
    AuthScaffold(onBackClick = onBackClick, scrollable = false) {
        AuthTitle(
            title = "Reset Password",
            subtitle = "Create a new password for your account."
        )

        Spacer(modifier = Modifier.height(28.dp))
        HomieePasswordField(
            value = newPassword,
            onValueChange = { newPassword = it },
            placeholder = "New Password"
        )
        Text(
            text = "Minimum 6 characters",
            fontSize = 12.sp,
            color = HintGray,
            modifier = Modifier.padding(top = 6.dp, start = 4.dp)
        )

        Spacer(modifier = Modifier.height(18.dp))
        HomieePasswordField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = "Confirm New Password"
        )

        Spacer(modifier = Modifier.height(24.dp))
        PrimaryButton(text = "Update Password", onClick = onUpdatePasswordClick)

        Spacer(modifier = Modifier.weight(1f))
        InfoCard(
            text = "Your password will be updated successfully.",
            icon = Icons.Filled.CheckCircle,
            tint = SuccessGreen,
            background = SuccessGreenBg
        )
    }
}
