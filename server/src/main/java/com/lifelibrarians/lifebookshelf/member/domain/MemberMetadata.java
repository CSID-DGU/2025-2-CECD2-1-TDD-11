package com.lifelibrarians.lifebookshelf.member.domain;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "member_metadatas") // ← 테이블명 실제 스키마와 맞춤
@Getter
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberMetadata {

	/* 고유 정보 { */
	@Id
	@Column(nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(name = "borned_at", nullable = false)
	private LocalDate bornedAt;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private GenderType gender;

	@Column(name = "has_children", nullable = false)
	private Boolean hasChildren;

	@Column
	private String occupation;

	@Column(name = "education_level")
	private String educationLevel;

	@Column(name = "marital_status")
	private String maritalStatus;

	@Column(name = "created_at", nullable = false)
	private LocalDateTime createdAt;

	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
	/* } 고유 정보 */

	/* ▼ v2 추가 필드들 */
	@Column(length = 50)
	private String theme;              // 주제

	@Column(name = "age_group")
	private String ageGroup;           // '1020','30','40','50','60','70+' → 일단 String으로 매핑

	@Column(length = 50)
	private String job;                // 짧은 직업명

	@Lob
	@Column(name = "why_create", columnDefinition = "TEXT")
	private String whyCreate;          // 생성 목적(문장)
	/* ▲ v2 추가 */

	/* 연관 정보 { */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;
	/* } 연관 정보 */

	/* 생성자 { */
	protected MemberMetadata(
			String name, LocalDate bornedAt,
			GenderType gender, Boolean hasChildren,
			String occupation, String educationLevel, String maritalStatus,
			LocalDateTime createdAt, LocalDateTime updatedAt, Member member,
			String theme, String ageGroup, String job, String whyCreate
	) {
		this.name = name;
		this.bornedAt = bornedAt;
		this.gender = gender;
		this.hasChildren = hasChildren;
		this.occupation = occupation;
		this.educationLevel = educationLevel;
		this.maritalStatus = maritalStatus;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.member = member;
		this.theme = theme;
		this.ageGroup = ageGroup;
		this.job = job;
		this.whyCreate = whyCreate;
	}

	public static MemberMetadata of(
			String name, LocalDate bornedAt,
			GenderType gender, Boolean hasChildren,
			String occupation, String educationLevel, String maritalStatus,
			LocalDateTime createdAt, LocalDateTime updatedAt, Member member,
			String theme, String ageGroup, String job, String whyCreate
	) {
		return new MemberMetadata(
				name, bornedAt, gender, hasChildren,
				occupation, educationLevel, maritalStatus,
				createdAt, updatedAt, member,
				theme, ageGroup, job, whyCreate
		);
	}
	/* } 생성자 */

	public void update(
			String name, LocalDate bornedAt, GenderType gender, boolean hasChildren,
			String occupation, String educationLevel, String maritalStatus
	) {
		this.name = name;
		this.bornedAt = bornedAt;
		this.gender = gender;
		this.hasChildren = hasChildren;
		this.occupation = occupation;
		this.educationLevel = educationLevel;
		this.maritalStatus = maritalStatus;
		this.updatedAt = LocalDateTime.now();
	}

	/** v2 필드만 별도로 갱신하고 싶을 때 */
	public void updateV2(GenderType gender, String theme, String ageGroup, String job, String whyCreate) {
		this.gender = gender;
		this.theme = theme;
		this.ageGroup = ageGroup;
		this.job = job;
		this.whyCreate = whyCreate;
		this.updatedAt = LocalDateTime.now();
	}

	public void setMember(Member member) {
		this.member = member;
	}
}
