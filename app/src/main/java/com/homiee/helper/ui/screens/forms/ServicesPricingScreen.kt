// ServicesPricingScreen.kt
package com.homiee.helper.ui.screens.forms

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.vector.ImageVector
import com.homiee.helper.ui.components.*

private data class ServiceOption(val title: String, val icon: ImageVector)

private val services = listOf(
    ServiceOption("Cleaning", Icons.Filled.CleaningServices),
    ServiceOption("Cooking", Icons.Filled.Restaurant),
    ServiceOption("Babysitting", Icons.Filled.ChildCare),
    ServiceOption("Eldercare", Icons.Filled.Elderly)
)

@Composable
fun ServicesPricingScreen(onBack: () -> Unit, onContinue: () -> Unit) {
    val checkedState = remember { mutableStateMapOf<String, Boolean>().apply { services.forEach { put(it.title, true) } } }
    val amountState = remember { mutableStateMapOf<String, String>().apply { services.forEach { put(it.title, "") } } }

    FormScaffold(
        title = "Services & Pricing",
        step = 4,
        totalSteps = 6,
        onBack = onBack,
        footer = { PrimaryButton(text = "Continue", onClick = onContinue) }
    ) {
        FieldLabel("Select services you provide", helper = "You can select multiple services")
        services.forEach { service ->
            ServiceItem(
                icon = service.icon,
                title = service.title,
                checked = checkedState[service.title] ?: false,
                onCheckedChange = { checkedState[service.title] = it },
                amount = amountState[service.title] ?: "",
                onAmountChange = { amountState[service.title] = it }
            )
        }
    }
}