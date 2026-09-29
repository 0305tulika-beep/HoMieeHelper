// PersonalInformationScreen.kt
package com.homiee.helper.ui.screens.forms

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homiee.helper.ui.components.*
import com.homiee.helper.ui.theme.BorderGray
import com.homiee.helper.ui.theme.HintGray
import com.homiee.helper.ui.theme.SosRed
import com.homiee.helper.ui.theme.TealPrimary
import com.homiee.helper.viewmodel.PersonalInformationViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

// Display label -> backend value expected by govt_id_type (Django choice field).
// Confirmed from API docs: "aadhaar". Others inferred as slugs - verify against
// the serializer's actual `choices=` if you hit another 400 on these.
private val govIdTypeOptions = listOf(
    "Aadhar Card" to "aadhaar",
    "PAN Card" to "pan",
    "Voter ID" to "voter_id",
    "Driving License" to "driving_license"
)
private val govIdTypes = govIdTypeOptions.map { it.first }

// Date picker gives UTC-midnight millis; format in UTC so the day never shifts.
// Output matches the API format, e.g. "1995-06-15".
private fun formatDob(millis: Long): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    formatter.timeZone = TimeZone.getTimeZone("UTC")
    return formatter.format(Date(millis))
}

/** Read-only field that opens the calendar when tapped. */
@Composable
private fun DobField(value: String, onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            singleLine = true,
            placeholder = { Text("Select date", color = HintGray, fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Filled.DateRange, contentDescription = null, tint = HintGray) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = TealPrimary,
                unfocusedBorderColor = BorderGray
            )
        )
        // Transparent layer on top: a text field swallows taps, so the click goes here.
        Box(modifier = Modifier.matchParentSize().clickable(onClick = onClick))
    }
}

/** Number pad for Aadhaar; normal keyboard (capital letters) for other ID types. */
@Composable
private fun IdNumberField(
    value: String,
    onValueChange: (String) -> Unit,
    numericOnly: Boolean
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        placeholder = { Text("Enter government ID number", color = HintGray, fontSize = 14.sp) },
        leadingIcon = { Icon(Icons.Filled.Badge, contentDescription = null, tint = HintGray) },
        keyboardOptions = if (numericOnly) {
            KeyboardOptions(keyboardType = KeyboardType.Number)
        } else {
            KeyboardOptions(
                keyboardType = KeyboardType.Text,
                capitalization = KeyboardCapitalization.Characters
            )
        },
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TealPrimary,
            unfocusedBorderColor = BorderGray
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInformationScreen(
    viewModel: PersonalInformationViewModel,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }          // "yyyy-MM-dd", sent to the API
    var idType by remember { mutableStateOf("") }       // display label shown in dropdown
    var idTypeValue by remember { mutableStateOf("") }  // backend value sent to API
    var idNumber by remember { mutableStateOf("") }
    var frontCardUri by remember { mutableStateOf<Uri?>(null) }
    var backCardUri by remember { mutableStateOf<Uri?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    val isAadhaar = idTypeValue == "aadhaar"

    // Continue stays disabled until every field is filled.
    val isFormValid =
        fullName.isNotBlank() &&
                dob.isNotBlank() &&
                idTypeValue.isNotBlank() &&
                idNumber.isNotBlank() &&
                (!isAadhaar || idNumber.length == 12) &&
                frontCardUri != null &&
                backCardUri != null

    val currentYear = remember { Calendar.getInstance().get(Calendar.YEAR) }
    val datePickerState = rememberDatePickerState(
        // Open around 25 years ago so the user doesn't scroll back from today.
        initialDisplayedMonthMillis = remember {
            Calendar.getInstance().apply { add(Calendar.YEAR, -25) }.timeInMillis
        },
        yearRange = 1940..currentYear
    )

    val frontCardPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) frontCardUri = uri
    }
    val backCardPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) backCardUri = uri
    }

    FormScaffold(
        title = "Personal Information",
        step = 1,
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
                    onClick = {
                        // id_verified is never exposed to the user - the repository always
                        // sends "false"; only the backend/admin can flip it after review.
                        viewModel.submit(
                            fullName = fullName.trim(),
                            dateOfBirth = dob,
                            govtIdType = idTypeValue,
                            govtIdNumber = idNumber.trim(),
                            frontCardUri = frontCardUri,
                            backCardUri = backCardUri,
                            onSuccess = onContinue
                        )
                    },
                    enabled = !viewModel.isLoading && isFormValid
                )
            }
        }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            FieldLabel("Full Name")
            HomieeTextField(fullName, { fullName = it }, "Enter your full name", leadingIcon = Icons.Filled.Person)
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            FieldLabel("Date of Birth")
            DobField(value = dob, onClick = { showDatePicker = true })
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            FieldLabel("Government ID Type")
            DropdownField(
                placeholder = "Select ID type",
                icon = Icons.Filled.Badge,
                options = govIdTypes,
                selected = idType,
                onSelect = { label ->
                    val newValue = govIdTypeOptions.first { it.first == label }.second
                    // Different ID types have different formats - start fresh on change.
                    if (newValue != idTypeValue) idNumber = ""
                    idType = label
                    idTypeValue = newValue
                }
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            FieldLabel("Government ID Number")
            IdNumberField(
                value = idNumber,
                onValueChange = { input ->
                    idNumber = if (isAadhaar) input.filter { it.isDigit() }.take(12) else input
                },
                numericOnly = isAadhaar
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            FieldLabel("Upload Government ID")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                UploadBox(
                    title = "Front Side",
                    hint = if (frontCardUri != null) "Selected ✓" else "JPG, PNG • Max 5MB",
                    modifier = Modifier.weight(1f),
                    onClick = { frontCardPicker.launch("image/*") }
                )
                UploadBox(
                    title = "Back Side",
                    hint = if (backCardUri != null) "Selected ✓" else "JPG, PNG • Max 5MB",
                    modifier = Modifier.weight(1f),
                    onClick = { backCardPicker.launch("image/*") }
                )
            }
        }
    }

    if (showDatePicker) {
        val selectedMillis = datePickerState.selectedDateMillis
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    // Can't pick a date in the future.
                    enabled = selectedMillis != null && selectedMillis <= System.currentTimeMillis(),
                    onClick = {
                        selectedMillis?.let { dob = formatDob(it) }
                        showDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}