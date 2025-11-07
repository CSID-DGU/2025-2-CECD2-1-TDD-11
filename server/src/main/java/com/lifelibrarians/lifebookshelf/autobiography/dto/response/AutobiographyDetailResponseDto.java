package com.lifelibrarians.lifebookshelf.autobiography.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Builder
@AllArgsConstructor
@Getter
@Schema(description = "자서전 상세 정보")
@ToString
public class AutobiographyDetailResponseDto {

	@Schema(description = "자서전 ID", example = "1")
	private final Long autobiographyId;

	@Schema(description = "인터뷰 ID", example = "1")
	private final Long interviewId;

	@Schema(description = "자서전 제목", example = "My Early Life")
	private final String title;

	@Schema(description = "자서전 내용", example = "This is the story of my early life.")
	private final String content;

	@Schema(description = "표지 이미지 URL", example = "https://example.com/image1.jpg")
	private final String coverImageUrl;

	@Schema(description = "생성일시", example = "2023-01-01T00:00:00Z")
	private final LocalDateTime createdAt;

	@Schema(description = "수정일시", example = "2023-01-02T00:00:00Z")
	private final LocalDateTime updatedAt;
}
