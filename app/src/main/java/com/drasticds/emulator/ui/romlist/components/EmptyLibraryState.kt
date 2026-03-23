package com.drasticds.emulator.ui.romlist.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.drasticds.emulator.R
import com.drasticds.emulator.ui.theme.Primary

@Composable
fun EmptyLibraryState(
    onAddRomClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // App icon centred
        Icon(
            imageVector = Icons.Default.Info, // Placeholder for app icon
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = Primary
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Text(
            text = "Add your DS game files to get started",
            style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onBackground
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "Tap the + button to load a game file from your device",
            style = MaterialTheme.typography.body2,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colors.onBackground.copy(alpha = 0.6f)
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Button(
            onClick = onAddRomClick,
            shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(backgroundColor = Primary),
            contentPadding = PaddingValues(horizontal = 32.dp, vertical = 12.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Add ROM", color = MaterialTheme.colors.onPrimary)
        }
    }
}
