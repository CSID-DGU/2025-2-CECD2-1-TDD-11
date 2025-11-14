package com.tdd.bookshelf.feature.my

import com.tdd.bookshelf.core.designsystem.Rejected
import com.tdd.bookshelf.core.ui.base.PageState
import com.tdd.bookshelf.domain.entity.response.member.MemberInfoModel
import com.tdd.bookshelf.domain.entity.response.publication.PublishMyListItemModel

data class MyPageState(
    val publishStatus: String = Rejected,
    val publishBookList: List<PublishMyListItemModel> = emptyList(),
    val isAlarmActivated: Boolean = false,
    val memberInfo: MemberInfoModel = MemberInfoModel(),
) : PageState
