package com.drasticds.emulator.ui.emulator.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import androidx.compose.ui.draw.clip
import com.drasticds.emulator.domain.model.SaveStateSlot
import com.drasticds.emulator.ui.theme.Primary
import java.io.File

@Composable
fun PauseMenu(
    slots: List<SaveStateSlot>,
    onSave: (SaveStateSlot) -> Unit,
    onLoad: (SaveStateSlot) -> Unit,
    onResume: () -> Unit,
    onExit: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.85f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PAUSED",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                IconButton(onClick = onResume) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            Text(
                text = "Save State Slots",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(slots) { slot ->
                    SaveSlotCard(slot, onSave, onLoad)
                }
            }
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                PauseActionButton(
                    text = "RESUME",
                    icon = Icons.Default.PlayArrow,
                    onClick = onResume,
                    primary = true
                )
                PauseActionButton(
                    text = "EXIT GAME",
                    icon = Icons.Default.Close,
                    onClick = onExit,
                    primary = false
                )
            }
        }
    }
}

@Composable
fun SaveSlotCard(
    slot: SaveStateSlot,
    onSave: (SaveStateSlot) -> Unit,
    onLoad: (SaveStateSlot) -> Unit
) {
    Card(
        modifier = Modifier.width(160.dp),
        shape = RoundedCornerShape(12.dp),
        backgroundColor = Color(0xFF1E293B),
        elevation = 4.dp
    ) {
        Column {
            Box(modifier = Modifier.height(100.dp).fillMaxWidth().background(Color.Black)) {
                if (slot.screenshot != null) {
                    AsyncImage(
                        model = slot.screenshot,
                        contentDescription = "Save slot thumbnail",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("EMPTY", color = Color.White.copy(alpha = 0.3f), fontSize = 12.sp)
                    }
                }
                
                Text(
                    text = "Slot ${slot.slot + 1}",
                    modifier = Modifier.align(Alignment.TopStart).padding(8.dp),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    style = androidx.compose.ui.text.TextStyle(
                        background = Color.Black.copy(alpha = 0.5f)
                    )
                )
            }
            
            Row(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Primary, RoundedCornerShape(4.dp))
                        .clickable { onSave(slot) }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("SAVE", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                        .clickable(enabled = slot.exists) { onLoad(slot) }
                        .padding(vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("LOAD", color = if (slot.exists) Color.White else Color.Gray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun PauseActionButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    primary: Boolean
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (primary) Primary else Color.White.copy(alpha = 0.1f))
            .clickable { onClick() }
            .padding(horizontal = 24.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}
