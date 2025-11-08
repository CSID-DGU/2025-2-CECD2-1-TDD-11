package utils.testdouble.autobiography;

import com.lifelibrarians.lifebookshelf.autobiography.dto.request.AutobiographyCreateRequestDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.request.InterviewQuestionRequestDto;
import java.util.List;

public class TestAutobiographyCreateRequestDto {

	public static String EMPTY_VALUE = "";
	public static String TOO_LONG_TITLE = "a".repeat(65);
	public static String TOO_LONG_CONTENT = "a".repeat(30001);
	public static String TOO_LONG_INTERVIEW_QUESTION = "a".repeat(65);

	public static AutobiographyCreateRequestDto createEmptyTitleAutobiography() {
		return AutobiographyCreateRequestDto.builder()
				.title(EMPTY_VALUE)
				.content(TestAutobiography.DEFAULT_CONTENT)
				.preSignedCoverImageUrl(TestAutobiography.DEFAULT_COVER_IMAGE_URL)
				.interviewQuestions(TestAutobiographyCreateRequestDto.createValidInterviewQuestions())
				.build();
	}

	public static AutobiographyCreateRequestDto createTooLongTitleAutobiography() {
		return AutobiographyCreateRequestDto.builder()
				.title(TOO_LONG_TITLE)
				.content(TestAutobiography.DEFAULT_CONTENT)
				.preSignedCoverImageUrl(TestAutobiography.DEFAULT_COVER_IMAGE_URL)
				.interviewQuestions(TestAutobiographyCreateRequestDto.createValidInterviewQuestions())
				.build();
	}

	public static AutobiographyCreateRequestDto createEmptyContentAutobiography() {
		return AutobiographyCreateRequestDto.builder()
				.title(TestAutobiography.DEFAULT_TITLE)
				.content(EMPTY_VALUE)
				.preSignedCoverImageUrl(TestAutobiography.DEFAULT_COVER_IMAGE_URL)
				.interviewQuestions(TestAutobiographyCreateRequestDto.createValidInterviewQuestions())
				.build();
	}

	public static AutobiographyCreateRequestDto createTooLongContentAutobiography() {
		return AutobiographyCreateRequestDto.builder()
				.title(TestAutobiography.DEFAULT_TITLE)
				.content(TOO_LONG_CONTENT)
				.preSignedCoverImageUrl(TestAutobiography.DEFAULT_COVER_IMAGE_URL)
				.interviewQuestions(TestAutobiographyCreateRequestDto.createValidInterviewQuestions())
				.build();
	}

	public static AutobiographyCreateRequestDto createTooLongInterviewQuestionAutobiography() {
		return AutobiographyCreateRequestDto.builder()
				.title(TestAutobiography.DEFAULT_TITLE)
				.content(TestAutobiography.DEFAULT_CONTENT)
				.preSignedCoverImageUrl(EMPTY_VALUE)
				.interviewQuestions(TestAutobiographyCreateRequestDto.createTooLongInterviewQuestions())
				.build();
	}

	public static AutobiographyCreateRequestDto createDuplicatedOrderInterviewQuestionAutobiography() {
		return AutobiographyCreateRequestDto.builder()
				.title(TestAutobiography.DEFAULT_TITLE)
				.content(TestAutobiography.DEFAULT_CONTENT)
				.preSignedCoverImageUrl(EMPTY_VALUE)
				.interviewQuestions(TestAutobiographyCreateRequestDto.createDuplicatedInterviewQuestions())
				.build();
	}

	public static AutobiographyCreateRequestDto createValidAutobiography() {
		return AutobiographyCreateRequestDto.builder()
				.title(TestAutobiography.DEFAULT_TITLE)
				.content(TestAutobiography.DEFAULT_CONTENT)
				.preSignedCoverImageUrl(TestAutobiography.DEFAULT_COVER_IMAGE_URL)
				.interviewQuestions(TestAutobiographyCreateRequestDto.createValidInterviewQuestions())
				.build();
	}

	public static List<InterviewQuestionRequestDto> createValidInterviewQuestions() {
		return List.of(
				InterviewQuestionRequestDto.builder()
						.order(1)
						.questionText("What is your name?")
						.build(),
				InterviewQuestionRequestDto.builder()
						.order(2)
						.questionText("What is your favorite color?")
						.build()
		);
	}

	public static List<InterviewQuestionRequestDto> createDuplicatedInterviewQuestions() {
		return List.of(
				InterviewQuestionRequestDto.builder()
						.order(1)
						.questionText("What is your name?")
						.build(),
				InterviewQuestionRequestDto.builder()
						.order(1)
						.questionText("What is your favorite color?")
						.build()
		);
	}

	public static List<InterviewQuestionRequestDto> createTooLongInterviewQuestions() {
		return List.of(
				InterviewQuestionRequestDto.builder()
						.order(1)
						.questionText(TOO_LONG_INTERVIEW_QUESTION)
						.build()
		);
	}
}
