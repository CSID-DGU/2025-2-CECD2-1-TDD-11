package com.lifelibrarians.lifebookshelf.interview.service;

import com.lifelibrarians.lifebookshelf.exception.status.InterviewExceptionStatus;
import com.lifelibrarians.lifebookshelf.interview.domain.Conversation;
import com.lifelibrarians.lifebookshelf.interview.domain.Interview;
import com.lifelibrarians.lifebookshelf.interview.domain.InterviewQuestion;
import com.lifelibrarians.lifebookshelf.interview.dto.request.InterviewConversationCreateRequestDto;
import com.lifelibrarians.lifebookshelf.interview.repository.ConversationRepository;
import com.lifelibrarians.lifebookshelf.interview.repository.InterviewRepository;
import com.lifelibrarians.lifebookshelf.log.Logging;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Logging
public class InterviewCommandService {

	private final ConversationRepository conversationRepository;
	private final InterviewRepository interviewRepository;

	public void createConversations(Interview interview,
			InterviewConversationCreateRequestDto requestDto) {

		LocalDateTime now = LocalDateTime.now();
		List<Conversation> conversations = requestDto.getConversations().stream()
				.map(conversationDto -> Conversation.of(conversationDto.getContent(),
						conversationDto.getConversationType(), interview, now))
				.collect(Collectors.toList());

		conversationRepository.saveAll(conversations);
	}

	public void updateCurrentQuestion(Interview interview) {
		List<InterviewQuestion> questions = interview.getQuestions();
		InterviewQuestion currentQuestion = interview.getCurrentQuestion();

		List<InterviewQuestion> questionsFiltered = questions.stream()
				.filter(question -> question.getOrder() > currentQuestion.getOrder())
				.sorted(Comparator.comparing(InterviewQuestion::getOrder))
				.collect(Collectors.toList());

		if (questionsFiltered.isEmpty()) {
			throw InterviewExceptionStatus.NEXT_INTERVIEW_QUESTION_NOT_FOUND.toServiceException();
		}
		InterviewQuestion nextQuestion = questionsFiltered.get(0);
		interview.setCurrentQuestion(nextQuestion);

		interviewRepository.save(interview);
	}
}
