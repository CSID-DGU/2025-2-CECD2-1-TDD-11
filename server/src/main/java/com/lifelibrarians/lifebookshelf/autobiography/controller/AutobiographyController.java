package com.lifelibrarians.lifebookshelf.autobiography.controller;

import com.lifelibrarians.lifebookshelf.auth.dto.MemberSessionDto;
import com.lifelibrarians.lifebookshelf.auth.jwt.LoginMemberInfo;
import com.lifelibrarians.lifebookshelf.autobiography.dto.request.AutobiographyUpdateRequestDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.AutobiographyDetailResponseDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.AutobiographyListResponseDto;
import com.lifelibrarians.lifebookshelf.autobiography.service.AutobiographyFacadeService;
import com.lifelibrarians.lifebookshelf.exception.status.AutobiographyExceptionStatus;
import com.lifelibrarians.lifebookshelf.exception.annotation.ApiErrorCodeExample;
import com.lifelibrarians.lifebookshelf.log.Logging;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/autobiographies")
@Tag(name = "자서전 (Autobiography)", description = "자서전 관련 API")
@Logging
public class AutobiographyController {

	private final AutobiographyFacadeService autobiographyFacadeService;

	@Operation(summary = "자서전 목록 조회", description = "유저가 보유한 전체 자서전 목록을 pagination으로 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@PreAuthorize("isAuthenticated()")
	@GetMapping
	public AutobiographyListResponseDto getAutobiographies(
			@LoginMemberInfo MemberSessionDto memberSessionDto,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size
	) {
		return autobiographyFacadeService.getAutobiographies(memberSessionDto.getMemberId(), PageRequest.of(page, size));
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

