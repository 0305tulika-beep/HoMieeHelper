package com.homiee.helper.ui.screens.helper

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homiee.helper.R
import com.homiee.helper.ui.components.*
import com.homiee.helper.ui.theme.*
import kotlinx.coroutines.delay

/**
 * Number dialed when the helper taps "Tap to SOS". 112 is India's unified
 * emergency helpline. Kept as a default parameter on HomeScreen so a
 * screen/backend-driven number (e.g. a country-specific or platform-configured
 * helpline) can be passed in instead without touching this file again.
 */
private const val DEFAULT_EMERGENCY_HELPLINE = "112"

private const val BANNER_INTERVAL_MS = 3000L   // how long each banner stays
private const val BANNER_SLIDE_ANIM_MS = 450   // how long the slide takes (quick)

private data class BannerChip(val icon: ImageVector, val label: String)

/** Text is drawn in Compose; [imageRes] is only the person photo on the right. */
private data class BannerSlide(
    val title: String,
    val subtitle: String,
    val chips: List<BannerChip>,
    @DrawableRes val imageRes: Int
)

private val bannerSlides = listOf(
    BannerSlide(
        title = "Real work.\nBetter opportunities.",
        subtitle = "Get steady jobs, fair pay and grow with us.",
        chips = listOf(
            BannerChip(Icons.Filled.DateRange, "Flexible"),
            BannerChip(Icons.Filled.Star, "Fair Wages"),
            BannerChip(Icons.Filled.Favorite, "Own Boss")
        ),
        imageRes = R.drawable.b1
    ),
    BannerSlide(
        title = "Your safety\nis our priority.",
        subtitle = "Verified helpers. Trusted homes. A safer community for everyone.",
        chips = listOf(
            BannerChip(Icons.Filled.CheckCircle, "Verified"),
            BannerChip(Icons.Filled.Call, "In-App Help"),
            BannerChip(Icons.Filled.Lock, "Secure")
        ),
        imageRes = R.drawable.b2
    ),
    BannerSlide(
        title = "Verified helpers.\nTrusted families.",
        subtitle = "Every helper goes through careful verification, so families book with confidence.",
        chips = listOf(
            BannerChip(Icons.Filled.CheckCircle, "ID Verified"),
            BannerChip(Icons.Filled.Star, "Skill Check"),
            BannerChip(Icons.Filled.Favorite, "Reviews")
        ),
        imageRes = R.drawable.b3
    ),
    BannerSlide(
        title = "More than just work.\nA supportive community.",
        subtitle = "Get help, share experiences and be part of something bigger.",
        chips = listOf(
            BannerChip(Icons.Filled.Call, "Support"),
            BannerChip(Icons.Filled.Person, "Connections"),
            BannerChip(Icons.Filled.ThumbUp, "Growth")
        ),
        imageRes = R.drawable.b4
    )
)

