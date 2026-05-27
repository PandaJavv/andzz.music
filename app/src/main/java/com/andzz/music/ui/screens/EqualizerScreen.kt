package com.andzz.music.ui.screens

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.andzz.music.data.model.equalizerPresets
import com.andzz.music.ui.theme.*
import com.andzz.music.viewmodel.MusicViewModel

@Composable
fun EqualizerScreen(vm: MusicViewModel) {
    val eqState by vm.equalizerState.collectAsState()
    val freqLabels = listOf("60", "230", "910", "3.6K", "14K")
    val presetNames = listOf("Flat", "Pop", "Rock", "Jazz", "Classical", "Bass", "Treble", "Vocal")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 20.dp)
    ) {
        Text("Equalizer", style = MaterialTheme.typography.headlineLarge, color = TextPrimary)
        Spacer(Modifier.height(4.dp))
        Text("Fine-tune your sound", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

        Spacer(Modifier.height(20.dp))

        // ── Enable switch ─────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardDark, RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Enable Equalizer", style = MaterialTheme.typography.titleMedium, color = TextPrimary, modifier = Modifier.weight(1f))
            Switch(
                checked = eqState.enabled,
                onCheckedChange = { vm.setEqualizerEnabled(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = DeepBlack,
                    checkedTrackColor = Accent
                )
            )
        }

        Spacer(Modifier.height(20.dp))

        // ── Presets ───────────────────────────────────────────────────────────
        Text("Presets", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
        Spacer(Modifier.height(10.dp))

        val chunked = presetNames.chunked(4)
        chunked.forEach { row ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { preset ->
                    val selected = eqState.presetName == preset
                    FilterChip(
                        selected = selected,
                        onClick = { vm.applyEqualizerPreset(preset) },
                        label = { Text(preset, style = MaterialTheme.typography.labelMedium) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Accent,
                            selectedLabelColor = DeepBlack,
                            containerColor = CardDark,
                            labelColor = TextSecondary
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        Spacer(Modifier.height(20.dp))

        // ── Band sliders ──────────────────────────────────────────────────────
        Text(
            text = if (eqState.presetName == "Custom") "Custom" else "Preset: ${eqState.presetName}",
            style = MaterialTheme.typography.titleMedium,
            color = TextPrimary
        )
        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .background(CardDark, RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            eqState.bands.forEachIndexed { index, band ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        "${band.gain.toInt()}dB",
                        style = MaterialTheme.typography.labelSmall,
                        color = Accent,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(4.dp))
                    Slider(
                        value = band.gain,
                        onValueChange = { vm.setEqualizerBand(index, it) },
                        valueRange = -15f..15f,
                        modifier = Modifier
                            .height(160.dp)
                            .rotate(-90f)
                            .width(120.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = Accent,
                            activeTrackColor = Accent,
                            inactiveTrackColor = Divider
                        )
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        freqLabels[index],
                        style = MaterialTheme.typography.labelSmall,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(
            "Drag each band up (+) or down (-) to boost or cut that frequency",
            style = MaterialTheme.typography.labelSmall,
            color = TextDisabled,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}
