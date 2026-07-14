package com.homiee.helper.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.homiee.helper.ui.theme.BorderGray
import com.homiee.helper.ui.theme.TealPrimary
import com.homiee.helper.ui.theme.TextSecondary

data class ProgressStep(val label: String, val icon: ImageVector)

/** Horizontal step progress used on the Active job card ("On the Way -> Started -> Completed -> Finished"). */
@Composable
fun JobProgressStepper(steps: List<ProgressStep>, currentStepIndex: Int) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        steps.forEachIndexed { index, step ->
            val done = index <= currentStepIndex
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    if (index > 0) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(2.dp)
                                .background(if (done) TealPrimary else BorderGray)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (done) TealPrimary else BorderGray.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (index < currentStepIndex) Icons.Filled.Check else step.icon,
                            contentDescription = step.label,
                            tint = if (done) Color.White else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    if (index < steps.lastIndex) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(2.dp)
                                .background(if (index < currentStepIndex) TealPrimary else BorderGray)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = step.label, fontSize = 11.sp, color = TextSecondary, fontWeight = FontWeight.Medium)
            }
        }
    }
}

/** Minimal line chart (no external chart library) used on the Total Earnings screen. */
@Composable
fun EarningsLineChart(
    points: List<Float>,
    labels: List<String>,
    valueLabels: List<String>,
    modifier: Modifier = Modifier,
    lineColor: Color = TealPrimary
) {
    Column(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            if (points.isEmpty()) return@Canvas
            val maxValue = (points.maxOrNull() ?: 1f).coerceAtLeast(1f)
            val stepX = size.width / (points.size - 1).coerceAtLeast(1)
            val chartHeight = size.height - 24.dp.toPx()

            val coords = points.mapIndexed { index, value ->
                Offset(
                    x = index * stepX,
                    y = chartHeight - (value / maxValue) * chartHeight
                )
            }

            // Fill under the line
            val fillPath = androidx.compose.ui.graphics.Path().apply {
                moveTo(coords.first().x, chartHeight)
                coords.forEach { lineTo(it.x, it.y) }
                lineTo(coords.last().x, chartHeight)
                close()
            }
            drawPath(fillPath, color = lineColor.copy(alpha = 0.08f))

            // Line
            for (i in 0 until coords.size - 1) {
                drawLine(
                    color = lineColor,
                    start = coords[i],
                    end = coords[i + 1],
                    strokeWidth = 3.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
            // Points
            coords.forEachIndexed { index, point ->
                val isLast = index == coords.lastIndex
                drawCircle(
                    color = Color.White,
                    radius = if (isLast) 6.dp.toPx() else 4.dp.toPx(),
                    center = point
                )
                drawCircle(
                    color = lineColor,
                    radius = if (isLast) 6.dp.toPx() else 4.dp.toPx(),
                    center = point,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.dp.toPx())
                )
            }
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            labels.forEach { label ->
                Text(text = label, fontSize = 10.sp, color = TextSecondary)
            }
        }
    }
}