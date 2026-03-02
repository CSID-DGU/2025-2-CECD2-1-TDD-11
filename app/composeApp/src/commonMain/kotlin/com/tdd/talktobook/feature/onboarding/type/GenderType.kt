package com.tdd.talktobook.feature.onboarding.type

import com.tdd.talktobook.core.designsystem.Empty
import com.tdd.talktobook.core.designsystem.Female
import com.tdd.talktobook.core.designsystem.FemaleContent
import com.tdd.talktobook.core.designsystem.Male
import com.tdd.talktobook.core.designsystem.MaleContent

enum class GenderType(
    val type: String,
    val content: String,
) {
    MALE_TYPE(Male, MaleContent),
    FEMALE_TYPE(Female, FemaleContent),
    DEFAULT(Empty, Empty),
}
