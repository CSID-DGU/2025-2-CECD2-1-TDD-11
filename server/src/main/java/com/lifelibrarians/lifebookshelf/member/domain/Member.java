package com.lifelibrarians.lifebookshelf.member.domain;

import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.community.book.domain.Book;
import com.lifelibrarians.lifebookshelf.chapter.domain.Chapter;
import com.lifelibrarians.lifebookshelf.chapter.domain.ChapterStatus;
import com.lifelibrarians.lifebookshelf.community.comment.domain.Comment;
import com.lifelibrarians.lifebookshelf.community.like.domain.Like;
import com.lifelibrarians.lifebookshelf.notification.domain.DeviceRegistry;
import com.lifelibrarians.lifebookshelf.notification.domain.NoticeHistory;
import com.lifelibrarians.lifebookshelf.notification.domain.NotificationSubscribe;

import java.util.Objects;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "members")
@Getter
@ToString(callSuper = true, exclude = {"memberNotificationSubscribes", "memberAutobiographies",
		"memberBooks", "memberComments", "memberLikes", "memberChapters", "memberMemberMetadata",
		"memberNoticeHistories", "memberChapterStatuses", "socialMember", "passwordMember",
		"memberDeviceRegistries"}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

	/* 고유 정보 { */
	@Id
	@Column(nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private LoginType loginType;

	@Column(nullable = false)
	private String email;

	@Column(nullable = false, name = "\"role\"")
	@Enumerated(EnumType.STRING)
	private MemberRole role;

	@Column
	private String profileImageUrl;

	@Column(nullable = false)
	private String nickname;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime nicknameUpdatedAt;

	@Column
	private LocalDateTime deletedAt;
	/* } 고유 정보 */

	/* 연관 정보 { */
	@OneToMany(mappedBy = "member")
	private Set<NotificationSubscribe> memberNotificationSubscribes;

	@OneToMany(mappedBy = "member")
	private Set<Autobiography> memberAutobiographies;

	@OneToMany(mappedBy = "member")
	private Set<Book> memberBooks;

	@OneToMany(mappedBy = "member")
	private Set<Comment> memberComments;

	@OneToMany(mappedBy = "member")
	private Set<Like> memberLikes;

	@OneToMany(mappedBy = "member")
	private Set<Chapter> memberChapters;

	@OneToOne(mappedBy = "member")
	private MemberMetadata memberMemberMetadata;

	@OneToMany(mappedBy = "member")
	private Set<NoticeHistory> memberNoticeHistories;

	@OneToMany(mappedBy = "member")
	private Set<ChapterStatus> memberChapterStatuses;

	@OneToMany(mappedBy = "member")
	private Set<DeviceRegistry> memberDeviceRegistries;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "social_member_id", unique = true)
	private SocialMember socialMember;

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "password_member_id", unique = true)
	private PasswordMember passwordMember;
	/* } 연관 정보 */

	/* 생성자 { */
	protected Member(LoginType loginType, String email, MemberRole role, String profileImageUrl,
			String nickname, LocalDateTime createdAt, LocalDateTime nicknameUpdatedAt,
			LocalDateTime deletedAt) {
		this.loginType = loginType;
		this.email = email;
		this.role = role;
		this.profileImageUrl = profileImageUrl;
		this.nickname = nickname;
		this.createdAt = createdAt;
		this.nicknameUpdatedAt = nicknameUpdatedAt;
		this.deletedAt = deletedAt;
	}

	public static Member of(LoginType loginType, String email, MemberRole role,
			String profileImageUrl,
			String nickname, LocalDateTime createdAt, LocalDateTime nicknameUpdatedAt,
			LocalDateTime deletedAt) {
		return new Member(loginType, email, role, profileImageUrl, nickname, createdAt,
				nicknameUpdatedAt, deletedAt);
	}
	/* } 생성자 */

	/* 연관 관계 편의 메서드 { */
	public void addSocialMember(SocialMember socialMember) {
		this.socialMember = socialMember;
	}

	public void addPasswordMember(PasswordMember passwordMember) {
		this.passwordMember = passwordMember;
	}

	public void softDelete(LocalDateTime now) {
		this.deletedAt = now;
	}

	public void setMemberMemberMetadata(MemberMetadata memberMetadata) {
		this.memberMemberMetadata = memberMetadata;
		memberMetadata.setMember(this);
	}

	public void changeDefaultProfileImage() {
		this.profileImageUrl = null;
	}

	public boolean isEqualProfileImageUrl(String profileImageUrl) {
		if (Objects.isNull(this.profileImageUrl)) {
			return Objects.isNull(profileImageUrl);
		}
		return profileImageUrl.equals(this.profileImageUrl);
	}

	public void updateProfileImageUrl(String profileImageUrl) {
		this.profileImageUrl = profileImageUrl;
	}

	public void updateNickname(String nickname, LocalDateTime now) {
		if (nickname == null || nickname.isEmpty() || nickname.equals(this.nickname)) {
			return;
		}
		this.nickname = nickname;
		this.nicknameUpdatedAt = now;
	}

	public boolean isAdmin() {
		return this.role == MemberRole.ADMIN;
	}

	/* } 연관 관계 편의 메서드 */
}
