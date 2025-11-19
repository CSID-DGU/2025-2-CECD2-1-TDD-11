package com.lifelibrarians.lifebookshelf.autobiography.controller;

import com.lifelibrarians.lifebookshelf.auth.dto.MemberSessionDto;
import com.lifelibrarians.lifebookshelf.auth.jwt.LoginMemberInfo;
import com.lifelibrarians.lifebookshelf.autobiography.dto.request.AutobiographyInitRequestDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.request.AutobiographyUpdateRequestDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.*;
import com.lifelibrarians.lifebookshelf.autobiography.service.AutobiographyFacadeService;
import com.lifelibrarians.lifebookshelf.exception.status.AutobiographyExceptionStatus;
import com.lifelibrarians.lifebookshelf.exception.annotation.ApiErrorCodeExample;
import com.lifelibrarians.lifebookshelf.exception.status.CommonExceptionStatus;
import com.lifelibrarians.lifebookshelf.log.Logging;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/autobiographies")
@Tag(name = "자서전 (Autobiography)", description = "자서전 관련 API")
@Logging
public class AutobiographyController {

	private final AutobiographyFacadeService autobiographyFacadeService;

    @Operation(summary = "테마와 자서전 생성 이유 등록 요청", description = "온보딩과 자서전을 최초 생성하는 시점에서, 자서전에 대한 테마와 생성 이유를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "created"),
    })
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/init", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public AutobiographyInitResponseDto initAutobiography(
            @LoginMemberInfo MemberSessionDto memberSessionDto,
            @Valid @ModelAttribute AutobiographyInitRequestDto requestDto
    ) {
        return autobiographyFacadeService.initAutobiography(memberSessionDto.getMemberId(), requestDto);
    }

	@Operation(summary = "자서전 목록 조회", description = "유저가 보유한 전체 자서전 목록을 pagination으로 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@PreAuthorize("isAuthenticated()")
	@GetMapping
    @ResponseStatus(HttpStatus.OK)
	public AutobiographyListResponseDto getAutobiographies(
			@LoginMemberInfo MemberSessionDto memberSessionDto,
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "size", defaultValue = "10") int size
	) {
		return autobiographyFacadeService.getAutobiographies(memberSessionDto.getMemberId(), PageRequest.of(page, size));
	}

    @Operation(summary = "특정 자서전에서 count된 소재를 오름차순으로 반환", description = "auto id에 맞는 자서전에서 count 된 모든 소재에서 가 해당 자서전에서 count 된 소재를 count(가중치) 기준 내림차순으로 반환한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok"),
    })
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{autobiographyId}/materials")
    @ResponseStatus(HttpStatus.OK)
    public AutobiographyMaterialsResponseDto getAutobiographyMaterials(
            @LoginMemberInfo MemberSessionDto memberSessionDto,
            @PathVariable("autobiographyId") @Parameter(description = "자서전 ID") Long autobiographyId,
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ) {
        return autobiographyFacadeService.getAutobiographyMaterials(memberSessionDto.getMemberId(), autobiographyId, PageRequest.of(page, size));
    }


    @Operation(summary = "특정 자서전 상세 조회", description = "자서전 id로 자서전의 상세 정보를 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@ApiErrorCodeExample(
			autobiographyExceptionStatuses = {
					AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND,
					AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER
			}
	)
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{autobiographyId}")
	public AutobiographyDetailResponseDto getAutobiography(
			@LoginMemberInfo MemberSessionDto memberSessionDto,
			@PathVariable("autobiographyId") @Parameter(description = "자서전 ID") Long autobiographyId
	) {
		return autobiographyFacadeService.getAutobiography(memberSessionDto.getMemberId(), autobiographyId);
	}

	@Operation(summary = "특정 자서전 수정 요청", description = "자서전 id로 자서전의 title, content, image를 수정합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@ApiErrorCodeExample(
			autobiographyExceptionStatuses = {
					AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND,
					AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER,
					AutobiographyExceptionStatus.AUTOBIOGRAPHY_TITLE_LENGTH_EXCEEDED,
					AutobiographyExceptionStatus.AUTOBIOGRAPHY_CONTENT_LENGTH_EXCEEDED,
			}
	)
	@PreAuthorize("isAuthenticated()")
	@PostMapping(value = "/{autobiographyId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public void updateAutobiography(
			@LoginMemberInfo MemberSessionDto memberSessionDto,
			@PathVariable("autobiographyId") @Parameter(description = "자서전 ID") Long autobiographyId,
			@Valid @ModelAttribute AutobiographyUpdateRequestDto requestDto
	) {
		autobiographyFacadeService.patchAutobiography(memberSessionDto.getMemberId(), autobiographyId, requestDto);
	}

    @Operation(summary = "특정 자서전의 생성 이유 수정 요청", description = "자서전 id로 자서전의 reason를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok"),
    })
    @ApiErrorCodeExample(
            autobiographyExceptionStatuses = {
                    AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND,
                    AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER,
                    AutobiographyExceptionStatus.AUTOBIOGRAPHY_REASON_LENGTH_EXCEEDED
            }
    )
    @PreAuthorize("isAuthenticated()")
    @PostMapping(value = "/{autobiographyId}/reason", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateReasonAutobiography(
            @LoginMemberInfo MemberSessionDto memberSessionDto,
            @PathVariable("autobiographyId") @Parameter(description = "자서전 ID") Long autobiographyId,
            @Valid @ModelAttribute AutobiographyInitRequestDto requestDto
    ) {
        autobiographyFacadeService.patchReasonAutobiography(memberSessionDto.getMemberId(), autobiographyId, requestDto);
    }

    @Operation(summary = "현재 진행중인 자서전 id 조회", description = "자서전 상태가 PROGRESSING인 자서전 중 updatedAt이 가장 최신인 자서전의 id를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ok"),
    })
    @ApiErrorCodeExample(
            autobiographyExceptionStatuses = {
                    AutobiographyExceptionStatus.AUTOBIOGRAPHY_STATUS_NOT_FOUND,
                    AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER
            }
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/current")
    public AutobiographyCurrentResponseDto getCurrentAutobiography(
            @LoginMemberInfo MemberSessionDto memberSessionDto
    ) {
        return autobiographyFacadeService.getCurrentAutobiography(memberSessionDto.getMemberId());
    }

	@Operation(summary = "특정 자서전 삭제 요청", description = "자서전 id로 자서전을 삭제합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@ApiErrorCodeExample(
			autobiographyExceptionStatuses = {
					AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_FOUND,
					AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_OWNER
			}
	)
	@PreAuthorize("isAuthenticated()")
	@DeleteMapping("/{autobiographyId}")
	public void deleteAutobiography(
			@LoginMemberInfo MemberSessionDto memberSessionDto,
			@PathVariable("autobiographyId") @Parameter(description = "자서전 ID") Long autobiographyId
	) {
		autobiographyFacadeService.deleteAutobiography(memberSessionDto.getMemberId(), autobiographyId);
	}
}

