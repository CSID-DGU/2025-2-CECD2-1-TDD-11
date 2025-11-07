package com.lifelibrarians.lifebookshelf.autobiography.controller;

import com.lifelibrarians.lifebookshelf.auth.dto.MemberSessionDto;
import com.lifelibrarians.lifebookshelf.auth.jwt.LoginMemberInfo;
import com.lifelibrarians.lifebookshelf.autobiography.dto.request.AutobiographyCreateRequestDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.request.AutobiographyUpdateRequestDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.request.ChapterCreateRequestDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.AutobiographyDetailResponseDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.AutobiographyListResponseDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.response.ChapterListResponseDto;
import com.lifelibrarians.lifebookshelf.autobiography.service.AutobiographyFacadeService;
import com.lifelibrarians.lifebookshelf.exception.status.AutobiographyExceptionStatus;
import com.lifelibrarians.lifebookshelf.exception.annotation.ApiErrorCodeExample;
import com.lifelibrarians.lifebookshelf.exception.status.InterviewExceptionStatus;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/autobiographies")
@Tag(name = "자서전 (Autobiography)", description = "자서전 관련 API")
@Logging
public class AutobiographyController {

	private final AutobiographyFacadeService autobiographyFacadeService;

	@Operation(summary = "자서전 챕터 목록 생성", description = "자서전 챕터 목록을 생성합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "created"),
	})
	@ApiErrorCodeExample(
			autobiographyExceptionStatuses = {
					AutobiographyExceptionStatus.SUBCHAPTER_NUMBER_INVALID,
					AutobiographyExceptionStatus.CHAPTER_NUMBER_FORMAT_INVALID,
					AutobiographyExceptionStatus.CHAPTER_NAME_LENGTH_EXCEEDED,
					AutobiographyExceptionStatus.CHAPTER_DESCRIPTION_LENGTH_EXCEEDED,
					AutobiographyExceptionStatus.CHAPTER_ALREADY_EXISTS,
					AutobiographyExceptionStatus.CHAPTER_SIZE_EXCEEDED,
					AutobiographyExceptionStatus.CHAPTER_NUMBER_DUPLICATED,
			}
	)
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/chapters")
	@ResponseStatus(HttpStatus.CREATED)
	public void createChapters(
			@LoginMemberInfo MemberSessionDto memberSessionDto,
			@Valid @RequestBody ChapterCreateRequestDto requestDto
	) {
		autobiographyFacadeService.createChapters(memberSessionDto.getMemberId(), requestDto);
	}

	@Operation(summary = "자서전 챕터 목록 조회", description = "자서전 챕터 목록을 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@PreAuthorize("isAuthenticated()")
	@GetMapping("/chapters")
	// FIXME: 페이지네이션 지원 여부에 대해 고민이 필요합니다. 우선, page와 size를 제공하더라도 페이지네이션 관련 응답을 제공하지 않습니다.
	public ChapterListResponseDto getChapters(
			@LoginMemberInfo MemberSessionDto memberSessionDto,
			@RequestParam(value = "page", defaultValue = "0") int page,
			@RequestParam(value = "size", defaultValue = "10") int size
	) {
		return autobiographyFacadeService.getChapters(memberSessionDto.getMemberId(),
				PageRequest.of(page, size));
	}

	@Operation(summary = "현재 진행중인 챕터를 다음 챕터로 갱신 요청", description = "현재 진행중인 챕터를 다음 챕터로 갱신합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@ApiErrorCodeExample(
			autobiographyExceptionStatuses = {
					AutobiographyExceptionStatus.CHAPTER_NOT_EXISTS,
					AutobiographyExceptionStatus.NEXT_CHAPTER_NOT_FOUND,
					AutobiographyExceptionStatus.AUTOBIOGRAPHY_NOT_EXISTS
			}
	)
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/chapters/current-chapter")
	public void updateCurrentChapter(
			@LoginMemberInfo MemberSessionDto memberSessionDto
	) {
		autobiographyFacadeService.updateCurrentChapter(memberSessionDto.getMemberId());
	}

	@Operation(summary = "전체 자서전 목록 조회", description = "자서전 목록을 조회합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "ok"),
	})
	@ApiErrorCodeExample(
			autobiographyExceptionStatuses = {
					AutobiographyExceptionStatus.CHAPTER_NOT_FOUND,
					AutobiographyExceptionStatus.CHAPTER_NOT_OWNER
			}
	)
	@PreAuthorize("isAuthenticated()")
	@GetMapping
	public AutobiographyListResponseDto getChapterAutobiographies(
			@LoginMemberInfo MemberSessionDto memberSessionDto
	) {
		return autobiographyFacadeService.getAutobiographies(memberSessionDto.getMemberId());
	}

	@Operation(summary = "자서전 생성 요청", description = "새 자서전을 생성합니다.")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "created"),
	})
	@ApiErrorCodeExample(
			autobiographyExceptionStatuses = {
					AutobiographyExceptionStatus.AUTOBIOGRAPHY_TITLE_LENGTH_EXCEEDED,
					AutobiographyExceptionStatus.AUTOBIOGRAPHY_CONTENT_LENGTH_EXCEEDED,
					AutobiographyExceptionStatus.NEXT_CHAPTER_NOT_FOUND
			},
			interviewExceptionStatuses = {
					InterviewExceptionStatus.INTERVIEW_QUESTION_TEXT_LENGTH_EXCEEDED,
					InterviewExceptionStatus.INTERVIEW_QUESTION_ORDER_DUPLICATED,
			}
	)
	@PreAuthorize("isAuthenticated()")
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public void createAutobiography(
			@LoginMemberInfo MemberSessionDto memberSessionDto,
			@Valid @RequestBody AutobiographyCreateRequestDto requestDto
	) {
		autobiographyFacadeService.createAutobiography(memberSessionDto.getMemberId(), requestDto);
	}

	@Operation(summary = "특정 자서전 상세 조회", description = "특정 자서전의 상세 정보를 조회합니다.")
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
		return autobiographyFacadeService.getAutobiography(memberSessionDto.getMemberId(),
				autobiographyId);
	}

	@Operation(summary = "자서전 수정 요청", description = "특정 자서전을 수정합니다.")
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
		autobiographyFacadeService.patchAutobiography(memberSessionDto.getMemberId(),
				autobiographyId, requestDto);
	}

	@Operation(summary = "자서전 삭제 요청", description = "특정 자서전을 삭제합니다.")
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
		autobiographyFacadeService.deleteAutobiography(memberSessionDto.getMemberId(),
				autobiographyId);
	}
}

