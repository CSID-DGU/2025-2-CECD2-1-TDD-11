package com.tdd.talktobook.feature

import com.tdd.talktobook.core.ui.base.PageState
import com.tdd.talktobook.core.ui.common.type.BottomSheetType
import com.tdd.talktobook.domain.entity.request.page.OneBtnDialogModel
import com.tdd.talktobook.domain.entity.request.page.ScrollSelectBottomSheetModel
import com.tdd.talktobook.domain.entity.request.page.TextFieldBottomSheetModel
import com.tdd.talktobook.domain.entity.request.page.TwoBtnDialogModel

data class MainPageState(
    val bottomNavType: BottomNavType = BottomNavType.DEFAULT,
    val oneBtnDialogModel: OneBtnDialogModel = OneBtnDialogModel(),
    val twoBtnDialogModel: TwoBtnDialogModel = TwoBtnDialogModel(),
    val bottomSheetType: BottomSheetType = BottomSheetType.DEFAULT,
    val scrollSelectBottomSheetModel: ScrollSelectBottomSheetModel = ScrollSelectBottomSheetModel(),
    val textFieldBottomSheetModel: TextFieldBottomSheetModel = TextFieldBottomSheetModel(),
) : PageState
