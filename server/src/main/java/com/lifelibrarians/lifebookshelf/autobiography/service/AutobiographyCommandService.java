package com.lifelibrarians.lifebookshelf.autobiography.service;

import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyChapter;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatus;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatusType;
import com.lifelibrarians.lifebookshelf.autobiography.dto.request.AutobiographyInitRequestDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.request.AutobiographyUpdateRequestDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.request.CoShowAutobiographyGenerateRequestDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.AutobiographyInitResponseDto;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyChapterRepository;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyRepository;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyStatusRepository;
import com.lifelibrarians.lifebookshelf.classification.service.ClassificationInitService;
import com.lifelibrarians.lifebookshelf.exception.status.AuthExceptionStatus;
import com.lifelibrarians.lifebookshelf.exception.status.AutobiographyExceptionStatus;
import com.lifelibrarians.lifebookshelf.exception.status.InterviewExceptionStatus;
import com.lifelibrarians.lifebookshelf.image.service.ImageService;
import com.lifelibrarians.lifebookshelf.interview.domain.Conversation;
import com.lifelibrarians.lifebookshelf.interview.domain.ConversationType;
import com.lifelibrarians.lifebookshelf.interview.domain.Interview;
import com.lifelibrarians.lifebookshelf.interview.domain.InterviewQuestion;
import com.lifelibrarians.lifebookshelf.interview.repository.ConversationRepository;
import com.lifelibrarians.lifebookshelf.interview.repository.InterviewQuestionRepository;
import com.lifelibrarians.lifebookshelf.interview.repository.InterviewRepository;
import com.lifelibrarians.lifebookshelf.log.Logging;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.lifelibrarians.lifebookshelf.member.domain.*;
import com.lifelibrarians.lifebookshelf.member.repository.MemberMetadataRepository;
import com.lifelibrarians.lifebookshelf.member.repository.MemberRepository;
import com.lifelibrarians.lifebookshelf.member.repository.PasswordMemberRepository;
import com.lifelibrarians.lifebookshelf.queue.service.AutobiographyCompletionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
@Logging
public class AutobiographyCommandService {

	private final AutobiographyRepository autobiographyRepository;
    private final AutobiographyStatusRepository autobiographyStatusRepository;
    private final AutobiographyChapterRepository autobiographyChapterRepository;
    private final InterviewRepository interviewRepository;
    private final MemberRepository memberRepository;
	private final ImageService imageService;

    // TODO: CoShow용 (나중에 제거하세요.)
    private final MemberMetadataRepository memberMetadataRepository;
    private final ConversationRepository conversationRepository;
    private final PasswordMemberRepository passwordMemberRepository;
    private final InterviewQuestionRepository interviewQuestionRepository;

    private final ClassificationInitService classificationInitService;
    private final AutobiographyCompletionService autobiographyCompletionService;

	@Value("${images.path.bio-cover}")
	public String BIO_COVER_IMAGE_DIR;

    public AutobiographyInitResponseDto initAutobiography(Long memberId, AutobiographyInitRequestDto requestDto) {
        log.info("[INIT_AUTOBIOGRAPHY] 자서전 초기화 시작 - memberId: {}, theme: {}", memberId, requestDto.getTheme());
        
        Member member = memberRepository.findById(memberId)
                .orElseThrow(AuthExceptionStatus.MEMBER_NOT_FOUND::toServiceException);

        LocalDateTime now = LocalDateTime.now();

        if (requestDto.getReason() != null && requestDto.getReason().length() > 500) {
            log.warn("[INIT_AUTOBIOGRAPHY] 자서전 이유 길이 초과 - memberId: {}, length: {}", memberId, requestDto.getReason().length());
            throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_REASON_LENGTH_EXCEEDED.toServiceException();
        }

        Autobiography autobiography = Autobiography.ofV2(
                null,
                null,
                null,

                requestDto.getTheme(),
                requestDto.getReason(),
                now,
                now,
                member
        );
        // save init autobiography
        Autobiography savedAutobiography = autobiographyRepository.save(autobiography);
        log.info("[INIT_AUTOBIOGRAPHY] 자서전 저장 완료 - autobiographyId: {}", savedAutobiography.getId());

        AutobiographyStatus autobiographyStatus = AutobiographyStatus.of(
                AutobiographyStatusType.EMPTY,
                member,
                autobiography,
                now
        );

        autobiographyStatusRepository.save(autobiographyStatus);
        log.info("[INIT_AUTOBIOGRAPHY] 자서전 상태 저장 완료 - status: {}", AutobiographyStatusType.EMPTY);

        // AI 데이터 기반으로 분류 체계 초기화
        classificationInitService.initializeFromAiData(autobiography);
        log.info("[INIT_AUTOBIOGRAPHY] 분류 체계 초기화 완료 - autobiographyId: {}", savedAutobiography.getId());

        // save init interview
        Interview interview = Interview.ofV2(
                now,
                autobiography,
                member,
                null
        );

        Interview savedInterview = interviewRepository.save(interview);
        log.info("[INIT_AUTOBIOGRAPHY] 인터뷰 저장 완료 - interviewId: {}", savedInterview.getId());
        log.info("[INIT_AUTOBIOGRAPHY] 자서전 초기화 완료 - autobiographyId: {}, interviewId: {}", savedAutobiography.getId(), savedInterview.getId());

        return AutobiographyInitResponseDto.builder()
                .autobiographyId(savedAutobiography.getId())
                .interviewId(savedInterview.getId())
                .build();
    }

