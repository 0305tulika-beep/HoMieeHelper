package com.homiee.helper.ui.screens.reviews

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.homiee.helper.data.remote.ReviewsApiService
import com.homiee.helper.data.remote.RetrofitClient
import com.homiee.helper.ui.theme.BackgroundWhite
import com.homiee.helper.ui.theme.TealPrimary
import com.homiee.helper.ui.theme.TealPrimaryDark
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

private val RvText = Color(0xFF1A1A1A)
private val RvMuted = Color(0xFF7A7A7A)
private val RvStar = Color(0xFFFFC107)

// ── ViewModel ─────────────────────────────────────────────────────────────────

data class ReviewUi(val id: Int, val score: Int, val feedback: String, val timeAgo: String)

data class ReviewsState(
    val isLoading: Boolean = true,
    val average: Float = 0f,
    val count: Int = 0,
    val reviews: List<ReviewUi> = emptyList(),
    val errorMessage: String? = null
)

class ReviewsReceivedViewModel(
    // All params have defaults, so plain viewModel() works (no factory needed)
    private val api: ReviewsApiService = RetrofitClient.reviewsApi
) : ViewModel() {

    private val _state = MutableStateFlow(ReviewsState())
    val state: StateFlow<ReviewsState> = _state.asStateFlow()
    private var lastId = 0

    fun load(helperId: Int) {
        lastId = helperId
        _state.value = _state.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            try {
                val res = api.helperRatings(helperId)
                val d = res.body()?.data
                if (res.isSuccessful && d != null) {
                    _state.value = ReviewsState(
                        isLoading = false,
                        average = d.avg_rating?.toFloatOrNull() ?: 0f,
                        count = d.rating_count ?: 0,
                        reviews = d.ratings.orEmpty().map {
                            ReviewUi(
                                id = it.id,
                                score = (it.score ?: 0).coerceIn(0, 5),
                                feedback = it.feedback.orEmpty().trim(),
                                timeAgo = timeAgoFrom(it.created_at)
                            )
                        }
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        errorMessage = "Couldn't load reviews. Please try again."
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = "Unable to connect. Please check your internet connection."
                )
            }
        }
    }

    fun retry() = load(lastId)
}

private fun timeAgoFrom(iso: String?): String {
    if (iso.isNullOrBlank() || iso.length < 19) return ""
    return try {
        val fmt = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }
        val then = fmt.parse(iso.substring(0, 19))?.time ?: return ""
        val mins = ((System.currentTimeMillis() - then) / 60_000).coerceAtLeast(0)
        val hours = mins / 60
        val days = hours / 24
        when {
            mins < 1 -> "Just now"
            mins < 60 -> "$mins min ago"
            hours < 24 -> if (hours == 1L) "1 hour ago" else "$hours hours ago"
            days < 7 -> if (days == 1L) "1 day ago" else "$days days ago"
            days < 30 -> "${days / 7} week(s) ago"
            days < 365 -> "${days / 30} month(s) ago"
            else -> "${days / 365} year(s) ago"
        }
    } catch (e: Exception) {
        ""
    }
}

// ── Screen ────────────────────────────────────────────────────────────────────

@Composable
fun ReviewsReceivedScreen(
    helperId: Int,
    onBack: () -> Unit = {},
    viewModel: ReviewsReceivedViewModel = viewModel()
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(helperId) { viewModel.load(helperId) }

    Column(Modifier.fillMaxSize().background(BackgroundWhite)) {

        // Same teal header as the Profile screen, so the status bar icons stay readable
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(TealPrimary, TealPrimaryDark)))
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 12.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Spacer(Modifier.width(4.dp))
                Text("Reviews Received", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        when {
            state.isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator(color = TealPrimary)
            }

            state.errorMessage != null -> Column(
                Modifier.fillMaxSize().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(state.errorMessage ?: "", color = RvMuted, fontSize = 14.sp, textAlign = TextAlign.Center)
                TextButton(onClick = { viewModel.retry() }) {
                    Text("Retry", color = TealPrimary, fontWeight = FontWeight.Bold)
                }
            }

            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
            ) {
                item { SummaryCard(state.average, state.count) }

                if (state.reviews.isEmpty()) {
                    item {
                        Text(
                            "No reviews yet. They'll show up here after residents rate your completed bookings.",
                            color = RvMuted,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(top = 32.dp, start = 16.dp, end = 16.dp)
                        )
                    }
                } else {
                    items(state.reviews, key = { it.id }) { ReviewCard(it) }
                }
            }
        }
    }
}

@Composable
private fun SummaryCard(average: Float, count: Int) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(
                String.format(Locale.US, "%.1f", average),
                fontSize = 36.sp, fontWeight = FontWeight.Bold, color = RvText
            )
            Spacer(Modifier.width(14.dp))
            Column {
                Stars(average.toInt().coerceIn(0, 5), 18.sp)
                Spacer(Modifier.height(2.dp))
                Text(if (count == 1) "1 review" else "$count reviews", fontSize = 13.sp, color = RvMuted)
            }
        }
    }
}

@Composable
private fun ReviewCard(review: ReviewUi) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Stars(review.score, 15.sp)
                Text(review.timeAgo, fontSize = 11.sp, color = RvMuted)
            }
            if (review.feedback.isNotBlank()) {
                Spacer(Modifier.height(8.dp))
                Text(review.feedback, fontSize = 13.sp, color = RvMuted, lineHeight = 18.sp)
            }
        }
    }
}

@Composable
private fun Stars(score: Int, size: TextUnit) {
    Row {
        repeat(5) { i ->
            Text(
                if (i < score) "★" else "☆",
                color = if (i < score) RvStar else RvMuted,
                fontSize = size
            )
        }
    }
}