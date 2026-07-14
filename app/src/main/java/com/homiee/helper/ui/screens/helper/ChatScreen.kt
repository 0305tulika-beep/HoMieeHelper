package com.homiee.helper.ui.screens.helper

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.SentimentSatisfied
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homiee.helper.ui.components.*
import com.homiee.helper.ui.theme.*

@Composable
fun ChatScreen(
    conversationId: String,
    onBackClick: () -> Unit,
    onCallClick: (String) -> Unit = {}
) {
    DashboardSystemBars(darkStatusBarIcons = false)
    val chat = HelperSampleData.chatById(conversationId)
    var draft by remember { mutableStateOf("") }
    val messages = remember { HelperSampleData.messagesFor(conversationId).toMutableStateList() }

    Scaffold(containerColor = BackgroundWhite) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(TealPrimary)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(Icons.Filled.ArrowBackIosNew, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(18.dp))
                }
                InitialsAvatar(
                    initials = chat?.initials ?: "?",
                    size = 38.dp,
                    background = Color.White.copy(alpha = 0.25f),
                    textColor = Color.White
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(chat?.name ?: "Unknown", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(chat?.service ?: "", fontSize = 11.sp, color = Color.White.copy(alpha = 0.85f))
                }
                IconButton(onClick = { onCallClick(conversationId) }) {
                    Icon(Icons.Filled.Call, contentDescription = "Call", tint = Color.White, modifier = Modifier.size(20.dp))
                }
                IconButton(onClick = {}) {
                    Icon(Icons.Filled.MoreVert, contentDescription = "More", tint = Color.White)
                }
            }

            // Booking context banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(InfoCardBg)
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(chat?.service ?: "", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary)
                    Text(chat?.date ?: "", fontSize = 11.sp, color = TextSecondary)
                }
                StatusChip(text = "Upcoming", background = SuccessGreenBg, textColor = SuccessGreen)
            }

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                items(messages) { message ->
                    if (message.dateSeparator != null) {
                        Box(modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
                            Text(message.dateSeparator, fontSize = 11.sp, color = HintGray)
                        }
                    }
                    ChatBubble(message = message)
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            // Composer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(TealPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Attach", tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(50))
                        .background(TealPale)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.weight(1f)) {
                        if (draft.isEmpty()) {
                            Text("Type a message...", fontSize = 13.sp, color = HintGray)
                        }
                        BasicTextField(
                            value = draft,
                            onValueChange = { draft = it },
                            singleLine = true,
                            textStyle = TextStyle(fontSize = 13.sp, color = TextPrimary)
                        )
                    }
                    Icon(Icons.Filled.SentimentSatisfied, contentDescription = null, tint = HintGray, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(TealPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(onClick = {
                        if (draft.isNotBlank()) {
                            messages.add(ChatMessage(draft, "Now", isMe = true))
                            draft = ""
                        }
                    }) {
                        Icon(Icons.Filled.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ChatBubble(message: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isMe) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 260.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 14.dp,
                        topEnd = 14.dp,
                        bottomStart = if (message.isMe) 14.dp else 2.dp,
                        bottomEnd = if (message.isMe) 2.dp else 14.dp
                    )
                )
                .background(if (message.isMe) TealPrimary else TealPale)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.text,
                fontSize = 13.sp,
                color = if (message.isMe) Color.White else TextPrimary,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message.time,
                fontSize = 10.sp,
                color = if (message.isMe) Color.White.copy(alpha = 0.75f) else HintGray,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}

// Small helper to keep the messages list mutable/observable within the screen.
private fun <T> List<T>.toMutableStateList() = androidx.compose.runtime.mutableStateListOf<T>().apply { addAll(this@toMutableStateList) }