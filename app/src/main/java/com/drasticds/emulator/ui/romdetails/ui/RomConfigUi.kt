package com.drasticds.emulator.ui.romdetails.ui

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.drasticds.emulator.R
import com.drasticds.emulator.common.Permission
import com.drasticds.emulator.common.contracts.FilePickerContract
import com.drasticds.emulator.domain.model.rom.Rom
import com.drasticds.emulator.domain.model.rom.config.RomConfig
import com.drasticds.emulator.domain.model.rom.config.RuntimeConsoleType
import com.drasticds.emulator.domain.model.rom.config.RuntimeMicSource
import com.drasticds.emulator.ui.common.MelonPreviewSet
import com.drasticds.emulator.ui.common.component.dialog.TextInputDialog
import com.drasticds.emulator.ui.common.component.dialog.rememberTextInputDialogState
import com.drasticds.emulator.ui.common.preference.ActionLauncherItem
import com.drasticds.emulator.ui.common.preference.SingleChoiceItem
import com.drasticds.emulator.ui.layouts.LayoutSelectorActivity
import com.drasticds.emulator.ui.romdetails.model.RomConfigUiModel
import com.drasticds.emulator.ui.romdetails.model.RomConfigUiState
import com.drasticds.emulator.ui.romdetails.model.RomConfigUpdateEvent
import com.drasticds.emulator.ui.romdetails.model.RomGbaSlotConfigUiModel
import com.drasticds.emulator.ui.theme.DrasticDSTheme
import java.util.Date
import java.util.UUID

@Composable
fun RomConfigUi(
    modifier: Modifier,
    contentPadding: PaddingValues,
    rom: Rom,
    romConfigUiState: RomConfigUiState,
    onConfigUpdate: (RomConfigUpdateEvent) -> Unit,
) {
    when (romConfigUiState) {
        is RomConfigUiState.Loading -> Loading(modifier.padding(contentPadding))
        is RomConfigUiState.Ready -> Content(
            modifier = modifier,
            contentPadding = contentPadding,
            rom = rom,
            romConfig = romConfigUiState.romConfigUiModel,
            onConfigUpdate = onConfigUpdate,
        )
    }
}

@Composable
private fun Loading(modifier: Modifier) {
    Box(modifier) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = MaterialTheme.colors.onSurface,
        )
    }
}

