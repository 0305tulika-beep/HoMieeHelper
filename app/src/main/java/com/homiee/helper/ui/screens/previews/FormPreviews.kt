package com.homiee.helper.ui.screens.previews

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.homiee.helper.data.repository.UserDetailsRepository
import com.homiee.helper.ui.screens.forms.AddressInformationScreen
import com.homiee.helper.ui.screens.forms.AvailabilityScreen
import com.homiee.helper.ui.screens.forms.DocumentsScreen
import com.homiee.helper.ui.screens.forms.ExperienceAboutScreen
import com.homiee.helper.ui.screens.forms.PersonalInformationScreen
import com.homiee.helper.ui.screens.forms.ServicesPricingScreen
import com.homiee.helper.ui.theme.HomieeHelperTheme
import com.homiee.helper.viewmodel.AddressInformationViewModel
import com.homiee.helper.viewmodel.AvailabilityViewModel
import com.homiee.helper.viewmodel.DocumentsViewModel
import com.homiee.helper.viewmodel.ExperienceAboutViewModel
import com.homiee.helper.viewmodel.PersonalInformationViewModel
import com.homiee.helper.viewmodel.ServicesPricingViewModel

@Preview(name = "Personal Information", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun PersonalInformationScreenPreview() {
    HomieeHelperTheme {
        val context = LocalContext.current
        PersonalInformationScreen(
            viewModel = PersonalInformationViewModel(UserDetailsRepository(context)),
            onBack = {},
            onContinue = {}
        )
    }
}

@Preview(name = "Address Information", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun AddressInformationScreenPreview() {
    HomieeHelperTheme {
        val context = LocalContext.current
        AddressInformationScreen(
            viewModel = AddressInformationViewModel(UserDetailsRepository(context)),
            onBack = {},
            onContinue = {}
        )
    }
}

@Preview(name = "Documents", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun DocumentsScreenPreview() {
    HomieeHelperTheme {
        val context = LocalContext.current
        DocumentsScreen(
            viewModel = DocumentsViewModel(UserDetailsRepository(context)),
            onBack = {},
            onContinue = {}
        )
    }
}

@Preview(name = "Services & Pricing", showBackground = true, widthDp = 360, heightDp = 900)
@Composable
fun ServicesPricingScreenPreview() {
    HomieeHelperTheme {
        val context = LocalContext.current
        ServicesPricingScreen(
            viewModel = ServicesPricingViewModel(UserDetailsRepository(context)),
            onBack = {},
            onContinue = {}
        )
    }
}

@Preview(name = "Experience & About You", showBackground = true, widthDp = 360, heightDp = 900)
@Composable
fun ExperienceAboutScreenPreview() {
    HomieeHelperTheme {
        val context = LocalContext.current
        ExperienceAboutScreen(
            viewModel = ExperienceAboutViewModel(UserDetailsRepository(context)),
            onBack = {},
            onContinue = {}
        )
    }
}

@Preview(name = "Availability", showBackground = true, widthDp = 360, heightDp = 800)
@Composable
fun AvailabilityScreenPreview() {
    HomieeHelperTheme {
        val context = LocalContext.current
        AvailabilityScreen(
            viewModel = AvailabilityViewModel(UserDetailsRepository(context)),
            onBack = {},
            onContinue = {}
        )
    }
}