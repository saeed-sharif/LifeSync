package com.dalgeer.lifeplanner.util

import android.content.Context
import android.content.res.Configuration
import com.dalgeer.lifeplanner.data.preferences.AppPreferences
import java.util.Locale

object LocaleHelper {

    fun getLanguageCode(languageName: String): String {
        return when (languageName.lowercase()) {
            "hindi", "हिन्दी" -> "hi"
            "urdu", "اردو" -> "ur"
            "arabic", "العربية" -> "ar"
            "spanish", "español" -> "es"
            "french", "français" -> "fr"
            "german", "deutsch" -> "de"
            "turkish", "türkçe" -> "tr"
            "portuguese", "português" -> "pt"
            "italian", "italiano" -> "it"
            "russian", "русский" -> "ru"
            "chinese", "中文" -> "zh"
            "japanese", "日本語" -> "ja"
            else -> "en"
        }
    }

    fun applyLocale(context: Context, languageName: String): Context {
        val languageCode = getLanguageCode(languageName)
        AppPreferences.setSelectedLanguage(context, languageName)

        val locale = Locale.Builder().setLanguage(languageCode).build()
        Locale.setDefault(locale)

        val resources = context.resources
        val config = Configuration(resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        return context.createConfigurationContext(config)
    }
}
