package com.tdd.talktobook.domain.entity.request.autobiography

import com.tdd.talktobook.domain.entity.enums.AutobiographyStatusType

data class ChangeAutobiographyStatusRequestModel(
    val autobiographyId: Int = 0,
    val status: AutobiographyStatusType = AutobiographyStatusType.ENOUGH,
)
