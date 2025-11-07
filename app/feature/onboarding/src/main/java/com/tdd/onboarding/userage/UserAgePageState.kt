package com.tdd.onboarding.userage

import com.tdd.domain.entity.request.CreateUserModel
import com.tdd.ui.base.PageState

data class UserAgePageState (
    val userModel: CreateUserModel = CreateUserModel(),
    val selectedUserAge: String = ""
): PageState