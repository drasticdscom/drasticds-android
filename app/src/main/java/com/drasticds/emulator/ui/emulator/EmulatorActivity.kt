package com.drasticds.emulator.ui.emulator

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.hardware.display.DisplayManager
import android.hardware.input.InputManager
import android.os.Bundle
import android.os.Handler
import android.view.Display
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.os.ConfigurationCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.DEFAULT_ARGS_KEY
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.MutableCreationExtras
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import com.squareup.picasso.Picasso
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.launch
import com.drasticds.emulator.MelonEmulator
import com.drasticds.emulator.R
import com.drasticds.emulator.common.PermissionHandler
import com.drasticds.emulator.databinding.ActivityEmulatorBinding
import com.drasticds.emulator.domain.model.ConsoleType
import com.drasticds.emulator.domain.model.ControllerConfiguration
import com.drasticds.emulator.domain.model.FpsCounterPosition
import com.drasticds.emulator.domain.model.Rect
import com.drasticds.emulator.domain.model.SaveStateSlot
import com.drasticds.emulator.domain.model.layout.LayoutComponent
import com.drasticds.emulator.domain.model.layout.ScreenFold
import com.drasticds.emulator.domain.model.rom.Rom
import com.drasticds.emulator.domain.model.ui.Orientation
import com.drasticds.emulator.extensions.insetsControllerCompat
import com.drasticds.emulator.extensions.setLayoutOrientation
import com.drasticds.emulator.impl.emulator.LifecycleOwnerProvider
import com.drasticds.emulator.impl.layout.DeviceLayoutDisplayMapper
import com.drasticds.emulator.impl.layout.SecondaryDisplaySelector
import com.drasticds.emulator.impl.system.AppForegroundStateObserver
import com.drasticds.emulator.parcelables.RomInfoParcelable
import com.drasticds.emulator.parcelables.RomParcelable
import com.drasticds.emulator.ui.cheats.CheatsActivity
import com.drasticds.emulator.ui.emulator.component.EmulatorOverlayTracker
import com.drasticds.emulator.ui.emulator.input.ConnectedControllerManager
import com.drasticds.emulator.ui.emulator.input.EmulatorRumbleManager
import com.drasticds.emulator.ui.emulator.input.FrontendInputHandler
import com.drasticds.emulator.ui.emulator.input.INativeInputListener
import com.drasticds.emulator.ui.emulator.input.InputProcessor
import com.drasticds.emulator.ui.emulator.input.MelonTouchHandler
import com.drasticds.emulator.ui.emulator.model.EmulatorOverlay
import com.drasticds.emulator.ui.emulator.model.EmulatorState
import com.drasticds.emulator.ui.emulator.model.EmulatorUiEvent
import com.drasticds.emulator.ui.emulator.model.LaunchArgs
import com.drasticds.emulator.ui.emulator.model.PauseMenu
import com.drasticds.emulator.ui.emulator.model.RAEventUi
import com.drasticds.emulator.ui.emulator.model.RumbleEvent
import com.drasticds.emulator.ui.emulator.model.RuntimeInputLayoutConfiguration
import com.drasticds.emulator.ui.emulator.model.ToastEvent
import com.drasticds.emulator.ui.emulator.render.ChoreographerFrameRenderer
import com.drasticds.emulator.ui.emulator.render.ChoreographerFrameRendererFactory
import com.drasticds.emulator.ui.emulator.render.ExternalPresentation
import com.drasticds.emulator.ui.emulator.render.FrameRenderCoordinator
import com.drasticds.emulator.ui.emulator.rewind.EdgeSpacingDecorator
import com.drasticds.emulator.ui.emulator.rewind.RewindSaveStateAdapter
import com.drasticds.emulator.ui.emulator.rewind.model.RewindWindow
import com.drasticds.emulator.ui.emulator.rom.SaveStateAdapter
import com.drasticds.emulator.ui.emulator.ui.AchievementListDialog
import com.drasticds.emulator.ui.emulator.ui.AchievementUpdatesUi
import com.drasticds.emulator.ui.layouteditor.model.LayoutTarget
import com.drasticds.emulator.ui.settings.SettingsActivity
import com.drasticds.emulator.ui.theme.DrasticDSTheme
import java.text.SimpleDateFormat
import javax.inject.Inject

