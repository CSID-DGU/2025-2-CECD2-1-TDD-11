package utils.testdouble.interview;

import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.chapter.domain.Chapter;
import com.lifelibrarians.lifebookshelf.interview.domain.Interview;
import com.lifelibrarians.lifebookshelf.interview.domain.InterviewQuestion;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import utils.testdouble.TestEntity;
import utils.testdouble.autobiography.TestAutobiography;
import utils.testdouble.chapter.TestChapter;
import utils.testdouble.chapter.TestChapterStatus;

@Builder
public class TestInterview implements TestEntity<Interview, Long> {

	public static final LocalDateTime DEFAULT_TIME = LocalDateTime.of(LocalDate.EPOCH,
			LocalTime.MIDNIGHT);

	@Builder.Default
	private LocalDateTime createdAt = DEFAULT_TIME;
	@Builder.Default
	private Autobiography autobiography = null;
	@Builder.Default
	private Chapter chapter = null;
	@Builder.Default
	private Member member = null;
	@Builder.Default
	private InterviewQuestion currentQuestion = null;

	public static Interview asDefaultEntity(
			Autobiography autobiography,
			Chapter chapter,
			Member member,
			InterviewQuestion currentQuestion
	) {
		return TestInterview.builder()
				.autobiography(autobiography)
				.chapter(chapter)
				.member(member)
				.currentQuestion(currentQuestion)
				.build().asEntity();
	}

	public static Interview asDefaultEntity(
			Member member
	) {
		return TestInterview.builder()
				.member(member)
				.build().asEntity();
	}

	@Override
	public Interview asEntity() {
		return Interview.of(
				this.createdAt,
				this.autobiography,
				this.chapter,
				this.member,
				this.currentQuestion
		);
	}

	@Override
	public Interview asMockEntity(Long aLong) {
		return null;
	}
}
