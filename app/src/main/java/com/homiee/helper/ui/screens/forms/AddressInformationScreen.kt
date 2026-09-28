// AddressInformationScreen.kt
package com.homiee.helper.ui.screens.forms

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.homiee.helper.ui.components.*
import com.homiee.helper.ui.theme.BorderGray
import com.homiee.helper.ui.theme.HintGray
import com.homiee.helper.ui.theme.SosRed
import com.homiee.helper.ui.theme.TealPrimary
import com.homiee.helper.viewmodel.AddressInformationViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

// All 28 states + 8 union territories of India, alphabetical.
private val indianStates = listOf(
    "Andaman and Nicobar Islands",
    "Andhra Pradesh",
    "Arunachal Pradesh",
    "Assam",
    "Bihar",
    "Chandigarh",
    "Chhattisgarh",
    "Dadra and Nagar Haveli and Daman and Diu",
    "Delhi",
    "Goa",
    "Gujarat",
    "Haryana",
    "Himachal Pradesh",
    "Jammu and Kashmir",
    "Jharkhand",
    "Karnataka",
    "Kerala",
    "Ladakh",
    "Lakshadweep",
    "Madhya Pradesh",
    "Maharashtra",
    "Manipur",
    "Meghalaya",
    "Mizoram",
    "Nagaland",
    "Odisha",
    "Puducherry",
    "Punjab",
    "Rajasthan",
    "Sikkim",
    "Tamil Nadu",
    "Telangana",
    "Tripura",
    "Uttar Pradesh",
    "Uttarakhand",
    "West Bengal"
)

/** Wraps the FusedLocationProviderClient callback API in a suspend function. Returns null
 *  if permission isn't granted or the location couldn't be resolved. */
private suspend fun fetchCurrentLocation(context: android.content.Context): Location? {
    val hasFinePermission = ContextCompat.checkSelfPermission(
        context, Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    if (!hasFinePermission) return null

    val client = LocationServices.getFusedLocationProviderClient(context)
    val cts = CancellationTokenSource()
    return suspendCancellableCoroutine { cont ->
        client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, cts.token)
            .addOnSuccessListener { location -> cont.resume(location) }
            .addOnFailureListener { cont.resume(null) }
        cont.invokeOnCancellation { cts.cancel() }
    }
}

/** Pincode field: number keyboard, digits only, max 6 digits. */
@Composable
private fun PincodeField(value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = { input -> onValueChange(input.filter { it.isDigit() }.take(6)) },
        singleLine = true,
        placeholder = { Text("Enter pincode", color = HintGray, fontSize = 14.sp) },
        leadingIcon = { Icon(Icons.Filled.Place, contentDescription = null, tint = HintGray) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = TealPrimary,
            unfocusedBorderColor = BorderGray
        )
    )
}

@Composable
fun AddressInformationScreen(
    viewModel: AddressInformationViewModel,
    onBack: () -> Unit,
    onContinue: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var street by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }
    var state by remember { mutableStateOf("") }
    var pincode by remember { mutableStateOf("") }
    var latitude by remember { mutableStateOf("") }
    var longitude by remember { mutableStateOf("") }

    var isFetchingLocation by remember { mutableStateOf(false) }
    var locationError by remember { mutableStateOf<String?>(null) }

    fun fetchLocation() {
        scope.launch {
            isFetchingLocation = true
            locationError = null
            val location = fetchCurrentLocation(context)
            isFetchingLocation = false
            if (location != null) {
                latitude = "%.6f".format(location.latitude)
                longitude = "%.6f".format(location.longitude)
            } else {
                locationError = "Couldn't get your location. Make sure location is turned on and permission is granted."
            }
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) fetchLocation()
        else locationError = "Location permission is required to auto-fill coordinates."
    }

    fun onUseCurrentLocationClick() {
        val hasPermission = ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        if (hasPermission) fetchLocation()
        else locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    FormScaffold(
        title = "Address Information",
        step = 2,
        totalSteps = 6,
        onBack = onBack,
        // No Skip - the address is required backend data, so there's nothing to skip to.
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
                        viewModel.submit(
                            houseNo = street,
                            state = state,
                            city = city,
                            pincode = pincode,
                            latitude = latitude,
                            longitude = longitude,
                            onSuccess = onContinue
                        )
                    },
                    enabled = !viewModel.isLoading && latitude.isNotBlank() && longitude.isNotBlank()
                )
            }
        }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            FieldLabel("House / Street")
            HomieeTextField(street, { street = it }, "Enter house no. / street", leadingIcon = Icons.Filled.Home)
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                FieldLabel("City")
                HomieeTextField(city, { city = it }, "Enter city", leadingIcon = Icons.Filled.LocationCity)
            }
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                FieldLabel("State")
                DropdownField(
                    placeholder = "Select state",
                    icon = Icons.Filled.Map,
                    options = indianStates,
                    selected = state,
                    onSelect = { state = it }
                )
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            FieldLabel("Pincode")
            PincodeField(value = pincode, onValueChange = { pincode = it })
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FieldLabel("Location")

            if (latitude.isNotBlank() && longitude.isNotBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.MyLocation, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Lat: $latitude, Long: $longitude", fontSize = 14.sp)
                }
            }

            OutlinedButton(
                onClick = { onUseCurrentLocationClick() },
                enabled = !isFetchingLocation,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isFetchingLocation) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Getting location...")
                } else {
                    Icon(Icons.Filled.MyLocation, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(if (latitude.isBlank()) "Use Current Location" else "Refresh Location")
                }
            }

            if (locationError != null) {
                Text(
                    text = locationError.orEmpty(),
                    fontSize = 12.sp,
                    color = SosRed,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}