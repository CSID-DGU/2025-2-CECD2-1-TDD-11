package com.tdd.bookshelf.data.entity.response.interview.ai

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class InterviewQuestionsResponseDto(
    @SerialName("interview_questions")
    val interviewQuestions: List<String> = emptyList(),
)
