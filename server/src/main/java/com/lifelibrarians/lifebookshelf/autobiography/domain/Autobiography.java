package com.lifelibrarians.lifebookshelf.autobiography.domain;

import com.lifelibrarians.lifebookshelf.chapter.domain.Chapter;
import com.lifelibrarians.lifebookshelf.interview.domain.Interview;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import java.util.List;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "autobiographies")
@Getter
@ToString(callSuper = true, exclude = {"chapter"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Autobiography {

	/* 고유 정보 { */
	@Id
	@Column(nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column
	private String title;

	@Lob
	private String content;

	@Column
	private String coverImageUrl;

	/* V2 신규 필드 { */
	@Column(length = 255)
	private String theme;

	@Column(length = 500)
	private String reason;
	/* } V2 신규 필드 */

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Column(nullable = false)
	private LocalDateTime updatedAt;
	/* } 고유 정보 */

	/* 연관 정보 { */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chapter_id", nullable = false)
	private Chapter chapter;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@OneToMany(mappedBy = "autobiography")
	private List<Interview> autobiographyInterviews;

	@OneToMany(mappedBy = "chapter")
	private Set<Interview> chapterInterviews;

	@OneToMany(mappedBy = "member")
	private Set<Interview> memberInterviews;
	/* } 연관 정보 */

	/* 생성자 (V1) { */
	/**
	 * V1 용 생성자 – theme, reason 없이 생성
	 */
	protected Autobiography(String title, String content, String coverImageUrl,
							LocalDateTime createdAt, LocalDateTime updatedAt,
							Chapter chapter, Member member) {
		this.title = title;
		this.content = content;
		this.coverImageUrl = coverImageUrl;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.chapter = chapter;
		this.member = member;
	}
	public static Autobiography of(String title, String content, String coverImageUrl,
								   LocalDateTime createdAt, LocalDateTime updatedAt,
								   Chapter chapter, Member member) {
		return new Autobiography(title, content, coverImageUrl, createdAt, updatedAt, chapter, member);
	}
	/* } 생성자 (V1) */

	/* 생성자 (V2) { */
	/**
	 * V2 용 생성자 – theme, reason 포함
	 */
	protected Autobiography(String title, String content, String coverImageUrl,
							String theme, String reason,
							LocalDateTime createdAt, LocalDateTime updatedAt,
							Chapter chapter, Member member) {
		this.title = title;
		this.content = content;
		this.coverImageUrl = coverImageUrl;
		this.theme = theme;
		this.reason = reason;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.chapter = chapter;
		this.member = member;
	}

	/**
	 * V2 용 팩토리 메서드 – 신규 컬럼까지 한 번에 설정
	 */
	public static Autobiography ofV2(String title, String content, String coverImageUrl,
									 String theme, String reason,
									 LocalDateTime createdAt, LocalDateTime updatedAt,
									 Chapter chapter, Member member) {
		return new Autobiography(title, content, coverImageUrl, theme, reason,
				createdAt, updatedAt, chapter, member);
	}
	/* } 생성자 (V2) */

	/* 업데이트 메서드 (V1) { */
	public void updateAutoBiography(String title, String content, String preSignedCoverImageUrl,
									LocalDateTime now) {
		if (title != null && !title.isEmpty()) {
			this.title = title;
		}
		if (content != null && !content.isEmpty()) {
			this.content = content;
		}
		if (preSignedCoverImageUrl != null && !preSignedCoverImageUrl.isEmpty()) {
			this.coverImageUrl = preSignedCoverImageUrl;
		}
		this.updatedAt = now;
	}
	/* } 업데이트 메서드 (V1) */

	/* 업데이트 메서드 (V2) { */
	/**
	 * V2 용 업데이트 – 기존 필드 + theme, reason 업데이트
	 */
	public void updateAutoBiographyV2(String title, String content, String preSignedCoverImageUrl,
									  String theme, String reason,
									  LocalDateTime now) {
		// V1 로직 재사용
		updateAutoBiography(title, content, preSignedCoverImageUrl, now);

		if (theme != null && !theme.isEmpty()) {
			this.theme = theme;
		}
		if (reason != null && !reason.isEmpty()) {
			this.reason = reason;
		}
	}
	/* } 업데이트 메서드 (V2) */

	/* 기타 메서드 { */
	public void setChapter(Chapter chapter) {
		this.chapter = chapter;
	}

	public void setAutobiographyInterviews(List<Interview> autobiographyInterviews) {
		this.autobiographyInterviews = autobiographyInterviews;
	}

	/**
	 * theme / reason 만 따로 변경하고 싶은 경우용 (선택)
	 */
	public void updateThemeAndReasonV2(String theme, String reason, LocalDateTime now) {
		if (theme != null && !theme.isEmpty()) {
			this.theme = theme;
		}
		if (reason != null && !reason.isEmpty()) {
			this.reason = reason;
		}
		this.updatedAt = now;
	}
	/* } 기타 메서드 */
}
