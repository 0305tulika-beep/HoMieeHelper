// ServicesPricingScreen.kt
package com.homiee.helper.ui.screens.forms

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homiee.helper.R
import com.homiee.helper.data.model.ServicePriceItem
import com.homiee.helper.ui.components.FieldLabel
import com.homiee.helper.ui.components.FormScaffold
import com.homiee.helper.ui.components.PrimaryButton
import com.homiee.helper.ui.theme.SosRed
import com.homiee.helper.viewmodel.ServicesPricingViewModel

// Matches on slug + name (partial match), so "house_cleaning", "Cleaning", etc. all work.
// Unknown services get the default icon.
@DrawableRes
private fun iconForService(slug: String?, name: String): Int {
    val key = "${slug.orEmpty()} $name".lowercase()
    return when {
        "clean" in key -> R.drawable.ic_cleaning
        "cook" in key -> R.drawable.ic_cooking
        "baby" in key || "child" in key -> R.drawable.ic_babysitting
        "elder" in key -> R.drawable.ic_eldercare
        else -> R.drawable.ic_service_default
    }
}

/**
 * Local row for one service: icon, name, checkbox, and (when checked)
 * a price field that opens the NUMBER keyboard.
 */
@Composable
private fun ServicePriceRow(
    @DrawableRes icon: Int,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    amount: String,
    onAmountChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = BorderStroke(1.dp, Color(0xFFE5E5E5))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Image (not Icon) so the PNG keeps its original colors
                    Image(
                        painter = painterResource(id = icon),
                        contentDescription = null,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Checkbox(
                    checked = checked,
                    onCheckedChange = onCheckedChange
                )
            }

            if (checked) {
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = amount,
                    onValueChange = { input ->
                        // Digits only, even if the keyboard lets other characters through
                        onAmountChange(input.filter { it.isDigit() })
                    },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Enter amount per hour") },
                    prefix = { Text("₹ ") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    )
                )
            }
        }
    }
}

@Composable
fun ServicesPricingScreen(
    viewModel: ServicesPricingViewModel,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    // checkedState / amountState now live in the ViewModel (keyed by backend service id),
    // so the selections survive navigating away and back.
    val selectedServices = viewModel.services.filter { viewModel.checkedState[it.id] == true }

    // Continue stays disabled until at least one service is ticked
    // and every ticked service has a price above zero.
    val isFormValid = selectedServices.isNotEmpty() &&
            selectedServices.all { (viewModel.amountState[it.id]?.toIntOrNull() ?: 0) > 0 }

    FormScaffold(
        title = "Services & Pricing",
        step = 4,
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
                        val servicePrices = selectedServices.map {
                            ServicePriceItem(
                                service = it.id,
                                price_per_hour = (viewModel.amountState[it.id] ?: "").trim()
                            )
                        }
                        viewModel.submit(servicePrices, onSuccess = onContinue)
                    },
                    enabled = !viewModel.isLoading && isFormValid
                )
            }
        }
    ) {
        FieldLabel("Select services you provide", helper = "You can select multiple services")

        when {
            viewModel.isLoadingServices -> {
                Text("Loading services...", fontSize = 13.sp)
            }
            viewModel.services.isEmpty() -> {
                Text("Couldn't load services.", fontSize = 13.sp, color = SosRed)
                PrimaryButton(text = "Retry", onClick = { viewModel.loadServices() })
            }
            else -> {
                viewModel.services.forEach { service ->
                    ServicePriceRow(
                        icon = iconForService(service.slug, service.name),
                        title = service.name,
                        checked = viewModel.checkedState[service.id] ?: false,
                        onCheckedChange = { viewModel.checkedState[service.id] = it },
                        amount = viewModel.amountState[service.id] ?: "",
                        onAmountChange = { viewModel.amountState[service.id] = it }
                    )
                }
            }
        }
    }
}