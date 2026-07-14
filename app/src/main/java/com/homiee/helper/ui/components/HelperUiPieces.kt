package com.homiee.helper.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homiee.helper.ui.theme.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.*
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.Density
import androidx.compose.ui.graphics.Path

/** Circular colored icon badge used for service types (cleaning, cooking, etc.). */
@Composable
fun ServiceIconBadge(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    background: Color = TealLight,
    tint: Color = TealPrimary
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(12.dp))
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(size * 0.5f))
    }
}

/** Small pill-shaped status label, e.g. "New", "Confirmed", "Completed". */
@Composable
fun StatusChip(
    text: String,
    modifier: Modifier = Modifier,
    background: Color = InfoCardBg,
    textColor: Color = TealPrimary
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(50))
            .background(background)
            .padding(horizontal = 10.dp, vertical = 4.dp)
    ) {
        Text(text = text, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = textColor)
    }
}

/** Round initials avatar used for residents / the helper's own profile placeholder. */
@Composable
fun InitialsAvatar(
    initials: String,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    background: Color = AvatarTealBg,
    textColor: Color = TealPrimaryDark
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Text(text = initials, fontWeight = FontWeight.Bold, color = textColor, fontSize = (size.value * 0.34f).sp)
    }
}

/** Simple label/value row used in Job Details, Request Details, Profile, etc. */
@Composable
fun InfoRow(label: String, value: String, valueColor: Color = TextPrimary) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 13.sp, color = TextSecondary)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = valueColor)
    }
}

/** A rounded white card container with consistent padding/elevation-free border look. */
@Composable
fun SectionCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.White)
            .padding(16.dp),
        content = content
    )
}

/** Section title with an optional trailing "View All"-style action. */
@Composable
fun SectionHeader(
    title: String,
    actionText: String? = null,
    onActionClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        if (actionText != null) {
            Text(
                text = actionText,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = TealPrimary,
                modifier = Modifier.clickableNoRipple(onActionClick)
            )
        }
    }
}

/** A back-arrow + title header used by full-screen detail pages. */
@Composable
fun DetailTopBar(title: String, onBackClick: () -> Unit, trailing: @Composable () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBackClick) {
            Icon(Icons.Filled.ArrowBackIosNew, contentDescription = "Back", tint = TextPrimary, modifier = Modifier.size(18.dp))
        }
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        trailing()
    }
}

/** Circular percentage progress ring, e.g. "Complete Your Profile — 60%". */
@Composable
fun ProgressRing(
    percent: Int,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    strokeWidth: Dp = 5.dp,
    trackColor: Color = BorderGray,
    progressColor: Color = TealPrimary
) {
    Box(modifier = modifier.size(size), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(size)) {
            val stroke = Stroke(width = strokeWidth.toPx(), cap = StrokeCap.Round)
            drawArc(
                color = trackColor,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = stroke,
                size = Size(size.toPx() - stroke.width, size.toPx() - stroke.width),
                topLeft = androidx.compose.ui.geometry.Offset(stroke.width / 2, stroke.width / 2)
            )
            drawArc(
                color = progressColor,
                startAngle = -90f,
                sweepAngle = 360f * (percent / 100f),
                useCenter = false,
                style = stroke,
                size = Size(size.toPx() - stroke.width, size.toPx() - stroke.width),
                topLeft = androidx.compose.ui.geometry.Offset(stroke.width / 2, stroke.width / 2)
            )
        }
        Text(text = "$percent%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
    }
}

// Small helper so SectionHeader's action text doesn't need a ripple import cascade.
@Composable
private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier =
    this.then(
        Modifier.clickableSimple(onClick)
    )

@Composable
private fun Modifier.clickableSimple(onClick: () -> Unit): Modifier {
    return this.clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = onClick
    )
}

/**
 * Bottom edge dips slightly INTO the shape near the left/right corners,
 * while the middle stays flat. Use for the header — creates the
 * "white card pokes up into me" look.
 */
class NotchedBottomShape(
    private val cornerRadius: Dp,
    private val notchDepth: Dp,
    private val notchWidth: Dp
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val cr = with(density) { cornerRadius.toPx() }
        val depth = with(density) { notchDepth.toPx() }
        val nw = with(density) { notchWidth.toPx() }
        val w = size.width
        val h = size.height

        val path = Path().apply {
            moveTo(0f, cr)
            quadraticBezierTo(0f, 0f, cr, 0f)
            lineTo(w - cr, 0f)
            quadraticBezierTo(w, 0f, w, cr)
            lineTo(w, h)
            lineTo(w - nw * 0.2f, h)
            quadraticBezierTo(w - nw * 0.5f, h - depth, w - nw, h)
            lineTo(nw, h)
            quadraticBezierTo(nw * 0.5f, h - depth, nw * 0.2f, h)
            lineTo(0f, h)
            close()
        }
        return Outline.Generic(path)
    }
}

/**
 * Top edge pokes UP into the header near the left/right corners.
 */
class PokedTopShape(
    private val cornerRadius: Dp,
    private val pokeHeight: Dp,
    private val pokeWidth: Dp
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val cr = with(density) { cornerRadius.toPx() }
        val poke = with(density) { pokeHeight.toPx() }
        val pw = with(density) { pokeWidth.toPx() }
        val w = size.width
        val h = size.height

        val path = Path().apply {
            moveTo(0f, 0f)
            lineTo(pw * 0.2f, 0f)
            quadraticBezierTo(pw * 0.5f, -poke, pw, 0f)
            lineTo(w - pw, 0f)
            quadraticBezierTo(w - pw * 0.5f, -poke, w - pw * 0.2f, 0f)
            lineTo(w, 0f)
            lineTo(w, h - cr)
            quadraticBezierTo(w, h, w - cr, h)
            lineTo(cr, h)
            quadraticBezierTo(0f, h, 0f, h - cr)
            close()
        }
        return Outline.Generic(path)
    }
    }
