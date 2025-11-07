package com.tdd.onboarding.marriage

import com.tdd.domain.entity.request.CreateUserModel
import com.tdd.ui.base.PageState

data class MarriagePageState (
    val userModel: CreateUserModel = CreateUserModel(),
    val marriage: String = ""
): PageState