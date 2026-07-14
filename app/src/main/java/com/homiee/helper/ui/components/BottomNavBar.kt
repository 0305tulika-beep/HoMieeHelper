package com.homiee.helper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homiee.helper.ui.navigation.Screen
import com.homiee.helper.ui.theme.HintGray
import com.homiee.helper.ui.theme.TealPrimary

enum class HelperNavItem(val label: String, val icon: ImageVector, val route: String) {
    Home("Home", Icons.Filled.Home, Screen.Home.route),
    JobRequests("Job Requests", Icons.Filled.Description, Screen.JobRequests.route),
    MyJobs("My Jobs", Icons.Filled.Work, Screen.MyJobs.route),
    Messages("Messages", Icons.Filled.ChatBubbleOutline, Screen.Messages.route),
    Profile("Profile", Icons.Filled.Person, Screen.Profile.route)
}

/**
 * Bottom nav bar shared by Home, Job Requests, My Jobs, Messages and Profile.
 *
 * Pairs with [DashboardSystemBars]: this bar has a white background and pads
 * itself with `WindowInsets.navigationBars`, so it renders as an elevated
 * white strip sitting visually *above* the (also white) system navigation
 * bar rather than overlapping or blending into it.
 */
@Composable
fun HelperBottomNavBar(
    currentRoute: String?,
    badgeCounts: Map<HelperNavItem, Int> = emptyMap(),
    onItemClick: (HelperNavItem) -> Unit
) {
    Surface(
        color = androidx.compose.ui.graphics.Color.White,
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.navigationBars)
                .padding(top = 10.dp, bottom = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            HelperNavItem.entries.forEach { item ->
                val selected = currentRoute == item.route
                val tint = if (selected) TealPrimary else HintGray
                val badgeCount = badgeCounts[item] ?: 0

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onItemClick(item) }
                ) {
                    BadgedBox(
                        badge = {
                            if (badgeCount > 0) {
                                Badge(containerColor = com.homiee.helper.ui.theme.SosRed) {
                                    Text(if (badgeCount > 9) "9+" else "$badgeCount")
                                }
                            }
                        }
                    ) {
                        Icon(imageVector = item.icon, contentDescription = item.label, tint = tint, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.label,
                        fontSize = 10.sp,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        color = tint,
                        maxLines = 1
                    )
                }
            }
        }
    }
}