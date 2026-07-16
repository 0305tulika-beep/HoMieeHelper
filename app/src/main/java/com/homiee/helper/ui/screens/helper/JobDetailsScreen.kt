package com.homiee.helper.ui.screens.helper

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homiee.helper.ui.components.*
import com.homiee.helper.ui.theme.*

/** Same subtle 1dp shadow used on Home / Job Requests / My Jobs / Profile / Request Details cards. */
private val cardElevation = Modifier.shadow(elevation = 1.dp, shape = RoundedCornerShape(16.dp), clip = false)

@Composable
fun JobDetailsScreen(
    jobId: String,
    onBackClick: () -> Unit,
    onChatWithResident: (String) -> Unit,
    onCallResident: (String) -> Unit
) {
    DashboardSystemBars(darkStatusBarIcons = true)
    val booking = HelperSampleData.bookingById(jobId)

    Scaffold(containerColor = BackgroundWhite) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            DetailTopBar(
                title = "Job Details",
                onBackClick = onBackClick,
                trailing = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.Share, contentDescription = "Share", tint = TextPrimary)
                    }
                }
            )

            if (booking == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Job not found", color = TextSecondary)
                }
                return@Column
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                Text("Booking Details", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(10.dp))
                SectionCard(modifier = cardElevation) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        ServiceIconBadge(icon = booking.service.icon, size = 48.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(booking.service.label, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = TextPrimary, modifier = Modifier.weight(1f))
                        StatusChip(
                            text = when (booking.status) {
                                BookingStatus.UPCOMING -> "Confirmed"
                                BookingStatus.ACTIVE -> "In Progress"
                                BookingStatus.COMPLETED -> "Completed"
                            },
                            background = if (booking.status == BookingStatus.COMPLETED) SuccessGreenBg else InfoCardBg,
                            textColor = if (booking.status == BookingStatus.COMPLETED) SuccessGreen else TealPrimary
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    InfoRow("Date", booking.date)
                    InfoRow("Time", booking.time)
                    InfoRow("Duration", booking.duration)
                    InfoRow("Salary", booking.salary, valueColor = TealPrimary)
                    if (booking.status == BookingStatus.COMPLETED) {
                        InfoRow("Status", "Completed", valueColor = SuccessGreen)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Resident Information", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(10.dp))
                SectionCard(modifier = cardElevation) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        InitialsAvatar(initials = booking.residentInitials, size = 48.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(booking.residentName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            if (booking.email.isNotBlank()) Text(booking.email, fontSize = 12.sp, color = TextSecondary)
                            if (booking.phone.isNotBlank()) Text(booking.phone, fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                    if (booking.fullAddress.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(booking.fullAddress, fontSize = 12.sp, color = TextSecondary, lineHeight = 17.sp)
                    }
                }

                if (booking.instructions.isNotBlank()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Special Instructions", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(10.dp))
                    InfoCard(text = "\u201C${booking.instructions}\u201D", icon = Icons.Filled.Chat)
                }

                if (booking.status == BookingStatus.COMPLETED) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Payment Information", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(10.dp))
                    SectionCard(modifier = cardElevation) {
                        InfoRow("Total Amount", booking.salary)
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Paid To You", fontSize = 13.sp, color = TextSecondary)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(booking.salary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                StatusChip(text = "Paid", background = SuccessGreenBg, textColor = SuccessGreen)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))
                OutlinedButton(
                    onClick = { onChatWithResident(booking.id) },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TealPrimary),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TealPrimary)
                ) {
                    Icon(Icons.Filled.Chat, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Chat with Resident", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}