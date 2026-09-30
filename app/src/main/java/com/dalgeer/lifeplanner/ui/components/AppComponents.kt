package com.dalgeer.lifeplanner.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.dalgeer.lifeplanner.ui.theme.*

// 1. Reusable Rounded Floating Action Button with Ambient & Spot Shadows
@Composable
fun AppFloatingActionButton(
    onClick: () -> Unit,
    icon: ImageVector = Icons.Default.Add,
    contentDescription: String = "Add",
    backgroundColor: Color = PrimaryPurple,
    tint: Color = Color.White,
    isRoundedSquare: Boolean = true
) {
    val shape = if (isRoundedSquare) RoundedCornerShape(18.dp) else CircleShape
    Box(
        modifier = Modifier
            .size(56.dp)
            .shadow(
                elevation = 16.dp,
                shape = shape,
                ambientColor = backgroundColor.copy(alpha = 0.5f),
                spotColor = backgroundColor.copy(alpha = 0.5f)
            )
            .clip(shape)
            .background(backgroundColor)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(28.dp)
        )
    }
}
