package com.drasticds.emulator.ui.romdetails

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.net.toUri
import dagger.hilt.android.AndroidEntryPoint
import com.drasticds.emulator.domain.model.ConsoleType
import com.drasticds.emulator.domain.model.rom.Rom
import com.drasticds.emulator.ui.common.rom.EmulatorLaunchValidatorDelegate
import com.drasticds.emulator.ui.emulator.EmulatorActivity
import com.drasticds.emulator.ui.romdetails.ui.RomDetailsScreen
import com.drasticds.emulator.ui.theme.DrasticDSTheme

@AndroidEntryPoint
class RomDetailsActivity : AppCompatActivity() {

    companion object {
        const val KEY_ROM = "rom"
    }

    private val romDetailsViewModel by viewModels<RomDetailsViewModel>()
    private val romRetroAchievementsViewModel by viewModels<RomDetailsRetroAchievementsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        val emulatorLauncherValidatorDelegate = EmulatorLaunchValidatorDelegate(this, object : EmulatorLaunchValidatorDelegate.Callback {
            override fun onRomValidated(rom: Rom) {
                val intent = EmulatorActivity.getRomEmulatorActivityIntent(this@RomDetailsActivity, rom)
                startActivity(intent)
            }

            override fun onFirmwareValidated(consoleType: ConsoleType) {
                // Do nothing (can't launch firmware fro this screen)
            }

            override fun onValidationAborted() {
                // Do nothing
            }
        })

        setContent {
            val rom by romDetailsViewModel.rom.collectAsState()
            val romConfig by romDetailsViewModel.romConfigUiState.collectAsState()

            val retroAchievementsUiState by romRetroAchievementsViewModel.uiState.collectAsState()

            LaunchedEffect(null) {
                romRetroAchievementsViewModel.viewAchievementEvent.collect {
                    launchViewAchievementIntent(it)
                }
            }

            DrasticDSTheme {
                RomDetailsScreen(
                    rom = rom,
                    romConfigUiState = romConfig,
                    retroAchievementsUiState = retroAchievementsUiState,
                    onNavigateBack = { onNavigateUp() },
                    onLaunchRom = {
                        emulatorLauncherValidatorDelegate.validateRom(it)
                    },
                    onRomConfigUpdate = {
                        romDetailsViewModel.onRomConfigUpdateEvent(it)
                    },
                    onRetroAchievementsLogin = { username, password ->
                        romRetroAchievementsViewModel.login(username, password)
                    },
                    onRetroAchievementsRetryLoad = {
                        romRetroAchievementsViewModel.retryLoadAchievements()
                    },
                    onViewAchievement = {
                        romRetroAchievementsViewModel.viewAchievement(it)
                    }
                )
            }
        }
    }

    private fun launchViewAchievementIntent(achievementUrl: String) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = achievementUrl.toUri()
        }
        startActivity(intent)
    }
}