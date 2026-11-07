package com.controlcenter.app.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Granat ikon na jasnym tle (aktywny przycisk). */
val Ink = Color(0xFF14297F)
private val GlassTop = Color(0xFF3358E0)
private val GlassBottom = Color(0xFF1B2F9E)

/** Polprzezroczyste "szklane" tlo - rozmycie za oknem robi sie na poziomie systemu. */
fun Modifier.glass(shape: Shape): Modifier =
    this
        .clip(shape)
        .background(
            Brush.verticalGradient(
                listOf(GlassTop.copy(alpha = 0.58f), GlassBottom.copy(alpha = 0.64f))
            )
        )
        .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.14f)), shape)

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .glass(RoundedCornerShape(30.dp))
            .padding(16.dp),
        content = content,
    )
}

/** Okragly przycisk przelacznika (aktywny = bialy, nieaktywny = przezroczysty). */
@Composable
fun RoundToggle(
    icon: ImageVector,
    active: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 56.dp,
    onClick: () -> Unit,
) {
    val bg by animateColorAsState(
        if (active) Color.White.copy(alpha = 0.94f) else Color.White.copy(alpha = 0.16f),
        label = "toggleBg",
    )
    val tint by animateColorAsState(if (active) Ink else Color.White, label = "toggleTint")
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(bg)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(26.dp))
    }
}

/** Szeroka tabletka z ikona i podpisem (Wi-Fi, Bluetooth). */
@Composable
fun WidePill(
    icon: ImageVector,
    label: String,
    active: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val shape = RoundedCornerShape(32.dp)
    Row(
        modifier = modifier
            .height(64.dp)
            .glass(shape)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RoundToggle(icon = icon, active = active, size = 48.dp, onClick = onClick)
        Spacer(Modifier.width(10.dp))
        Text(label, color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
    }
}

/** Maly przezroczysty przycisk z sama ikona (poprzedni / nastepny itp.). */
@Composable
fun IconTap(
    icon: ImageVector,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    onClick: () -> Unit,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
    }
}

/** Duzy suwak w ksztalcie tabletki (jasnosc, glosnosc). */
@Composable
fun PillSlider(
    value: Float,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onDragging: (Boolean) -> Unit,
    onValueChange: (Float) -> Unit,
) {
    var widthPx by remember { mutableFloatStateOf(1f) }
    val shape = RoundedCornerShape(50)
    val v = value.coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .height(56.dp)
            .clip(shape)
            .background(Color.White.copy(alpha = 0.18f))
            .onSizeChanged { widthPx = it.width.toFloat().coerceAtLeast(1f) }
            .pointerInput(Unit) {
                detectTapGestures { offset ->
                    onValueChange((offset.x / widthPx).coerceIn(0f, 1f))
                }
            }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = { onDragging(true) },
                    onDragEnd = { onDragging(false) },
                    onDragCancel = { onDragging(false) },
                ) { change, _ ->
                    change.consume()
                    onValueChange((change.position.x / widthPx).coerceIn(0f, 1f))
                }
            },
    ) {
        Box(
            Modifier
                .fillMaxHeight()
                .fillMaxWidth(v)
                .background(Color.White.copy(alpha = 0.92f))
        )
        Icon(
            icon,
            contentDescription = null,
            tint = if (v > 0.1f) Ink else Color.White,
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 18.dp)
                .size(24.dp),
        )
    }
}

/** Uchwyt na dole karty (ozdoba jak w iOS). */
@Composable
fun GrabHandle(modifier: Modifier = Modifier) {
    Box(
        modifier
            .width(28.dp)
            .height(4.dp)
            .clip(RoundedCornerShape(50))
            .background(Color.White.copy(alpha = 0.35f))
    )
}

@Suppress("unused")
private val keepArrangementImport = Arrangement.Center
