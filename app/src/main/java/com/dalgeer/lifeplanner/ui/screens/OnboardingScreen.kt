package com.dalgeer.lifeplanner.ui.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dalgeer.lifeplanner.R
import com.dalgeer.lifeplanner.ui.theme.*
import kotlinx.coroutines.launch

data class OnboardingPageData(
    val imageRes: Int,
    val title: String,
    val description: String
)

@Composable
fun OnboardingScreen(
    onFinish: () -> Unit = {}
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()

    val pages = listOf(
        OnboardingPageData(
            imageRes = R.drawable.onboarding_1,
            title = "Manage Your\nDaily Tasks",
            description = "Add, organise and never miss what matters. Set priorities, reminders and recurring tasks effortlessly."
        ),
        OnboardingPageData(
            imageRes = R.drawable.onboarding_2,
            title = "Build Better\nHabits & Routines",
            description = "Create healthy routines, track your habits and stay consistent. Build the lifestyle you want, one day at a time."
        ),
        OnboardingPageData(
            imageRes = R.drawable.onboarding_3,
            title = "Set Goals &\nTrack Progress",
            description = "Turn your dreams into achievements. Set your goals, track progress and stay motivated every step of the way."
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Horizontal Pager (Image + Title + Description)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { pageIndex ->
            val page = pages[pageIndex]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.Start
            ) {
                Spacer(modifier = Modifier.height(12.dp))

                // Top Illustration Card Area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.3f),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = page.imageRes),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 8.dp, bottom = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Title
                Text(
                    text = page.title,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextDark,
                    lineHeight = 32.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Description
                Text(
                    text = page.description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Normal,
                    color = TextGray,
                    lineHeight = 20.sp,
                    modifier = Modifier.fillMaxWidth(0.92f)
                )

                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        // Bottom Navigation Bar: Skip (Left) + Page Indicator Dots (Center) + Next FAB (Right)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left: Skip Button
            Text(
                text = "Skip",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextGray,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onFinish() }
                    .padding(vertical = 8.dp, horizontal = 4.dp)
            )

            // Center: Page Indicator Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    val isSelected = pagerState.currentPage == index
                    val width by animateDpAsState(
                        targetValue = if (isSelected) 8.dp else 8.dp,
                        label = "dotWidth"
                    )
                    Box(
                        modifier = Modifier
                            .size(width)
                            .clip(CircleShape)
                            .background(
                                color = if (isSelected) PrimaryPurple else Color(0xFFE2E8F0)
                            )
                    )
                }
            }

            // Right: Floating Action Button (Next Arrow / Finish Checkmark)
            Surface(
                onClick = {
                    if (pagerState.currentPage < 2) {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    } else {
                        onFinish()
                    }
                },
                modifier = Modifier
                    .size(52.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = CircleShape,
                        ambientColor = PrimaryPurple.copy(alpha = 0.3f),
                        spotColor = PrimaryPurple.copy(alpha = 0.4f)
                    ),
                shape = CircleShape,
                color = PrimaryPurple
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (pagerState.currentPage == 2) Icons.Default.Check else Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    OnboardingScreen()
}
