package com.tdd.talktobook.feature.setting

import com.tdd.talktobook.core.ui.base.PageState
import com.tdd.talktobook.domain.entity.response.member.MemberInfoResponseModel

data class SettingPageState(
    val memberInfo: MemberInfoResponseModel = MemberInfoResponseModel(),
) : PageState