@AndroidEntryPoint
class EmulatorActivity : AppCompatActivity() {
    companion object {
        const val KEY_ROM = "rom"
        const val KEY_PATH = "PATH"
        const val KEY_URI = "uri"
        const val KEY_BOOT_FIRMWARE_CONSOLE = "boot_firmware_console"
        const val KEY_BOOT_FIRMWARE_ONLY = "boot_firmware_only"

        fun getRomEmulatorActivityIntent(context: Context, rom: Rom): Intent {
            return Intent(context, EmulatorActivity::class.java).apply {
                putExtra(KEY_ROM, RomParcelable(rom))
            }
        }

        fun getFirmwareEmulatorActivityIntent(context: Context, consoleType: ConsoleType): Intent {
            return Intent(context, EmulatorActivity::class.java).apply {
                putExtra(KEY_BOOT_FIRMWARE_ONLY, true)
                putExtra(KEY_BOOT_FIRMWARE_CONSOLE, consoleType.ordinal)
            }
        }
    }

    // Removed binding
    private val viewModel: EmulatorViewModel by viewModels(
        extrasProducer = {
            val extras = MutableCreationExtras(defaultViewModelCreationExtras)
            // Inject intent data into view-model creation extras to make it accessible through the SavedStateHandle
            intent.data?.let { dataUri ->
                val existingExtras = extras[DEFAULT_ARGS_KEY]?.let { Bundle(it) } ?: Bundle()
                existingExtras.putString(KEY_URI, dataUri.toString())
                extras[DEFAULT_ARGS_KEY] = existingExtras
            }
            extras
        }
    )

    @Inject
    lateinit var secondaryDisplaySelector: SecondaryDisplaySelector

    @Inject
    lateinit var deviceLayoutDisplayMapper: DeviceLayoutDisplayMapper

    @Inject
    lateinit var picasso: Picasso

    @Inject
    lateinit var permissionHandler: PermissionHandler

    @Inject
    lateinit var lifecycleOwnerProvider: LifecycleOwnerProvider

    @Inject
    lateinit var appForegroundStateObserver: AppForegroundStateObserver

    private var presentation: ExternalPresentation? = null

    private lateinit var handler: Handler
    private lateinit var displayManager: DisplayManager
    private val displayListener = object : DisplayManager.DisplayListener {

        override fun onDisplayAdded(displayId: Int) {
            runOnUiThread {
                updateDisplays()
            }
        }

        override fun onDisplayRemoved(displayId: Int) {
            runOnUiThread {
                updateDisplays()
            }
        }

        override fun onDisplayChanged(displayId: Int) {
            updateDisplays()
        }
    }

