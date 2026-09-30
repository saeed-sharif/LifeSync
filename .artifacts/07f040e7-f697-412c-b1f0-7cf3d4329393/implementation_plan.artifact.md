# Implementation Plan - Navigation 3 & UI Integration

This plan details the steps to integrate Navigation 3 and implement the Splash, Language, Onboarding, and Home (Bottom Nav) screens as requested.

## User Review Required

> [!IMPORTANT]
> - Navigation 3 is a new API. I will be using the core `navigation3-runtime` and `navigation3-ui` libraries as per the official migration guide.
> - I will use a placeholder for the Language selection screen as specific designs weren't provided for it, but I'll ensure it fits the overall theme.
> - The UI will be recreated using Compose components to match the provided images as closely as possible.

## Proposed Changes

### Build Configuration

#### [MODIFY] [build.gradle.kts](file:///Users/blackcoder/AndroidStudioProjects/LifePlanner/app/build.gradle.kts)
- Add Navigation 3 dependencies (`androidx.navigation3:navigation3-runtime`, `androidx.navigation3:navigation3-ui`).
- Add Kotlin Serialization plugin and dependency for type-safe routes.
- Add Compose Foundation for Pager (Onboarding).
- Update `compileSdk` to 36+ as required by Navigation 3.

---

### Navigation Logic

#### [NEW] [Routes.kt](file:///Users/blackcoder/AndroidStudioProjects/LifePlanner/app/src/main/java/com/dalgeer/lifeplanner/navigation/Routes.kt)
- Define `NavKey` implementations for:
    - `Splash`
    - `Language`
    - `Onboarding`
    - `Main` (The Bottom Nav container)
    - `Home`, `Search`, `Profile` (Screens inside Bottom Nav)

#### [NEW] [NavigationState.kt](file:///Users/blackcoder/AndroidStudioProjects/LifePlanner/app/src/main/java/com/dalgeer/lifeplanner/navigation/NavigationState.kt)
- Implement `rememberNavigationState` and `Navigator` classes as per Navigation 3 best practices.
- This will handle both Root and Bottom navigation stacks.

---

### UI Components

#### [NEW] [SplashScreen.kt](file:///Users/blackcoder/AndroidStudioProjects/LifePlanner/app/src/main/java/com/dalgeer/lifeplanner/ui/screens/SplashScreen.kt)
- Implement the "DayPlan" splash screen with the checklist illustration and branding.

#### [NEW] [LanguageScreen.kt](file:///Users/blackcoder/AndroidStudioProjects/LifePlanner/app/src/main/java/com/dalgeer/lifeplanner/ui/screens/LanguageScreen.kt)
- A simple screen to choose a language, transitioning to Onboarding.

#### [NEW] [OnboardingScreen.kt](file:///Users/blackcoder/AndroidStudioProjects/LifePlanner/app/src/main/java/com/dalgeer/lifeplanner/ui/screens/OnboardingScreen.kt)
- Implement the 3-page onboarding flow using `HorizontalPager`.
- Pages: "Set goals", "Plan your day", "Track progress" with icons and text matching the images.

#### [NEW] [MainScreen.kt](file:///Users/blackcoder/AndroidStudioProjects/LifePlanner/app/src/main/java/com/dalgeer/lifeplanner/ui/screens/MainScreen.kt)
- The main container with a `Scaffold` and `NavigationBar`.
- Contains its own internal `NavDisplay` for bottom navigation.

---

### Entry Point

#### [MODIFY] [MainActivity.kt](file:///Users/blackcoder/AndroidStudioProjects/LifePlanner/app/src/main/java/com/dalgeer/lifeplanner/MainActivity.kt)
- Initialize Root Navigation State.
- Set up the top-level `NavDisplay`.
- Wire up back press handling.

## Verification Plan

### Automated Tests
- I'll verify the build finishes successfully after adding dependencies.

### Manual Verification
- Deploy the app to the device.
- Verify the flow: Splash -> Language -> Onboarding (swipe through 3 pages) -> Home.
- Verify Bottom Navigation switches between screens within the `MainScreen`.
