package com.tdd.talktobook.data.mapper.autobiograph

import com.tdd.talktobook.data.base.BaseMapper
import com.tdd.talktobook.data.entity.request.autobiography.PostEditAutobiographyRequestDto
import com.tdd.talktobook.domain.entity.request.autobiography.EditAutobiographyDetailRequestModel

object EditAutobiographyDetailMapper : BaseMapper() {
    fun EditAutobiographyDetailRequestModel.toDto() =
        PostEditAutobiographyRequestDto(
            title = title,
            content = content,
            preSignedCoverImageUrl = preSignedCoverImageUrl,
        )
}
