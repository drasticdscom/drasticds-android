package com.drasticds.emulator.ui.emulator.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drasticds.emulator.domain.model.Input
import com.drasticds.emulator.domain.model.layout.LayoutComponent
import com.drasticds.emulator.domain.model.layout.PositionedLayoutComponent
import com.drasticds.emulator.ui.emulator.model.RuntimeInputLayoutConfiguration

@Composable
fun TouchController(
    config: RuntimeInputLayoutConfiguration,
    skin: ControllerSkin,
    onInputEvent: (Input, Boolean) -> Unit
) {
    val density = LocalDensity.current
    val layout = config.layout
    
    // Track pressed states for each input
    val pressedInputs = remember { mutableStateMapOf<Input, Boolean>() }

    Box(modifier = Modifier.fillMaxSize()) {
        val components = (layout.mainScreenLayout.components ?: emptyList()) + 
                         (layout.secondaryScreenLayout.components ?: emptyList())
        
        components.filter { !it.isScreen() }.forEach { positioned ->
            ControllerButton(
                positioned = positioned,
                skin = skin,
                isPressed = positioned.component.matchingInputs.any { pressedInputs[it] == true },
                onInputEvent = { input, pressed ->
                    pressedInputs[input] = pressed
                    onInputEvent(input, pressed)
                }
            )
        }
    }
}

@Composable
fun ControllerButton(
    positioned: PositionedLayoutComponent,
    skin: ControllerSkin,
    isPressed: Boolean,
    onInputEvent: (Input, Boolean) -> Unit
) {
    val density = LocalDensity.current
    val rect = positioned.rect
    val component = positioned.component
    
    Box(
        modifier = Modifier
            .offset(
                x = with(density) { rect.x.toDp() },
                y = with(density) { rect.y.toDp() }
            )
            .size(
                width = with(density) { rect.width.toDp() },
                height = with(density) { rect.height.toDp() }
            )
            .pointerInput(component) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val type = event.type
                        
                        when (component) {
                            LayoutComponent.DPAD -> handleDpadInput(event, rect, onInputEvent)
                            LayoutComponent.BUTTONS -> handleButtonsInput(event, rect, onInputEvent)
                            else -> {
                                component.matchingInputs.firstOrNull()?.let { input ->
                                    if (type == PointerEventType.Press || type == PointerEventType.Move) {
                                        onInputEvent(input, true)
                                    } else if (type == PointerEventType.Release) {
                                        onInputEvent(input, false)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            .clip(CircleShape)
            .background(if (isPressed) skin.buttonPressedColor.copy(alpha = positioned.alpha) else skin.buttonColor.copy(alpha = positioned.alpha)),
        contentAlignment = Alignment.Center
    ) {
        when (component) {
            LayoutComponent.DPAD -> DPadView(skin)
            LayoutComponent.BUTTONS -> ButtonsClusterView(skin)
            else -> {
                Text(
                    text = component.name.replace("BUTTON_", "").replace("_", " "),
                    color = skin.textColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun handleDpadInput(event: PointerEvent, rect: com.drasticds.emulator.domain.model.Rect, onInputEvent: (Input, Boolean) -> Unit) {
    // Basic D-Pad logic: divide rect into 9 zones (3x3)
    val touch = event.changes.first().position
    val xZone = (touch.x / (event.changes.first().scrollDelta.x + 1) * 3).toInt() // Simplified
    // ... complex directional logic ...
}

private fun handleButtonsInput(event: PointerEvent, rect: com.drasticds.emulator.domain.model.Rect, onInputEvent: (Input, Boolean) -> Unit) {
    // ABXY logic
}

@Composable
fun DPadView(skin: ControllerSkin) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val center = Offset(size.width / 2, size.height / 2)
        val radius = size.width / 2.5f
        
        // Draw cross
        drawRect(
            color = skin.dpadColor,
            topLeft = Offset(size.width / 3, 0f),
            size = androidx.compose.ui.geometry.Size(size.width / 3, size.height)
        )
        drawRect(
            color = skin.dpadColor,
            topLeft = Offset(0f, size.height / 3),
            size = androidx.compose.ui.geometry.Size(size.width, size.height / 3)
        )
    }
}

@Composable
fun ButtonsClusterView(skin: ControllerSkin) {
    Box(modifier = Modifier.fillMaxSize()) {
        Text("X", Modifier.align(Alignment.TopCenter), color = skin.textColor)
        Text("A", Modifier.align(Alignment.CenterEnd), color = skin.textColor)
        Text("B", Modifier.align(Alignment.BottomCenter), color = skin.textColor)
        Text("Y", Modifier.align(Alignment.CenterStart), color = skin.textColor)
    }
}
