// AvailabilityScreen.kt
package com.homiee.helper.ui.screens.forms

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Keyboard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.homiee.helper.ui.components.*
import com.homiee.helper.ui.theme.SosRed
import com.homiee.helper.viewmodel.AvailabilityViewModel
import java.util.Locale

private val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

@Composable
fun AvailabilityScreen(
    viewModel: AvailabilityViewModel,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    FormScaffold(
        title = "Availability",
        step = 6,
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
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            FieldLabel("Working Days", helper = "Select the days you are available")
            DaySelector(days, viewModel.selectedDays.toSet()) { day ->
                viewModel.toggleDay(day)
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            FieldLabel("Working Slot", helper = "Select your daily working time slot")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FieldLabel("Start Time")
                    TimePickerField(
                        placeholder = "Select start time",
                        selected = viewModel.startTime,
                        onTimeSelected = { viewModel.onStartSelected(it) }
                    )
                }
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FieldLabel("End Time")
                    TimePickerField(
                        placeholder = "Select end time",
                        selected = viewModel.endTime,
                        onTimeSelected = { viewModel.onEndSelected(it) }
                    )
                }
            }
        }

        InfoCard(
            text = "You can update your availability later from your profile.",
            icon = Icons.Filled.Info
        )
    }
}

/** "HH:mm" (24h) -> "09:05 AM" for display */
private fun formatTime12h(time24: String?): String? {
    if (time24.isNullOrBlank()) return null
    val parts = time24.split(":")
    val h = parts.getOrNull(0)?.toIntOrNull() ?: return null
    val m = parts.getOrNull(1)?.toIntOrNull() ?: return null
    val amPm = if (h < 12) "AM" else "PM"
    val hour12 = if (h % 12 == 0) 12 else h % 12
    return String.format(Locale.getDefault(), "%02d:%02d %s", hour12, m, amPm)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimePickerField(
    placeholder: String,
    selected: String?,                 // "HH:mm" or null
    onTimeSelected: (String) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { showDialog = true },
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.AccessTime,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = formatTime12h(selected) ?: placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = if (selected.isNullOrBlank())
                    MaterialTheme.colorScheme.onSurfaceVariant
                else
                    MaterialTheme.colorScheme.onSurface
            )
        }
    }

    if (showDialog) {
        val parts = (selected ?: "09:00").split(":")
        val state = rememberTimePickerState(
            initialHour = parts.getOrNull(0)?.toIntOrNull() ?: 9,
            initialMinute = parts.getOrNull(1)?.toIntOrNull() ?: 0,
            is24Hour = false
        )
        var showKeyboard by remember { mutableStateOf(false) }

        Dialog(onDismissRequest = { showDialog = false }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Select time",
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(Modifier.height(16.dp))

                    if (showKeyboard) TimeInput(state = state) else TimePicker(state = state)

                    Spacer(Modifier.height(12.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { showKeyboard = !showKeyboard }) {
                            Icon(
                                imageVector = if (showKeyboard) Icons.Filled.AccessTime
                                else Icons.Filled.Keyboard,
                                contentDescription = "Toggle input mode"
                            )
                        }
                        Spacer(Modifier.weight(1f))
                        TextButton(onClick = { showDialog = false }) { Text("Cancel") }
                        TextButton(onClick = {
                            onTimeSelected(
                                String.format(Locale.US, "%02d:%02d", state.hour, state.minute)
                            )
                            showDialog = false
                        }) { Text("OK") }
                    }
                }
            }
        }
    }
}