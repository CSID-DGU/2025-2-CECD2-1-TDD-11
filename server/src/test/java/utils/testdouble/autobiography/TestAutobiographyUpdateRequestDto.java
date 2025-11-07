package utils.testdouble.autobiography;


import com.lifelibrarians.lifebookshelf.autobiography.dto.request.AutobiographyUpdateRequestDto;

public class TestAutobiographyUpdateRequestDto {

	public static String TOO_LONG_TITLE = "a".repeat(65);
	public static String TOO_LONG_CONTENT = "a".repeat(30001);

	public static AutobiographyUpdateRequestDto createTooLongTitleAutobiography() {
		return AutobiographyUpdateRequestDto.builder()
				.title(TOO_LONG_TITLE)
				.content(TestAutobiography.DEFAULT_CONTENT)
				.preSignedCoverImageUrl(TestAutobiography.DEFAULT_COVER_IMAGE_URL)
				.build();
	}

	public static AutobiographyUpdateRequestDto createTooLongContentAutobiography() {
		return AutobiographyUpdateRequestDto.builder()
				.title(TestAutobiography.DEFAULT_TITLE)
				.content(TOO_LONG_CONTENT)
				.preSignedCoverImageUrl(TestAutobiography.DEFAULT_COVER_IMAGE_URL)
				.build();
	}

	public static AutobiographyUpdateRequestDto createValidAutobiography() {
		return AutobiographyUpdateRequestDto.builder()
				.title(TestAutobiography.DEFAULT_TITLE)
				.content(TestAutobiography.DEFAULT_CONTENT)
				.preSignedCoverImageUrl(TestAutobiography.DEFAULT_COVER_IMAGE_URL)
				.build();
	}
}
