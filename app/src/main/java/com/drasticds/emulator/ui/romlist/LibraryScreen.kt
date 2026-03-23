package com.drasticds.emulator.ui.romlist

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.drasticds.emulator.R
import com.drasticds.emulator.domain.model.rom.Rom
import com.drasticds.emulator.ui.romlist.components.EmptyLibraryState
import com.drasticds.emulator.ui.romlist.components.RomGrid
import com.drasticds.emulator.ui.romlist.components.RomList

@Composable
fun LibraryScreen(
    viewModel: RomListViewModel,
    onRomClick: (Rom) -> Unit,
    onSettingsClick: () -> Unit,
    onAboutClick: () -> Unit,
    onAddRomClick: () -> Unit
) {
    val roms by viewModel.roms.collectAsState()
    val hasDirectories by viewModel.hasSearchDirectories.collectAsState(initial = false)
    val isGridView by viewModel.isGridView.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("DrasticDS") },
                actions = {
                    IconButton(onClick = { /* Search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { viewModel.toggleGridView(!isGridView) }) {
                        Icon(
                            if (isGridView) Icons.Default.ViewList else Icons.Default.ViewModule,
                            contentDescription = "Toggle View"
                        )
                    }
                    IconButton(onClick = { /* Sort */ }) {
                        Icon(Icons.Default.Sort, contentDescription = "Sort")
                    }
                },
                backgroundColor = MaterialTheme.colors.primary,
                contentColor = MaterialTheme.colors.onPrimary
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddRomClick,
                backgroundColor = MaterialTheme.colors.secondary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add ROM")
            }
        },
        bottomBar = {
            BottomNavigation(
                backgroundColor = MaterialTheme.colors.surface,
                contentColor = MaterialTheme.colors.primary
            ) {
                BottomNavigationItem(
                    icon = { Icon(Icons.Default.LibraryBooks, contentDescription = null) },
                    label = { Text("Library") },
                    selected = true,
                    onClick = { /* Already here */ }
                )
                BottomNavigationItem(
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("Settings") },
                    selected = false,
                    onClick = onSettingsClick
                )
                BottomNavigationItem(
                    icon = { Icon(Icons.Default.Info, contentDescription = null) },
                    label = { Text("About") },
                    selected = false,
                    onClick = onAboutClick
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            if (!hasDirectories || (roms != null && roms!!.isEmpty())) {
                EmptyLibraryState(onAddRomClick)
            } else if (roms == null) {
                // Loading state
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = androidx.compose.ui.Alignment.Center) {
                    CircularProgressIndicator(color = MaterialTheme.colors.primary)
                }
            } else {
                if (isGridView) {
                    RomGrid(roms!!, onRomClick)
                } else {
                    RomList(roms!!, onRomClick)
                }
            }
        }
    }
}
