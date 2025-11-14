package com.tdd.bookshelf.feature.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
internal fun OnboardingScreen() {
    OnboardingContent()
}

@Composable
private fun OnboardingContent() {
    Box(
        modifier =
            Modifier
                .fillMaxSize(),
    ) {
        Text(text = "Onboarding")
    }
}

@Preview
@Composable
fun PreviewOnboarding() {
    OnboardingContent()
}
