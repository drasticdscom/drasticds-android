package com.drasticds.emulator.ui.layouts

import android.graphics.Color
import android.os.Bundle
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint
import com.drasticds.emulator.ui.layouts.ui.LayoutsScreen
import com.drasticds.emulator.ui.layouts.viewmodel.LayoutsViewModel
import com.drasticds.emulator.ui.theme.DrasticDSTheme

@AndroidEntryPoint
class LayoutListActivity : AppCompatActivity() {

    private val viewModel: LayoutsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.auto(Color.TRANSPARENT, Color.TRANSPARENT),
        )
        super.onCreate(savedInstanceState)
        setContent {
            DrasticDSTheme {
                LayoutsScreen(
                    viewModel = viewModel,
                    onNavigateBack = ::finish,
                )
            }
        }
    }
}