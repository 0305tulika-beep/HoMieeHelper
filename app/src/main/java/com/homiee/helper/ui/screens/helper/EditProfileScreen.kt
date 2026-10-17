package com.homiee.helper.ui.screens.helper

import android.app.TimePickerDialog
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homiee.helper.ui.components.*
import com.homiee.helper.ui.theme.*
import com.homiee.helper.viewmodel.EditProfileViewModel
import java.util.Locale

/** Same subtle 1dp shadow used on the other helper screens. */
private val cardElevation = Modifier.shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp), clip = false)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun EditProfileScreen(
    viewModel: EditProfileViewModel,
    onBack: () -> Unit,
    onSaved: () -> Unit
) {
    DashboardSystemBars(darkStatusBarIcons = true)
    val context = LocalContext.current

    Scaffold(containerColor = BackgroundWhite) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            DetailTopBar(title = "Edit Profile", onBackClick = onBack)

            if (!viewModel.isReady) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = TealPrimary)
                }
                return@Column
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                // ── Address ─────────────────────────────────────────────────
                SectionCard(modifier = cardElevation) {
                    CardTitle("Address")
                    Spacer(modifier = Modifier.height(10.dp))
                    EditField(label = "House No. / Street", value = viewModel.houseNo, onChange = viewModel::onHouseNoChange)
                    Spacer(modifier = Modifier.height(10.dp))
                    EditField(label = "City", value = viewModel.city, onChange = viewModel::onCityChange)
                    Spacer(modifier = Modifier.height(10.dp))
                    EditField(label = "State", value = viewModel.state, onChange = viewModel::onStateChange)
                    Spacer(modifier = Modifier.height(10.dp))
                    EditField(
                        label = "Pincode",
                        value = viewModel.pincode,
                        onChange = viewModel::onPincodeChange,
                        keyboardType = KeyboardType.Number
                    )
                }

                // ── Experience & about ──────────────────────────────────────
                Spacer(modifier = Modifier.height(14.dp))
                SectionCard(modifier = cardElevation) {
                    CardTitle("Experience")
                    Spacer(modifier = Modifier.height(10.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        EditProfileViewModel.EXPERIENCE_OPTIONS.forEach { (label, years) ->
                            SelectChip(
                                text = label,
                                selected = viewModel.yearsOfExperience == years,
                                onClick = { viewModel.onExperienceSelected(years) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    CardTitle("About Me")
                    Spacer(modifier = Modifier.height(10.dp))
                    EditField(
                        label = "Tell residents about yourself",
                        value = viewModel.about,
                        onChange = viewModel::onAboutChange,
                        singleLine = false,
                        minLines = 4
                    )
                }

                // ── Languages ───────────────────────────────────────────────
                Spacer(modifier = Modifier.height(14.dp))
                SectionCard(modifier = cardElevation) {
                    CardTitle("Languages Spoken")
                    Spacer(modifier = Modifier.height(10.dp))
                    when {
                        viewModel.isLoadingLanguages -> CircularProgressIndicator(
                            color = TealPrimary,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(22.dp)
                        )
                        viewModel.languagesError != null -> Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(viewModel.languagesError ?: "", fontSize = 12.sp, color = SosRed, modifier = Modifier.weight(1f))
                            TextButton(onClick = { viewModel.loadLanguages() }) {
                                Text("Retry", color = TealPrimary, fontWeight = FontWeight.Bold)
                            }
                        }
                        else -> FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            viewModel.languages.forEach { language ->
                                SelectChip(
                                    text = language.name,
                                    selected = language.id in viewModel.selectedLanguageIds,
                                    onClick = { viewModel.toggleLanguage(language.id) }
                                )
                            }
                        }
                    }
                }

                // ── Availability ────────────────────────────────────────────
                Spacer(modifier = Modifier.height(14.dp))
                SectionCard(modifier = cardElevation) {
                    CardTitle("Working Days")
                    Spacer(modifier = Modifier.height(10.dp))
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        EditProfileViewModel.DAY_CODES.forEach { code ->
                            SelectChip(
                                text = code.replaceFirstChar { it.uppercase() },
                                selected = code in viewModel.workingDays,
                                onClick = { viewModel.toggleDay(code) }
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    CardTitle("Working Hours")
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        TimeButton(
                            label = "Start",
                            value = displayTime(viewModel.startTime),
                            onClick = { showTimePicker(context, viewModel.startTime, viewModel::onStartTimePicked) },
                            modifier = Modifier.weight(1f)
                        )
                        TimeButton(
                            label = "End",
                            value = displayTime(viewModel.endTime),
                            onClick = { showTimePicker(context, viewModel.endTime, viewModel::onEndTimePicked) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // ── Save ────────────────────────────────────────────────────
                // A server error wins; otherwise explain why Save is disabled.
                val problem = viewModel.errorMessage
                    ?: if (viewModel.hasChanges) viewModel.validationError else null
                problem?.let { message ->
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(message, fontSize = 12.sp, color = SosRed)
                }

                Spacer(modifier = Modifier.height(18.dp))
                Button(
                    onClick = { viewModel.save(onSaved) },
                    enabled = viewModel.hasChanges && viewModel.isFormValid && !viewModel.isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TealPrimary,
                        disabledContainerColor = BorderGray
                    )
                ) {
                    if (viewModel.isSaving) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(22.dp)
                        )
                    } else {
                        Text("Save Changes", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun CardTitle(text: String) {
    Text(text, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
}

@Composable
private fun EditField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    keyboardType: KeyboardType = KeyboardType.Text,
    singleLine: Boolean = true,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label, fontSize = 12.sp) },
        singleLine = singleLine,
        minLines = minLines,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TealPrimary,
            focusedLabelColor = TealPrimary,
            unfocusedBorderColor = BorderGray
        ),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun SelectChip(text: String, selected: Boolean, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(if (selected) TealPrimary else TealPale)
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) Color.White else TealPrimaryDark
        )
    }
}

@Composable
private fun TimeButton(label: String, value: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .border(1.dp, BorderGray, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        Text(label, fontSize = 11.sp, color = TextSecondary)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = value.ifBlank { "Select" },
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}

/** "09:00:00" -> "09:00 AM". Blank stays blank. */
private fun displayTime(raw: String): String {
    val parts = raw.split(":")
    val hour = parts.getOrNull(0)?.toIntOrNull() ?: return raw
    val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
    val suffix = if (hour >= 12) "PM" else "AM"
    val hour12 = when {
        hour == 0 -> 12
        hour > 12 -> hour - 12
        else -> hour
    }
    return String.format(Locale.US, "%02d:%02d %s", hour12, minute, suffix)
}

/** Opens the system time picker and returns the result as "HH:mm:ss". */
private fun showTimePicker(context: Context, current: String, onPicked: (String) -> Unit) {
    val parts = current.split(":")
    val hour = parts.getOrNull(0)?.toIntOrNull() ?: 9
    val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
    TimePickerDialog(
        context,
        { _, pickedHour, pickedMinute ->
            onPicked(String.format(Locale.US, "%02d:%02d:00", pickedHour, pickedMinute))
        },
        hour,
        minute,
        false
    ).show()
}