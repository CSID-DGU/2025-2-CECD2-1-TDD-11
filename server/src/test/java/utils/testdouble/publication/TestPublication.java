package utils.testdouble.publication;

import com.lifelibrarians.lifebookshelf.community.book.domain.Book;
import com.lifelibrarians.lifebookshelf.publication.domain.Publication;
import com.lifelibrarians.lifebookshelf.publication.domain.PublishStatus;
import com.lifelibrarians.lifebookshelf.publication.domain.TitlePosition;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Builder;
import utils.testdouble.TestEntity;

@Builder
public class TestPublication implements TestEntity<Publication, Long> {

	public static final LocalDateTime DEFAULT_TIME = LocalDateTime.of(LocalDate.EPOCH,
			LocalTime.MIDNIGHT);
	public static final Integer DEFAULT_PRICE = 5_000;
	public static final TitlePosition DEFAULT_TITLE_POSITION = TitlePosition.LEFT;
	public static final PublishStatus DEFAULT_PUBLISH_STATUS = PublishStatus.REQUESTED;

	@Builder.Default
	private Integer price = DEFAULT_PRICE;
	@Builder.Default
	private TitlePosition titlePosition = DEFAULT_TITLE_POSITION;
	@Builder.Default
	private PublishStatus publishStatus = DEFAULT_PUBLISH_STATUS;
	@Builder.Default
	private LocalDateTime requestedAt = DEFAULT_TIME;
	@Builder.Default
	private LocalDateTime willPublishedAt = DEFAULT_TIME.plusDays(14);
	@Builder.Default
	private LocalDateTime publishedAt = null;
	@Builder.Default
	private LocalDateTime deletedAt = null;
	@Builder.Default
	private Book book = null;


	@Override
	public Publication asEntity() {
		return Publication.of(
				this.price,
				this.titlePosition,
				this.publishStatus,
				this.requestedAt,
				this.willPublishedAt,
				this.publishedAt,
				this.deletedAt,
				this.book
		);
	}

	public static Publication asDefaultEntity(Book book) {
		return TestPublication.builder()
				.book(book)
				.build().asEntity();
	}

	@Override
	public Publication asMockEntity(Long aLong) {
		return null;
	}
}
