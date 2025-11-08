package utils.testdouble.member;

import com.lifelibrarians.lifebookshelf.member.domain.GenderType;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import com.lifelibrarians.lifebookshelf.member.domain.MemberMetadata;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Builder;
import utils.testdouble.TestEntity;

@Builder
public class TestMemberMetadata implements TestEntity<MemberMetadata, Long> {

	public static final LocalDateTime DEFAULT_TIME = LocalDateTime.of(LocalDate.EPOCH,
			LocalTime.MIDNIGHT);
	public static final String DEFAULT_NAME = "테스트 회원 이름";
	public static final LocalDate DEFAULT_BORNED_AT = LocalDate.EPOCH;
	public static final GenderType DEFAULT_GENDER = GenderType.MALE;
	public static final Boolean DEFAULT_HAS_CHILDREN = false;
	public static final String DEFAULT_OCCUPATION = "테스트 직업";
	public static final String DEFAULT_EDUCATION_LEVEL = "테스트 학력";
	public static final String DEFAULT_MARITAL_STATUS = "테스트 결혼 상태";

	@Builder.Default
	private String name = DEFAULT_NAME;
	@Builder.Default
	private LocalDate bornedAt = DEFAULT_BORNED_AT;
	@Builder.Default
	private GenderType gender = DEFAULT_GENDER;
	@Builder.Default
	private Boolean hasChildren = DEFAULT_HAS_CHILDREN;
	@Builder.Default
	private String occupation = DEFAULT_OCCUPATION;
	@Builder.Default
	private String educationLevel = DEFAULT_EDUCATION_LEVEL;
	@Builder.Default
	private String maritalStatus = DEFAULT_MARITAL_STATUS;
	@Builder.Default
	private LocalDateTime createdAt = DEFAULT_TIME;
	@Builder.Default
	private LocalDateTime updatedAt = DEFAULT_TIME;
	@Builder.Default
	private Member member = null;

	public static MemberMetadata asDefaultEntity(Member member) {
		return TestMemberMetadata.builder()
				.member(member)
				.build().asEntity();
	}

	@Override
	public MemberMetadata asEntity() {
		return MemberMetadata.of(
				this.name,
				this.bornedAt,
				this.gender,
				this.hasChildren,
				this.occupation,
				this.educationLevel,
				this.maritalStatus,
				this.createdAt,
				this.updatedAt,
				this.member
		);
	}

	@Override
	public MemberMetadata asMockEntity(Long aLong) {
		return null;
	}
}
