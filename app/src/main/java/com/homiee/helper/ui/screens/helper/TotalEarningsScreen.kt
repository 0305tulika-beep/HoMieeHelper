package com.homiee.helper.ui.screens.helper

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homiee.helper.ui.components.*
import com.homiee.helper.ui.theme.*

private data class Transaction(val label: String, val date: String, val amount: String)

@Composable
fun TotalEarningsScreen(onBackClick: () -> Unit, onViewAllTransactions: () -> Unit = {}) {
    DashboardSystemBars(darkStatusBarIcons = true)

    val monthLabels = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun")
    val values = listOf(4250f, 5650f, 6900f, 7800f, 6250f, 24750f)
    val transactions = rememberTransactions()

    Scaffold(containerColor = BackgroundWhite) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 20.dp)
        ) {
            DetailTopBar(title = "Total Earnings", onBackClick = onBackClick)

            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                Spacer(modifier = Modifier.height(8.dp))

                SectionCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(36.dp).clip(CircleShape).background(InfoCardBg),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.AccountBalanceWallet, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Total Earnings", fontSize = 12.sp, color = TextSecondary)
                                Text("₹ 24,750", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                            }
                        }
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(TealPale)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("This Year", fontSize = 12.sp, color = TealPrimaryDark, fontWeight = FontWeight.SemiBold)
                            Icon(Icons.Filled.ExpandMore, contentDescription = null, tint = TealPrimaryDark, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))
                    EarningsLineChart(points = values, labels = monthLabels, valueLabels = emptyList())
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text("Breakdown", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                Spacer(modifier = Modifier.height(10.dp))
                SectionCard {
                    InfoRow("Completed Jobs", "45")
                    InfoRow("This Month", "₹ 6,250")
                    InfoRow("Last Month", "₹ 7,800")
                    InfoRow("This Year", "₹ 24,750", valueColor = TealPrimary)
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Recent Transactions", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text(
                        "View All",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TealPrimary,
                        modifier = Modifier.clickable { onViewAllTransactions() }
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                SectionCard {
                    transactions.forEachIndexed { index, tx ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(tx.label, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                                Text(tx.date, fontSize = 11.sp, color = TextSecondary)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(tx.amount, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Spacer(modifier = Modifier.width(8.dp))
                                StatusChip(text = "Paid", background = SuccessGreenBg, textColor = SuccessGreen)
                            }
                        }
                        if (index != transactions.lastIndex) {
                            Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(DividerLight))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                InfoCard(
                    text = "Earnings will be credited to your registered account within 24-48 hours of job completion.",
                    icon = Icons.Filled.Shield
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun rememberTransactions(): List<Transaction> = androidx.compose.runtime.remember {
    listOf(
        Transaction("House Cleaning", "12 May 2025", "₹ 500"),
        Transaction("Cooking", "11 May 2025", "₹ 600"),
        Transaction("Babysitting", "10 May 2025", "₹ 550"),
        Transaction("Eldercare", "09 May 2025", "₹ 600")
    )
}