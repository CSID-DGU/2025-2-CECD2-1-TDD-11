package com.tdd.onboarding.education

import com.tdd.domain.entity.request.CreateUserModel
import com.tdd.ui.base.PageState

data class ScholarShipPageState (
    val userModel: CreateUserModel = CreateUserModel(),
    val scholarShip: String = ""
): PageState