	public void patchAutobiography(Long memberId, Long autobiographyId, AutobiographyUpdateRequestDto requestDto) {
        log.info("[PATCH_AUTOBIOGRAPHY] 자서전 챕터 수정 시작 - memberId: {}, chapterId: {}", memberId, autobiographyId);
        
        AutobiographyChapter autobiographyChapter = autobiographyChapterRepository.findById(autobiographyId)
                .orElseThrow(AutobiographyExceptionStatus.AUTOBIOGRAPHY_CHAPTER_NOT_FOUND::toServiceException);
        
        if (!autobiographyChapter.getAutobiography().getMember().getId().equals(memberId)) {
            log.warn("[PATCH_AUTOBIOGRAPHY] 자서전 소유자 불일치 - memberId: {}, ownerId: {}", memberId, autobiographyChapter.getAutobiography().getMember().getId());
            throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER.toServiceException();
        }

        LocalDateTime now = LocalDateTime.now();

		String preSignedImageUrl = null;
		if (!Objects.isNull(requestDto.getPreSignedCoverImageUrl()) && !requestDto.getPreSignedCoverImageUrl().isBlank()) {
			preSignedImageUrl = imageService.parseImageUrl(requestDto.getPreSignedCoverImageUrl(), BIO_COVER_IMAGE_DIR);
			log.info("[PATCH_AUTOBIOGRAPHY] 커버 이미지 URL 파싱 완료 - chapterId: {}", autobiographyId);
		}
		autobiographyChapter.updateAutoBiographyChapter(requestDto.getTitle(), requestDto.getContent(), preSignedImageUrl, now);
		log.info("[PATCH_AUTOBIOGRAPHY] 자서전 챕터 수정 완료 - chapterId: {}", autobiographyId);
	}

    public void patchReasonAutobiography(Long memberId, Long autobiographyId, AutobiographyInitRequestDto requestDto) {
        log.info("[PATCH_REASON_AUTOBIOGRAPHY] 자서전 이유 수정 시작 - memberId: {}, autobiographyId: {}", memberId, autobiographyId);
        
        Autobiography autobiography = autobiographyRepository.findById(autobiographyId)
                .orElseThrow(AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND::toServiceException);
        
        if (!autobiography.getMember().getId().equals(memberId)) {
            log.warn("[PATCH_REASON_AUTOBIOGRAPHY] 자서전 소유자 불일치 - memberId: {}, ownerId: {}", memberId, autobiography.getMember().getId());
            throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER.toServiceException();
        }

        if (requestDto.getReason() != null && requestDto.getReason().length() > 500) {
            log.warn("[PATCH_REASON_AUTOBIOGRAPHY] 자서전 이유 길이 초과 - memberId: {}, length: {}", memberId, requestDto.getReason().length());
            throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_REASON_LENGTH_EXCEEDED.toServiceException();
        }

        LocalDateTime now = LocalDateTime.now();

        autobiography.updateAutoBiographyV2(autobiography.getTitle(), autobiography.getContent(), autobiography.getCoverImageUrl(), autobiography.getTheme(), requestDto.getReason(), now);
        log.info("[PATCH_REASON_AUTOBIOGRAPHY] 자서전 이유 수정 완료 - autobiographyId: {}", autobiographyId);
    }

