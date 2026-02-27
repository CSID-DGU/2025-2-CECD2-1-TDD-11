package com.tdd.talktobook.domain.entity.request.page

data class TextFieldBottomSheetModel (
    val titleText: String = "",
    val btnText: String = "",
    val textFieldHintText: String = "",
    val onClickConfirmBtnAction: (String) -> Unit = {},
)