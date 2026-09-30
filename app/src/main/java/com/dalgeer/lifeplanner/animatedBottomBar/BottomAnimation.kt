package io.jadu.animatedBottomBar

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.jadu.animatedBottomBar.models.CurveAnimationType
import io.jadu.animatedBottomBar.models.NavItem
import io.jadu.animatedBottomBar.util.RenderIcon
import kotlinx.coroutines.launch

@Composable
fun CurvedBottomNavigation(
    items: List<NavItem>,
    selectedIndex: Int,
    modifier: Modifier = Modifier.fillMaxWidth(),
    bottomBarAlignment: Alignment = Alignment.BottomCenter,
    curveAnimationType: CurveAnimationType = CurveAnimationType.SMOOTH,
    animationDuration: Int = 300,
    fabBackgroundColor: Color = Color(0xFF6C63FF),
    fabIconTint: Color = Color.White,
    fabDotColor: Color = Color.Transparent,
    navBarBackgroundColor: Color = Color.White,
    unselectedIconTint: Color = Color.Gray,
    unselectedTextColor: Color = Color.Gray,
    fabSize: Dp = 50.dp,
    fabIconSize: Dp = 24.dp,
    fabDotSize: Dp = 0.dp,
    enableFabIconScale: Boolean = true,
    navBarHeight: Dp = 54.dp,
    unselectedIconSize: Dp = 20.dp,
    unselectedTextSize: TextUnit = 10.sp,
    fabElevation: Dp = 6.dp,
    curveRadius: Dp = 46.dp,
    curveDepth: Dp = 25.dp,
    curveSmoothness: Dp = 20.dp,
    showDot: Boolean = false,
    showLabels: Boolean = true,
    enableHapticFeedback: Boolean = true,
    onFabClick: (() -> Unit)? = null,
    onItemSelected: (Int) -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier,
        contentAlignment = bottomBarAlignment
    ) {
        val componentWidth = maxWidth

        CurvedBottomNavigationContent(
            items = items,
            selectedIndex = selectedIndex,
            onItemSelected = onItemSelected,
            componentWidth = componentWidth,
            fabBackgroundColor = fabBackgroundColor,
            fabIconTint = fabIconTint,
            fabDotColor = fabDotColor,
            navBarBackgroundColor = navBarBackgroundColor,
            unselectedIconTint = unselectedIconTint,
            unselectedTextColor = unselectedTextColor,
            fabSize = fabSize,
            fabIconSize = fabIconSize,
            fabDotSize = fabDotSize,
            navBarHeight = navBarHeight,
            unselectedIconSize = unselectedIconSize,
            unselectedTextSize = unselectedTextSize,
            fabElevation = fabElevation,
            curveRadius = curveRadius,
            curveDepth = curveDepth,
            curveSmoothness = curveSmoothness,
            showDot = showDot,
            showLabels = showLabels,
            curveAnimationType = curveAnimationType,
            animationDuration = animationDuration,
            enableHapticFeedback = enableHapticFeedback,
            enableFabIconScale = enableFabIconScale,
            onFabClick = onFabClick
        )
    }
}

