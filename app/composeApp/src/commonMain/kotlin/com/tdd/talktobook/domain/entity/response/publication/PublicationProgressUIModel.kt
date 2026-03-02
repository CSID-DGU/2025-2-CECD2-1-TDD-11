package com.tdd.talktobook.domain.entity.response.publication

import androidx.compose.ui.graphics.Color
import com.tdd.talktobook.core.designsystem.White0

data class PublicationProgressUIModel(
    val submitUI: UIModel = UIModel(),
    val progressUI: UIModel = UIModel(),
    val completeUI: UIModel = UIModel(),
) {
    data class UIModel(
        val backgroundColor: Color = White0,
        val strokeColor: Color = White0,
        val titleColor: Color = White0,
        val subTitleColor: Color = White0,
        val isChecked: Boolean = false,
    )
}
