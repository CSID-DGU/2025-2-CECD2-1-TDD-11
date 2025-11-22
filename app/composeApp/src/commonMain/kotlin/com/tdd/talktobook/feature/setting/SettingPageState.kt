package com.tdd.talktobook.feature.setting

import com.tdd.talktobook.core.ui.base.PageState
import com.tdd.talktobook.domain.entity.response.member.MemberInfoModel

data class SettingPageState(
    val memberInfo: MemberInfoModel = MemberInfoModel(),
) : PageState
