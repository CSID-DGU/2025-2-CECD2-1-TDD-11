package com.tdd.bookshelf.data.mapper.autobiograph

import com.tdd.bookshelf.data.base.BaseMapper
import com.tdd.bookshelf.data.entity.request.autobiography.PostCreateAutobiographyRequestDto
import com.tdd.bookshelf.domain.entity.request.autobiography.CreateAutobiographyRequestModel

object CreateAutobiographyMapper : BaseMapper() {
    fun CreateAutobiographyRequestModel.toDto() =
        PostCreateAutobiographyRequestDto(
            title = title,
            content = content,
            preSignedCoverImageUrl = preSignedCoverImageUrl,
            interviewQuestions =
                interviewQuestions.map { questionModel ->
                    PostCreateAutobiographyRequestDto.InterviewQuestion(
                        order = questionModel.order,
                        questionText = questionModel.questionText,
                    )
                },
        )
}
