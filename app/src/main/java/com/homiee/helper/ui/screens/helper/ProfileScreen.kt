package com.homiee.helper.ui.screens.helper

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homiee.helper.data.model.HelperProfileResponse
import com.homiee.helper.data.remote.RetrofitClient
import androidx.compose.ui.layout.ContentScale
import coil.compose.SubcomposeAsyncImage
import com.homiee.helper.ui.components.*
import com.homiee.helper.ui.theme.*
import com.homiee.helper.viewmodel.AccountActionUiState
import com.homiee.helper.viewmodel.AccountActionViewModel
import androidx.compose.ui.text.input.VisualTransformation
import com.homiee.helper.viewmodel.AccountAction
import com.homiee.helper.viewmodel.ProfileViewModel
import java.text.SimpleDateFormat
import java.util.Locale

// ── UI model built from the GET profile response ────────────────────────────

private data class ServiceCharge(val label: String, val price: String)

private data class ProfileUi(
    val name: String,
    val initials: String,
    val photoUrl: String?,
    val about: String,
    val dob: String,
    val address: String,
    val services: List<ServiceCharge>,
    val languages: List<String>,
    val experience: String,
    val workingDays: List<String>,
    val workingSlot: String,
    val totalEarnings: String
)

private val allDays = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

/** "1990-05-12" -> "12 May 1990". Falls back to the raw value if it can't be parsed. */
private fun formatDob(raw: String?): String {
    if (raw.isNullOrBlank()) return "—"
    return try {
        val parsed = SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(raw)
        SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH).format(parsed!!)
    } catch (e: Exception) {
        raw
    }
}

/** "09:00:00" -> "09:00 AM". Falls back to the raw value if it can't be parsed. */
private fun formatTime(raw: String?): String {
    if (raw.isNullOrBlank()) return ""
    return try {
        val parsed = SimpleDateFormat("HH:mm:ss", Locale.US).parse(raw)
        SimpleDateFormat("hh:mm a", Locale.US).format(parsed!!)
    } catch (e: Exception) {
        raw
    }
}

/** "200.00" -> "200". */
private fun formatPrice(raw: String?): String =
    raw?.toBigDecimalOrNull()?.stripTrailingZeros()?.toPlainString() ?: raw.orEmpty()

/** Backend stores the lower bound of each bucket used on the experience form. */
private fun experienceLabel(years: Int?): String = when {
    years == null -> "—"
    years < 1 -> "Less than 1 year"
    years < 3 -> "1-2 years"
    years < 5 -> "3-5 years"
    else -> "5+ years"
}

private fun HelperProfileResponse.toUi(): ProfileUi {
    val fullName = full_name.orEmpty().trim()
    val initials = fullName.split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .joinToString("") { it.first().uppercase() }
        .ifBlank { "?" }

    val addressLine = listOf(house_no, city, state)
        .filter { !it.isNullOrBlank() }
        .joinToString(", ")
    val address = when {
        addressLine.isBlank() && pincode.isNullOrBlank() -> "—"
        pincode.isNullOrBlank() -> addressLine
        addressLine.isBlank() -> pincode
        else -> "$addressLine - $pincode"
    }

    val start = formatTime(start_time)
    val end = formatTime(end_time)

    return ProfileUi(
        name = fullName.ifBlank { "Helper" },
        initials = initials,
        photoUrl = RetrofitClient.absoluteUrl(profile_photo),
        about = about?.takeIf { it.isNotBlank() } ?: "—",
        dob = formatDob(date_of_birth),
        address = address,
        services = service_prices.orEmpty().mapNotNull { item ->
            val name = item.service?.name?.takeIf { it.isNotBlank() } ?: return@mapNotNull null
            ServiceCharge(name, "₹ ${formatPrice(item.price_per_hour)} / Hour")
        },
        languages = languages_spoken.orEmpty().map { it.name }.filter { it.isNotBlank() },
        experience = experienceLabel(years_of_experience),
        workingDays = working_days.orEmpty().map { it.lowercase() },
        workingSlot = if (start.isNotBlank() && end.isNotBlank()) "$start  -  $end" else "—",
        // TODO: not part of the profile response yet — comes from the earnings endpoint.
        totalEarnings = "₹ 24,750"
    )
}

/** Consistent 1dp shadow used across Home / Job Requests / My Jobs — applied here too. */
private val cardElevation = Modifier.shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp), clip = false)

