package utils.testdouble.chapter;

import com.lifelibrarians.lifebookshelf.autobiography.dto.request.ChapterRequestDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.request.SubchapterRequestDto;
import com.lifelibrarians.lifebookshelf.chapter.domain.Chapter;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;
import lombok.Builder;
import utils.testdouble.TestEntity;

@Builder
public class TestChapter implements TestEntity<Chapter, Long> {

	public static final LocalDateTime DEFAULT_TIME = LocalDateTime.of(LocalDate.EPOCH,
			LocalTime.MIDNIGHT);

	@Builder.Default
	private Long parentChapterId = null;
	@Builder.Default
	private String number = null;
	@Builder.Default
	private String name = null;
	@Builder.Default
	private String description = null;
	@Builder.Default
	private LocalDateTime createdAt = DEFAULT_TIME;
	@Builder.Default
	private Member member = null;

	public static Chapter asDefaultEntity() {
		return TestChapter.builder().build().asEntity();
	}

	public static List<Chapter> asDefaultEntities(Member member) {
		return Arrays.asList(
				TestChapter.builder().number("1").name("1장").description("1장 설명").member(member).build()
						.asEntity(),
				TestChapter.builder().number("2").name("2장").description("2장 설명").member(member).build()
						.asEntity(),
				TestChapter.builder().number("3").name("3장").description("3장 설명").member(member).build()
						.asEntity()
		);
	}

	public static List<Chapter> asDefaultSubchapterEntities(Chapter chapter, Member loginMember) {
		return Arrays.asList(
				TestChapter.builder().number(chapter.getNumber() + ".1").name(chapter.getNumber() + ".1절")
						.description(chapter.getNumber() + ".1절 설명")
						.member(loginMember).parentChapterId(chapter.getId()).build().asEntity(),
				TestChapter.builder().number(chapter.getNumber() + ".2").name(chapter.getNumber() + ".2절")
						.description(chapter.getNumber() + ".2절 설명")
						.member(loginMember).parentChapterId(chapter.getId()).build().asEntity(),
				TestChapter.builder().number(chapter.getNumber() + ".3").name(chapter.getNumber() + ".3절")
						.description(chapter.getNumber() + ".3절 설명")
						.member(loginMember).parentChapterId(chapter.getId()).build().asEntity()
		);
	}

	@Override
	public Chapter asEntity() {
		return Chapter.of(
				this.number,
				this.name,
				this.description,
				this.createdAt,
				this.parentChapterId,
				this.member
		);
	}

	@Override
	public Chapter asMockEntity(Long aLong) {
		return null;
	}
}
