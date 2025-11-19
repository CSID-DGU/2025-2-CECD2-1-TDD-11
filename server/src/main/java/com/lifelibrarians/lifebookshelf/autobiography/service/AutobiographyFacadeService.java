package com.lifelibrarians.lifebookshelf.autobiography.service;

import com.lifelibrarians.lifebookshelf.autobiography.dto.request.AutobiographyInitRequestDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.request.AutobiographyUpdateRequestDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.*;
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

    public AutobiographyInitResponseDto initAutobiography(Long memberId, AutobiographyInitRequestDto requestDto) {
        return autobiographyCommandService.initAutobiography(memberId, requestDto);
    }

	public AutobiographyListResponseDto getAutobiographies(Long memberId, Pageable pageable) {
		return autobiographyQueryService.getAutobiographies(memberId, pageable);
	}

    public AutobiographyMaterialsResponseDto getAutobiographyMaterials(Long memberId, Long autobiographyId, Pageable pageable) {
        return autobiographyQueryService.getAutobiographyMaterials(memberId, autobiographyId, pageable);
    }

	public AutobiographyDetailResponseDto getAutobiography(Long memberId, Long autobiographyId) {
		return autobiographyQueryService.getAutobiography(memberId, autobiographyId);
	}

    public AutobiographyCurrentResponseDto getCurrentAutobiography(Long memberId) {
        return autobiographyQueryService.getCurrentAutobiography(memberId);
    }

	public void patchAutobiography(Long memberId, Long autobiographyId, AutobiographyUpdateRequestDto requestDto) {
		autobiographyCommandService.patchAutobiography(memberId, autobiographyId, requestDto);
	}

    public void patchReasonAutobiography(Long memberId, Long autobiographyId, AutobiographyInitRequestDto requestDto) {
        autobiographyCommandService.patchReasonAutobiography(memberId, autobiographyId, requestDto);
    }

	public void deleteAutobiography(Long memberId, Long autobiographyId) {
		autobiographyCommandService.deleteAutobiography(memberId, autobiographyId);
	}
}
