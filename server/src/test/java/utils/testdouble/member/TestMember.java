package utils.testdouble.member;

import com.lifelibrarians.lifebookshelf.member.domain.LoginType;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import com.lifelibrarians.lifebookshelf.member.domain.MemberRole;
import com.lifelibrarians.lifebookshelf.member.domain.OauthType;
import com.lifelibrarians.lifebookshelf.member.domain.SocialMember;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;
import lombok.Builder;
import utils.testdouble.TestEntity;

@Builder
public class TestMember implements TestEntity<Member, Long> {

	public static final LoginType DEFAULT_LOGIN_TYPE = LoginType.PASSWORD;
	public static final MemberRole DEFAULT_ROLE = MemberRole.MEMBER;
	public static final String DEFAULT_PROFILE_IMAGE_URL = "https://test.com/profile.jpg";
	public static final String DEFAULT_NICKNAME = "테스트 닉네임";
	public static final LocalDateTime DEFAULT_TIME = LocalDateTime.of(LocalDate.EPOCH,
			LocalTime.MIDNIGHT);
	public static final String DEFAULT_OAUTH_ID = "oauthId";
	public static final OauthType DEFAULT_OAUTH_TYPE = OauthType.NAVER;

	@Builder.Default
	private LoginType loginType = DEFAULT_LOGIN_TYPE;
	@Builder.Default
	// 6자리 랜덤 문자열 + @ + 6자리 랜덤 문자열 + .com
	private String email = UUID.randomUUID().toString().substring(0, 6) + "@"
			+ UUID.randomUUID().toString().substring(0, 6) + ".com";
	@Builder.Default
	private MemberRole role = DEFAULT_ROLE;
	@Builder.Default
	private String profileImageUrl = DEFAULT_PROFILE_IMAGE_URL;
	@Builder.Default
	private String nickname = DEFAULT_NICKNAME;
	@Builder.Default
	private LocalDateTime createdAt = DEFAULT_TIME;
	@Builder.Default
	private LocalDateTime nicknameUpdatedAt = DEFAULT_TIME;


	public static Member asDefaultEntity() {
		return TestMember.builder().build().asEntity();
	}

	public static SocialMember asDefaultSocialMember() {
		return SocialMember.of(
				DEFAULT_OAUTH_ID,
				DEFAULT_OAUTH_TYPE
		);
	}

	@Override
	public Member asEntity() {
		return Member.of(
				this.loginType,
				this.email,
				this.role,
				this.profileImageUrl,
				this.nickname,
				this.createdAt,
				this.nicknameUpdatedAt,
				null
		);
	}

	@Override
	public Member asMockEntity(Long aLong) {
		return null;
	}
}
