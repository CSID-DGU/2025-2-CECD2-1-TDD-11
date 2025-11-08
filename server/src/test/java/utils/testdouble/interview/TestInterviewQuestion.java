package utils.testdouble.interview;

import com.lifelibrarians.lifebookshelf.interview.domain.Interview;
import com.lifelibrarians.lifebookshelf.interview.domain.InterviewQuestion;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import lombok.Builder;
import utils.testdouble.TestEntity;

@Builder
public class TestInterviewQuestion implements TestEntity<InterviewQuestion, Long> {

	public static final LocalDateTime DEFAULT_TIME = LocalDateTime.of(LocalDate.EPOCH,
			LocalTime.MIDNIGHT);
	public static final String DEFAULT_QUESTION = "테스트 질문";

	@Builder.Default
	private Integer order = null;
	@Builder.Default
	private String question = DEFAULT_QUESTION;
	@Builder.Default
	private LocalDateTime createdAt = DEFAULT_TIME;
	@Builder.Default
	private Interview interview = null;

	public static InterviewQuestion asDefaultEntity(
			Integer order,
			String question,
			Interview interview
	) {
		return TestInterviewQuestion.builder()
				.order(order)
				.question(question)
				.interview(interview)
				.build().asEntity();
	}

	public static List<InterviewQuestion> asDefaultEntities(Interview interview) {
		return List.of(
				asDefaultEntity(1, "질문 1", interview),
				asDefaultEntity(2, "질문 2", interview),
				asDefaultEntity(3, "질문 3", interview)
		);
	}

	@Override
	public InterviewQuestion asEntity() {
		return InterviewQuestion.of(
				this.order,
				this.question,
				this.createdAt,
				this.interview
		);
	}

	@Override
	public InterviewQuestion asMockEntity(Long aLong) {
		return null;
	}
}
