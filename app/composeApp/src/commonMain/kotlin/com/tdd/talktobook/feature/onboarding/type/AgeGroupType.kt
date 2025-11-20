package com.tdd.talktobook.feature.onboarding.type

import com.tdd.talktobook.core.designsystem.Empty
import com.tdd.talktobook.core.designsystem.Fifty
import com.tdd.talktobook.core.designsystem.FiftyContent
import com.tdd.talktobook.core.designsystem.Forty
import com.tdd.talktobook.core.designsystem.FortyContent
import com.tdd.talktobook.core.designsystem.Seventy
import com.tdd.talktobook.core.designsystem.SeventyContent
import com.tdd.talktobook.core.designsystem.Sixty
import com.tdd.talktobook.core.designsystem.SixtyContent
import com.tdd.talktobook.core.designsystem.Ten
import com.tdd.talktobook.core.designsystem.TenContent
import com.tdd.talktobook.core.designsystem.Thirty
import com.tdd.talktobook.core.designsystem.ThirtyContent

enum class AgeGroupType(
    val type: String,
    val content: String
) {
    TEN(Ten, TenContent),
    THIRTY(Thirty, ThirtyContent),
    FORTY(Forty, FortyContent),
    FIFTY(Fifty, FiftyContent),
    SIXTY(Sixty, SixtyContent),
    SEVENTY(Seventy, SeventyContent),
    DEFAULT(Empty, Empty);
}