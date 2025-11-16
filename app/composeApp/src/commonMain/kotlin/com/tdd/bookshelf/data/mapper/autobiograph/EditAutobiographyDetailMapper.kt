package com.tdd.bookshelf.data.mapper.autobiograph

import com.tdd.bookshelf.data.base.BaseMapper
import com.tdd.bookshelf.data.entity.request.autobiography.PostEditAutobiographyRequestDto
import com.tdd.bookshelf.domain.entity.request.autobiography.EditAutobiographyDetailRequestModel

object EditAutobiographyDetailMapper : BaseMapper() {
    fun EditAutobiographyDetailRequestModel.toDto() =
        PostEditAutobiographyRequestDto(
            title = title,
            content = content,
            preSignedCoverImageUrl = preSignedCoverImageUrl,
        )
}