@Composable
fun HomeScreen(
    userName: String = "Priya",
    // Real data comes from HelperBookingViewModel (mapped in HomieeNavHost).
    // The defaults keep @Preview working with the sample data.
    requests: List<JobRequest> = HelperSampleData.newRequests,
    upcomingJobs: List<JobBooking> = HelperSampleData.upcomingBookings,
    profileCompletionPercent: Int = 60,
    emergencyHelplineNumber: String = DEFAULT_EMERGENCY_HELPLINE,
    onCompleteProfileClick: () -> Unit = {},
    onViewRequest: (String) -> Unit = {},
    onAcceptRequest: (String) -> Unit = {},
    onViewAllRequests: () -> Unit = {},
    onViewAllUpcoming: () -> Unit = {},
    onUpcomingJobClick: (String) -> Unit = {},
    onSosClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    currentRoute: String? = null,
    onNavItemClick: (HelperNavItem) -> Unit = {}
) {
    DashboardSystemBars(darkStatusBarIcons = false)
    var isOnline by remember { mutableStateOf(true) }
    val context = LocalContext.current

    Scaffold(
        // Transparent so the full-screen background image (below) shows through
        // everywhere the scrolling content doesn't cover it.
        containerColor = Color.Transparent,
        bottomBar = {
            HelperBottomNavBar(
                currentRoute = currentRoute,
                badgeCounts = mapOf(HelperNavItem.JobRequests to requests.size),
                onItemClick = onNavItemClick
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Full-screen background image - fixed, sits behind everything,
            // does NOT scroll with the content.
            Image(
                painter = painterResource(id = R.drawable.homebg2),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = innerPadding.calculateBottomPadding())
                    .verticalScroll(rememberScrollState())
            ) {
                // Logo + tagline on the left, notification bell on the right.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(horizontal = 20.dp, vertical = 20.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Image(
                            painter = painterResource(id = R.drawable.logotext1),
                            contentDescription = "Good morning, $userName",
                            modifier = Modifier.height(50.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Great helpers make happy homes.\nLet's get you more opportunities!",
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 18.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.22f))
                            .clickable { onNotificationsClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    // Banner card: below the logo, above Go Online.
                    HomeBannerCarousel(slides = bannerSlides)

                    Spacer(modifier = Modifier.height(16.dp))

                    // ---------- Go Online ----------
                    ElevatedHomeCard(background = TealPale) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            // Person icon with a status dot (green when online).
                            Box(modifier = Modifier.size(48.dp)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.7f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Person,
                                        contentDescription = null,
                                        tint = TealPrimary,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .size(14.dp)
                                        .align(Alignment.BottomEnd)
                                        .clip(CircleShape)
                                        .background(Color.White)
                                        .padding(2.dp)
                                        .clip(CircleShape)
                                        .background(if (isOnline) SuccessGreen else BorderGray)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Go Online", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    "Residents can see you and send job requests.",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    lineHeight = 15.sp
                                )
                            }
                            Switch(
                                checked = isOnline,
                                onCheckedChange = { isOnline = it },
                                modifier = Modifier.scale(0.8f),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color.White,
                                    checkedTrackColor = TealPrimary,
                                    uncheckedTrackColor = BorderGray
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // ---------- New Requests ----------
                    ElevatedHomeCard {
                        SectionHeaderRow(
                            icon = Icons.Filled.List,
                            title = "New Requests",
                            subtitle = "Check and respond to new job requests from families in your area.",
                            count = requests.size,
                            onClick = onViewAllRequests
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        if (requests.isEmpty()) {
                            EmptyStateBox(
                                icon = Icons.Filled.Notifications,
                                title = "No new requests right now.",
                                subtitle = "You'll be notified when a family needs help."
                            )
                        } else {
                            val visibleRequests = requests.take(3)
                            visibleRequests.forEachIndexed { index, request ->
                                HomeRequestCard(
                                    request = request,
                                    onView = { onViewRequest(request.id) },
                                    onAccept = { onAcceptRequest(request.id) }
                                )
                                if (index != visibleRequests.lastIndex) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "View All Requests",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TealPrimary,
                                modifier = Modifier.clickable { onViewAllRequests() }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // ---------- Upcoming Jobs ----------
                    ElevatedHomeCard {
                        SectionHeaderRow(
                            icon = Icons.Filled.DateRange,
                            title = "Upcoming Jobs",
                            subtitle = "View your scheduled jobs and manage your day.",
                            count = upcomingJobs.size,
                            onClick = onViewAllUpcoming
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        if (upcomingJobs.isEmpty()) {
                            EmptyStateBox(
                                icon = Icons.Filled.DateRange,
                                title = "No upcoming jobs yet.",
                                subtitle = "Your next opportunity will appear here."
                            )
                        } else {
                            val visibleJobs = upcomingJobs.take(2)
                            visibleJobs.forEachIndexed { index, booking ->
                                HomeUpcomingJobRow(
                                    booking = booking,
                                    onClick = { onUpcomingJobClick(booking.id) }
                                )
                                if (index != visibleJobs.lastIndex) {
                                    Spacer(modifier = Modifier.height(12.dp))
                                }
                            }
                            Spacer(modifier = Modifier.height(14.dp))
                            Text(
                                text = "View All Upcoming Jobs",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TealPrimary,
                                modifier = Modifier.clickable { onViewAllUpcoming() }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // ---------- Emergency SOS ----------
                    // Tapping the button dials the emergency helpline directly.
                    ElevatedHomeCard(background = SosRedBg) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(SosRed),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("SOS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Emergency SOS", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = SosRed)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    "Tap to alert your emergency contacts in case of any urgent situation.",
                                    fontSize = 11.sp,
                                    color = TextSecondary,
                                    lineHeight = 14.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            OutlinedButton(
                                onClick = {
                                    onSosClick()
                                    makePhoneCall(context, emergencyHelplineNumber)
                                },
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp),
                                border = BorderStroke(1.dp, SosRed),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = SosRed)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Warning,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Tap to SOS", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

/**
 * Header row used by New Requests and Upcoming Jobs: round teal icon, title +
 * subtitle, a count pill, and a chevron. The whole row is tappable.
 */
@Composable
private fun SectionHeaderRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    count: Int,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(TealPale),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TealPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 11.sp, color = TextSecondary, lineHeight = 15.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(50))
                .background(TealPale)
                .padding(horizontal = 16.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = count.toString(),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = TealPrimaryDark
            )
        }
        Icon(
            imageVector = Icons.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = HintGray,
            modifier = Modifier.size(22.dp)
        )
    }
}

/** Soft rounded box with an icon, a title and a hint, shown when a list is empty. */
@Composable
private fun EmptyStateBox(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(TealPale.copy(alpha = 0.55f))
            .padding(horizontal = 16.dp, vertical = 18.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = TealPrimary.copy(alpha = 0.8f),
            modifier = Modifier.size(34.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = subtitle,
            fontSize = 11.sp,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Banner carousel: banner 1 -> 2 -> 3 -> 4 -> 1 ... Each banner slides fully
 * into the next one (no spacing, no peeking neighbours) and the card itself is
 * clipped, so only one whole banner is ever visible.
 *
 * The pager has a large finite page count and maps page % 4 to a banner, so
 * going from banner 4 to banner 1 is also a normal forward slide instead of a
 * long rewind.
 *
 * The auto-advance loop is a single coroutine: wait, then slide, repeat. It
 * skips a tick while the user is dragging.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun HomeBannerCarousel(slides: List<BannerSlide>) {
    val count = slides.size
    val totalPages = 4000                       // multiple of 4
    val startPage = totalPages / 2              // multiple of 4, so it starts on banner 1
    val pagerState = rememberPagerState(initialPage = startPage, pageCount = { totalPages })

    LaunchedEffect(pagerState) {
        while (true) {
            delay(BANNER_INTERVAL_MS)
            if (!pagerState.isScrollInProgress) {
                pagerState.animateScrollToPage(
                    page = pagerState.currentPage + 1,
                    animationSpec = tween(
                        durationMillis = BANNER_SLIDE_ANIM_MS,
                        easing = FastOutSlowInEasing
                    )
                )
            }
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .height(170.dp)
                .clip(RoundedCornerShape(18.dp))
        ) { page ->
            BannerSlideCard(slides[page % count])
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Four dots, one per banner. The active one is a longer teal pill.
        val current = pagerState.currentPage % count
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(count) { index ->
                val selected = current == index
                Box(
                    modifier = Modifier
                        .padding(horizontal = 3.dp)
                        .height(6.dp)
                        .width(if (selected) 20.dp else 6.dp)
                        .clip(CircleShape)
                        .background(if (selected) TealPrimary else TealPrimary.copy(alpha = 0.25f))
                )
            }
        }
    }
}

@Composable
private fun BannerSlideCard(slide: BannerSlide) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.linearGradient(listOf(Color.White, TealPale)))
    ) {
        // Person photo: full height of the card, flush to the right edge.
        Image(
            painter = painterResource(id = slide.imageRes),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(165.dp),
            contentScale = ContentScale.Crop,
            alignment = Alignment.CenterEnd
        )

        // Text on the left, leaving room for the photo.
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(start = 16.dp, end = 150.dp, top = 14.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = slide.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TealPrimaryDark,
                lineHeight = 19.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = slide.subtitle,
                fontSize = 11.sp,
                color = TextSecondary,
                lineHeight = 15.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                slide.chips.forEach { chip ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = chip.icon,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = chip.label,
                            fontSize = 8.sp,
                            color = TextPrimary,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

/**
 * Launches the phone dialer pre-filled with [number]. Uses ACTION_DIAL (not
 * ACTION_CALL) on purpose: it opens the dialer with the number ready to go but
 * requires the user to tap the call button themselves, which means the app
 * does not need the CALL_PHONE runtime permission at all. Falls back to a
 * Toast if no dialer app is available on the device.
 */
private fun makePhoneCall(context: Context, number: String) {
    try {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))
        context.startActivity(intent)
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "No dialer app found on this device", Toast.LENGTH_SHORT).show()
    }
}

/**
 * Card with a very subtle elevation (1dp) used to wrap every section on
 * the Home screen - Go Online, New Requests, Upcoming Jobs, and Emergency SOS.
 */
@Composable
private fun ElevatedHomeCard(
    background: Color = Color.White,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = background,
        tonalElevation = 0.dp,   // kills the unwanted primary-tint overlay
        shadowElevation = 1.dp,  // keeps the subtle shadow, no color shift
        content = { Column(modifier = Modifier.padding(16.dp), content = content) }
    )
}

/**
 * Individual new-request card: white background, subtle border, teal left
 * accent strip. Name (and location, when we have one) on the left; View +
 * Accept buttons on the right.
 */
@Composable
private fun HomeRequestCard(request: JobRequest, onView: () -> Unit, onAccept: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(RoundedCornerShape(14.dp))
            .background(Color.White)
            .border(1.dp, BorderGray, RoundedCornerShape(14.dp))
    ) {
        Box(
            modifier = Modifier
                .width(4.dp)
                .fillMaxHeight()
                .background(TealPrimary)
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(request.residentName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(request.service.label, fontSize = 12.sp, color = TextSecondary)
                    if (request.location.isNotBlank()) {
                        Text(request.location, fontSize = 11.sp, color = HintGray)
                    }
                }
                Spacer(modifier = Modifier.width(10.dp))
                OutlinedButton(
                    onClick = onView,
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BorderGray),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary)
                ) { Text("View", fontSize = 12.sp) }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onAccept,
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) { Text("Accept", fontSize = 12.sp, color = Color.White) }
            }
        }
    }
}

/** Individual tinted job-row card inside the white "Upcoming Jobs" section. */
@Composable
private fun HomeUpcomingJobRow(booking: JobBooking, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(TealPale)
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(46.dp)) {
            Text(booking.date.substringBefore(" "), fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TealPrimaryDark)
            Text(booking.date.substringAfter(" ").substringBefore(" "), fontSize = 11.sp, color = TextSecondary)
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(booking.service.label, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text(booking.residentName, fontSize = 12.sp, color = TextSecondary)
            if (booking.location.isNotBlank()) {
                Text(booking.location, fontSize = 11.sp, color = HintGray)
            } else {
                Text(booking.time, fontSize = 11.sp, color = HintGray)
            }
        }
        StatusChip(text = "Confirmed", background = SuccessGreenBg, textColor = SuccessGreen)
    }
}