@Composable
fun ProfileScreen(
    profileViewModel: ProfileViewModel,
    email: String? = null,
    onViewVerifiedDocuments: () -> Unit,
    onViewTotalEarnings: () -> Unit,
    accountViewModel: AccountActionViewModel? = null,
    onAccountCleared: () -> Unit,
    onContactSupport: () -> Unit,
    onEditProfilePhoto: () -> Unit = {},
    currentRoute: String? = null,
    onNavItemClick: (HelperNavItem) -> Unit = {}
) {
    DashboardSystemBars(darkStatusBarIcons = false)
    var settingsOpen by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = BackgroundWhite,
            bottomBar = {
                HelperBottomNavBar(
                    currentRoute = currentRoute,
                    badgeCounts = mapOf(HelperNavItem.JobRequests to HelperSampleData.newRequests.size),
                    onItemClick = onNavItemClick
                )
            }
        ) { innerPadding ->
            Column(modifier = Modifier.fillMaxSize().padding(bottom = innerPadding.calculateBottomPadding())) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(TealPrimary, TealPrimaryDark)))
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(horizontal = 20.dp, vertical = 18.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Profile", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        IconButton(onClick = { settingsOpen = true }) {
                            Icon(Icons.Filled.Settings, contentDescription = "Settings", tint = Color.White)
                        }
                    }
                }

                val profile = profileViewModel.profile
                when {
                    profile != null -> {
                        val ui = remember(profile) { profile.toUi() }
                        ProfileContent(
                            ui = ui,
                            email = email,
                            onViewVerifiedDocuments = onViewVerifiedDocuments,
                            onViewTotalEarnings = onViewTotalEarnings,
                            onEditProfilePhoto = onEditProfilePhoto
                        )
                    }
                    profileViewModel.isLoading -> {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = TealPrimary)
                        }
                    }
                    else -> {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = profileViewModel.errorMessage ?: "Couldn't load your profile.",
                                fontSize = 13.sp,
                                color = SosRed,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            PrimaryButton(text = "Retry", onClick = { profileViewModel.loadProfile() })
                        }
                    }
                }
            }
        }

        SettingsPanel(
            visible = settingsOpen,
            onDismiss = { settingsOpen = false },
            accountViewModel = accountViewModel,
            onAccountCleared = onAccountCleared,
            onContactSupport = onContactSupport
        )
    }
}

