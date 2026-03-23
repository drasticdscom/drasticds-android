package com.drasticds.emulator.ui.emulator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.viewinterop.AndroidView
import com.drasticds.emulator.domain.model.layout.LayoutComponent
import com.drasticds.emulator.ui.emulator.components.ClassicSkin
import com.drasticds.emulator.ui.emulator.components.TouchController
import com.drasticds.emulator.ui.emulator.model.EmulatorState
import com.drasticds.emulator.ui.emulator.render.FrameRenderCoordinator
import android.graphics.Rect as AndroidRect

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drasticds.emulator.domain.model.SaveStateSlot

import com.drasticds.emulator.domain.model.Point
import com.drasticds.emulator.ui.emulator.input.IInputListener
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.ui.input.pointer.pointerInput

import androidx.activity.compose.BackHandler
import com.drasticds.emulator.ui.emulator.components.PauseMenu
import com.drasticds.emulator.ui.emulator.model.EmulatorUiEvent
import com.drasticds.emulator.ui.emulator.model.PauseMenu as PauseMenuModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun EmulationScreen(
    viewModel: EmulatorViewModel,
    renderer: DSRenderer,
    frameRenderCoordinator: FrameRenderCoordinator,
    inputListener: IInputListener
) {
    val emulatorState by viewModel.emulatorState.collectAsState()
    val runtimeLayout by viewModel.runtimeLayout.collectAsState()
    val currentFps by viewModel.currentFps.collectAsState()
    
    var saveStateSlots by remember { mutableStateOf<List<SaveStateSlot>>(emptyList()) }
    var topScreenRect by remember { mutableStateOf<AndroidRect?>(null) }
    var bottomScreenRect by remember { mutableStateOf<AndroidRect?>(null) }
    
    val density = LocalDensity.current
    
    var activePauseMenu by remember { mutableStateOf<PauseMenuModel?>(null) }
    
    // Subscribe to UI events
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { event ->
            when (event) {
                is EmulatorUiEvent.ShowPauseMenu -> activePauseMenu = event.pauseMenu
                is EmulatorUiEvent.ShowRomSaveStates -> {
                    saveStateSlots = event.saveStates
                    activePauseMenu = PauseMenuModel(emptyList()) // Dummy to trigger show
                }
                is EmulatorUiEvent.CloseEmulator -> { /* Handled by activity finish */ }
                else -> {}
            }
        }
    }

    BackHandler {
        if (activePauseMenu != null) {
            activePauseMenu = null
            viewModel.resumeEmulator()
        } else {
            viewModel.pauseEmulator(true)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 1. Core Rendering Layer (SurfaceView)
        DualScreenView(viewModel, renderer, frameRenderCoordinator)
        
        // 2. Interactive Layout Helpers
        runtimeLayout?.layout?.let { layout ->
            (layout.mainScreenLayout.components?.filter { it.isScreen() } ?: emptyList()).forEach { comp ->
                ScreenCoordinatesHelper(comp, density, inputListener) { rect ->
                    if (comp.component == LayoutComponent.TOP_SCREEN) topScreenRect = rect
                    else bottomScreenRect = rect
                }
            }
            (layout.secondaryScreenLayout.components?.filter { it.isScreen() } ?: emptyList()).forEach { comp ->
                ScreenCoordinatesHelper(comp, density, inputListener) { rect ->
                    if (comp.component == LayoutComponent.TOP_SCREEN) topScreenRect = rect
                    else bottomScreenRect = rect
                }
            }
        }

        // 3. Touch Controller
        if (emulatorState.isRunning() && activePauseMenu == null) {
            runtimeLayout?.let { config ->
                TouchController(
                    config = config,
                    skin = ClassicSkin,
                    onInputEvent = { input, isDown ->
                        if (isDown) inputListener.onKeyPress(input)
                        else inputListener.onKeyReleased(input)
                    }
                )
            }
        }

        // 4. FPS Counter
        currentFps?.let { fps ->
            Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.TopStart) {
                Text(
                    text = "FPS: $fps",
                    color = Color.Green,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp)).padding(4.dp)
                )
            }
        }
        
        // 5. Loading State
        if (emulatorState is EmulatorState.LoadingRom || emulatorState is EmulatorState.LoadingFirmware) {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF38629F))
            }
        }

        // 6. Pause Menu Overlay
        activePauseMenu?.let { menu ->
            PauseMenu(
                slots = saveStateSlots,
                onSave = { viewModel.saveStateToSlot(it) },
                onLoad = { viewModel.loadStateFromSlot(it) },
                onResume = { 
                    activePauseMenu = null
                    viewModel.resumeEmulator()
                },
                onExit = { viewModel.stopEmulator() }
            )
        }
    }
}

@Composable
fun ScreenCoordinatesHelper(
    comp: com.drasticds.emulator.domain.model.layout.PositionedLayoutComponent,
    density: androidx.compose.ui.unit.Density,
    inputListener: IInputListener,
    onRectChanged: (AndroidRect) -> Unit
) {
    Box(
        modifier = Modifier
            .offset(
                x = with(density) { comp.rect.x.toDp() },
                y = with(density) { comp.rect.y.toDp() }
            )
            .size(
                width = with(density) { comp.rect.width.toDp() },
                height = with(density) { comp.rect.height.toDp() }
            )
            .onGloballyPositioned { coordinates ->
                val rect = coordinates.parentLayoutCoordinates?.localBoundingBoxOf(coordinates)
                if (rect != null) {
                    onRectChanged(AndroidRect(rect.left.toInt(), rect.top.toInt(), rect.right.toInt(), rect.bottom.toInt()))
                }
            }
            .then(if (comp.component == LayoutComponent.BOTTOM_SCREEN) {
                Modifier.pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            val x = (offset.x / size.width * 256).toInt()
                            val y = (offset.y / size.height * 192).toInt()
                            inputListener.onTouch(com.drasticds.emulator.domain.model.Point(x, y))
                        },
                        onDrag = { change, _ ->
                            val x = (change.position.x / size.width * 256).toInt()
                            val y = (change.position.y / size.height * 192).toInt()
                            inputListener.onTouch(com.drasticds.emulator.domain.model.Point(x, y))
                        },
                        onDragEnd = {
                            inputListener.onTouch(com.drasticds.emulator.domain.model.Point(-1, -1))
                        }
                    )
                }
            } else Modifier)
    )
}

@Composable
fun DualScreenView(
    viewModel: EmulatorViewModel,
    renderer: DSRenderer,
    frameRenderCoordinator: FrameRenderCoordinator
) {
    AndroidView(
        factory = { ctx ->
            EmulatorSurfaceView(ctx).apply {
                setRenderer(renderer)
                frameRenderCoordinator.addSurface(this)
            }
        },
        modifier = Modifier.fillMaxSize(),
        onRelease = { view ->
            frameRenderCoordinator.removeSurface(view)
        }
    )
}
