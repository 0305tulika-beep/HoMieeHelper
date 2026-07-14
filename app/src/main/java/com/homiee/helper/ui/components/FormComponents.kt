package com.homiee.helper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.PopupProperties
import com.homiee.helper.ui.theme.*

/** Shared header + step progress + scrollable content + fixed footer, for all form screens. */
@Composable
fun FormScaffold(
    title: String,
    step: Int,
    totalSteps: Int,
    onBack: () -> Unit,
    onSkip: (() -> Unit)? = null,
    footer: @Composable () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    WhiteSystemBars()
    Column(modifier = Modifier.fillMaxSize().background(BackgroundWhite)) {

        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = Icons.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary,
                    modifier = Modifier.size(24.dp).clickable { onBack() }
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.weight(1f))
                if (onSkip != null) {
                    Text(
                        text = "Skip",
                        fontSize = 14.sp,
                        color = TealPrimary,
                        modifier = Modifier.clickable { onSkip() }
                    )
                } else {
                    Spacer(modifier = Modifier.width(32.dp))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Step $step of $totalSteps",
                fontSize = 12.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            StepProgress(step = step, totalSteps = totalSteps)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            content()
            Spacer(modifier = Modifier.height(8.dp))
        }

        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {
            footer()
        }
    }
}

@Composable
fun StepProgress(step: Int, totalSteps: Int) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
        for (i in 1..totalSteps) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(RoundedCornerShape(50))
                    .background(if (i <= step) TealPrimary else BorderGray)
            )
            if (i != totalSteps) {
                HorizontalDivider(
                    modifier = Modifier.weight(1f).padding(horizontal = 2.dp),
                    thickness = 2.dp,
                    color = if (i < step) TealPrimary else BorderGray
                )
            }
        }
    }
}

@Composable
fun FieldLabel(text: String, helper: String? = null) {
    Text(text = text, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
    if (helper != null) {
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = helper, fontSize = 12.sp, color = TextSecondary)
    }
}

/** Square dashed-style upload box (used for ID front/back). */
@Composable
fun UploadBox(title: String, hint: String, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Column(modifier = modifier) {
        Text(text = title, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, BorderGray, RoundedCornerShape(14.dp))
                .background(TealPale)
                .clickable { onClick() }
                .padding(vertical = 20.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Filled.CloudUpload, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(26.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Tap to upload", fontSize = 13.sp, color = TealPrimary, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = hint, fontSize = 11.sp, color = TextSecondary, textAlign = TextAlign.Center)
        }
    }
}

/** Horizontal upload row (icon left, description right) — used on the Documents screen. */
@Composable
fun UploadRow(title: String, hint: String, fileTypes: String, onClick: () -> Unit = {}) {
    Column {
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .border(1.dp, BorderGray, RoundedCornerShape(14.dp))
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(TealPale)
                    .clickable { onClick() },
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.CloudUpload, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(20.dp))
                    Text("Tap to upload", fontSize = 9.sp, color = TealPrimary)
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(hint, fontSize = 13.sp, color = TextPrimary)
                Text(fileTypes, fontSize = 11.sp, color = TextSecondary)
            }
        }
    }
}

@Composable
fun DropdownField(
    placeholder: String,
    icon: ImageVector,
    options: List<String>,
    selected: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            enabled = false,
            placeholder = { Text(placeholder, color = HintGray, fontSize = 14.sp) },
            leadingIcon = { Icon(icon, contentDescription = null, tint = HintGray) },
            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = HintGray) },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.fillMaxWidth().clickable { expanded = true },
            colors = OutlinedTextFieldDefaults.colors(
                disabledBorderColor = BorderGray,
                disabledContainerColor = SurfaceField,
                disabledTextColor = TextPrimary,
                disabledLeadingIconColor = HintGray,
                disabledTrailingIconColor = HintGray,
                disabledPlaceholderColor = HintGray
            )
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            properties = PopupProperties(focusable = true)
        ) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = { onSelect(option); expanded = false })
            }
        }
    }
}

@Composable
fun ChipCheckbox(label: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, if (checked) TealPrimary else BorderGray, RoundedCornerShape(10.dp))
            .background(if (checked) TealPale else BackgroundWhite)
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = CheckboxDefaults.colors(checkedColor = TealPrimary),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, fontSize = 13.sp, color = TextPrimary)
    }
}

@Composable
fun DaySelector(days: List<String>, selectedDays: Set<String>, onToggle: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        days.forEach { day ->
            val selected = day in selectedDays
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selected) TealPrimary else BackgroundWhite)
                    .border(1.dp, if (selected) TealPrimary else BorderGray, RoundedCornerShape(10.dp))
                    .clickable { onToggle(day) },
                contentAlignment = Alignment.Center
            ) {
                Text(text = day, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = if (selected) BackgroundWhite else TextPrimary)
            }
        }
    }
}

@Composable
fun ServiceItem(
    icon: ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    amount: String,
    onAmountChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, BorderGray, RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(checkedColor = TealPrimary)
            )
            Box(
                modifier = Modifier.size(32.dp).clip(RoundedCornerShape(50)).background(TealPale),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = title, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
        }
        if (checked) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("Set your monthly salary", fontSize = 12.sp, color = TextSecondary, modifier = Modifier.padding(start = 42.dp))
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = amount,
                onValueChange = onAmountChange,
                placeholder = { Text("Enter amount", color = HintGray, fontSize = 13.sp) },
                leadingIcon = { Text("₹", color = HintGray, fontSize = 15.sp) },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().padding(start = 42.dp),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TealPrimary, unfocusedBorderColor = BorderGray)
            )
        }
    }
}

@Composable
fun PhoneField(countryCode: String, onCountryCodeClick: () -> Unit, phone: String, onPhoneChange: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = countryCode,
            onValueChange = {},
            readOnly = true,
            leadingIcon = { Icon(Icons.Filled.Call, contentDescription = null, tint = HintGray) },
            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null, tint = HintGray) },
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.width(110.dp).clickable { onCountryCodeClick() },
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TealPrimary, unfocusedBorderColor = BorderGray)
        )
        OutlinedTextField(
            value = phone,
            onValueChange = onPhoneChange,
            placeholder = { Text("Enter phone number", color = HintGray, fontSize = 14.sp) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier.weight(1f),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = TealPrimary, unfocusedBorderColor = BorderGray)
        )
    }
}