@Composable
private fun ProfileContent(
    ui: ProfileUi,
    email: String?,
    onViewVerifiedDocuments: () -> Unit,
    onViewTotalEarnings: () -> Unit,
    onEditProfilePhoto: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Identity card — avatar has a small edit/camera badge on it.
        // Actually swapping the photo needs an image picker + upload flow,
        // so onEditProfilePhoto is just wired as a stub for now.
        // TODO: hook onEditProfilePhoto up to an image picker + upload to backend.
        SectionCard(modifier = cardElevation) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box {
                    ProfileAvatar(photoUrl = ui.photoUrl, initials = ui.initials, size = 64.dp)
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .border(1.dp, BorderGray, CircleShape)
                            .clickable { onEditProfilePhoto() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.CameraAlt,
                            contentDescription = "Edit profile photo",
                            tint = TealPrimary,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(ui.name, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    if (!email.isNullOrBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(email, fontSize = 12.sp, color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    StatusChip(text = "Verified", background = SuccessGreenBg, textColor = SuccessGreen)
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        SectionCard(modifier = cardElevation) {
            SectionHeader(icon = Icons.Filled.Person, title = "About Me")
            Spacer(modifier = Modifier.height(6.dp))
            Text(ui.about, fontSize = 12.sp, color = TextSecondary, lineHeight = 17.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))
        SectionCard(modifier = cardElevation) {
            SectionHeader(icon = Icons.Filled.Badge, title = "Personal Information")
            Spacer(modifier = Modifier.height(10.dp))
            LabeledDetailRow(label = "Date of Birth", value = ui.dob)
            Spacer(modifier = Modifier.height(10.dp))
            // Address gets its own row: label stays put on the left, the value
            // sits in a fixed column to the right and grows downward (wraps
            // onto as many lines as it needs) instead of squeezing sideways
            // into — or overlapping — the "Address" label.
            LabeledDetailRow(label = "Address", value = ui.address)
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(TealPale)
                    .clickable { onViewVerifiedDocuments() }
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text("View Verified Documents", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Text("Aadhaar, PAN, police verification & more", fontSize = 11.sp, color = TextSecondary)
                }
                Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TealPrimary)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        SectionCard(modifier = cardElevation) {
            SectionHeader(icon = Icons.Filled.Payments, title = "Services & Charges")
            Spacer(modifier = Modifier.height(6.dp))
            if (ui.services.isEmpty()) {
                Text("No services added", fontSize = 12.sp, color = TextSecondary)
            } else {
                ui.services.forEach { InfoRow(it.label, it.price) }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        SectionCard(modifier = cardElevation) {
            SectionHeader(icon = Icons.Filled.Translate, title = "Languages Spoken")
            Spacer(modifier = Modifier.height(10.dp))
            if (ui.languages.isEmpty()) {
                Text("—", fontSize = 12.sp, color = TextSecondary)
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ui.languages.forEach { lang ->
                        Box(
                            modifier = Modifier.clip(RoundedCornerShape(50)).background(TealPale).padding(horizontal = 12.dp, vertical = 6.dp)
                        ) { Text(lang, fontSize = 12.sp, color = TealPrimaryDark, fontWeight = FontWeight.Medium) }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        SectionCard(modifier = cardElevation) {
            SectionHeader(icon = Icons.Filled.WorkHistory, title = "Experience")
            Spacer(modifier = Modifier.height(6.dp))
            Text(ui.experience, fontSize = 13.sp, color = TextSecondary)
        }

        Spacer(modifier = Modifier.height(14.dp))
        SectionCard(modifier = cardElevation) {
            SectionHeader(icon = Icons.Filled.CalendarMonth, title = "Working Days")
            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                allDays.forEach { day ->
                    val active = day.lowercase() in ui.workingDays
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (active) TealPrimary else TealPale)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(day, fontSize = 11.sp, color = if (active) Color.White else TealPrimaryDark, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
            Text("Working Slot", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(ui.workingSlot, fontSize = 13.sp, color = TextSecondary)
        }

        Spacer(modifier = Modifier.height(14.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(cardElevation)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .clickable { onViewTotalEarnings() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(40.dp).clip(CircleShape).background(InfoCardBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Filled.AccountBalanceWallet, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text("Total Earnings", fontSize = 12.sp, color = TextSecondary)
                Text(ui.totalEarnings, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            }
            Text("View Details", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TealPrimary)
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TealPrimary)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

/**
 * Circular profile photo loaded from the backend. While loading, or if there's
 * no photo / the download fails, it falls back to the initials avatar.
 */
@Composable
private fun ProfileAvatar(photoUrl: String?, initials: String, size: androidx.compose.ui.unit.Dp) {
    if (photoUrl.isNullOrBlank()) {
        InitialsAvatar(initials = initials, size = size)
        return
    }
    SubcomposeAsyncImage(
        model = photoUrl,
        contentDescription = "Profile photo",
        contentScale = ContentScale.Crop,
        modifier = Modifier.size(size).clip(CircleShape),
        loading = { InitialsAvatar(initials = initials, size = size) },
        error = { InitialsAvatar(initials = initials, size = size) }
    )
}

/** Small icon + bold title used at the top of every profile section card. */
@Composable
private fun SectionHeader(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(title, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}

/**
 * Label on the left in a fixed-width column, value on the right in its own
 * column. The value wraps onto multiple lines and grows vertically as needed
 * instead of ever touching or overlapping the label — this is what keeps a
 * long address contained and readable regardless of its length.
 */
@Composable
private fun LabeledDetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = TextSecondary,
            modifier = Modifier.width(96.dp)
        )
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = TextPrimary,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SettingsPanel(
    visible: Boolean,
    onDismiss: () -> Unit,
    accountViewModel: AccountActionViewModel?,
    onAccountCleared: () -> Unit,
    onContactSupport: () -> Unit
) {
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showDeactivateDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val uiState by accountViewModel?.uiState?.collectAsState()
        ?: remember { mutableStateOf(AccountActionUiState()) }

    LaunchedEffect(uiState.actionCompleted) {
        if (uiState.actionCompleted) {
            onAccountCleared()
            accountViewModel?.resetState()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedVisibility(
            visible = visible,
            enter = fadeIn(tween(150)),
            exit = fadeOut(tween(150))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onDismiss() }
            )
        }

        AnimatedVisibility(
            visible = visible,
            enter = slideInHorizontally(animationSpec = tween(220)) { it },
            exit = slideOutHorizontally(animationSpec = tween(200)) { it },
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(300.dp)
                    .background(Color.White)
                    .windowInsetsPadding(WindowInsets.systemBars)
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Settings", fontSize = 19.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = TextPrimary)
                    }
                }
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(TealPale)
                        .padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier.size(48.dp).clip(CircleShape).background(TealPrimary),
                            contentAlignment = Alignment.Center
                        ) { Icon(Icons.Filled.Settings, contentDescription = null, tint = Color.White) }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text("Manage your account", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                        Text("and app preferences", fontSize = 13.sp, color = TextSecondary)
                    }
                }

                if (uiState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(uiState.errorMessage ?: "", color = SosRed, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(20.dp))
                SettingsRow(
                    icon = Icons.AutoMirrored.Filled.Logout,
                    label = if (uiState.activeAction == AccountAction.LOGOUT) "Please wait..." else "Logout",
                    subtitle = "Sign out from your account",
                    tint = TealPrimary,
                    onClick = { if (!uiState.isLoading) showLogoutDialog = true }
                )
                SettingsRow(
                    icon = Icons.Filled.PauseCircle,
                    label = if (uiState.activeAction == AccountAction.DEACTIVATE) "Please wait..." else "Deactivate Account",
                    subtitle = "Temporarily deactivate your account",
                    tint = WarningAmber,
                    onClick = { if (!uiState.isLoading) showDeactivateDialog = true }
                )
                SettingsRow(
                    icon = Icons.Filled.DeleteForever,
                    label = if (uiState.activeAction == AccountAction.DELETE) "Please wait..." else "Delete Account",
                    subtitle = "Permanently delete your account and all data",
                    tint = SosRed,
                    onClick = { if (!uiState.isLoading) showDeleteDialog = true }
                )

                Spacer(modifier = Modifier.height(22.dp))
                Text("Our Commitment", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(10.dp))
                CommitmentRow(text = "Your data is safe with us.")
                CommitmentRow(text = "We respect your privacy.")

                Spacer(modifier = Modifier.height(22.dp))
                Text("Need Help?", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(10.dp))
                SettingsRow(icon = Icons.AutoMirrored.Filled.HelpOutline, label = "Contact Support", subtitle = "We're here to help you", tint = TealPrimary, onClick = onContactSupport)

                Spacer(modifier = Modifier.height(22.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("App Version", fontSize = 12.sp, color = TextSecondary)
                    Text("v1.3.0", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }

    // ---- Logout confirmation ----
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?", fontSize = 13.sp, color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    showLogoutDialog = false
                    accountViewModel?.logout()
                }) { Text("Logout", color = TealPrimary) }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
            }
        )
    }

    // ---- Deactivate: requires password ----
    if (showDeactivateDialog) {
        var password by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = { showDeactivateDialog = false },
            title = { Text("Deactivate Account") },
            text = {
                Column {
                    Text("Enter your password to confirm. You'll be logged out on all devices.", fontSize = 13.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = TextSecondary
                                )
                            }
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = password.isNotBlank(),
                    onClick = {
                        showDeactivateDialog = false
                        accountViewModel?.deactivateAccount(password)
                    }
                ) { Text("Deactivate", color = WarningAmber) }
            },
            dismissButton = {
                TextButton(onClick = { showDeactivateDialog = false }) { Text("Cancel") }
            }
        )
    }

    // ---- Delete: requires password ----
    if (showDeleteDialog) {
        var password by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Account") },
            text = {
                Column {
                    Text("This permanently deletes your account and all data. This cannot be undone.", fontSize = 13.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = TextSecondary
                                )
                            }
                        }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = password.isNotBlank(),
                    onClick = {
                        showDeleteDialog = false
                        accountViewModel?.deleteAccount(password)
                    }
                ) { Text("Delete Forever", color = SosRed) }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }
}

@Composable
private fun SettingsRow(icon: ImageVector, label: String, subtitle: String, tint: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = tint)
            Text(subtitle, fontSize = 11.sp, color = TextSecondary)
        }
    }
}

@Composable
private fun CommitmentRow(text: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 12.sp, color = TextSecondary)
    }
}