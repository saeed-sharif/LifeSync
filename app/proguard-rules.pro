# LifeSync ProGuard & R8 Optimization Rules

# Room Database Keep Rules
-keepclassmembers class * extends androidx.room.RoomDatabase {
    <init>();
}
-keep class * extends androidx.room.RoomDatabase
-dontwarn androidx.room.paging.**

# Kotlin Serialization Keep Rules
-keepattributes *Annotation*,ElementValuePairs*,Signature,InnerClasses,EnclosingMethod
-keepclassmembers class * {
    *** Companion;
}
-keepclassmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}

# Keep App Data Models & Entities
-keep class com.dalgeer.lifeplanner.data.model.** { *; }
-keep class com.dalgeer.lifeplanner.data.local.entities.** { *; }

# Google Play Core (In-App Update & Review) Keep Rules
-keep class com.google.android.play.core.** { *; }
-keep class com.google.android.gms.** { *; }
-dontwarn com.google.android.gms.common.annotation.NoNullnessRewrite
