package com.drasticds.emulator.ui.romdetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.drasticds.emulator.common.Permission
import com.drasticds.emulator.common.UriPermissionManager
import com.drasticds.emulator.domain.model.rom.Rom
import com.drasticds.emulator.domain.model.rom.config.RomConfig
import com.drasticds.emulator.domain.model.rom.config.RomGbaSlotConfig
import com.drasticds.emulator.domain.repositories.RomsRepository
import com.drasticds.emulator.domain.repositories.SettingsRepository
import com.drasticds.emulator.impl.RomIconProvider
import com.drasticds.emulator.parcelables.RomParcelable
import com.drasticds.emulator.ui.romdetails.model.RomConfigUiState
import com.drasticds.emulator.ui.romdetails.model.RomConfigUpdateEvent
import com.drasticds.emulator.ui.romdetails.model.RomGbaSlotConfigUiModel
import com.drasticds.emulator.ui.romlist.RomIcon
import javax.inject.Inject

@HiltViewModel
class RomDetailsViewModel @Inject constructor(
    private val romDetailsUiMapper: RomDetailsUiMapper,
    private val romsRepository: RomsRepository,
    private val settingsRepository: SettingsRepository,
    private val romIconProvider: RomIconProvider,
    private val uriPermissionManager: UriPermissionManager,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _rom = MutableStateFlow(savedStateHandle.get<RomParcelable>(RomDetailsActivity.KEY_ROM)!!.rom)
    val rom = _rom.asStateFlow()

    private val _romConfig = MutableStateFlow(_rom.value.config)

    val romConfigUiState by lazy {
        val uiStateFlow = MutableStateFlow<RomConfigUiState>(RomConfigUiState.Loading)
        viewModelScope.launch {
            _romConfig.map {
                romDetailsUiMapper.mapRomConfigToUi(it)
            }.collect {
                uiStateFlow.value = RomConfigUiState.Ready(it)
            }
        }

        uiStateFlow.asStateFlow()
    }

    fun onRomConfigUpdateEvent(event: RomConfigUpdateEvent) {
        val currentRomConfig = _romConfig.value
        val newRomConfig = when(event) {
            is RomConfigUpdateEvent.RuntimeConsoleUpdate -> currentRomConfig.copy(runtimeConsoleType = event.newRuntimeConsole)
            is RomConfigUpdateEvent.RuntimeMicSourceUpdate -> currentRomConfig.copy(runtimeMicSource = event.newRuntimeMicSource)
            is RomConfigUpdateEvent.LayoutUpdate -> currentRomConfig.copy(layoutId = event.newLayoutId)
            is RomConfigUpdateEvent.GbaSlotTypeUpdated -> currentRomConfig.let {
                val newGbaSlotConfig = when (event.type) {
                    RomGbaSlotConfigUiModel.Type.None -> RomGbaSlotConfig.None
                    RomGbaSlotConfigUiModel.Type.GbaRom -> RomGbaSlotConfig.GbaRom(null, null)
                    RomGbaSlotConfigUiModel.Type.RumblePak -> RomGbaSlotConfig.RumblePak
                    RomGbaSlotConfigUiModel.Type.MemoryExpansion -> RomGbaSlotConfig.MemoryExpansion
                }
                it.copy(gbaSlotConfig = newGbaSlotConfig)
            }
            is RomConfigUpdateEvent.GbaRomPathUpdate -> currentRomConfig.let {
                (currentRomConfig.gbaSlotConfig as? RomGbaSlotConfig.GbaRom)?.let { gbaConfig ->
                    it.copy(gbaSlotConfig = gbaConfig.copy(romPath = event.gbaRomPath))
                }
            }
            is RomConfigUpdateEvent.GbaSavePathUpdate -> currentRomConfig.let {
                (currentRomConfig.gbaSlotConfig as? RomGbaSlotConfig.GbaRom)?.let { gbaConfig ->
                    it.copy(gbaSlotConfig = gbaConfig.copy(savePath = event.gbaSavePath))
                }
            }
            is RomConfigUpdateEvent.CustomNameUpdate -> currentRomConfig.copy(customName = event.customName)
        }

        newRomConfig?.let { newConfig ->
            _romConfig.value = newConfig
            _rom.update { it.copy(config = newConfig) }
            saveRomConfig(newConfig)
        }
    }

    suspend fun getRomIcon(rom: Rom): RomIcon {
        val romIconBitmap = romIconProvider.getRomIcon(rom)
        val iconFiltering = settingsRepository.getRomIconFiltering()
        return RomIcon(romIconBitmap, iconFiltering)
    }

    private fun saveRomConfig(newConfig: RomConfig) {
        if (newConfig.gbaSlotConfig is RomGbaSlotConfig.GbaRom) {
            newConfig.gbaSlotConfig.romPath?.let { uriPermissionManager.persistFilePermissions(it, Permission.READ) }
            newConfig.gbaSlotConfig.savePath?.let { uriPermissionManager.persistFilePermissions(it, Permission.READ_WRITE) }
        }
        romsRepository.updateRomConfig(_rom.value, newConfig)
    }
}