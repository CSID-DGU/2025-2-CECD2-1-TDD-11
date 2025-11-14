package com.tdd.bookshelf.data.entity.request.autobiography

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PostCreateAutobiographyRequestDto(
    @SerialName("title")
    val title: String = "",
    @SerialName("content")
    val content: String = "",
    @SerialName("preSignedCoverImageUrl")
    val preSignedCoverImageUrl: String = "",
    @SerialName("interviewQuestions")
    val interviewQuestions: List<InterviewQuestion> = emptyList(),
) {
    @Serializable
    data class InterviewQuestion(
        @SerialName("order")
        val order: Int = 0,
        @SerialName("questionText")
        val questionText: String = "",
    )
}
