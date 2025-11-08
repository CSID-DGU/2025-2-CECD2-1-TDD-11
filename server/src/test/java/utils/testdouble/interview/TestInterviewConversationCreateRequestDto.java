package utils.testdouble.interview;

import com.lifelibrarians.lifebookshelf.interview.domain.ConversationType;
import com.lifelibrarians.lifebookshelf.interview.dto.request.InterviewConversationCreateRequestDto;
import com.lifelibrarians.lifebookshelf.interview.dto.request.InterviewConversationDto;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestInterviewConversationCreateRequestDto {

	public static InterviewConversationCreateRequestDto createValidInterviewConversationCreateRequestDto() {
		return InterviewConversationCreateRequestDto.builder()
				.conversations(TestInterviewConversationCreateRequestDto.createValidConversations())
				.build();
	}

	public static InterviewConversationCreateRequestDto createTooManyConversationsInterviewConversationCreateRequestDto() {
		return InterviewConversationCreateRequestDto.builder()
				.conversations(
						TestInterviewConversationCreateRequestDto.createTooManyConversations())
				.build();
	}

	public static InterviewConversationCreateRequestDto createTooLongContentInterviewConversationCreateRequestDto() {
		return InterviewConversationCreateRequestDto.builder()
				.conversations(
						TestInterviewConversationCreateRequestDto.createTooLongContentConversations())
				.build();
	}

	public static List<InterviewConversationDto> createTooManyConversations() {
		return IntStream.range(0, 21)
				.mapToObj(i -> InterviewConversationDto.builder()
						.content("Content " + i)
						.conversationType(i % 2 == 0 ? ConversationType.BOT : ConversationType.HUMAN)
						.build())
				.collect(Collectors.toList());
	}

	public static List<InterviewConversationDto> createValidConversations() {
		return List.of(
				InterviewConversationDto.builder()
						.content("What is your hometown?")
						.conversationType(ConversationType.BOT)
						.build(),
				InterviewConversationDto.builder()
						.content("I was born in Seoul.")
						.conversationType(ConversationType.HUMAN)
						.build()
		);
	}


	public static List<InterviewConversationDto> createTooLongContentConversations() {
		return List.of(
				InterviewConversationDto.builder()
						.content("What is your hometown?")
						.conversationType(ConversationType.BOT)
						.build(),
				InterviewConversationDto.builder()
						.content("a".repeat(513))
						.conversationType(ConversationType.HUMAN)
						.build()
		);
	}
}
