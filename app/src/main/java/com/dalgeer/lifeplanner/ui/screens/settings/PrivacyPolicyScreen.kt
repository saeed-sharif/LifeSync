package com.dalgeer.lifeplanner.ui.screens.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dalgeer.lifeplanner.ui.theme.*

@Composable
fun PrivacyPolicyScreen(
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ScreenBackground)
            .statusBarsPadding()
            .navigationBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = PaddingLarge, vertical = PaddingMedium)
    ) {
        // Top Bar Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(White)
                    .border(1.dp, DividerColor, CircleShape)
                    .clickable { onBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextDark,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = "Privacy Policy",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
                Text(
                    text = "LifeSync • Effective & Last Updated: 2026",
                    fontSize = 11.sp,
                    color = TextGray
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Privacy Highlight Banner Card
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 6.dp,
                    shape = RoundedCornerShape(20.dp),
                    ambientColor = PrimaryPurple.copy(alpha = 0.15f),
                    spotColor = PrimaryPurple.copy(alpha = 0.2f)
                ),
            shape = RoundedCornerShape(20.dp),
            color = White
        ) {
            Row(
                modifier = Modifier.padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(PrimaryPurple.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Security,
                        contentDescription = null,
                        tint = PrimaryPurple,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "100% Offline & Private",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextDark
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "LifeSync is designed as an offline-first productivity app. Your tasks, habits, and goals stay 100% on your device.",
                        fontSize = 12.sp,
                        color = TextGray,
                        lineHeight = 17.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Section 1: Local Storage
        PolicySectionItem(
            icon = Icons.Outlined.Storage,
            iconTint = Color(0xFF2563EB),
            title = "1. Local Data Storage & Security",
            description = "All tasks, routines, habits, goals, categories, and notes created in LifeSync are saved locally in an encrypted Room SQLite database on your device. We do not transmit, upload, or sync your data to any external cloud servers."
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Section 2: Alarms & System Permissions
        PolicySectionItem(
            icon = Icons.Outlined.Alarm,
            iconTint = Color(0xFFF57C00),
            title = "2. System Permissions & Alarms",
            description = "LifeSync requests specific system permissions to function reliably:\n" +
                    "• Post Notifications: To display task reminders.\n" +
                    "• Exact Alarms: To trigger alarm reminders exactly at scheduled times.\n" +
                    "• Receive Boot Completed: To reschedule pending alarms automatically after device restarts.\n" +
                    "• Battery Restrictions: To prevent OS battery saver from missing scheduled habit alarms."
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Section 3: Background Work & Habit Reset
        PolicySectionItem(
            icon = Icons.Outlined.Autorenew,
            iconTint = Color(0xFF00A878),
            title = "3. Daily Habit Reset & Background Work",
            description = "The app uses Android WorkManager to run a local daily reset at midnight. This process resets habit completion status for the new day and updates streak counters completely offline without accessing any internet network."
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Section 4: Profile & Preferences
        PolicySectionItem(
            icon = Icons.Outlined.Person,
            iconTint = PrimaryPurple,
            title = "4. Profile & Preferences",
            description = "Information you enter during setup (Name and Avatar choice) and app preferences (Theme and Language selection) are stored in local SharedPreferences. This data remains on your phone and is used solely to personalize your app experience."
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Section 5: Zero Analytics & Third-Party Sharing
        PolicySectionItem(
            icon = Icons.Outlined.Block,
            iconTint = Color(0xFFDC2626),
            title = "5. Zero Tracking & No Data Sharing",
            description = "LifeSync does not contain any third-party tracking tools, analytics SDKs, or advertising networks. We do not sell, rent, share, or monetize any of your personal productivity data."
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Section 6: Data Retention & Permanent Deletion
        PolicySectionItem(
            icon = Icons.Outlined.DeleteSweep,
            iconTint = Color(0xFF7C3AED),
            title = "6. Data Retention & Control",
            description = "You have full ownership of your data. Deleting a task, habit, or goal permanently removes it from your local database immediately. Uninstalling the app completely removes all saved LifeSync database files."
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Section 7: Updates to Policy
        PolicySectionItem(
            icon = Icons.Outlined.VerifiedUser,
            iconTint = Color(0xFF0284C7),
            title = "7. Policy Updates & Contact",
            description = "If we update our privacy practices, the changes will be reflected directly in this Privacy Policy screen. For any questions or suggestions regarding LifeSync, please contact our support team."
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Footer Card
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            color = PrimaryPurple.copy(alpha = 0.08f),
            border = BorderStroke(1.dp, PrimaryPurple.copy(alpha = 0.2f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = null,
                    tint = PrimaryPurple,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "LifeSync — Your Personal & Secure Life Companion",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = PrimaryPurple
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun PolicySectionItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    description: String
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = White,
        border = BorderStroke(1.dp, DividerColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(iconTint.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = iconTint,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDark
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = description,
                fontSize = 13.sp,
                color = TextGray,
                lineHeight = 19.sp
            )
        }
    }
}