@Composable
private fun CurvedBottomNavigationContent(
    items: List<NavItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    componentWidth: Dp,
    fabBackgroundColor: Color,
    fabIconTint: Color,
    fabDotColor: Color,
    navBarBackgroundColor: Color,
    unselectedIconTint: Color,
    unselectedTextColor: Color,
    fabSize: Dp,
    fabIconSize: Dp,
    fabDotSize: Dp,
    navBarHeight: Dp,
    unselectedIconSize: Dp,
    unselectedTextSize: TextUnit,
    fabElevation: Dp,
    curveRadius: Dp,
    curveDepth: Dp,
    curveSmoothness: Dp,
    showDot: Boolean,
    showLabels: Boolean,
    curveAnimationType: CurveAnimationType,
    animationDuration: Int,
    enableHapticFeedback: Boolean,
    enableFabIconScale: Boolean,
    onFabClick: (() -> Unit)? = null,
) {
    val haptic = LocalHapticFeedback.current
    var currentIndex by remember { mutableStateOf(selectedIndex) }
    var previousIndex by remember { mutableStateOf(selectedIndex) }

    LaunchedEffect(selectedIndex) {
        if (selectedIndex != currentIndex) {
            previousIndex = currentIndex
            currentIndex = selectedIndex
        }
    }

    val cellWidth = componentWidth / items.size

    val startX = cellWidth * previousIndex + (cellWidth / 2) - (fabSize / 2)
    val endX = cellWidth * currentIndex + (cellWidth / 2) - (fabSize / 2)

    val fabX = remember { Animatable(startX.value) }
    val fabY = remember { Animatable(-25f) }
    val iconScale = remember { Animatable(1f) }

    var displayedIconIndex by remember { mutableStateOf(currentIndex) }

    LaunchedEffect(currentIndex) {
        if (currentIndex != previousIndex) {
            val startXValue = startX.value
            val endXValue = endX.value
            val distance = endXValue - startXValue
            var iconSwitched = false
            launch {
                fabX.animateTo(
                    targetValue = endXValue,
                    animationSpec = tween(animationDuration, easing = FastOutSlowInEasing)
                ) {
                    val progress = if (distance != 0f) {
                        (this.value - startXValue) / distance
                    } else {
                        0f
                    }

                    if (!iconSwitched && progress >= 0.3f) {
                        displayedIconIndex = currentIndex
                        iconSwitched = true
                    }

                    val curveHeight = 32f
                    val parabolicY = -4f * progress * (progress - 1f)
                    val targetY = -25f + curveHeight * parabolicY

                    launch {
                        fabY.snapTo(targetY)
                    }
                }
                if (!iconSwitched) {
                    displayedIconIndex = currentIndex
                    iconSwitched = true
                }
            }
        }
    }

    LaunchedEffect(currentIndex) {
        iconScale.animateTo(
            targetValue = 1.18f,
            animationSpec = tween(durationMillis = 140, easing = FastOutSlowInEasing)
        )
        iconScale.animateTo(
            targetValue = 1f,
            animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
        )
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        // FAB
        FloatingActionButton(
            onClick = { onFabClick?.invoke() },
            containerColor = fabBackgroundColor,
            shape = RoundedCornerShape(fabSize),
            elevation = FloatingActionButtonDefaults.elevation(fabElevation),
            modifier = Modifier
                .offset(x = fabX.value.dp, y = fabY.value.dp)
                .size(fabSize)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                RenderIcon(
                    iconSource = items[displayedIconIndex].selectedIcon
                        ?: items[displayedIconIndex].icon,
                    contentDescription = null,
                    tint = fabIconTint,
                    modifier = Modifier.size(if (enableFabIconScale) (fabIconSize.value * iconScale.value).dp else fabIconSize)
                )

                if (showDot) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Box(
                        modifier = Modifier
                            .size(fabDotSize)
                            .background(fabDotColor, CircleShape)
                    )
                }
            }
        }

        // Curved Navigation Bar
        CurvedNavigationBar(
            selectedIndex = currentIndex,
            itemCount = items.size,
            animationType = curveAnimationType,
            backgroundColor = navBarBackgroundColor,
            curveRadius = curveRadius,
            curveDepth = curveDepth,
            curveSmoothness = curveSmoothness,
            navBarHeight = navBarHeight,
            animationDuration = animationDuration
        )

        // Bottom Nav Items
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(navBarHeight)
                .align(Alignment.BottomCenter),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEachIndexed { index, item ->
                BottomNavItem(
                    item = item,
                    isSelected = index == currentIndex,
                    onClick = {
                        if (currentIndex != index) {
                            if (enableHapticFeedback) haptic.performHapticFeedback(HapticFeedbackType.Confirm)
                            previousIndex = currentIndex
                            currentIndex = index
                            onItemSelected(index)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    iconTint = unselectedIconTint,
                    textColor = unselectedTextColor,
                    iconSize = unselectedIconSize,
                    textSize = unselectedTextSize,
                    showLabel = showLabels
                )
            }
        }
    }
}
