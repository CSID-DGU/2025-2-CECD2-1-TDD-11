package utils.testdouble.chapter;

import com.lifelibrarians.lifebookshelf.autobiography.dto.request.ChapterRequestDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.request.SubchapterRequestDto;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestChapterCreateRequestDto {

	// 빈 챕터를 생성하는 메소드
	public static List<ChapterRequestDto> createEmptyChapters() {
		return List.of();
	}

	// 16자리 초과의 챕터를 생성하는 메소드
	public static List<ChapterRequestDto> createTooManyChapters() {
		return IntStream.range(0, 17)  // 17개의 챕터를 생성
				.mapToObj(i -> ChapterRequestDto.builder()
						.number(String.valueOf(i + 1))
						.name("Chapter " + (i + 1))
						.description("This is chapter " + (i + 1) + ".")
						.build())
				.collect(Collectors.toList());
	}

	// 챕터 번호 포맷이 유효하지 않은 챕터를 생성하는 메소드
	public static List<ChapterRequestDto> createInvalidNumberChapters() {
		return List.of(
				ChapterRequestDto.builder()
						.number("blabla")
						.name("Chapter 1")
						.description("This is chapter 1.")
						.build()
		);
	}

	// 챕터 번호가 중복되는 챕터를 생성하는 메소드
	public static List<ChapterRequestDto> createDuplicatedNumberChapters() {
		return Arrays.asList(
				ChapterRequestDto.builder()
						.number("1")
						.name("Chapter 1")
						.description("This is chapter 1.")
						.build(),
				ChapterRequestDto.builder()
						.number("1")
						.name("Chapter 2")
						.description("This is chapter 2.")
						.build()
		);
	}

	// 챕터 이름이 64자를 초과하는 챕터를 생성하는 메소드
	public static List<ChapterRequestDto> createTooLongNameChapters() {
		return List.of(
				ChapterRequestDto.builder()
						.number("1")
						.name("a".repeat(65))
						.description("This is chapter 1.")
						.build()
		);
	}

	// 챕터 설명이 64자를 초과하는 챕터를 생성하는 메소드
	public static List<ChapterRequestDto> createTooLongDescriptionChapters() {
		return List.of(
				ChapterRequestDto.builder()
						.number("1")
						.name("Chapter 1")
						.description("a".repeat(65))
						.build()
		);
	}

	// 서브챕터 번호가 중복되는 챕터를 생성하는 메소드
	public static List<ChapterRequestDto> createDuplicatedSubchapterNumberChapters() {
		return List.of(
				ChapterRequestDto.builder()
						.number("1")
						.name("Chapter 1")
						.description("This is chapter 1.")
						.subchapters(Arrays.asList(
								SubchapterRequestDto.builder()
										.number("1.1")
										.name("Subchapter 1")
										.description("This is subchapter 1.")
										.build(),
								SubchapterRequestDto.builder()
										.number("1.1")
										.name("Subchapter 2")
										.description("This is subchapter 2.")
										.build()
						))
						.build()
		);
	}

	// 서브챕터 이름이 64자를 초과하는 챕터를 생성하는 메소드
	public static List<ChapterRequestDto> createTooLongSubchapterNameChapters() {
		return List.of(
				ChapterRequestDto.builder()
						.number("1")
						.name("Chapter 1")
						.description("This is chapter 1.")
						.subchapters(List.of(
								SubchapterRequestDto.builder()
										.number("1.1")
										.name("a".repeat(65))
										.description("This is subchapter 1.")
										.build()
						))
						.build()
		);
	}

	// 서브챕터의 부모 챕터 번호와 일치하지 않는 챕터를 생성하는 메소드
	public static List<ChapterRequestDto> createInvalidSubchapterNumberChapters() {
		return List.of(
				ChapterRequestDto.builder()
						.number("1")
						.name("Chapter 1")
						.description("This is chapter 1.")
						.subchapters(List.of(
								SubchapterRequestDto.builder()
										.number("2.1")
										.name("Subchapter 1")
										.description("This is subchapter 1.")
										.build()
						))
						.build()
		);
	}

	// 유효한 챕터를 생성하는 메소드
	public static List<ChapterRequestDto> createValidChapters() {
		return Arrays.asList(
				ChapterRequestDto.builder()
						.number("1")
						.name("나의 첫번째 챕터")
						.description("나의 첫번째 챕터 설명")
						.subchapters(Arrays.asList(
								SubchapterRequestDto.builder()
										.number("1.1")
										.name("나의 첫번째 서브챕터")
										.description("나의 첫번째 서브챕터 설명")
										.build(),
								SubchapterRequestDto.builder()
										.number("1.2")
										.name("나의 두번째 서브챕터")
										.description("나의 두번째 서브챕터 설명")
										.build()
						))
						.build(),
				ChapterRequestDto.builder()
						.number("2")
						.name("나의 두번째 챕터")
						.description("나의 두번째 챕터 설명")
						.subchapters(Arrays.asList(
								SubchapterRequestDto.builder()
										.number("2.1")
										.name("나의 첫번째 서브챕터")
										.description("나의 첫번째 서브챕터 설명")
										.build(),
								SubchapterRequestDto.builder()
										.number("2.2")
										.name("나의 두번째 서브챕터")
										.description("나의 두번째 서브챕터 설명")
										.build()
						))
						.build()
		);
	}
}
