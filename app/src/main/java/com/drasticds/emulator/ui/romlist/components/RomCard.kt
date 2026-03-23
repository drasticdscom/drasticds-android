package com.drasticds.emulator.ui.romlist.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.drasticds.emulator.domain.model.rom.Rom
import com.drasticds.emulator.ui.theme.Primary

@Composable
fun RomCard(
    rom: Rom,
    isGridView: Boolean,
    onClick: () -> Unit
) {
    if (isGridView) {
        RomGridItem(rom, onClick)
    } else {
        RomListItem(rom, onClick)
    }
}

@Composable
private fun RomGridItem(rom: Rom, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp,
        backgroundColor = MaterialTheme.colors.surface
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(MaterialTheme.colors.onSurface.copy(alpha = 0.05f)),
                contentAlignment = Alignment.Center
            ) {
                // Placeholder for now, later integrate with Thumbnail provider
                Icon(
                    imageVector = Icons.Default.SdCard,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Primary
                )
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = rom.name,
                    style = MaterialTheme.typography.body1.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colors.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Last played: ${rom.lastPlayed ?: "Never"}",
                    style = MaterialTheme.typography.caption,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

@Composable
private fun RomListItem(rom: Rom, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colors.onSurface.copy(alpha = 0.05f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.SdCard,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = Primary
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = rom.name,
                style = MaterialTheme.typography.body1.copy(fontWeight = FontWeight.Bold),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colors.onSurface
            )
            Text(
                text = rom.fileName,
                style = MaterialTheme.typography.caption,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
            Text(
                text = "Last played: ${rom.lastPlayed ?: "Never"}",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
            )
        }
        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = MaterialTheme.colors.onSurface.copy(alpha = 0.3f)
        )
    }
}
