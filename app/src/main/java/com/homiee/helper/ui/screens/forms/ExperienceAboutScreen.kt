// ExperienceAboutScreen.kt
package com.homiee.helper.ui.screens.forms

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.WorkOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homiee.helper.ui.components.*
import com.homiee.helper.ui.theme.*
import com.homiee.helper.viewmodel.ExperienceAboutViewModel

@Composable
private fun FieldError(message: String) {
    Text(
        text = message,
        fontSize = 12.sp,
        color = SosRed,
        fontWeight = FontWeight.Medium
    )
}

@Composable
fun ExperienceAboutScreen(
    viewModel: ExperienceAboutViewModel,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    FormScaffold(
        title = "Experience & About You",
        step = 5,
        totalSteps = 6,
        onBack = onBack,
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
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            FieldLabel("Years of Experience")
            DropdownField(
                placeholder = "Select years of experience",
                icon = Icons.Filled.WorkOutline,
                options = ExperienceAboutViewModel.experienceLabels,
                selected = viewModel.experience,
                onSelect = { viewModel.onExperienceSelected(it) }
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            FieldLabel("Languages Spoken", helper = "Select at least one")

            when {
                viewModel.isLoadingLanguages -> {
                    Text("Loading languages...", fontSize = 13.sp, color = TextSecondary)
                }
                viewModel.languages.isEmpty() -> {
                    Text(
                        text = viewModel.languagesError ?: "No languages available right now. You can add other languages below.",
                        fontSize = 13.sp,
                        color = if (viewModel.languagesError != null) SosRed else TextSecondary
                    )
                    if (viewModel.languagesError != null) {
                        PrimaryButton(text = "Retry", onClick = { viewModel.loadLanguages() })
                    }
                }
                else -> {
                    viewModel.languages.chunked(3).forEach { rowItems ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowItems.forEach { lang ->
                                ChipCheckbox(
                                    label = lang.name,
                                    checked = lang.id in viewModel.selectedLanguageIds,
                                    onCheckedChange = { viewModel.onLanguageToggled(lang.id, it) },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Checkbox(
                    checked = viewModel.otherChecked,
                    onCheckedChange = { viewModel.onOtherCheckedChange(it) },
                    colors = CheckboxDefaults.colors(checkedColor = TealPrimary)
                )
                Text("Other", fontSize = 13.sp, color = TextPrimary)

                if (viewModel.otherChecked) {
                    Spacer(modifier = Modifier.width(10.dp))
                    HomieeTextField(
                        viewModel.otherLanguage,
                        { viewModel.onOtherLanguageChange(it) },
                        "Enter language",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Live hint: "Other" is ticked but nothing typed yet.
            if (viewModel.otherChecked && viewModel.otherLanguage.isBlank()) {
                FieldError("Please enter the language")
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            FieldLabel("About You", helper = "Tell us something about yourself")
            OutlinedTextField(
                value = viewModel.aboutYou,
                onValueChange = { viewModel.onAboutChange(it) },
                placeholder = { Text("Write about yourself...", color = HintGray, fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = BorderGray
                )
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Text(
                    "${viewModel.aboutYou.length}/300",
                    fontSize = 11.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}