    public void requestAutobiographyGenerate(Long memberId, Long autobiographyId, CoShowAutobiographyGenerateRequestDto requestDto) {
        log.info("[REQUEST_AUTOBIOGRAPHY_GENERATE] 자서전 생성 요청 시작 - memberId: {}, autobiographyId: {}, name: {}", memberId, autobiographyId, requestDto.getName());
        
        Autobiography autobiography = autobiographyRepository.findById(autobiographyId)
                .orElseThrow(AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND::toServiceException);

        /*
        AutobiographyStatus autobiographyStatus = autobiographyStatusRepository
                .findTopByMemberIdAndStatusInOrderByUpdatedAtDesc(
                        memberId,
                        List.of(AutobiographyStatusType.EMPTY, AutobiographyStatusType.PROGRESSING, AutobiographyStatusType.ENOUGH)
                )
                .orElseThrow(AutobiographyExceptionStatus.AUTOBIOGRAPHY_STATUS_NOT_FOUND::toServiceException);
*/

        if (!autobiography.getMember().getId().equals(memberId)) {
            log.warn("[REQUEST_AUTOBIOGRAPHY_GENERATE] 자서전 소유자 불일치 - memberId: {}, ownerId: {}", memberId, autobiography.getMember().getId());
            throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER.toServiceException();
        }

        if (autobiography.getAutobiographyStatus().getStatus() != AutobiographyStatusType.ENOUGH) {
            log.warn("[REQUEST_AUTOBIOGRAPHY_GENERATE] 자서전 상태 불충분 - autobiographyId: {}, status: {}", autobiographyId, autobiography.getAutobiographyStatus().getStatus());
            throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_ENOUTH_STATUS_NOT_FOUND.toServiceException();
        }

        // 자서전 생성 요청
        log.info("[REQUEST_AUTOBIOGRAPHY_GENERATE] 자서전 생성 큐 전송 시작 - autobiographyId: {}", autobiographyId);
        autobiographyCompletionService.triggerPublicationRequest(autobiography, requestDto.getName());
        log.info("[REQUEST_AUTOBIOGRAPHY_GENERATE] 자서전 생성 요청 완료 - autobiographyId: {}", autobiographyId);
    }

    public void patchAutobiographyStatus(Long memberId, Long autobiographyId, String status) {
        log.info("[PATCH_AUTOBIOGRAPHY_STATUS] 자서전 상태 변경 시작 - memberId: {}, autobiographyId: {}, requestedStatus: {}", memberId, autobiographyId, status);
        
        Autobiography autobiography = autobiographyRepository.findById(autobiographyId)
                .orElseThrow(AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND::toServiceException);

        /*
        AutobiographyStatus autobiographyStatus = autobiographyStatusRepository
                .findTopByMemberIdAndStatusInOrderByUpdatedAtDesc(
                        memberId,
                        List.of(AutobiographyStatusType.EMPTY, AutobiographyStatusType.PROGRESSING)
                )
                .orElseThrow(AutobiographyExceptionStatus.AUTOBIOGRAPHY_STATUS_NOT_FOUND::toServiceException);
*/

        log.info("[PATCH_AUTOBIOGRAPHY_STATUS] 현재 상태 확인 - autobiographyId: {}, currentStatus: {}", autobiographyId, autobiography.getAutobiographyStatus().getStatus());
        
        if (!autobiography.getMember().getId().equals(memberId)) {
            log.warn("[PATCH_AUTOBIOGRAPHY_STATUS] 자서전 소유자 불일치 - memberId: {}, ownerId: {}", memberId, autobiography.getMember().getId());
            throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER.toServiceException();
        }
        // TODO: FINISH, CREATING인 경우, 상태 변경이 불가능합니다.

        AutobiographyStatusType newStatus = AutobiographyStatusType.valueOf(status);

        autobiography.getAutobiographyStatus().updateStatusType(
                newStatus,
                LocalDateTime.now()
        );
        log.info("[PATCH_AUTOBIOGRAPHY_STATUS] 자서전 상태 변경 완료 - autobiographyId: {}, newStatus: {}", autobiographyId, newStatus);
    }