    private val connectedControllerManager = ConnectedControllerManager()
    private lateinit var emulatorRumbleManager: EmulatorRumbleManager
    private lateinit var frameRenderCoordinator: FrameRenderCoordinator
    private lateinit var choreographerFrameRenderer: ChoreographerFrameRenderer
    private lateinit var mainScreenRenderer: DSRenderer
    private lateinit var melonTouchHandler: MelonTouchHandler
    private lateinit var nativeInputListener: INativeInputListener
    private val frontendInputHandler = object : FrontendInputHandler() {
        var fastForwardEnabled = false
            private set
        var microphoneEnabled = true
            private set

        override fun onSoftInputTogglePressed() {
            // binding.viewLayoutControls.toggleSoftInputVisibility() - Handled in Compose
        }

        override fun onPausePressed() {
            viewModel.pauseEmulator(true)
        }

        override fun onFastForwardPressed() {
            fastForwardEnabled = !fastForwardEnabled
            // binding.viewLayoutControls.setLayoutComponentToggleState... - Handled in Compose
            MelonEmulator.setFastForwardEnabled(fastForwardEnabled)
        }

        override fun onMicrophonePressed() {
            microphoneEnabled = !microphoneEnabled
            // binding.viewLayoutControls.setLayoutComponentToggleState... - Handled in Compose
            MelonEmulator.setMicrophoneEnabled(microphoneEnabled)
        }

        override fun onResetPressed() {
            viewModel.resetEmulator()
        }

        override fun onSwapScreens() {
            swapScreen()
        }

        override fun onQuickSave() {
            viewModel.doQuickSave()
        }

        override fun onQuickLoad() {
            viewModel.doQuickLoad()
        }

        override fun onRewind() {
            viewModel.onOpenRewind()
        }
    }
    private val settingsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        viewModel.onSettingsChanged()
        setupSustainedPerformanceMode()
        setupFpsCounter()
        viewModel.resumeEmulator()
    }
    private val cheatsLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        viewModel.onCheatsChanged()
        viewModel.resumeEmulator()
    }
    private val permissionRequestLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        lifecycleScope.launch {
            it.keys.forEach { permission ->
                permissionHandler.notifyPermissionStatusUpdated(permission)
            }
        }
    }
    private val backPressedCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            handleBackPressed()
        }
    }

    private val rewindSaveStateAdapter = RewindSaveStateAdapter {
        viewModel.rewindToState(it)
        closeRewindWindow()
    }
    private val showAchievementList = mutableStateOf(false)

    private val activeOverlays = EmulatorOverlayTracker(
        onOverlaysCleared = {
            disableScreenTimeOut()
            presentation?.setPauseOverlayVisibility(false)
        },
        onOverlaysPresent = {
            enableScreenTimeOut()
            presentation?.setPauseOverlayVisibility(true)
        }
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handler = Handler(mainLooper)
        lifecycleOwnerProvider.setCurrentLifecycleOwner(this)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setupFullscreen()
        
        val rootView = window.decorView.rootView
        ViewCompat.setOnApplyWindowInsetsListener(rootView) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
            view.setPadding(
                insets.left,
                insets.top,
                insets.right,
                insets.bottom,
            )
            WindowInsetsCompat.CONSUMED
        }

        onBackPressedDispatcher.addCallback(backPressedCallback)
        
        emulatorRumbleManager = EmulatorRumbleManager(this, lifecycleScope, connectedControllerManager)
        frameRenderCoordinator = FrameRenderCoordinator()
        choreographerFrameRenderer = ChoreographerFrameRendererFactory.createFrameRenderer(frameRenderCoordinator)
        melonTouchHandler = MelonTouchHandler()
        mainScreenRenderer = DSRenderer(this)
        
        setContent {
            DrasticDSTheme {
                EmulationScreen(
                    viewModel = viewModel,
                    renderer = mainScreenRenderer,
                    frameRenderCoordinator = frameRenderCoordinator,
                    inputListener = melonTouchHandler
                )
            }
        }

        updateOrientation(resources.configuration)
        disableScreenTimeOut()

        // Achievement UI - will move to EmulationScreen

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                permissionHandler.observePermissionRequests().collect {
                    permissionRequestLauncher.launch(arrayOf(it))
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.runtimeLayout.collectLatest {
                    setupSoftInput(it)
                }
            }
        }
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.controllerConfiguration.collect {
                    setupInputHandling(it)
                    connectedControllerManager.setCurrentControllerConfiguration(it)
                }
            }
        }
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                connectedControllerManager.controllersState.collect {
                    // binding.viewLayoutControls.setConnectedControllersState(it)
                    presentation?.layoutView?.setConnectedControllersState(it)
                }
            }
        }
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.mainScreenBackground.collectLatest {
                    mainScreenRenderer.setBackground(it)
                }
            }
        }
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.secondaryScreenBackground.collectLatest {
                    presentation?.updateBackground(it)
                }
            }
        }
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.runtimeRendererConfiguration.collectLatest {
                    mainScreenRenderer.updateRendererConfiguration(it)
                    presentation?.updateRendererConfiguration(it)
                }
            }
        }
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentFps.collectLatest {
                    if (it == null) {
                        // binding.textFps.text = null
                    } else {
                        // binding.textFps.text = getString(R.string.info_fps, it)
                    }
                }
            }
        }
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.toastEvent.collectLatest {
                    val (message, duration) = when (it) {
                        ToastEvent.GbaLoadFailed -> R.string.error_load_gba_rom to Toast.LENGTH_SHORT
                        ToastEvent.QuickSaveSuccessful -> R.string.saved to Toast.LENGTH_SHORT
                        ToastEvent.QuickLoadSuccessful -> R.string.loaded to Toast.LENGTH_SHORT
                        ToastEvent.RewindNotEnabled -> R.string.rewind_not_enabled to Toast.LENGTH_SHORT
                        ToastEvent.RewindNotAvailableWhileRAHardcoreModeEnabled -> R.string.rewind_unavailable_ra_hardcore_enabled to Toast.LENGTH_LONG
                        ToastEvent.StateLoadFailed -> R.string.failed_load_state to Toast.LENGTH_SHORT
                        ToastEvent.StateSaveFailed -> R.string.failed_save_state to Toast.LENGTH_SHORT
                        ToastEvent.StateStateDoesNotExist -> R.string.cant_load_empty_slot to Toast.LENGTH_SHORT
                        ToastEvent.CannotUseSaveStatesWhenRAHardcoreIsEnabled -> R.string.save_states_unavailable_ra_hardcore_enabled to Toast.LENGTH_LONG
                        ToastEvent.CannotLoadStateWhenRunningFirmware,
                        ToastEvent.CannotSaveStateWhenRunningFirmware -> R.string.save_states_not_supported to Toast.LENGTH_LONG
                        ToastEvent.CannotSwitchRetroAchievementsMode -> R.string.retro_achievements_relaunch_to_apply_settings to Toast.LENGTH_LONG
                        ToastEvent.GbaModeNotSupported -> R.string.emulator_stop_gba_mode_unsupported to Toast.LENGTH_SHORT
                        ToastEvent.InternalError -> R.string.emulator_stop_internal_error to Toast.LENGTH_LONG
                    }

                    Toast.makeText(this@EmulatorActivity, message, duration).show()
                }
            }
        }
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.uiEvent.collectLatest {
                    when (it) {
                        EmulatorUiEvent.CloseEmulator -> {
                            choreographerFrameRenderer.stopRendering()
                            presentation?.apply {
                                dismiss()
                            }
                            finish()
                        }
                        is EmulatorUiEvent.OpenScreen.CheatsScreen -> {
                            val intent = Intent(this@EmulatorActivity, CheatsActivity::class.java)
                            intent.putExtra(CheatsActivity.KEY_ROM_INFO, RomInfoParcelable.fromRomInfo(it.romInfo))
                            cheatsLauncher.launch(intent)
                        }
                        EmulatorUiEvent.OpenScreen.SettingsScreen -> {
                            val settingsIntent = Intent(this@EmulatorActivity, SettingsActivity::class.java)
                            settingsLauncher.launch(settingsIntent)
                        }
                        is EmulatorUiEvent.ShowPauseMenu -> showPauseMenu(it.pauseMenu)
                        is EmulatorUiEvent.ShowRewindWindow -> showRewindWindow(it.rewindWindow)
                        is EmulatorUiEvent.ShowRomSaveStates -> {
                            showSaveStateSlotsDialog(it.saveStates) { slot ->
                                if (it.reason == EmulatorUiEvent.ShowRomSaveStates.Reason.SAVING) {
                                    viewModel.saveStateToSlot(slot)
                                } else {
                                    viewModel.loadStateFromSlot(slot)
                                }
                            }
                        }
                        EmulatorUiEvent.ShowAchievementList -> {
                            activeOverlays.addActiveOverlay(EmulatorOverlay.ACHIEVEMENTS_DIALOG)
                            showAchievementList.value = true
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.rumbleEvent.collect {
                    when (it) {
                        is RumbleEvent.RumbleStart -> emulatorRumbleManager.startRumbling()
                        RumbleEvent.RumbleStop -> emulatorRumbleManager.stopRumbling()
                    }
                }
            }
        }
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.emulatorState.collectLatest {
                    when (it) {
                        is EmulatorState.Uninitialized -> {
                            // binding.viewLayoutControls.isInvisible = true
                        }
                        EmulatorState.LoadingFirmware,
                        EmulatorState.LoadingRom -> {
                            // binding.viewLayoutControls.isInvisible = true
                        }
                        is EmulatorState.RunningRom,
                        is EmulatorState.RunningFirmware -> {
                            setupSustainedPerformanceMode()
                            setupFpsCounter()
                            backPressedCallback.isEnabled = true
                        }
                        is EmulatorState.RomLoadError -> {
                            showRomLoadErrorDialog()
                        }
                        is EmulatorState.FirmwareLoadError -> {
                            showFirmwareLoadErrorDialog(it)
                        }
                        is EmulatorState.RomNotFoundError -> {
                            showRomNotFoundDialog(it.romPath)
                        }
                    }
                }
            }
        }
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                WindowInfoTracker.getOrCreate(this@EmulatorActivity).windowLayoutInfo(this@EmulatorActivity).collect {
                    val folds = it.displayFeatures.mapNotNull {
                        if (it is FoldingFeature) {
                            ScreenFold(
                                orientation = if (it.orientation == FoldingFeature.Orientation.HORIZONTAL) Orientation.LANDSCAPE else Orientation.PORTRAIT,
                                type = if (it.isSeparating) ScreenFold.FoldType.SEAMLESS else ScreenFold.FoldType.GAP,
                                foldBounds = Rect(it.bounds.left, it.bounds.top, it.bounds.width(), it.bounds.height())
                            )
                        } else {
                            null
                        }
                    }
                    viewModel.setScreenFolds(folds)
                }
            }
        }
        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                appForegroundStateObserver.onAppMovedToBackgroundEvent.collect {
                    presentation?.dismiss()
                    presentation = null
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        updateDisplays()
        displayManager.registerDisplayListener(displayListener, null)
        connectedControllerManager.startTrackingControllers()
        // frameRenderCoordinator.addSurface(binding.surfaceMain) - Handled in Compose
    }

    private fun updateDisplays() {
        val currentDisplay = ContextCompat.getDisplayOrDefault(this)
        val secondaryDisplay = secondaryDisplaySelector.getSecondaryDisplay(this)

        val displays = deviceLayoutDisplayMapper.mapDisplaysToLayoutDisplays(currentDisplay, secondaryDisplay)
        viewModel.setConnectedDisplays(displays)

        showExternalDisplay(secondaryDisplay)
    }

    private fun showExternalDisplay(secondaryDisplay: Display?) {
        if (presentation?.display?.displayId == secondaryDisplay?.displayId) {
            return
        }

        presentation?.dismiss()
        presentation = null

        if (secondaryDisplay != null) {
            presentation = ExternalPresentation(
                context = this,
                display = secondaryDisplay,
                frameRenderCoordinator = frameRenderCoordinator,
            ).apply {
                layoutView.apply {
                    setLayoutComponentViewBuilderFactory(RuntimeLayoutComponentViewBuilderFactory())
                    setFrontendInputHandler(frontendInputHandler)
                    setSystemInputHandler(melonTouchHandler)
                    viewModel.runtimeLayout.value?.let {
                        updateLayout(it)
                    }

                    setLayoutComponentToggleState(LayoutComponent.BUTTON_FAST_FORWARD_TOGGLE, frontendInputHandler.fastForwardEnabled)
                    setLayoutComponentToggleState(LayoutComponent.BUTTON_MICROPHONE_TOGGLE, frontendInputHandler.microphoneEnabled)
                    setConnectedControllersState(connectedControllerManager.controllersState.value)
                }

                updateRendererConfiguration(viewModel.runtimeRendererConfiguration.value)
                updateBackground(viewModel.secondaryScreenBackground.value)
                // if (binding.viewLayoutControls.areScreensSwapped()) {
                //     swapScreens()
                // }
                if (activeOverlays.hasActiveOverlays()) {
                    setPauseOverlayVisibility(true)
                }

                show()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        val launchArgs = LaunchArgs.fromIntent(intent)
        // Invalid arguments. Ignore completely
        if (launchArgs == null)
            return

        if (viewModel.emulatorState.value.isRunning()) {
            viewModel.pauseEmulator(false)

            activeOverlays.addActiveOverlay(EmulatorOverlay.SWITCH_NEW_ROM_DIALOG)
            AlertDialog.Builder(this)
                    .setTitle(getString(R.string.title_emulator_running))
                    .setMessage(getString(R.string.message_stop_emulation))
                    .setPositiveButton(R.string.ok) { _, _ ->
                        setIntent(intent)
                        viewModel.relaunchWithNewArgs(launchArgs)
                    }
                    .setNegativeButton(R.string.no) { dialog, _ ->
                        dialog.cancel()
                    }
                    .setOnDismissListener {
                        activeOverlays.removeActiveOverlay(EmulatorOverlay.SWITCH_NEW_ROM_DIALOG)
                    }
                    .setOnCancelListener {
                        viewModel.resumeEmulator()
                    }
                    .show()
        }
    }

    override fun onResume() {
        super.onResume()
        choreographerFrameRenderer.startRendering()

        if (!activeOverlays.hasActiveOverlays()) {
            disableScreenTimeOut()
            viewModel.resumeEmulator()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        setupFullscreen()
    }

    private fun setupFullscreen() {
        window.insetsControllerCompat?.let {
            it.hide(WindowInsetsCompat.Type.navigationBars())
            it.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    private fun setupSustainedPerformanceMode() {
        window.setSustainedPerformanceMode(viewModel.isSustainedPerformanceModeEnabled())
    }

    private fun setupFpsCounter() {
        val fpsCounterPosition = viewModel.getFpsCounterPosition()
        if (fpsCounterPosition == FpsCounterPosition.HIDDEN) {
            // binding.textFps.isGone = true
        } else {
            // binding.textFps.isVisible = true
            // ...
        }
    }

    private fun setupSoftInput(layoutConfiguration: RuntimeInputLayoutConfiguration?) {
        // Handled in Compose
    }

    private fun swapScreen() {
        // binding.viewLayoutControls.swapScreens()
        presentation?.swapScreens()

        updateRendererScreenAreas()
    }

    private fun updateRendererScreenAreas() {
        // Will be handled in EmulationScreen via Compose layout callbacks
    }

    private fun setupInputHandling(controllerConfiguration: ControllerConfiguration) {
        nativeInputListener = InputProcessor(controllerConfiguration, melonTouchHandler, frontendInputHandler)
    }

    private fun handleBackPressed() {
        if (isRewindWindowOpen()) {
            closeRewindWindow()
        } else {
            viewModel.pauseEmulator(true)
        }
    }

    private fun showPauseMenu(pauseMenu: PauseMenu) {
        val options = Array(pauseMenu.options.size) {
            getString(pauseMenu.options[it].textResource)
        }

        activeOverlays.addActiveOverlay(EmulatorOverlay.PAUSE_MENU)
        AlertDialog.Builder(this)
                .setTitle(R.string.pause)
                .setItems(options) { _, which ->
                    val selectedOption = pauseMenu.options[which]
                    viewModel.onPauseMenuOptionSelected(selectedOption)
                }
                .setOnDismissListener {
                    activeOverlays.removeActiveOverlay(EmulatorOverlay.PAUSE_MENU)
                }
                .setOnCancelListener {
                    viewModel.resumeEmulator()
                }
                .show()
    }

    private fun disableScreenTimeOut() {
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    private fun enableScreenTimeOut() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (!activeOverlays.hasActiveOverlays() && nativeInputListener.onKeyEvent(event))
            return true

        return super.dispatchKeyEvent(event)
    }

    override fun dispatchGenericMotionEvent(event: MotionEvent): Boolean {
        if (!activeOverlays.hasActiveOverlays() && nativeInputListener.onMotionEvent(event))
            return true

        return super.dispatchGenericMotionEvent(event)
    }

    private fun isRewindWindowOpen(): Boolean {
        return false // Will be handled in Compose state
    }

    private fun showSaveStateSlotsDialog(slots: List<SaveStateSlot>, onSlotPicked: (SaveStateSlot) -> Unit) {
        val dateFormatter = SimpleDateFormat("EEE, dd MMM yyyy", ConfigurationCompat.getLocales(resources.configuration)[0])
        val timeFormatter = SimpleDateFormat("kk:mm:ss", ConfigurationCompat.getLocales(resources.configuration)[0])
        var dialog: AlertDialog? = null
        var adapter: SaveStateAdapter? = null

        adapter = SaveStateAdapter(
            slots = slots,
            picasso = picasso,
            dateFormat = dateFormatter,
            timeFormat = timeFormatter,
            onSlotSelected = {
                dialog?.dismiss()
                onSlotPicked(it)
            },
            onDeletedSlot = {
                viewModel.deleteSaveStateSlot(it)?.let { newSlots ->
                    adapter?.updateSaveStateSlots(newSlots)
                }
            },
        )

        val recyclerView = RecyclerView(this).apply {
            val layoutManager = LinearLayoutManager(this@EmulatorActivity)
            this.layoutManager = layoutManager
            addItemDecoration(DividerItemDecoration(context, layoutManager.orientation))
            this.adapter = adapter
            descendantFocusability = ViewGroup.FOCUS_AFTER_DESCENDANTS
        }

        activeOverlays.addActiveOverlay(EmulatorOverlay.SAVE_STATES_DIALOG)

        dialog = AlertDialog.Builder(this)
            .setTitle(getString(R.string.save_slot))
            .setView(recyclerView)
            .setNegativeButton(R.string.cancel) { _dialog, _ ->
                _dialog.cancel()
            }
            .setOnDismissListener {
                activeOverlays.removeActiveOverlay(EmulatorOverlay.SAVE_STATES_DIALOG)
            }
            .setOnCancelListener {
                viewModel.resumeEmulator()
            }
            .show()
    }

    private fun showRomLoadErrorDialog() {
        activeOverlays.addActiveOverlay(EmulatorOverlay.ROM_LOAD_ERROR_DIALOG)
        AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle(R.string.error_load_rom)
            .setMessage(R.string.error_load_rom_message)
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .show()
    }

    private fun showRomNotFoundDialog(romPath: String) {
        activeOverlays.addActiveOverlay(EmulatorOverlay.ROM_NOT_FOUND_DIALOG)
        AlertDialog.Builder(this)
            .setTitle(R.string.error_rom_not_found)
            .setMessage(getString(R.string.error_rom_not_found_info, romPath))
            .setPositiveButton(R.string.ok) { _, _ ->
                finish()
            }
            .setOnDismissListener {
                finish()
            }
            .show()
    }

    private fun showFirmwareLoadErrorDialog(error: EmulatorState.FirmwareLoadError) {
        activeOverlays.addActiveOverlay(EmulatorOverlay.FIRMWARE_LOAD_ERROR_DIALOG)
        AlertDialog.Builder(this)
            .setCancelable(false)
            .setTitle(R.string.error_load_firmware)
            .setMessage(resources.getString(R.string.error_load_firmware_message, error.reason.toString()))
            .setPositiveButton(R.string.ok) { dialog, _ ->
                dialog.dismiss()
                finish()
            }
            .show()
    }

    private fun showRewindWindow(rewindWindow: RewindWindow) {
        activeOverlays.addActiveOverlay(EmulatorOverlay.REWIND_WINDOW)
        // binding.root.transitionToState(R.id.rewind_visible)
        rewindSaveStateAdapter.setRewindWindow(rewindWindow)
    }

    private fun closeRewindWindow() {
        activeOverlays.removeActiveOverlay(EmulatorOverlay.REWIND_WINDOW)
        // binding.root.transitionToState(R.id.rewind_hidden)
        viewModel.resumeEmulator()
    }

    private fun updateOrientation(configuration: Configuration) {
        val orientation = if (configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            Orientation.PORTRAIT
        } else {
            Orientation.LANDSCAPE
        }
        viewModel.setSystemOrientation(orientation)
    }

    override fun onPause() {
        super.onPause()
        enableScreenTimeOut()
        choreographerFrameRenderer.stopRendering()
        viewModel.pauseEmulator(false)
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        updateOrientation(newConfig)
        // There is an issue in which, after moving the app to a different display, the app reports that it is still running on the previous display. Adding a frame of delay
        // seems to fix the problem.
        handler.post {
            updateDisplays()
        }
    }

    override fun onStop() {
        super.onStop()
        getSystemService<InputManager>()?.unregisterInputDeviceListener(connectedControllerManager)
        connectedControllerManager.stopTrackingControllers()
        // frameRenderCoordinator.removeSurface(binding.surfaceMain) - Handled in Compose
    }

    override fun onDestroy() {
        super.onDestroy()
        frameRenderCoordinator.stop()
        presentation?.dismiss()
        displayManager.unregisterDisplayListener(displayListener)
    }
}