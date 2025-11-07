package utils.testdouble.book;

import com.lifelibrarians.lifebookshelf.community.book.domain.Book;
import com.lifelibrarians.lifebookshelf.community.book.domain.VisibleScope;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Builder;
import utils.testdouble.TestEntity;

@Builder
public class TestBook implements TestEntity<Book, Long> {

	public static final LocalDateTime DEFAULT_TIME = LocalDateTime.of(LocalDate.EPOCH,
			LocalTime.MIDNIGHT);
	public static final String DEFAULT_TITLE = "테스트 출판 책 제목";
	public static final String DEFAULT_COVER_IMAGE_URL = "book-cover-images/RANDOM_STRING/image.jpg";
	public static final VisibleScope DEFAULT_VISIBLE_SCOPE = VisibleScope.PRIVATE;
	public static final Integer DEFAULT_PAGE = 20;

	@Builder.Default
	private String title = DEFAULT_TITLE;
	@Builder.Default
	private String coverImageUrl = DEFAULT_COVER_IMAGE_URL;
	@Builder.Default
	private VisibleScope visibleScope = DEFAULT_VISIBLE_SCOPE;
	@Builder.Default
	private Integer page = DEFAULT_PAGE;
	@Builder.Default
	private LocalDateTime createdAt = DEFAULT_TIME;
	@Builder.Default
	private LocalDateTime deletedAt = null;
	@Builder.Default
	private Member member = null;

	@Override
	public Book asEntity() {
		return Book.of(
				this.title,
				this.coverImageUrl,
				this.visibleScope,
				this.page,
				this.createdAt,
				this.deletedAt,
				this.member
		);
	}

	public static Book asDefaultEntity(Member member) {
		return TestBook.builder()
				.member(member)
				.build().asEntity();
	}

	@Override
	public Book asMockEntity(Long aLong) {
		return null;
	}
}
