package com.tdd.onboarding

import com.tdd.domain.entity.request.CreateUserModel
import com.tdd.ui.base.PageState

data class OnBoardingPageState (
    val userModel: CreateUserModel = CreateUserModel(),
    val isDataUpdateSuccess: Boolean = false,
): PageState