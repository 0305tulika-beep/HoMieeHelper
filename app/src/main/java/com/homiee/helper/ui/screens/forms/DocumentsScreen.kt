// DocumentsScreen.kt
package com.homiee.helper.ui.screens.forms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import com.homiee.helper.ui.components.*

@Composable
fun DocumentsScreen(onBack: () -> Unit, onSkip: () -> Unit, onContinue: () -> Unit) {
    FormScaffold(
        title = "Documents",
        step = 3,
        totalSteps = 6,
        onBack = onBack,
        onSkip = onSkip,
        footer = { PrimaryButton(text = "Continue", onClick = onContinue) }
    ) {
        UploadRow("Upload Profile Photo", "Upload a clear profile photo", "JPG, PNG • Max 5MB")
        UploadRow("Upload Police Verification Certificate", "Upload certificate", "JPG, PNG, PDF • Max 5MB")
        UploadRow("Upload Address Proof", "Upload proof", "JPG, PNG, PDF • Max 5MB")
    }
}