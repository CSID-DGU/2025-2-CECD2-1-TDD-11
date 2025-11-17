package com.lifelibrarians.lifebookshelf.autobiography.service;

import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.AutobiographyDetailResponseDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.AutobiographyListResponseDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.AutobiographyPreviewDto;
import com.lifelibrarians.lifebookshelf.autobiography.repository.AutobiographyRepository;
import com.lifelibrarians.lifebookshelf.exception.status.AutobiographyExceptionStatus;
import com.lifelibrarians.lifebookshelf.log.Logging;
import com.lifelibrarians.lifebookshelf.mapper.AutobiographyMapper;
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
public class AutobiographyQueryService {

	private final AutobiographyRepository autobiographyRepository;
	private final AutobiographyMapper autobiographyMapper;

	public AutobiographyListResponseDto getAutobiographies(Long memberId, Pageable pageable) {
		Page<Autobiography> autobiographyPage = autobiographyRepository.findWithInterviewByMemberId(memberId, pageable);
		List<AutobiographyPreviewDto> autobiographyPreviewDtos = autobiographyPage.getContent().stream()
				.map(autobiography -> autobiographyMapper.toAutobiographyPreviewDto(
						autobiography,
						autobiography.getChapter().getId(),
						autobiography.getAutobiographyInterviews().get(0).getId()
				))
				.collect(Collectors.toList());
		return AutobiographyListResponseDto.builder()
				.results(autobiographyPreviewDtos)
				.build();
	}

	public AutobiographyDetailResponseDto getAutobiography(Long memberId, Long autobiographyId) {
		Autobiography autobiography = autobiographyRepository.findWithInterviewById(autobiographyId)
				.orElseThrow(
						AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND::toServiceException);
		if (!autobiography.getMember().getId().equals(memberId)) {
			throw AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER.toServiceException();
		}
		return autobiographyMapper.toAutobiographyDetailResponseDto(autobiography,
				autobiography.getAutobiographyInterviews().get(0).getId());
	}
}
