package com.homiee.helper.ui.screens.helper

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homiee.helper.data.model.HelperProfileResponse
import com.homiee.helper.data.remote.RetrofitClient
import com.homiee.helper.ui.components.*
import com.homiee.helper.ui.theme.*
import com.homiee.helper.viewmodel.ProfileViewModel

private data class DocItem(
    val label: String,
    val subtitle: String?,
    val url: String,
    val icon: ImageVector
)

/** "aadhaar" -> "Aadhaar", "pan" -> "PAN", anything else -> tidied-up words. */
private fun idTypeLabel(raw: String?): String? {
    if (raw.isNullOrBlank()) return null
    return when (raw.lowercase()) {
        "aadhaar", "aadhar" -> "Aadhaar"
        "pan" -> "PAN"
        else -> raw.replace("_", " ").split(" ")
            .joinToString(" ") { word -> word.replaceFirstChar { it.uppercase() } }
    }
}

/** Shows only the last 4 characters of the ID number: "1234 5678 9012" -> "••••••••9012". */
private fun maskId(number: String?): String? {
    val clean = number?.filter { it.isLetterOrDigit() }?.takeIf { it.isNotBlank() } ?: return null
    if (clean.length <= 4) return clean
    return "•".repeat(clean.length - 4) + clean.takeLast(4)
}

/** Only documents that actually have a file on the server are listed. */
private fun HelperProfileResponse.toDocItems(): List<DocItem> {
    val idSubtitle = listOfNotNull(idTypeLabel(govt_id_type), maskId(govt_id_number))
        .joinToString(" • ")
        .ifBlank { null }

    val items = mutableListOf<DocItem>()
    RetrofitClient.absoluteUrl(front_card)?.let {
        items += DocItem("Government ID (Front)", idSubtitle, it, Icons.Filled.Badge)
    }
    RetrofitClient.absoluteUrl(back_card)?.let {
        items += DocItem("Government ID (Back)", idSubtitle, it, Icons.Filled.Badge)
    }
    RetrofitClient.absoluteUrl(police_verification_cert)?.let {
        items += DocItem("Police Verification Certificate", null, it, Icons.Filled.Description)
    }
    return items
}

@Composable
fun VerifiedDocumentsScreen(
    profileViewModel: ProfileViewModel,
    onBackClick: () -> Unit
) {
    DashboardSystemBars(darkStatusBarIcons = true)

    val context = LocalContext.current
    // Opens the file in whichever app can handle it (browser / PDF viewer / gallery).
    val openDocument: (String) -> Unit = { url ->
        try {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No app found to open this document", Toast.LENGTH_SHORT).show()
        }
    }

    val profile = profileViewModel.profile

    Scaffold(containerColor = BackgroundWhite) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
        ) {
            DetailTopBar(title = "Verified Documents", onBackClick = onBackClick)
            Spacer(modifier = Modifier.height(8.dp))

            when {
                profile != null -> {
                    val docs = remember(profile) { profile.toDocItems() }
                    DocumentsContent(docs = docs, onOpenDocument = openDocument)
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
                            text = profileViewModel.errorMessage ?: "Couldn't load your documents.",
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
}

@Composable
private fun DocumentsContent(docs: List<DocItem>, onOpenDocument: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        if (docs.isEmpty()) {
            Text("No documents uploaded yet.", fontSize = 13.sp, color = TextSecondary)
        } else {
            docs.forEach { doc ->
                DocumentRow(doc = doc, onClick = { onOpenDocument(doc.url) })
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        InfoCard(
            text = "Your documents are securely stored. Tap a document to view it.",
            icon = Icons.Filled.Shield,
            tint = SuccessGreen,
            background = SuccessGreenBg
        )
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DocumentRow(doc: DocItem, onClick: () -> Unit) {
    SectionCard(modifier = Modifier.clickable { onClick() }) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            ServiceIconBadge(icon = doc.icon, size = 44.dp, background = InfoCardBg, tint = TealPrimary)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(doc.label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                if (doc.subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(doc.subtitle, fontSize = 11.sp, color = TextSecondary)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Uploaded", fontSize = 11.sp, color = SuccessGreen)
                }
            }
            Icon(Icons.Filled.ChevronRight, contentDescription = null, tint = HintGray)
        }
    }
}