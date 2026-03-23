package com.drasticds.emulator.ui.romlist.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.drasticds.emulator.domain.model.rom.Rom

@Composable
fun RomGrid(
    roms: List<Rom>,
    onRomClick: (Rom) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 140.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(roms) { rom ->
            RomCard(rom = rom, isGridView = true, onClick = { onRomClick(rom) })
        }
    }
}

@Composable
fun RomList(
    roms: List<Rom>,
    onRomClick: (Rom) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(roms) { rom ->
            RomCard(rom = rom, isGridView = false, onClick = { onRomClick(rom) })
        }
    }
}
