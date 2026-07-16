package com.homiee.helper.ui.screens.helper

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homiee.helper.ui.components.*
import com.homiee.helper.ui.theme.*
import com.homiee.helper.viewmodel.AccountActionUiState
import com.homiee.helper.viewmodel.AccountActionViewModel
import com.homiee.helper.viewmodel.AccountAction
import androidx.compose.ui.text.input.VisualTransformation

private data class HelperProfile(
    val name: String = "Sunita Agarwal",
    val initials: String = "SA",
    val helperId: String = "HLP87654",
    val memberSince: String = "May 2025",
    val about: String = "I am a dedicated and trustworthy helper with 3+ years of experience in providing excellent household support. I take pride in my work and always try to keep my surroundings clean and organized.",
    val dob: String = "12 Aug 1992",
    val address: String = "A-1204, ATS Advantage, Indirapuram, Ghaziabad, Uttar Pradesh - 201014",
    val languages: List<String> = listOf("Hindi", "English", "Punjabi"),
    val experience: String = "3 Years",
    val workingDays: List<String> = listOf("Mon", "Tue", "Wed", "Thu", "Fri"),
    val allDays: List<String> = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"),
    val workingSlot: String = "09:00 AM  -  06:00 PM",
    val totalEarnings: String = "₹ 24,750"
)

private data class ServiceCharge(val label: String, val price: String)

@Composable
fun ProfileScreen(
    onViewVerifiedDocuments: () -> Unit,
    onViewTotalEarnings: () -> Unit,
    accountViewModel: AccountActionViewModel? = null,
    onAccountCleared: () -> Unit,
    onContactSupport: () -> Unit,
    currentRoute: String? = null,
    onNavItemClick: (HelperNavItem) -> Unit = {}
) {
    DashboardSystemBars(darkStatusBarIcons = false)
    val profile = remember { HelperProfile() }
    val services = remember {
        listOf(
            ServiceCharge("Cleaning", "₹ 500 / Visit"),
            ServiceCharge("Cooking", "₹ 600 / Visit"),
            ServiceCharge("Babysitting", "₹ 550 / Hour"),
            ServiceCharge("Eldercare", "₹ 600 / Hour")
        )
    }
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

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp)
                ) {
                    Spacer(modifier = Modifier.height(16.dp))

                    // Identity card
                    SectionCard {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            InitialsAvatar(initials = profile.initials, size = 64.dp)
                            Spacer(modifier = Modifier.width(14.dp))
                            Column {
                                Text(profile.name, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Spacer(modifier = Modifier.height(4.dp))
                                StatusChip(text = "Verified", background = SuccessGreenBg, textColor = SuccessGreen)
                                Spacer(modifier = Modifier.height(4.dp))
                                Text("Helper ID: ${profile.helperId}", fontSize = 11.sp, color = TextSecondary)
                                Text("Member since ${profile.memberSince}", fontSize = 11.sp, color = TextSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    SectionCard {
                        Text("About Me", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(profile.about, fontSize = 12.sp, color = TextSecondary, lineHeight = 17.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    SectionCard {
                        Text("Personal Information", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        InfoRow("Date of Birth", profile.dob)
                        InfoRow("Address", profile.address)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            "View Verified Documents",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TealPrimary,
                            modifier = Modifier.clickable { onViewVerifiedDocuments() }
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    SectionCard {
                        Text("Services & Charges", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        services.forEach { InfoRow(it.label, it.price) }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    SectionCard {
                        Text("Languages Spoken", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            profile.languages.forEach { lang ->
                                Box(
                                    modifier = Modifier.clip(RoundedCornerShape(50)).background(TealPale).padding(horizontal = 12.dp, vertical = 6.dp)
                                ) { Text(lang, fontSize = 12.sp, color = TealPrimaryDark, fontWeight = FontWeight.Medium) }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    SectionCard {
                        Text("Experience", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(profile.experience, fontSize = 13.sp, color = TextSecondary)
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    SectionCard {
                        Text("Working Days", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            profile.allDays.forEach { day ->
                                val active = day in profile.workingDays
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
                        Text(profile.workingSlot, fontSize = 13.sp, color = TextSecondary)
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
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
                            Text(profile.totalEarnings, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Text("View Details", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = TealPrimary)
                        Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = TealPrimary)
                    }

                    Spacer(modifier = Modifier.height(24.dp))
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
private fun SettingsRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, subtitle: String, tint: Color, onClick: () -> Unit) {
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