package com.dalgeer.lifeplanner.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dalgeer.lifeplanner.R
import com.dalgeer.lifeplanner.ui.theme.*

data class LanguageItemModel(
    val name: String,
    val nativeName: String,
    val flagResId: Int
)

@Composable
fun LanguageScreen(onNext: () -> Unit) {
    val context = LocalContext.current
    var selectedLanguage by remember {
        mutableStateOf(com.dalgeer.lifeplanner.data.preferences.AppPreferences.getSelectedLanguage(context))
    }

    val languages = listOf(
        LanguageItemModel("English", "English", R.drawable.ic_flag_english),
        LanguageItemModel("Hindi", "हिन्दी", R.drawable.ic_flag_hindi),
        LanguageItemModel("Urdu", "اردو", R.drawable.ic_flag_urdu),
        LanguageItemModel("Arabic", "العربية", R.drawable.ic_flag_arabic),
        LanguageItemModel("Spanish", "Español", R.drawable.ic_flag_spanish),
        LanguageItemModel("French", "Français", R.drawable.ic_flag_french),
        LanguageItemModel("German", "Deutsch", R.drawable.ic_flag_german),
        LanguageItemModel("Turkish", "Türkçe", R.drawable.ic_flag_turkish),
        LanguageItemModel("Portuguese", "Português", R.drawable.ic_flag_portuguese),
        LanguageItemModel("Italian", "Italiano", R.drawable.ic_flag_italian),
        LanguageItemModel("Russian", "Русский", R.drawable.ic_flag_russian),
        LanguageItemModel("Chinese", "中文", R.drawable.ic_flag_chinese),
        LanguageItemModel("Japanese", "日本語", R.drawable.ic_flag_japanese)
    )

    Scaffold(
        containerColor = ScreenBackground,
        modifier = Modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = PaddingLarge, vertical = 12.dp)
        ) {
            // Header
            Text(
                text = "Language",
                style = Typography.headlineLarge,
                color = TextDark,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Choose your preferred language. Fully localized in English, Hindi, Urdu, Arabic, Spanish, French, German, Turkish, Portuguese, Italian, Russian, Chinese, and Japanese.",
                style = Typography.bodyMedium,
                color = TextGray
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Select Language Section Header + Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select Language",
                    style = Typography.titleLarge,
                    color = TextDark,
                    fontWeight = FontWeight.Bold
                )
                Surface(

                    color = LightPurple,
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "${languages.size} Languages",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = Typography.labelMedium,
                        color = PrimaryPurple,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Language Cards List
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 8.dp)
            ) {
                items(languages) { lang ->
                    val isSelected = selectedLanguage == lang.name
                    LanguageCardItem(
                        language = lang,
                        isSelected = isSelected,
                        onClick = { selectedLanguage = lang.name }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Continue Button
            Button(
                onClick = {
                    com.dalgeer.lifeplanner.util.LocaleHelper.applyLocale(context, selectedLanguage)
                    onNext()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(20.dp),
                        ambientColor = PrimaryPurple.copy(alpha = 0.4f),
                        spotColor = PrimaryPurple.copy(alpha = 0.4f)
                    )
                    .height(54.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryPurple)
            ) {
                Text("Continue", style = Typography.titleMedium, color = White, fontWeight = FontWeight.Bold)
                Spacer(Modifier.width(12.dp))
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, modifier = Modifier.size(20.dp), tint = White)
            }
        }
    }
}

@Composable
fun LanguageCardItem(
    language: LanguageItemModel,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = White,
        border = BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) PrimaryPurple else DividerColor
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Flag Icon
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = language.flagResId),
                    contentDescription = language.name,
                    modifier = Modifier.fillMaxSize()
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Language Names
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = language.name,
                    style = Typography.titleMedium,
                    color = TextDark,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = language.nativeName,
                    style = Typography.bodyMedium,
                    color = TextGray
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Right Radio Selection Icon
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .border(
                        width = 2.dp,
                        color = if (isSelected) PrimaryPurple else Color.LightGray,
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(PrimaryPurple)
                    )
                }
            }
        }
    }
}
