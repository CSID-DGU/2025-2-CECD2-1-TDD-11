package com.lifelibrarians.lifebookshelf.autobiography.service;

import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatus;
import com.lifelibrarians.lifebookshelf.autobiography.domain.AutobiographyStatusType;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.AutobiographyCurrentResponseDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.AutobiographyDetailResponseDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.AutobiographyListResponseDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.AutobiographyPreviewDto;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyRepository;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyStatusRepository;
import com.lifelibrarians.lifebookshelf.exception.status.AutobiographyExceptionStatus;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.mapper.AutobiographyMapper;
import java.util.List;
import java.util.Optional;
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
public class AutobiographyQueryService {

	private final AutobiographyRepository autobiographyRepository;
    private final AutobiographyStatusRepository autobiographyStatusRepository;
	private final AutobiographyMapper autobiographyMapper;

    public AutobiographyListResponseDto getAutobiographies(Long memberId, Pageable pageable) {

        Page<Autobiography> autobiographyPage =
                autobiographyRepository.findWithAtLeastOneInterview(memberId, pageable);

        List<AutobiographyPreviewDto> dtoList =
                autobiographyPage.getContent().stream()
                        .map(a -> autobiographyMapper.toAutobiographyPreviewDto(
                                a,
                                a.getAutobiographyStatus()   // 그냥 이거 한 줄이면 됨
                        ))
                        .collect(Collectors.toList());

        return AutobiographyListResponseDto.builder()
                .results(dtoList)
                .build();
    }

	public AutobiographyDetailResponseDto getAutobiography(Long memberId, Long autobiographyId) {
		Autobiography autobiography = autobiographyRepository.findWithInterviewById(autobiographyId)
				.orElseThrow(
						AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND::toServiceException);
		if (!autobiography.getMember().getId().equals(memberId)) {
			throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER.toServiceException();
		}
		return autobiographyMapper.toAutobiographyDetailResponseDto(autobiography);
	}

    public AutobiographyCurrentResponseDto getCurrentAutobiography(Long memberId) {
        AutobiographyStatus status = autobiographyStatusRepository
                .findTopByMemberIdAndStatusOrderByUpdatedAtDesc(
                        memberId,
                        AutobiographyStatusType.PROGRESSING
                )
                .orElseThrow(AutobiographyExceptionStatus.AUTOBIOGRAPHY_STATUS_NOT_FOUND::toServiceException);

        return autobiographyMapper.toAutobiographyCurrentResponseDto(status.getCurrentAutobiography());
    }
}
