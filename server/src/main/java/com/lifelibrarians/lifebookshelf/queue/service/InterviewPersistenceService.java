package com.lifelibrarians.lifebookshelf.queue.service;

import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyRepository;
import com.lifelibrarians.lifebookshelf.exception.status.AuthExceptionStatus;
import com.lifelibrarians.lifebookshelf.exception.status.AutobiographyExceptionStatus;
import com.lifelibrarians.lifebookshelf.exception.status.InterviewExceptionStatus;
import com.lifelibrarians.lifebookshelf.exception.status.MemberExceptionStatus;
import com.lifelibrarians.lifebookshelf.interview.domain.Conversation;
import com.lifelibrarians.lifebookshelf.interview.domain.ConversationType;
import com.lifelibrarians.lifebookshelf.interview.domain.Interview;
import com.lifelibrarians.lifebookshelf.interview.domain.InterviewQuestion;
import com.lifelibrarians.lifebookshelf.interview.repository.ConversationRepository;
import com.lifelibrarians.lifebookshelf.interview.repository.InterviewQuestionRepository;
import com.lifelibrarians.lifebookshelf.interview.repository.InterviewRepository;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.member.repository.MemberRepository;
import com.lifelibrarians.lifebookshelf.queue.dto.request.InterviewPayloadRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

// DB 저장 수행 계층
@Service
@RequiredArgsConstructor
@Logging
public class InterviewPersistenceService {
    private final MemberRepository memberRepository;
    private final InterviewRepository interviewRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;
    private final ConversationRepository conversationRepository;
    private final AutobiographyRepository autobiographyRepository;

    @Transactional
    public void receiveInterviewPayload(InterviewPayloadRequestDto payload) {

        // 1. 가장 최근 interview 찾기
        Interview interview = interviewRepository.findTopByAutobiographyIdOrderByCreatedAtDesc(payload.getAutobiographyId())
                .orElseThrow(InterviewExceptionStatus.INTERVIEW_NOT_FOUND::toServiceException);

        // 만일 해당 유저의 interview가 아닌 경우 오류 출력
        if (!interview.getMember().getId().equals(payload.getUserId())) {
            throw AuthExceptionStatus.MEMBER_NOT_FOUND.toServiceException();
        }

        // 2. 인터뷰 질문 저장
        InterviewQuestion question = InterviewQuestion.ofV2(
                payload.getInterviewQuestion().getQuestionOrder(),
                payload.getInterviewQuestion().getQuestionText(),
                "", // material은 임시로 빈 문자열로 설정
                payload.getInterviewQuestion().getTimestamp(),
                interview
        );

        interviewQuestionRepository.save(question);

        // 3. 인터뷰 응답(Conversation) 저장
        List<Conversation> conversations = payload.getConversation().stream()
                .map(dto -> {
                    ConversationType type = ConversationType.valueOf(dto.getConversationType().toUpperCase());
                    return Conversation.of(
                            dto.getContent(),
                            type,
                            interview,
                            dto.getTimestamp()
                    );
                })
                .collect(Collectors.toList());

        conversationRepository.saveAll(conversations);
    }
}
