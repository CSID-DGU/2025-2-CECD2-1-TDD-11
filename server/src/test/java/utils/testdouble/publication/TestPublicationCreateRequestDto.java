package utils.testdouble.publication;

import com.lifelibrarians.lifebookshelf.publication.dto.request.PublicationCreateRequestDto;
import utils.testdouble.book.TestBook;

public class TestPublicationCreateRequestDto {

	public static String TOO_LONG_TITLE = "a".repeat(65);

	public static PublicationCreateRequestDto createNonBookTitlePublication() {
		return PublicationCreateRequestDto.builder()
				.preSignedCoverImageUrl(TestBook.DEFAULT_COVER_IMAGE_URL)
				.titlePosition(TestPublication.DEFAULT_TITLE_POSITION)
				.build();
	}

	public static PublicationCreateRequestDto createTooLongTitlePublication() {
		return PublicationCreateRequestDto.builder()
				.title(TOO_LONG_TITLE)
				.preSignedCoverImageUrl(TestBook.DEFAULT_COVER_IMAGE_URL)
				.titlePosition(TestPublication.DEFAULT_TITLE_POSITION)
				.build();
	}

	public static PublicationCreateRequestDto createValidPublication() {
		return PublicationCreateRequestDto.builder()
				.title(TestBook.DEFAULT_TITLE)
				.preSignedCoverImageUrl(TestBook.DEFAULT_COVER_IMAGE_URL)
				.titlePosition(TestPublication.DEFAULT_TITLE_POSITION)
				.build();
	}
}
