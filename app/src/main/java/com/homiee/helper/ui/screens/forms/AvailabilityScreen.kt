// AvailabilityScreen.kt
package com.homiee.helper.ui.screens.forms

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.homiee.helper.ui.components.*

private val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
private val timeSlots = listOf(
    "6:00 AM", "7:00 AM", "8:00 AM", "9:00 AM", "10:00 AM", "11:00 AM", "12:00 PM",
    "1:00 PM", "2:00 PM", "3:00 PM", "4:00 PM", "5:00 PM", "6:00 PM", "7:00 PM", "8:00 PM"
)

@Composable
fun AvailabilityScreen(onBack: () -> Unit, onSkip: () -> Unit, onContinue: () -> Unit) {
    val selectedDays = remember { mutableStateListOf("Mon", "Tue", "Wed", "Thu", "Fri") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }

    FormScaffold(
        title = "Availability",
        step = 6,
        totalSteps = 6,
        onBack = onBack,
        onSkip = onSkip,
        footer = { PrimaryButton(text = "Continue", onClick = onContinue) }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            FieldLabel("Working Days", helper = "Select the days you are available")
            DaySelector(days, selectedDays.toSet()) { day ->
                if (day in selectedDays) selectedDays.remove(day) else selectedDays.add(day)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            FieldLabel("Working Slot", helper = "Select your daily working time slot")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    FieldLabel("Start Time")
                    DropdownField(placeholder = "Select start time", icon = Icons.Filled.AccessTime, options = timeSlots, selected = startTime, onSelect = { startTime = it })
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    FieldLabel("End Time")
                    DropdownField(placeholder = "Select end time", icon = Icons.Filled.AccessTime, options = timeSlots, selected = endTime, onSelect = { endTime = it })

                }
            }
        }

        InfoCard(text = "You can update your availability later from your profile.", icon = Icons.Filled.Info)
    }
}