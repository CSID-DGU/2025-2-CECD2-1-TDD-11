package com.lifelibrarians.lifebookshelf.autobiography.service;

import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatus;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatusType;
import com.lifelibrarians.lifebookshelf.autobiography.dto.request.AutobiographyInitRequestDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.request.AutobiographyUpdateRequestDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.AutobiographyInitResponseDto;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyRepository;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyStatusRepository;
import com.lifelibrarians.lifebookshelf.classification.service.ClassificationInitService;
import com.lifelibrarians.lifebookshelf.exception.status.AuthExceptionStatus;
import com.lifelibrarians.lifebookshelf.exception.status.AutobiographyExceptionStatus;
import com.lifelibrarians.lifebookshelf.image.service.ImageService;
import com.lifelibrarians.lifebookshelf.interview.domain.Interview;
import com.lifelibrarians.lifebookshelf.interview.repository.InterviewRepository;
import com.lifelibrarians.lifebookshelf.log.Logging;
import java.time.LocalDateTime;
import java.util.Objects;

import com.lifelibrarians.lifebookshelf.member.domain.Member;
import com.lifelibrarians.lifebookshelf.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
@Logging
public class AutobiographyCommandService {

	private final AutobiographyRepository autobiographyRepository;
    private final AutobiographyStatusRepository autobiographyStatusRepository;
    private final InterviewRepository interviewRepository;
    private final MemberRepository memberRepository;
	private final ImageService imageService;

    private final ClassificationInitService classificationInitService;

	@Value("${images.path.bio-cover}")
	public String BIO_COVER_IMAGE_DIR;

    public AutobiographyInitResponseDto initAutobiography(Long memberId, AutobiographyInitRequestDto requestDto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(AuthExceptionStatus.MEMBER_NOT_FOUND::toServiceException);

        LocalDateTime now = LocalDateTime.now();

        if (requestDto.getReason() != null && requestDto.getReason().length() > 500) {
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

        AutobiographyStatus autobiographyStatus = AutobiographyStatus.of(
                AutobiographyStatusType.EMPTY,
                member,
                autobiography,
                now
        );

        autobiographyStatusRepository.save(autobiographyStatus);

        // AI 데이터 기반으로 분류 체계 초기화
        classificationInitService.initializeFromAiData(autobiography);

        // save init interview
        Interview interview = Interview.ofV2(
                now,
                autobiography,
                member,
                null
        );

        Interview savedInterview = interviewRepository.save(interview);

        return AutobiographyInitResponseDto.builder()
                .autobiographyId(savedAutobiography.getId())
                .interviewId(savedInterview.getId())
                .build();
    }

	public void patchAutobiography(Long memberId, Long autobiographyId, AutobiographyUpdateRequestDto requestDto) {
		Autobiography autobiography = autobiographyRepository.findById(autobiographyId)
				.orElseThrow(AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND::toServiceException);
		if (!autobiography.getMember().getId().equals(memberId)) {
			throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER.toServiceException();
		}
		String preSignedImageUrl = null;
		if (!Objects.isNull(requestDto.getPreSignedCoverImageUrl()) && !requestDto.getPreSignedCoverImageUrl().isBlank()) {
			preSignedImageUrl = imageService.parseImageUrl(requestDto.getPreSignedCoverImageUrl(), BIO_COVER_IMAGE_DIR);
		}
		autobiography.updateAutoBiography(requestDto.getTitle(), requestDto.getContent(), preSignedImageUrl, LocalDateTime.now());
	}

    public void patchReasonAutobiography(Long memberId, Long autobiographyId, AutobiographyInitRequestDto requestDto) {
        Autobiography autobiography = autobiographyRepository.findById(autobiographyId)
                .orElseThrow(AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND::toServiceException);
        if (!autobiography.getMember().getId().equals(memberId)) {
            throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER.toServiceException();
        }

        if (requestDto.getReason() != null && requestDto.getReason().length() > 500) {
            throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_REASON_LENGTH_EXCEEDED.toServiceException();
        }

        LocalDateTime now = LocalDateTime.now();

        autobiography.updateAutoBiographyV2(autobiography.getTitle(), autobiography.getContent(), autobiography.getCoverImageUrl(), autobiography.getTheme(), requestDto.getReason(), now);
    }

    public void requestAutobiographyGenerate(Long memberId, Long autobiographyId) {
        Autobiography autobiography = autobiographyRepository.findById(autobiographyId)
                .orElseThrow(AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND::toServiceException);
        if (!autobiography.getMember().getId().equals(memberId)) {
            throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER.toServiceException();
        }

        if (autobiography.getAutobiographyStatus().getStatus() != AutobiographyStatusType.ENOUGH) {
            throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_ENOUTH_STATUS_NOT_FOUND.toServiceException();
        }

        autobiography.getAutobiographyStatus().updateStatusType(
                AutobiographyStatusType.CREATING,
                LocalDateTime.now()
        );
    }

    public void patchAutobiographyReady(Long memberId, Long autobiographyId) {
        Autobiography autobiography = autobiographyRepository.findById(autobiographyId)
                .orElseThrow(AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND::toServiceException);
        if (!autobiography.getMember().getId().equals(memberId)) {
            throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER.toServiceException();
        }

        if (autobiography.getAutobiographyStatus().getStatus() != AutobiographyStatusType.PROGRESSING) {
            throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_ENOUTH_STATUS_NOT_FOUND.toServiceException();
        }

        autobiography.getAutobiographyStatus().updateStatusType(
                AutobiographyStatusType.ENOUGH,
                LocalDateTime.now()
        );
    }

    public void deleteAutobiography(Long memberId, Long autobiographyId) {
		Autobiography autobiography = autobiographyRepository.findById(autobiographyId)
				.orElseThrow(AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND::toServiceException);
		if (!autobiography.getMember().getId().equals(memberId)) {
			throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER.toServiceException();
		}
		autobiographyRepository.delete(autobiography);
	}
}
