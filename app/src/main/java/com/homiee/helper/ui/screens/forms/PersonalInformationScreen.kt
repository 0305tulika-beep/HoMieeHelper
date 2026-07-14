// PersonalInformationScreen.kt
package com.homiee.helper.ui.screens.forms

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.homiee.helper.ui.components.*

private val govIdTypes = listOf("Aadhar Card", "PAN Card", "Voter ID", "Driving License")

@Composable
fun PersonalInformationScreen(onBack: () -> Unit, onContinue: () -> Unit) {
    var fullName by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var idType by remember { mutableStateOf("") }
    var idNumber by remember { mutableStateOf("") }

    FormScaffold(
        title = "Personal Information",
        step = 1,
        totalSteps = 6,
        onBack = onBack,
        footer = { PrimaryButton(text = "Continue", onClick = onContinue) }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            FieldLabel("Full Name")
            HomieeTextField(fullName, { fullName = it }, "Enter your full name", leadingIcon = Icons.Filled.Person)
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                FieldLabel("Date of Birth")
                DropdownField(
                    placeholder = "Select date",
                    icon = Icons.Filled.DateRange,
                    options = listOf("2000-01-01", "1995-06-15", "1990-11-20"),
                    selected = dob,
                    onSelect = { dob = it }
                )
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                FieldLabel("Government ID Type")
                DropdownField(placeholder = "Select ID type", icon = Icons.Filled.Badge, options = govIdTypes, selected = idType, onSelect = { idType = it })
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            FieldLabel("Government ID Number")
            HomieeTextField(idNumber, { idNumber = it }, "Enter government ID number", leadingIcon = Icons.Filled.Badge)
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            FieldLabel("Upload Government ID")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                UploadBox("Front Side", "JPG, PNG • Max 5MB", modifier = Modifier.weight(1f))
                UploadBox("Back Side", "JPG, PNG • Max 5MB", modifier = Modifier.weight(1f))
            }
        }
    }
}