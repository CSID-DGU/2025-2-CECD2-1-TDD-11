package utils.testdouble.interview;

import com.lifelibrarians.lifebookshelf.interview.domain.Conversation;
import com.lifelibrarians.lifebookshelf.interview.domain.ConversationType;
import com.lifelibrarians.lifebookshelf.interview.domain.Interview;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Builder;
import utils.testdouble.TestEntity;

@Builder
public class TestConversation implements TestEntity<Conversation, Long> {

	public static final LocalDateTime DEFAULT_TIME = LocalDateTime.of(LocalDate.EPOCH,
			LocalTime.MIDNIGHT);
	public static final String DEFAULT_CONTENT = "테스트 대화 내용";

	@Builder.Default
	private String content = DEFAULT_CONTENT;
	@Builder.Default
	private ConversationType type = ConversationType.BOT;
	@Builder.Default
	private Interview interview = null;
	@Builder.Default
	private LocalDateTime createdAt = DEFAULT_TIME;

	public static Conversation asDefaultEntity(
			Interview interview
	) {
		return TestConversation.builder()
				.interview(interview)
				.build().asEntity();
	}

	@Override
	public Conversation asEntity() {
		return Conversation.of(
				this.content,
				this.type,
				this.interview,
				this.createdAt
		);
	}

	@Override
	public Conversation asMockEntity(Long aLong) {
		return null;
	}
}
