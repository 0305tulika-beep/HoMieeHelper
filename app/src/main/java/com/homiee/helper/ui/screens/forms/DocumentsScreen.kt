// DocumentsScreen.kt
package com.homiee.helper.ui.screens.forms

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homiee.helper.ui.components.*
import com.homiee.helper.ui.theme.SosRed
import com.homiee.helper.viewmodel.DocumentsViewModel

@Composable
fun DocumentsScreen(
    viewModel: DocumentsViewModel,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    var profilePhotoUri by remember { mutableStateOf<Uri?>(null) }
    var policeCertUri by remember { mutableStateOf<Uri?>(null) }

    val profilePhotoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) profilePhotoUri = uri
    }
    val policeCertPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) policeCertUri = uri
    }

    FormScaffold(
        title = "Documents",
        step = 3,
        totalSteps = 6,
        onBack = onBack,
        // No Skip - the backend requires both files before the profile can proceed.
        footer = {
            Column {
                if (viewModel.errorMessage != null) {
                    Text(
                        text = viewModel.errorMessage.orEmpty(),
                        fontSize = 12.sp,
                        color = SosRed,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                PrimaryButton(
                    text = if (viewModel.isLoading) "Saving..." else "Continue",
                    onClick = {
                        viewModel.submit(profilePhotoUri, policeCertUri, onSuccess = onContinue)
                    },
                    enabled = !viewModel.isLoading
                )
            }
        }
    ) {
        UploadRow(
            title = "Upload Profile Photo",
            hint = if (profilePhotoUri != null) "Selected ✓" else "Upload a clear profile photo",
            fileTypes = "JPG, PNG • Max 5MB",
            onClick = { profilePhotoPicker.launch("image/*") }
        )
        UploadRow(
            title = "Upload Police Verification Certificate",
            hint = if (policeCertUri != null) "Selected ✓" else "Upload certificate",
            fileTypes = "JPG, PNG, PDF • Max 5MB",
            onClick = { policeCertPicker.launch(arrayOf("image/*", "application/pdf")) }
        )
    }
}