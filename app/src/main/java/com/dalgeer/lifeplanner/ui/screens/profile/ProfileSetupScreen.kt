package com.dalgeer.lifeplanner.ui.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dalgeer.lifeplanner.data.preferences.AppPreferences
import com.dalgeer.lifeplanner.ui.theme.*

data class AvatarStyle(
    val id: Int,
    val bgColor: Color,
    val textColor: Color,
    val label: String
)

val AVATAR_PRESETS = listOf(
    AvatarStyle(0, Color(0xFFD0BCFF), Color(0xFF381E72), "Purple"),
    AvatarStyle(1, Color(0xFFC7D2FE), Color(0xFF1E1B4B), "Indigo"),
    AvatarStyle(2, Color(0xFFA7F3D0), Color(0xFF064E3B), "Emerald"),
    AvatarStyle(3, Color(0xFFFBCFE8), Color(0xFF831843), "Rose"),
    AvatarStyle(4, Color(0xFFFDE68A), Color(0xFF78350F), "Amber"),
    AvatarStyle(5, Color(0xFFA5F3FC), Color(0xFF164E63), "Cyan")
)

@Composable
fun ProfileSetupScreen(
    onComplete: () -> Unit,
    isEditMode: Boolean = false
) {
    val context = LocalContext.current
    var name by remember { mutableStateOf(AppPreferences.getUserName(context)) }
    var selectedAvatarIndex by remember { mutableIntStateOf(AppPreferences.getUserAvatarIndex(context)) }

    val currentAvatar = AVATAR_PRESETS.getOrElse(selectedAvatarIndex) { AVATAR_PRESETS[0] }
    val initial = if (name.isNotBlank()) name.first().uppercase() else "A"

    Scaffold(
        containerColor = ScreenBackground,
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = PaddingLarge, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isEditMode) "Edit Profile" else "Set Up Your Profile",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = TextDark
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Personalize your experience in LifePlanner.",
                fontSize = 14.sp,
                color = TextGray
            )

            Spacer(modifier = Modifier.height(28.dp))

            // Large Avatar Preview Circle
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = CircleShape,
                        ambientColor = currentAvatar.bgColor,
                        spotColor = currentAvatar.bgColor
                    )
                    .clip(CircleShape)
                    .background(currentAvatar.bgColor),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initial,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    color = currentAvatar.textColor
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Avatar Color Presets Selector
            Text(
                text = "Choose Avatar Style",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextGray
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AVATAR_PRESETS.forEach { preset ->
                    val isSelected = preset.id == selectedAvatarIndex
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(preset.bgColor)
                            .then(
                                if (isSelected) {
                                    Modifier.border(2.dp, PrimaryPurple, CircleShape)
                                } else Modifier
                            )
                            .clickable { selectedAvatarIndex = preset.id },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = preset.textColor,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                text = initial,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = preset.textColor
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Name Input Field
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text(
                    text = "Your Name",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter your name", color = TextGray) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Person,
                            contentDescription = null,
                            tint = PrimaryPurple
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = White,
                        unfocusedContainerColor = White,
                        focusedBorderColor = PrimaryPurple,
                        unfocusedBorderColor = DividerColor,
                        cursorColor = TextDark,
                        focusedTextColor = TextDark,
                        unfocusedTextColor = TextDark
                    )
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Save & Continue Button
            Button(
                onClick = {
                    val finalName = if (name.isNotBlank()) name.trim() else "Alex"
                    AppPreferences.setUserName(context, finalName)
                    AppPreferences.setUserAvatarIndex(context, selectedAvatarIndex)
                    onComplete()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(16.dp),
                        ambientColor = PrimaryPurple.copy(alpha = 0.3f)
                    ),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (isEditMode) "Save Changes" else "Get Started",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = White
                )
            }
        }
    }
}
