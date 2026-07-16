package com.homiee.helper.ui.screens.auth

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homiee.helper.R
import com.homiee.helper.ui.components.HiddenSystemBars
import com.homiee.helper.ui.theme.TealPrimary
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    // System bars fully hidden only on this screen, per the requirement.
    HiddenSystemBars()

    var startAnim by remember { mutableStateOf(false) }

    val logoScale by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0.6f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "logoScale"
    )
    val logoAlpha by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(600),
        label = "logoAlpha"
    )
    val textAlpha by animateFloatAsState(
        targetValue = if (startAnim) 1f else 0f,
        animationSpec = tween(700, delayMillis = 250),
        label = "textAlpha"
    )

    LaunchedEffect(Unit) {
        startAnim = true
        delay(2400)
        onFinished()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Full-screen background image.
        Image(
            painter = painterResource(id = R.drawable.flashbg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logotext),
                contentDescription = "Homiee",
                modifier = Modifier
                    .height(56.dp)
                    .scale(logoScale)
                    .alpha(logoAlpha)
            )
            Text(
                text = "—  H e l p e r  —",
                fontSize = 14.sp,
                color = TealPrimary,
                letterSpacing = 2.sp,
                modifier = Modifier.alpha(textAlpha)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Text(
                text = "Trusted help.",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.alpha(textAlpha)
            )
            Text(
                text = "Stronger communities.",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF1A1A1A),
                modifier = Modifier.alpha(textAlpha)
            )
        }
    }
}