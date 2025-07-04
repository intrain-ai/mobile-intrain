package com.mercu.intrain.ui.onboarding

import androidx.annotation.DrawableRes

// Data class untuk satu halaman onboarding
data class OnboardingPage(
    @DrawableRes val imageRes: Int,
    val title: String,
    val description: String
) 