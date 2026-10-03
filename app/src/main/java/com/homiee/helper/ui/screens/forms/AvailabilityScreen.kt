// AvailabilityScreen.kt
package com.homiee.helper.ui.screens.forms

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homiee.helper.ui.components.*
import com.homiee.helper.ui.theme.SosRed
import com.homiee.helper.viewmodel.AvailabilityViewModel

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
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    FieldLabel("Start Time")
                    DropdownField(
                        placeholder = "Select start time",
                        icon = Icons.Filled.AccessTime,
                        options = viewModel.startOptions,
                        selected = viewModel.startTime,
                        onSelect = { viewModel.onStartSelected(it) }
                    )
                }
                Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    FieldLabel("End Time")
                    DropdownField(
                        placeholder = "Select end time",
                        icon = Icons.Filled.AccessTime,
                        options = viewModel.endOptions,
                        selected = viewModel.endTime,
                        onSelect = { viewModel.onEndSelected(it) }
                    )
                }
            }
        }

        InfoCard(text = "You can update your availability later from your profile.", icon = Icons.Filled.Info)
    }
}