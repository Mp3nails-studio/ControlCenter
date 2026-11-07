package com.controlcenter.app.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.AirplanemodeActive
import androidx.compose.material.icons.rounded.Bedtime
import androidx.compose.material.icons.rounded.Bluetooth
import androidx.compose.material.icons.rounded.BluetoothDisabled
import androidx.compose.material.icons.rounded.BrightnessMedium
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FlashlightOff
import androidx.compose.material.icons.rounded.FlashlightOn
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.NotificationsOff
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.ScreenLockRotation
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.SignalCellularAlt
import androidx.compose.material.icons.rounded.SkipNext
import androidx.compose.material.icons.rounded.SkipPrevious
import androidx.compose.material.icons.rounded.VolumeOff
import androidx.compose.material.icons.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.material.icons.rounded.WifiTethering
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Caly panel. Wyjezdza z gory, pod spodem jest przyciemnienie (i systemowe rozmycie
 * ustawiane w oknie, gdy urzadzenie je wspiera).
 */
@Composable
fun ControlCenterPanel(
    c: PanelController,
    closeRequested: Boolean,
    blurActive: Boolean,
    topInsetPx: Int,
    onRequestClose: () -> Unit,
) {
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { shown = true }
    LaunchedEffect(closeRequested) { if (closeRequested) shown = false }
    LaunchedEffect(Unit) {
        while (true) {
            c.refresh()
            delay(700)
        }
    }

    val scrimTarget = if (blurActive) 0.22f else 0.55f
    val scrim by animateFloatAsState(if (shown) scrimTarget else 0f, tween(250), label = "scrim")
    val topInset = with(LocalDensity.current) { topInsetPx.toDp() }

    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = scrim))
            .pointerInput(Unit) { detectTapGestures { onRequestClose() } },
    ) {
        AnimatedVisibility(
            visible = shown,
            modifier = Modifier.align(Alignment.TopCenter),
            enter = slideInVertically(tween(300)) { -it } + fadeIn(tween(200)),
            exit = slideOutVertically(tween(220)) { -it } + fadeOut(tween(180)),
        ) {
            Column(
                Modifier
                    .widthIn(max = 520.dp)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .padding(top = topInset + 6.dp, bottom = 20.dp)
                    // klikniecie w panel nie zamyka go (tapa lapie tlo)
                    .pointerInput(Unit) { detectTapGestures { } }
                    // przeciagniecie w gore zamyka
                    .pointerInput(Unit) {
                        var sum = 0f
                        detectVerticalDragGestures(
                            onDragStart = { sum = 0f },
                            onDragEnd = { sum = 0f },
                            onDragCancel = { sum = 0f },
                        ) { change, amount ->
                            change.consume()
                            sum += amount
                            if (sum < -90f) {
                                sum = 0f
                                onRequestClose()
                            }
                        }
                    },
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                // --- gorny rząd: edycja i ustawienia ---
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconTap(Icons.Rounded.Edit, onClick = { c.openApp() })
                    IconTap(Icons.Rounded.Settings, onClick = { c.openSystemSettings() })
                }

                // --- Wi-Fi i Bluetooth ---
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    WidePill(
                        icon = if (c.wifi) Icons.Rounded.Wifi else Icons.Rounded.WifiOff,
                        label = "Wi-Fi",
                        active = c.wifi,
                        modifier = Modifier.weight(1f),
                        onClick = { c.toggleWifi() },
                    )
                    WidePill(
                        icon = if (c.bluetooth) Icons.Rounded.Bluetooth else Icons.Rounded.BluetoothDisabled,
                        label = "Bluetooth",
                        active = c.bluetooth,
                        modifier = Modifier.weight(1f),
                        onClick = { c.toggleBluetooth() },
                    )
                }

                // --- siatka przelacznikow ---
                GlassCard {
                    Column(
                        Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            RoundToggle(Icons.Rounded.SignalCellularAlt, c.mobileData) { c.toggleMobileData() }
                            RoundToggle(Icons.Rounded.ScreenLockRotation, c.rotationLocked) { c.toggleRotation() }
                            RoundToggle(
                                if (c.torch) Icons.Rounded.FlashlightOn else Icons.Rounded.FlashlightOff,
                                c.torch,
                            ) { c.toggleTorch() }
                            RoundToggle(Icons.Rounded.AirplanemodeActive, c.airplane) { c.toggleAirplane() }
                        }
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            RoundToggle(Icons.Rounded.CameraAlt, false) { c.openCamera() }
                            RoundToggle(Icons.Rounded.NotificationsOff, c.silent) { c.toggleRinger() }
                            RoundToggle(Icons.Rounded.Alarm, false) { c.openAlarms() }
                            RoundToggle(Icons.Rounded.WifiTethering, false) { c.openHotspot() }
                        }
                        GrabHandle()
                    }
                }

                // --- jasnosc i glosnosc ---
                GlassCard {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            PillSlider(
                                value = c.brightness,
                                icon = Icons.Rounded.BrightnessMedium,
                                modifier = Modifier.weight(1f),
                                onDragging = { c.brightnessDragging(it) },
                                onValueChange = { c.onBrightness(it) },
                            )
                            RoundToggle(Icons.Rounded.Bedtime, c.dnd) { c.toggleDnd() }
                        }
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            PillSlider(
                                value = c.volume,
                                icon = Icons.Rounded.VolumeUp,
                                modifier = Modifier.weight(1f),
                                onDragging = { c.volumeDragging(it) },
                                onValueChange = { c.onVolume(it) },
                            )
                            RoundToggle(Icons.Rounded.VolumeOff, c.musicMuted) { c.toggleMusicMute() }
                        }
                    }
                }

                // --- odtwarzacz ---
                val media = c.media
                GlassCard {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Rounded.MusicNote,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.padding(end = 10.dp),
                        )
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = media?.title ?: "Nic nie gra",
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                            if (media != null && media.artist.isNotBlank()) {
                                Text(
                                    text = media.artist,
                                    color = Color.White.copy(alpha = 0.7f),
                                    fontSize = 13.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                        }
                        if (media != null) {
                            IconTap(Icons.Rounded.SkipPrevious, onClick = { c.mediaPrev() })
                            IconTap(
                                if (media.playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow,
                                onClick = { c.mediaToggle() },
                            )
                            IconTap(Icons.Rounded.SkipNext, onClick = { c.mediaNext() })
                        }
                    }
                }
            }
        }
    }
}

@Suppress("unused")
private val keepShapeImport = RoundedCornerShape(0.dp)
