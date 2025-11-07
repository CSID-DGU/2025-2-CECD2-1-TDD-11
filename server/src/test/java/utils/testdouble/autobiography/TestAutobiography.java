package utils.testdouble.autobiography;

import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.chapter.domain.Chapter;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Builder;
import utils.testdouble.TestEntity;

@Builder
public class TestAutobiography implements TestEntity<Autobiography, Long> {

	public static final LocalDateTime DEFAULT_TIME = LocalDateTime.of(LocalDate.EPOCH,
			LocalTime.MIDNIGHT);
	public static final String DEFAULT_TITLE = "테스트 자서전 제목";
	public static final String DEFAULT_CONTENT = "테스트 자서전 내용";
	public static final String DEFAULT_COVER_IMAGE_URL = "bio-cover-images/RANDOM_STRING/image.jpg";

	@Builder.Default
	private String title = DEFAULT_TITLE;
	@Builder.Default
	private String content = DEFAULT_CONTENT;
	@Builder.Default
	private String coverImageUrl = DEFAULT_COVER_IMAGE_URL;
	@Builder.Default
	private LocalDateTime createdAt = DEFAULT_TIME;
	@Builder.Default
	private LocalDateTime updatedAt = DEFAULT_TIME;
	@Builder.Default
	private Chapter chapter = null;
	@Builder.Default
	private Member member = null;

	public static Autobiography asDefaultEntity() {
		return TestAutobiography.builder().build().asEntity();
	}

	public static Autobiography asDefaultEntity(Member member, Chapter chapter) {
		return TestAutobiography.builder()
				.title(DEFAULT_TITLE)
				.content(DEFAULT_CONTENT)
				.coverImageUrl(DEFAULT_COVER_IMAGE_URL)
				.member(member)
				.chapter(chapter)
				.build().asEntity();
	}

	@Override
	public Autobiography asEntity() {
		return Autobiography.of(
				this.title,
				this.content,
				this.coverImageUrl,
				this.createdAt,
				this.updatedAt,
				this.chapter,
				this.member
		);
	}

	@Override
	public Autobiography asMockEntity(Long aLong) {
		return null;
	}
}
