package com.drasticds.emulator.ui.romlist

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import com.drasticds.emulator.ui.emulator.EmulatorActivity
import com.drasticds.emulator.ui.settings.SettingsActivity
import com.drasticds.emulator.ui.theme.DrasticDSTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RomListActivity : ComponentActivity() {

    private val viewModel: RomListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.enableEdgeToEdge(window)

        setContent {
            DrasticDSTheme {
                val searchDirectoryLauncher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.OpenDocumentTree()
                ) { uri: Uri? ->
                    uri?.let { viewModel.addRomSearchDirectory(it) }
                }

                LibraryScreen(
                    viewModel = viewModel,
                    onRomClick = { rom ->
                        val intent = EmulatorActivity.getRomEmulatorActivityIntent(this, rom)
                        startActivity(intent)
                    },
                    onSettingsClick = {
                        val intent = Intent(this, SettingsActivity::class.java)
                        startActivity(intent)
                    },
                    onAboutClick = {
                        // TODO: Implement AboutScreen
                    },
                    onAddRomClick = {
                        searchDirectoryLauncher.launch(null)
                    }
                )
            }
        }
    }
}