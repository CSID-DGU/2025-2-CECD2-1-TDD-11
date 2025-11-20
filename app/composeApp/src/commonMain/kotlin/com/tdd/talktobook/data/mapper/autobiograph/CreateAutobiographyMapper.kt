package com.tdd.talktobook.data.mapper.autobiograph

import com.tdd.talktobook.data.base.BaseMapper
import com.tdd.talktobook.data.entity.request.autobiography.PostCreateAutobiographyRequestDto
import com.tdd.talktobook.domain.entity.request.autobiography.CreateAutobiographyRequestModel

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
