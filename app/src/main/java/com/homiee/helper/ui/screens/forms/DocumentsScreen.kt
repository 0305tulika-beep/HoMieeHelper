// DocumentsScreen.kt
package com.homiee.helper.ui.screens.forms

import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
    val context = LocalContext.current

    val profilePhotoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) viewModel.onProfilePhotoPicked(uri)
    }
    val policeCertPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            // Keep read access to the picked file even after the app is backgrounded.
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: SecurityException) {
                // Provider doesn't offer persistable access; the Uri still works for now.
            }
            viewModel.onPoliceCertPicked(uri)
        }
    }

    FormScaffold(
        title = "Documents",
        step = 3,
        totalSteps = 6,
        onBack = onBack,
        // No Skip - both files are mandatory.
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
                    onClick = { viewModel.submit(onSuccess = onContinue) },
                    enabled = !viewModel.isLoading && viewModel.isFormValid
                )
            }
        }
    ) {
        UploadRow(
            title = "Upload Profile Photo *",
            hint = if (viewModel.profilePhotoUri != null) "Selected ✓" else "Upload a clear profile photo",
            fileTypes = "JPG, PNG • Max 5MB",
            onClick = { profilePhotoPicker.launch("image/*") }
        )

        UploadRow(
            title = "Upload Police Verification Certificate *",
            hint = if (viewModel.policeCertUri != null) "Selected ✓" else "Upload certificate",
            fileTypes = "JPG, PNG, PDF • Max 5MB",
            onClick = { policeCertPicker.launch(arrayOf("image/*", "application/pdf")) }
        )
    }
}