    public void deleteAutobiography(Long memberId, Long autobiographyId) {
		log.info("[DELETE_AUTOBIOGRAPHY] 자서전 삭제 시작 - memberId: {}, autobiographyId: {}", memberId, autobiographyId);
		
		Autobiography autobiography = autobiographyRepository.findById(autobiographyId)
				.orElseThrow(AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND::toServiceException);
		
		if (!autobiography.getMember().getId().equals(memberId)) {
			log.warn("[DELETE_AUTOBIOGRAPHY] 자서전 소유자 불일치 - memberId: {}, ownerId: {}", memberId, autobiography.getMember().getId());
			throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER.toServiceException();
		}
		
		autobiographyRepository.delete(autobiography);
		log.info("[DELETE_AUTOBIOGRAPHY] 자서전 삭제 완료 - autobiographyId: {}", autobiographyId);
	}

    // ------------------------------------------------------------------
    // CoShow용 서비스 메서드
    public void coShowRequestAutobiographyGenerate(Long autobiographyId, CoShowAutobiographyGenerateRequestDto requestDto) {
        log.info("[COSHOW_REQUEST_AUTOBIOGRAPHY_GENERATE] CoShow 자서전 생성 요청 시작 - autobiographyId: {}, name: {}", autobiographyId, requestDto.getName());

        Autobiography autobiography = autobiographyRepository.findById(autobiographyId)
                .orElseThrow(AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND::toServiceException);

        log.info("[COSHOW_REQUEST_AUTOBIOGRAPHY_GENERATE] 자서전 조회 완료 - autobiographyId: {}", autobiographyId);

        // 자서전 생성 요청
        log.info("[COSHOW_REQUEST_AUTOBIOGRAPHY_GENERATE] 자서전 생성 큐 전송 시작 - autobiographyId: {}", autobiographyId);
        autobiographyCompletionService.coShowTriggerPublicationRequest(autobiography, requestDto.getName());
        log.info("[COSHOW_REQUEST_AUTOBIOGRAPHY_GENERATE] CoShow 자서전 생성 요청 완료 - autobiographyId: {}", autobiographyId);
    }


