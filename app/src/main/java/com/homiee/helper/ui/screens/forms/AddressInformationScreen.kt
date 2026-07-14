// AddressInformationScreen.kt
package com.homiee.helper.ui.screens.forms

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.homiee.helper.ui.components.*

private val indianStates = listOf("Uttar Pradesh", "Delhi", "Maharashtra", "Karnataka", "Tamil Nadu", "West Bengal")

@Composable
fun AddressInformationScreen(onBack: () -> Unit, onSkip: () -> Unit, onContinue: () -> Unit) {
    var street by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var pincode by remember { mutableStateOf("") }

    FormScaffold(
        title = "Address Information",
        step = 2,
        totalSteps = 6,
        onBack = onBack,
        onSkip = onSkip,
        footer = { PrimaryButton(text = "Continue", onClick = onContinue) }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            FieldLabel("House / Street")
            HomieeTextField(street, { street = it }, "Enter house / street", leadingIcon = Icons.Filled.Home)
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                FieldLabel("City")
                HomieeTextField(city, { city = it }, "Enter city", leadingIcon = Icons.Filled.LocationCity)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                FieldLabel("State")
                DropdownField(placeholder = "Select state", icon = Icons.Filled.Map, options = indianStates, selected = state, onSelect = { state = it })
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            FieldLabel("Pincode")
            HomieeTextField(pincode, { pincode = it }, "Enter pincode", leadingIcon = Icons.Filled.LocationOn, keyboardType = KeyboardType.Number)
        }
    }
}