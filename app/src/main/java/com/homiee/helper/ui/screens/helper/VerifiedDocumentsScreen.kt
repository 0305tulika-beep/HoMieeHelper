package com.homiee.helper.ui.screens.helper

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homiee.helper.ui.components.*
import com.homiee.helper.ui.theme.*

private data class VerifiedDoc(val label: String, val verifiedOn: String, val icon: ImageVector)

@Composable
fun VerifiedDocumentsScreen(onBackClick: () -> Unit, onOpenDocument: (String) -> Unit = {}) {
    DashboardSystemBars(darkStatusBarIcons = true)

    val docs = rememberVerifiedDocs()

    Scaffold(containerColor = BackgroundWhite) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 20.dp)
        ) {
            DetailTopBar(title = "Verified Documents", onBackClick = onBackClick)
            Spacer(modifier = Modifier.height(8.dp))

            Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
                docs.forEach { doc ->
                    DocumentRow(doc = doc, onClick = { onOpenDocument(doc.label) })
                    Spacer(modifier = Modifier.height(12.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))
                InfoCard(
                    text = "All documents are verified. Your profile is 100% verified and trusted.",
                    icon = Icons.Filled.Shield,
                    tint = SuccessGreen,
                    background = SuccessGreenBg
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun rememberVerifiedDocs(): List<VerifiedDoc> = androidx.compose.runtime.remember {
    listOf(
        VerifiedDoc("Government ID (Aadhaar Card)", "Verified on 10 May 2025", Icons.Filled.Badge),
        VerifiedDoc("PAN Card", "Verified on 10 May 2025", Icons.Filled.CreditCard),
        VerifiedDoc("Police Verification Certificate", "Verified on 12 May 2025", Icons.Filled.Description),
        VerifiedDoc("Address Proof", "Verified on 12 May 2025", Icons.Filled.Home),
        VerifiedDoc("Profile Photo", "Verified on 10 May 2025", Icons.Filled.Person)
    )
}

@Composable
private fun DocumentRow(doc: VerifiedDoc, onClick: () -> Unit) {
    SectionCard(modifier = Modifier.clickable { onClick() }) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            ServiceIconBadge(icon = doc.icon, size = 44.dp, background = InfoCardBg, tint = TealPrimary)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(doc.label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Verified, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(doc.verifiedOn, fontSize = 11.sp, color = SuccessGreen)
                }
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = HintGray)
        }
    }
}