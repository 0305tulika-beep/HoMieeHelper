package com.homiee.helper.ui.screens.auth

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homiee.helper.ui.components.HiddenSystemBars
import com.homiee.helper.ui.theme.TealDeep
import com.homiee.helper.ui.theme.TealLight
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
    val waveOffset by rememberInfiniteTransition(label = "wave").animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(3200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "waveOffset"
    )

    LaunchedEffect(Unit) {
        startAnim = true
        delay(2400)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(TealLight, Color(0xFFB9DEDD))))
    ) {
        WaveBackground(
            offset = waveOffset,
            modifier = Modifier
                .fillMaxWidth()
                .height(420.dp)
                .align(Alignment.BottomCenter)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Homiee",
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = TealPrimary,
                modifier = Modifier
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

            Spacer(modifier = Modifier.height(28.dp))

            Row(modifier = Modifier.alpha(textAlpha)) {
                Box(
                    modifier = Modifier
                        .width(28.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(TealPrimary)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .width(10.dp)
                        .height(6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(TealPrimary.copy(alpha = 0.3f))
                )
            }
        }
    }
}

/** Layered, gently animating waves recreating the reference splash artwork. */
@Composable
private fun WaveBackground(offset: Float, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height
        val shift = (offset - 0.5f) * 24f

        fun wavePath(baseYFraction: Float, amplitude: Float, phase: Float): Path {
            val baseY = height * baseYFraction
            return Path().apply {
                moveTo(0f, baseY)
                quadraticBezierTo(width * 0.25f, baseY - amplitude + phase, width * 0.5f, baseY)
                quadraticBezierTo(width * 0.75f, baseY + amplitude - phase, width, baseY)
                lineTo(width, height)
                lineTo(0f, height)
                close()
            }
        }

        drawPath(wavePath(0.15f, 30f, shift), color = Color(0xFFCDE7E5))
        drawPath(wavePath(0.30f, 26f, -shift), color = Color(0xFFA7D6D3))
        drawPath(wavePath(0.48f, 22f, shift), color = Color(0xFF6FBDB8))
        drawPath(wavePath(0.68f, 18f, -shift), color = TealPrimary)
        drawPath(wavePath(0.85f, 14f, shift), color = TealDeep)
    }
}
