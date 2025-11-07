package com.tdd.onboarding.gender

import com.tdd.domain.entity.request.CreateUserModel
import com.tdd.ui.base.PageState

data class UserGenderPageState (
    val userModel: CreateUserModel = CreateUserModel(),
    val selectedGender: String = ""
): PageState