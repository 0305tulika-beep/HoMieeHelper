// ServicesPricingScreen.kt
package com.homiee.helper.ui.screens.forms

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homiee.helper.data.model.ServicePriceItem
import com.homiee.helper.ui.components.*
import com.homiee.helper.ui.theme.SosRed
import com.homiee.helper.viewmodel.ServicesPricingViewModel

// Icons are matched by the backend's slug; unknown slugs get a generic icon.
private fun iconForSlug(slug: String?, name: String): ImageVector =
    when ((slug ?: name).lowercase()) {
        "cleaning" -> Icons.Filled.CleaningServices
        "cooking" -> Icons.Filled.Restaurant
        "babysitting" -> Icons.Filled.ChildCare
        "eldercare" -> Icons.Filled.Elderly
        else -> Icons.Filled.Build
    }

@Composable
fun ServicesPricingScreen(
    viewModel: ServicesPricingViewModel,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    // Keyed by the real backend service id.
    val checkedState = remember { mutableStateMapOf<Int, Boolean>() }
    val amountState = remember { mutableStateMapOf<Int, String>() }

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
                        val servicePrices = viewModel.services
                            .filter { checkedState[it.id] == true }
                            .map {
                                ServicePriceItem(
                                    service = it.id,
                                    price_per_hour = (amountState[it.id] ?: "").trim()
                                )
                            }
                        viewModel.submit(servicePrices, onSuccess = onContinue)
                    },
                    enabled = !viewModel.isLoading && viewModel.services.isNotEmpty()
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
                    ServiceItem(
                        icon = iconForSlug(service.slug, service.name),
                        title = service.name,
                        checked = checkedState[service.id] ?: false,
                        onCheckedChange = { checkedState[service.id] = it },
                        amount = amountState[service.id] ?: "",
                        onAmountChange = { amountState[service.id] = it }
                    )
                }
            }
        }
    }
}