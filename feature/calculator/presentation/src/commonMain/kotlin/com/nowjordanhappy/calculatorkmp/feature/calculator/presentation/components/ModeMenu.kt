package com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.CalculatorAction
import kotlinx.coroutines.delay

@Composable
fun ModeMenu(
    isScientific: Boolean,
    isRad: Boolean = false,
    showDegRad: Boolean = false,
    delayToggle: Boolean = false,
    onAction: (CalculatorAction) -> Unit,
) {
    var showMenu by remember { mutableStateOf(false) }
    var pendingToggle by remember { mutableStateOf(false) }
    LaunchedEffect(showMenu) {
        if (!showMenu && pendingToggle) {
            if (delayToggle) delay(200)
            pendingToggle = false
            onAction(CalculatorAction.OnScientificToggle)
        }
    }
    Box {
        TextButton(onClick = { showMenu = true }) {
            Text("⋮", fontSize = 20.sp, color = MaterialTheme.colorScheme.onBackground)
        }
        DropdownMenu(
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
        ) {
            DropdownMenuItem(
                text = { Text("Basic${if (!isScientific) " ✓" else ""}") },
                onClick = {
                    if (isScientific) pendingToggle = true
                    showMenu = false
                },
            )
            DropdownMenuItem(
                text = { Text("Scientific${if (isScientific) " ✓" else ""}") },
                onClick = {
                    if (!isScientific) pendingToggle = true
                    showMenu = false
                },
            )
            if (showDegRad) {
                DropdownMenuItem(
                    text = { Text(if (isRad) "DEG" else "RAD") },
                    onClick = {
                        onAction(CalculatorAction.OnDegRadToggle)
                        showMenu = false
                    },
                )
            }
        }
    }
}
