package com.lifelibrarians.lifebookshelf.autobiography.service;

import com.lifelibrarians.lifebookshelf.autobiography.dto.request.AutobiographyUpdateRequestDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.AutobiographyDetailResponseDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.AutobiographyListResponseDto;
import com.lifelibrarians.lifebookshelf.log.Logging;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Logging
public class AutobiographyFacadeService {

	private final AutobiographyQueryService autobiographyQueryService;
	private final AutobiographyCommandService autobiographyCommandService;

	public AutobiographyListResponseDto getAutobiographies(Long memberId, Pageable pageable) {
		return autobiographyQueryService.getAutobiographies(memberId, pageable);
	}

	public AutobiographyDetailResponseDto getAutobiography(Long memberId, Long autobiographyId) {
		return autobiographyQueryService.getAutobiography(memberId, autobiographyId);
	}

	public void patchAutobiography(Long memberId, Long autobiographyId, AutobiographyUpdateRequestDto requestDto) {
		autobiographyCommandService.patchAutobiography(memberId, autobiographyId, requestDto);
	}

	public void deleteAutobiography(Long memberId, Long autobiographyId) {
		autobiographyCommandService.deleteAutobiography(memberId, autobiographyId);
	}
}
