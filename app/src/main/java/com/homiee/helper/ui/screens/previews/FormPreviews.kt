package com.homiee.helper.ui.screens.previews

import androidx.compose.ui.tooling.preview.Preview
import com.homiee.helper.ui.screens.forms.AddressInformationScreen
import com.homiee.helper.ui.screens.forms.AvailabilityScreen
import com.homiee.helper.ui.screens.forms.DocumentsScreen
import com.homiee.helper.ui.screens.forms.ExperienceAboutScreen
import com.homiee.helper.ui.screens.forms.PersonalInformationScreen
import com.homiee.helper.ui.screens.forms.ServicesPricingScreen
import com.homiee.helper.ui.theme.HomieeHelperTheme

@Preview(name = "Personal Information", showBackground = true, widthDp = 360, heightDp = 800)
@androidx.compose.runtime.Composable
fun PersonalInformationScreenPreview() {
    HomieeHelperTheme {
        PersonalInformationScreen(onBack = {}, onContinue = {})
    }
}

@Preview(name = "Address Information", showBackground = true, widthDp = 360, heightDp = 800)
@androidx.compose.runtime.Composable
fun AddressInformationScreenPreview() {
    HomieeHelperTheme {
        AddressInformationScreen(onBack = {}, onSkip = {}, onContinue = {})
    }
}

@Preview(name = "Documents", showBackground = true, widthDp = 360, heightDp = 800)
@androidx.compose.runtime.Composable
fun DocumentsScreenPreview() {
    HomieeHelperTheme {
        DocumentsScreen(onBack = {}, onSkip = {}, onContinue = {})
    }
}

@Preview(name = "Services & Pricing", showBackground = true, widthDp = 360, heightDp = 900)
@androidx.compose.runtime.Composable
fun ServicesPricingScreenPreview() {
    HomieeHelperTheme {
        ServicesPricingScreen(onBack = {}, onContinue = {})
    }
}

@Preview(name = "Experience & About You", showBackground = true, widthDp = 360, heightDp = 900)
@androidx.compose.runtime.Composable
fun ExperienceAboutScreenPreview() {
    HomieeHelperTheme {
        ExperienceAboutScreen(onBack = {}, onSkip = {}, onContinue = {})
    }
}

@Preview(name = "Availability", showBackground = true, widthDp = 360, heightDp = 800)
@androidx.compose.runtime.Composable
fun AvailabilityScreenPreview() {
    HomieeHelperTheme {
        AvailabilityScreen(onBack = {}, onSkip = {}, onContinue = {})
    }
}