    // CoShow용 - 자서전 초기화
    public AutobiographyInitResponseDto coShowInitAutobiography(AutobiographyInitRequestDto requestDto) {
        log.info("[COSHOW_INIT_AUTOBIOGRAPHY] CoShow 자서전 초기화 시작 - theme: {}", requestDto.getTheme());
        
        LocalDateTime now = LocalDateTime.now();

        String tempEmail = "coshow1234@test.com";

        // 임시 회원 조회 또는 생성
        Member member = memberRepository.findByEmail(tempEmail)
                .orElseGet(() -> {
                    log.warn("[COSHOW_INIT_AUTOBIOGRAPHY] 임시 회원 미존재 - email: {}, 신규 생성 시작", tempEmail);
                    PasswordMember passwordMember = PasswordMember.of("coshow1234");
                    passwordMemberRepository.save(passwordMember);
                    log.info("[REGISTER_EMAIL] PasswordMember 저장 완료 - id: {}", passwordMember.getId());

                    Member newMember = Member.of(
                            LoginType.PASSWORD,
                            tempEmail,
                            MemberRole.MEMBER,
                            null,
                            tempEmail,
                            now,
                            now,
                            null
                    );
                    newMember.addPasswordMember(passwordMember);
                    memberRepository.save(newMember);

                    MemberMetadata matadata = MemberMetadata.ofV2(
                            "coshow",
                            GenderType.FEMALE,
                            "30",
                            "학생",
                            newMember
                    );

                    memberMetadataRepository.save(matadata);

                    return newMember;
                });

        log.info("[COSHOW_INIT_AUTOBIOGRAPHY] 임시 회원 조회 완료 - memberId: {}", member.getId());

        if (requestDto.getReason() != null && requestDto.getReason().length() > 500) {
            log.warn("[COSHOW_INIT_AUTOBIOGRAPHY] 자서전 이유 길이 초과 - length: {}", requestDto.getReason().length());
            throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_REASON_LENGTH_EXCEEDED.toServiceException();
        }

        Autobiography autobiography = Autobiography.ofV2(
                null,
                null,
                null,

                requestDto.getTheme(),
                requestDto.getReason(),
                now,
                now,
                member
        );
        // save init autobiography
        Autobiography savedAutobiography = autobiographyRepository.save(autobiography);
        log.info("[COSHOW_INIT_AUTOBIOGRAPHY] 자서전 저장 완료 - autobiographyId: {}", savedAutobiography.getId());

        AutobiographyStatus autobiographyStatus = AutobiographyStatus.of(
                AutobiographyStatusType.PROGRESSING,
                member,
                autobiography,
                now
        );

        autobiographyStatusRepository.save(autobiographyStatus);
        log.info("[COSHOW_INIT_AUTOBIOGRAPHY] 자서전 상태 저장 완료 - status: PROGRESSING");

        // AI 데이터 기반으로 분류 체계 초기화
        // classificationInitService.initializeFromAiData(autobiography);
        // log.info("[COSHOW_INIT_AUTOBIOGRAPHY] 분류 체계 초기화 완료 - autobiographyId: {}", savedAutobiography.getId());

        // save init interview
        Interview interview = Interview.ofV2(
                now,
                autobiography,
                member,
                null
        );

        Interview savedInterview = interviewRepository.save(interview);
        log.info("[COSHOW_INIT_AUTOBIOGRAPHY] 인터뷰 저장 완료 - interviewId: {}", savedInterview.getId());

        // interview question을 dummy로 5개 주입
        log.info("[COSHOW_INIT_AUTOBIOGRAPHY] 더미 질문 생성 시작 - count: {}", DUMMY_QUESTIONS.size());

        for (int i = 1; i <= 5; i++) {
            String questionText = DUMMY_QUESTIONS.get(i - 1);
            String materials = DUMMY_MATERIALS.get(i - 1);

            InterviewQuestion interviewQuestion = InterviewQuestion.ofV2(
                    i,
                    questionText,
                    materials,
                    now,
                    savedInterview
            );

            interviewQuestionRepository.save(interviewQuestion);
        }

        log.info("[COSHOW_INIT_AUTOBIOGRAPHY] 더미 질문 생성 완료 - 최종 생성된 질문 개수 : {}",
                interviewQuestionRepository.findAllByInterviewId(savedInterview.getId()).size());

        InterviewQuestion firstQuestion = interviewQuestionRepository.findByInterviewIdAndQuestionOrder(
                savedInterview.getId(),
                1
        ).orElseThrow(() -> {
            log.error("[COSHOW_INIT_AUTOBIOGRAPHY] 첫 번째 질문 조회 실패 - interviewId: {}, questionOrder: 1", savedInterview.getId());
            return InterviewExceptionStatus.INTERVIEW_QUESTION_NOT_FOUND.toServiceException();
        });

        Conversation conversation = Conversation.ofV2(
                firstQuestion.getQuestionText(),
                ConversationType.BOT,
                firstQuestion.getMaterials(),
                savedInterview,
                now
        );

        conversationRepository.save(conversation);
        log.info("[COSHOW_INIT_AUTOBIOGRAPHY] 첫 번째 대화 저장 완료 - conversationId: {}", conversation.getId());

        // current question 세팅
        savedInterview.setCurrentQuestion(firstQuestion);
        log.info("[COSHOW_INIT_AUTOBIOGRAPHY] 현재 질문 설정 완료 - questionId: {}", firstQuestion.getId());

        log.info("[COSHOW_INIT_AUTOBIOGRAPHY] CoShow 자서전 초기화 완료 - autobiographyId: {}, interviewId: {}", 
                savedAutobiography.getId(), savedInterview.getId());

        return AutobiographyInitResponseDto.builder()
                .autobiographyId(savedAutobiography.getId())
                .interviewId(savedInterview.getId())
                .build();
    }

    // CoShow용 - 더미 질문 리스트 반환
    private static final List<String> DUMMY_QUESTIONS = List.of(
            "요즘 가장 큰 고민이나 걱정은 무엇인가요?",
            "가장 기억에 남는 어린 시절 에피소드는 무엇인가요?",
            "당신에게 가장 큰 영향을 준 사람은 누구인가요?",
            "최근에 스스로 뿌듯함을 느낀 순간은 언제인가요?",
            "앞으로 꼭 이루고 싶은 목표가 있다면 무엇인가요?"
    );

    // CoShow용 - 더미 자료 리스트 반환
    private static final List<String> DUMMY_MATERIALS = List.of(
            "[[1, 2, 3]]",
            "[[1, 2, 3]]",
            "[[1, 2, 3]]",
            "[[1, 2, 3]]",
            "[[1, 2, 3]]"
    );
}
