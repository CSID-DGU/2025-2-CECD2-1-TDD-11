package com.lifelibrarians.lifebookshelf.interview.service;

import com.lifelibrarians.lifebookshelf.exception.status.CommonExceptionStatus;
import com.lifelibrarians.lifebookshelf.exception.status.InterviewExceptionStatus;
import com.lifelibrarians.lifebookshelf.interview.domain.Conversation;
import com.lifelibrarians.lifebookshelf.interview.domain.ConversationType;
import com.lifelibrarians.lifebookshelf.interview.domain.Interview;
import com.lifelibrarians.lifebookshelf.interview.domain.InterviewQuestion;
import com.lifelibrarians.lifebookshelf.interview.dto.response.*;
import com.lifelibrarians.lifebookshelf.interview.repository.ConversationRepository;
import com.lifelibrarians.lifebookshelf.interview.repository.InterviewRepository;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.mapper.InterviewMapper;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
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

	public InterviewConversationResponseDto getConversations(Long memberId, Long interviewId, Pageable pageable) {

        // 1개만 가져오고 멤버 ID로 소유자 확인까지 같이
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(InterviewExceptionStatus.INTERVIEW_NOT_FOUND::toServiceException);

        if (!interview.getMember().getId().equals(memberId)) {
            throw InterviewExceptionStatus.INTERVIEW_NOT_OWNER.toServiceException();
        }

		Page<Conversation> conversations = conversationRepository.findAllByInterviewId(
				interviewId, pageable);
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

	public InterviewQuestionResponseDto getQuestions(Long memberId, Long interviewId) {

        // 1개만 가져오고 멤버 ID로 소유자 확인까지 같이
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(InterviewExceptionStatus.INTERVIEW_NOT_FOUND::toServiceException);

        if (!interview.getMember().getId().equals(memberId)) {
            throw InterviewExceptionStatus.INTERVIEW_NOT_OWNER.toServiceException();
        }

        List<InterviewQuestion> questions = interview.getQuestions();

        // createdAt 기준 가장 최신 질문 선택
        InterviewQuestion currentQuestion = questions.stream()
                .max(Comparator.comparing(InterviewQuestion::getCreatedAt))
                .orElseThrow(InterviewExceptionStatus.INTERVIEW_QUESTION_NOT_FOUND::toServiceException);

        List<InterviewQuestionDto> questionDtos = questions.stream()
                .map(interviewMapper::toInterviewQuestionDto)
                .collect(Collectors.toList());

        return interviewMapper.toInterviewQuestionResponseDto(currentQuestion.getId(), questionDtos);
    }

    public InterviewSummaryOfMonthResponseDto getInterviewSummaries(Long memberId, Long autobiographyId, Integer year, Integer month) {
        if (year < 2000) {
            throw CommonExceptionStatus.INVALID_YEAR.toServiceException();
        }
        if (month < 1 || month > 12) {
            throw CommonExceptionStatus.INVALID_MONTH.toServiceException();
        }

        List<Interview> interviews = interviewRepository.findAllByAutobiographyIdAndYearAndMonth(autobiographyId, year, month);

        if (!interviews.isEmpty() && !interviews.get(0).getMember().getId().equals(memberId)) {
            throw InterviewExceptionStatus.INTERVIEW_NOT_OWNER.toServiceException();
        }

        List<InterviewSummaryOfMonthResponseDto.InterviewSummaryDto> dtoList = interviews.stream()
                .map(interview -> {

                    // total message count는 interviewConversations의 size
                    long totalMessageCount = interview.getInterviewConversations().size();

                    // total answer count는 conversationType이 HUMAN인 것의 개수
                    long totalAnswerCount = interview.getInterviewConversations().stream()
                            .filter(c -> c.getConversationType() == ConversationType.HUMAN)
                            .count();

                    String date = interview.getCreatedAt()
                            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

                    return interviewMapper.toInterviewSummaryDto(
                            interview,
                            (int) totalMessageCount,
                            (int) totalAnswerCount,
                            date
                    );
                })
                .collect(Collectors.toList());

        return InterviewSummaryOfMonthResponseDto.builder()
                .interviews(dtoList)
                .build();
    }

//	public Interview getInterviewWithQuestions(Long interviewId) {
//		return interviewRepository.findWithQuestionsById(interviewId)
//				.orElseThrow(InterviewExceptionStatus.INTERVIEW_NOT_FOUND::toServiceException);
//	}
}
