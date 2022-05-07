package com.wilinz.imagepicker.ui.widget

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun NumberRadioButtonLarge(
    selected: Boolean,
    onClick: (() -> Unit)?,
    serialNumber: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: RadioButtonColors = RadioButtonDefaults.colors()
) {
    Box(
        modifier = modifier
            .clickable { onClick?.invoke() }
            .padding(12.dp)
    ) {
        NumberRadioButton(
            selected,
            onClick,
            serialNumber,
            modifier,
            enabled,
            interactionSource,
            colors
        )
    }
}

@Composable
fun NumberRadioButton(
    selected: Boolean,
    onClick: (() -> Unit)?,
    serialNumber: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    colors: RadioButtonColors = RadioButtonDefaults.colors()
) {
    val dotRadius by animateDpAsState(
        targetValue = if (selected) RadioButtonDotSize / 2 else 0.dp,
        animationSpec = tween(durationMillis = RadioAnimationDuration)
    )
    val radioColor by colors.radioColor(enabled, selected)
    val selectableModifier =
        if (onClick != null) {
            Modifier.selectable(
                selected = selected,
                onClick = onClick,
                enabled = enabled,
                role = Role.RadioButton,
                interactionSource = interactionSource,
                indication = rememberRipple(
                    bounded = false,
                    radius = RadioButtonRippleRadius
                )
            )
        } else {
            Modifier
        }
    Box(
        modifier = modifier
            .then(selectableModifier)
            .wrapContentSize(Alignment.Center)
            .padding(RadioButtonPadding)
            .requiredSize(RadioButtonSize),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier
                .then(selectableModifier)
                .wrapContentSize(Alignment.Center)
                .padding(RadioButtonPadding)
                .requiredSize(RadioButtonSize)
        ) {
            drawRadio(radioColor, dotRadius)
        }
        if (dotRadius > 0.dp) {
//        drawCircle(color, dotRadius.toPx() - strokeWidth / 2, style = Fill)
            serialNumber()
        }
    }

}

private fun DrawScope.drawRadio(color: Color, dotRadius: Dp) {
    if (dotRadius > 0.dp) {
//        drawCircle(color, dotRadius.toPx() - strokeWidth / 2, style = Fill)
        drawCircle(color, RadioRadius.toPx())
    } else {
        val strokeWidth = RadioStrokeWidth.toPx()
        drawCircle(color, RadioRadius.toPx() - strokeWidth / 2, style = Stroke(strokeWidth))

    }
}

private const val RadioAnimationDuration = 100

private val RadioButtonRippleRadius = 24.dp
private val RadioButtonPadding = 2.dp
private val RadioButtonSize = 20.dp
private val RadioRadius = RadioButtonSize / 2
private val RadioButtonDotSize = 12.dp
private val RadioStrokeWidth = 2.dp