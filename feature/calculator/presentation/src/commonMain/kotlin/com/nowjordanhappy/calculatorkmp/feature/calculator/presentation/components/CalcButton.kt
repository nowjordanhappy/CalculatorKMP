package com.nowjordanhappy.calculatorkmp.feature.calculator.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class ButtonType {
    Number,
    Operator,
    Action,
    Scientific,
    Equals
}

@Composable
fun CalcRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        content = content,
    )
}

@Composable
fun RowScope.CalcButton(
    text: String,
    type: ButtonType = ButtonType.Number,
    buttonHeight: Dp? = null,
    onClick: () -> Unit,
) {
    val containerColor =
        when (type) {
            ButtonType.Operator,
            ButtonType.Equals -> MaterialTheme.colorScheme.primary
            ButtonType.Action -> MaterialTheme.colorScheme.secondary
            ButtonType.Scientific -> MaterialTheme.colorScheme.surfaceVariant
            ButtonType.Number -> MaterialTheme.colorScheme.tertiary
        }
    val contentColor =
        when (type) {
            ButtonType.Operator,
            ButtonType.Equals -> MaterialTheme.colorScheme.onPrimary
            ButtonType.Action -> MaterialTheme.colorScheme.onSecondary
            ButtonType.Scientific -> MaterialTheme.colorScheme.onSurfaceVariant
            ButtonType.Number -> MaterialTheme.colorScheme.onTertiary
        }
    val sizeModifier =
        if (buttonHeight != null) {
            Modifier.weight(1f).height(buttonHeight)
        } else {
            Modifier.weight(1f).aspectRatio(1f)
        }
    val isSmall = buttonHeight != null && buttonHeight < 56.dp
    val fontSize =
        when {
            isSmall && text.length > 2 -> 12.sp
            isSmall -> 16.sp
            text.length > 2 -> 18.sp
            else -> 22.sp
        }
    Button(
        onClick = onClick,
        modifier = sizeModifier,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = containerColor,
                contentColor = contentColor,
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = ButtonDefaults.buttonElevation(0.dp),
        contentPadding = PaddingValues(2.dp),
    ) {
        Text(
            text = text,
            fontSize = fontSize,
            fontWeight = FontWeight.Medium,
        )
    }
}
