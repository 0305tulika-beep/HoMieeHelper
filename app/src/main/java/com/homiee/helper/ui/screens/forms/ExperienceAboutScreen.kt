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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homiee.helper.ui.components.*
import com.homiee.helper.ui.theme.*

private val experienceOptions = listOf("Less than 1 year", "1-2 years", "3-5 years", "5+ years")
private val languageOptions = listOf("Hindi", "English", "Punjabi", "Bengali", "Tamil", "Telugu")

@Composable
fun ExperienceAboutScreen(onBack: () -> Unit, onSkip: () -> Unit, onContinue: () -> Unit) {
    var experience by remember { mutableStateOf("") }
    val selectedLanguages = remember { mutableStateListOf("Hindi", "English") }
    var otherChecked by remember { mutableStateOf(false) }
    var otherLanguage by remember { mutableStateOf("") }
    var aboutYou by remember { mutableStateOf("") }

    FormScaffold(
        title = "Experience & About You",
        step = 5,
        totalSteps = 6,
        onBack = onBack,
        onSkip = onSkip,
        footer = { PrimaryButton(text = "Continue", onClick = onContinue) }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            FieldLabel("Years of Experience")
            DropdownField(
                placeholder = "Select years of experience",
                icon = Icons.Filled.WorkOutline,
                options = experienceOptions,
                selected = experience,
                onSelect = { experience = it }
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            FieldLabel("Languages Spoken", helper = "Select all that apply")
            languageOptions.chunked(3).forEach { rowItems ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    rowItems.forEach { lang ->
                        ChipCheckbox(
                            label = lang,
                            checked = lang in selectedLanguages,
                            onCheckedChange = { if (it) selectedLanguages.add(lang) else selectedLanguages.remove(lang) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Checkbox(checked = otherChecked, onCheckedChange = { otherChecked = it }, colors = CheckboxDefaults.colors(checkedColor = TealPrimary))
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
            Text("${aboutYou.length}/300", fontSize = 11.sp, color = TextSecondary, textAlign = TextAlign.End, modifier = Modifier.fillMaxWidth())
        }
    }
}