package com.lifelibrarians.lifebookshelf.interview.service;

import com.lifelibrarians.lifebookshelf.exception.status.InterviewExceptionStatus;
import com.lifelibrarians.lifebookshelf.interview.domain.Conversation;
import com.lifelibrarians.lifebookshelf.interview.domain.Interview;
import com.lifelibrarians.lifebookshelf.interview.domain.InterviewQuestion;
import com.lifelibrarians.lifebookshelf.interview.dto.response.InterviewConversationDto;
import com.lifelibrarians.lifebookshelf.interview.dto.response.InterviewConversationResponseDto;
import com.lifelibrarians.lifebookshelf.interview.dto.response.InterviewQuestionDto;
import com.lifelibrarians.lifebookshelf.interview.dto.response.InterviewQuestionResponseDto;
import com.lifelibrarians.lifebookshelf.interview.repository.ConversationRepository;
import com.lifelibrarians.lifebookshelf.interview.repository.InterviewRepository;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.mapper.InterviewMapper;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Logging
public class InterviewQueryService {

	private final InterviewRepository interviewRepository;
	private final ConversationRepository conversationRepository;
	private final InterviewMapper interviewMapper;

	public Interview getInterview(Long interviewId) {
		return interviewRepository.findWithQuestionsById(
						interviewId)
				.orElseThrow(InterviewExceptionStatus.INTERVIEW_NOT_FOUND::toServiceException);
	}

	public InterviewConversationResponseDto getConversations(Interview interview, Pageable pageable) {
		Page<Conversation> conversations = conversationRepository.findAllByInterviewId(
				interview.getId(), pageable);
		List<InterviewConversationDto> conversationDtos = conversations.stream()
				.map(interviewMapper::toInterviewConversationDto)
				.collect(Collectors.toList());
		return interviewMapper.toInterviewConversationResponseDto(
				conversationDtos,
				pageable.getPageNumber(),
				(int) conversations.getTotalElements(),
				conversations.getTotalPages(),
				conversations.hasNext(),
				conversations.hasPrevious()
		);
	}

	public InterviewQuestionResponseDto getQuestions(List<InterviewQuestion> questions,
			InterviewQuestion currentQuestion) {
		List<InterviewQuestionDto> questionDtos = questions.stream()
				.map(interviewMapper::toInterviewQuestionDto)
				.collect(Collectors.toList());

		return interviewMapper.toInterviewQuestionResponseDto(currentQuestion.getId(), questionDtos);
	}

//	public Interview getInterviewWithQuestions(Long interviewId) {
//		return interviewRepository.findWithQuestionsById(interviewId)
//				.orElseThrow(InterviewExceptionStatus.INTERVIEW_NOT_FOUND::toServiceException);
//	}
}
