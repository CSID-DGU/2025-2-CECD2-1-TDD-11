package com.tdd.talktobook.feature.my

import com.tdd.talktobook.core.designsystem.Rejected
import com.tdd.talktobook.core.ui.base.PageState
import com.tdd.talktobook.domain.entity.response.member.MemberInfoModel
import com.tdd.talktobook.domain.entity.response.publication.PublishMyListItemModel

data class MyPageState(
    val publishStatus: String = Rejected,
    val publishBookList: List<PublishMyListItemModel> = emptyList(),
    val isAlarmActivated: Boolean = false,
    val memberInfo: MemberInfoModel = MemberInfoModel(),
) : PageState
