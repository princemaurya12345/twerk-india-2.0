package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ReactionEmoji
import kotlin.math.sin

@Composable
fun ReactionsOverlay(
    reactions: List<ReactionEmoji>,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.fillMaxSize()) {
        reactions.forEach { reaction ->
            SingleFloatingEmoji(reaction = reaction)
        }
    }
}

@Composable
private fun SingleFloatingEmoji(reaction: ReactionEmoji) {
    val configuration = LocalConfiguration.current
    val screenHeightPx = remember { configuration.screenHeightDp * 3f }
    val screenWidthPx = remember { configuration.screenWidthDp * 3f }

    val progress = remember { Animatable(0f) }

    LaunchedEffect(reaction.id) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 2400, easing = LinearEasing)
        )
    }

    val currentProgress = progress.value
    val startX = reaction.startXFraction * screenWidthPx
    val waveOffset = (sin(currentProgress * Math.PI * 4) * 35).toFloat()
    val xPos = startX + waveOffset
    val yPos = (screenHeightPx * 0.75f) - (currentProgress * screenHeightPx * 0.65f)
    val opacity = (1f - (currentProgress * currentProgress)).coerceIn(0f, 1f)
    val scale = 1f + (currentProgress * 0.5f)

    Text(
        text = reaction.emoji,
        fontSize = (28 * scale).sp,
        modifier = Modifier
            .offset { IntOffset(xPos.toInt(), yPos.toInt()) }
            .alpha(opacity)
    )
}