@Composable
private fun Content(
    modifier: Modifier,
    contentPadding: PaddingValues,
    rom: Rom,
    romConfig: RomConfigUiModel,
    onConfigUpdate: (RomConfigUpdateEvent) -> Unit,
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
            .padding(
                start = contentPadding.calculateStartPadding(LocalLayoutDirection.current),
                end = contentPadding.calculateEndPadding(LocalLayoutDirection.current),
            ),
    ) {
        val renameDialogState = rememberTextInputDialogState()
        ActionLauncherItem(
            name = stringResource(id = R.string.label_rom_config_custom_name),
            value = romConfig.customName ?: rom.name,
            onLaunchAction = {
                renameDialogState.show(
                    initialText = romConfig.customName ?: rom.name,
                    onConfirm = { newName -> onConfigUpdate(RomConfigUpdateEvent.CustomNameUpdate(newName.ifBlank { null })) },
                )
            }
        )
        TextInputDialog(
            title = stringResource(id = R.string.label_rom_config_custom_name),
            dialogState = renameDialogState,
            textValidator = { true },
            onDelete = {
                onConfigUpdate(RomConfigUpdateEvent.CustomNameUpdate(null))
            },
        )

        val consoleOptions = stringArrayResource(id = R.array.game_runtime_console_type_options)
        SingleChoiceItem(
            name = stringResource(id = R.string.label_rom_config_console),
            value = consoleOptions[romConfig.runtimeConsoleType.ordinal],
            items = consoleOptions.toList(),
            selectedItemIndex = romConfig.runtimeConsoleType.ordinal,
            onItemSelected = {
                onConfigUpdate(RomConfigUpdateEvent.RuntimeConsoleUpdate(RuntimeConsoleType.entries[it]))
            },
            enabled = !rom.isDsiWareTitle,
        )

        val micSourceOptions = stringArrayResource(id = R.array.game_runtime_mic_source_options)
        SingleChoiceItem(
            name = stringResource(id = R.string.microphone_source),
            value = micSourceOptions[romConfig.runtimeMicSource.ordinal],
            items = micSourceOptions.toList(),
            selectedItemIndex = romConfig.runtimeMicSource.ordinal,
            onItemSelected = {
                onConfigUpdate(RomConfigUpdateEvent.RuntimeMicSourceUpdate(RuntimeMicSource.entries[it]))
            }
        )

        val context = LocalContext.current
        val layoutSelectorLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val layoutId = result.data?.getStringExtra(LayoutSelectorActivity.KEY_SELECTED_LAYOUT_ID)?.let { UUID.fromString(it) }
                onConfigUpdate(RomConfigUpdateEvent.LayoutUpdate(layoutId))
            }
        }
        ActionLauncherItem(
            name = stringResource(id = R.string.controller_layout),
            value = romConfig.layoutName ?: stringResource(id = R.string.use_global_layout),
            onLaunchAction = {
                val intent = Intent(context, LayoutSelectorActivity::class.java).apply {
                    putExtra(LayoutSelectorActivity.KEY_SELECTED_LAYOUT_ID, romConfig.layoutId?.toString())
                }
                layoutSelectorLauncher.launch(intent)
            }
        )

        val gbaSlotOptions = stringArrayResource(id = R.array.gba_slot_options)
        SingleChoiceItem(
            name = stringResource(id = R.string.label_rom_config_gba_slot),
            value = gbaSlotOptions[romConfig.gbaSlotConfig.type.ordinal],
            items = gbaSlotOptions.toList(),
            selectedItemIndex = romConfig.gbaSlotConfig.type.ordinal,
            onItemSelected = {
                onConfigUpdate(RomConfigUpdateEvent.GbaSlotTypeUpdated(RomGbaSlotConfigUiModel.Type.entries[it]))
            }
        )

        val gbaRomSelectorLauncher = rememberLauncherForActivityResult(FilePickerContract(Permission.READ)) { result ->
            if (result != null) {
                onConfigUpdate(RomConfigUpdateEvent.GbaRomPathUpdate(result))
            }
        }
        val gbaSaveSelectorLauncher = rememberLauncherForActivityResult(FilePickerContract(Permission.READ_WRITE)) { result ->
            if (result != null) {
                onConfigUpdate(RomConfigUpdateEvent.GbaSavePathUpdate(result))
            }
        }

        AnimatedVisibility(visible = romConfig.gbaSlotConfig.type == RomGbaSlotConfigUiModel.Type.GbaRom) {
            Column {
                ActionLauncherItem(
                    name = stringResource(id = R.string.label_rom_config_gba_rom_path),
                    value = romConfig.gbaSlotConfig.gbaRomPath ?: stringResource(id = R.string.not_set),
                    enabled = true,
                    onLaunchAction = {
                        gbaRomSelectorLauncher.launch(Pair(null, null))
                    }
                )

                ActionLauncherItem(
                    name = stringResource(id = R.string.label_rom_config_gba_save_path),
                    value = romConfig.gbaSlotConfig.gbaSavePath ?: stringResource(id = R.string.not_set),
                    enabled = true,
                    onLaunchAction = {
                        gbaSaveSelectorLauncher.launch(Pair(null, null))
                    }
                )
            }
        }

        Spacer(Modifier.height(contentPadding.calculateBottomPadding()))
    }
}

@MelonPreviewSet
@Composable
private fun PreviewRomConfigUi() {
    DrasticDSTheme {
        RomConfigUi(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(0.dp),
            rom = Rom(
                name = "Professor Layton and the Unwound Future",
                developerName = "Nontendo",
                fileName = "layton.nds",
                uri = Uri.EMPTY,
                parentTreeUri = Uri.EMPTY,
                config = RomConfig(),
                lastPlayed = Date(),
                isDsiWareTitle = false,
                retroAchievementsHash = "",
            ),
            romConfigUiState = RomConfigUiState.Ready(
                RomConfigUiModel(
                    layoutName = "Default",
                    gbaSlotConfig = RomGbaSlotConfigUiModel(type = RomGbaSlotConfigUiModel.Type.GbaRom)
                ),
            ),
            onConfigUpdate = { },
        )
    }
}