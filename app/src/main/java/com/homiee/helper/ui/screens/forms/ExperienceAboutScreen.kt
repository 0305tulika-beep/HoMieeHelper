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

// Display label -> backend years_of_experience (Int).
private val experienceOptions = listOf(
    "Less than 1 year" to 0,
    "1-2 years" to 1,
    "3-5 years" to 3,
    "5+ years" to 5
)
private val experienceLabels = experienceOptions.map { it.first }

@Composable
fun ExperienceAboutScreen(
    viewModel: ExperienceAboutViewModel,
    onBack: () -> Unit,
    onSkip: () -> Unit,
    onContinue: () -> Unit
) {
    var experience by remember { mutableStateOf("") }
    // Real backend language ids the user has ticked.
    val selectedLanguageIds = remember { mutableStateListOf<Int>() }
    var otherChecked by remember { mutableStateOf(false) }
    var otherLanguage by remember { mutableStateOf("") }
    var aboutYou by remember { mutableStateOf("") }

    FormScaffold(
        title = "Experience & About You",
        step = 5,
        totalSteps = 6,
        onBack = onBack,
        onSkip = onSkip,
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
                        val years = experienceOptions.firstOrNull { it.first == experience }?.second ?: 0
                        // "Other" has no backend id - fold it into the about text so it isn't silently dropped.
                        val about = if (otherChecked && otherLanguage.isNotBlank()) {
                            "$aboutYou\n\nAlso speaks: $otherLanguage".trim()
                        } else aboutYou
                        viewModel.submit(
                            yearsOfExperience = years,
                            languagesSpoken = selectedLanguageIds.toList(),
                            about = about,
                            onSuccess = onContinue
                        )
                    },
                    enabled = !viewModel.isLoading
                )
            }
        }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            FieldLabel("Years of Experience")
            DropdownField(
                placeholder = "Select years of experience",
                icon = Icons.Filled.WorkOutline,
                options = experienceLabels,
                selected = experience,
                onSelect = { experience = it }
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            FieldLabel("Languages Spoken", helper = "Select all that apply")

            when {
                viewModel.isLoadingLanguages -> {
                    Text("Loading languages...", fontSize = 13.sp, color = TextSecondary)
                }
                viewModel.languages.isEmpty() -> {
                    Text(
                        "No languages available right now. You can add other languages below.",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }
                else -> {
                    viewModel.languages.chunked(3).forEach { rowItems ->
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            rowItems.forEach { lang ->
                                ChipCheckbox(
                                    label = lang.name,
                                    checked = lang.id in selectedLanguageIds,
                                    onCheckedChange = {
                                        if (it) selectedLanguageIds.add(lang.id)
                                        else selectedLanguageIds.remove(lang.id)
                                    },
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Checkbox(
                    checked = otherChecked,
                    onCheckedChange = { otherChecked = it },
                    colors = CheckboxDefaults.colors(checkedColor = TealPrimary)
                )
                Text("Other", fontSize = 13.sp, color = TextPrimary)
                Spacer(modifier = Modifier.width(10.dp))
                HomieeTextField(otherLanguage, { otherLanguage = it }, "Enter language", modifier = Modifier.weight(1f))
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            FieldLabel("About You", helper = "Tell us something about yourself")
            OutlinedTextField(
                value = aboutYou,
                onValueChange = { if (it.length <= 300) aboutYou = it },
                placeholder = { Text("Write about yourself...", color = HintGray, fontSize = 14.sp) },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TealPrimary, unfocusedBorderColor = BorderGray)
            )
            Text(
                "${aboutYou.length}/300",
                fontSize = 11.sp,
                color = TextSecondary,
                textAlign = TextAlign.End,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}