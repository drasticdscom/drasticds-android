package com.drasticds.emulator.ui.dsiwaremanager

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import com.drasticds.emulator.ui.dsiwaremanager.ui.DSiWareManagerScreen
import com.drasticds.emulator.ui.theme.DrasticDSTheme

@AndroidEntryPoint
class DSiWareManagerActivity : AppCompatActivity() {

    private val viewModel by viewModels<DSiWareManagerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)

        setContent {
            DrasticDSTheme {
                DSiWareManagerScreen(
                    viewModel = viewModel,
                    onBackClick = { onSupportNavigateUp() },
                )
            }
        }
    }
}