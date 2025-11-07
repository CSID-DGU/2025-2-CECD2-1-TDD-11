package com.lifelibrarians.lifebookshelf.member.domain;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "memberMetadatas")
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

	@Column(nullable = false)
	private LocalDate bornedAt;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private GenderType gender;

	@Column(nullable = false)
	private Boolean hasChildren;

	@Column
	private String occupation;

	@Column
	private String educationLevel;

	@Column
	private String maritalStatus;

	@Column(nullable = false)
	private LocalDateTime createdAt;

	@Column
	private LocalDateTime updatedAt;
	/* } 고유 정보 */

	/* 연관 정보 { */
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;
	/* } 연관 정보 */

	/* 생성자 { */
	protected MemberMetadata(String name, LocalDate bornedAt,
			GenderType gender, Boolean hasChildren,
			String occupation, String educationLevel, String maritalStatus,
			LocalDateTime createdAt, LocalDateTime updatedAt, Member member) {
		this.name = name;
		this.bornedAt = bornedAt;
		this.gender = gender;
		this.hasChildren = hasChildren;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.member = member;
	}

	public static MemberMetadata of(
			String name, LocalDate bornedAt,
			GenderType gender, Boolean hasChildren,
			String occupation, String educationLevel, String maritalStatus,
			LocalDateTime createdAt, LocalDateTime updatedAt, Member member) {
		return new MemberMetadata(
				name, bornedAt, gender, hasChildren,
				occupation, educationLevel, maritalStatus,
				createdAt, updatedAt, member
		);
	}
	/* } 생성자 */

	public void update(String name, LocalDate bornedAt, GenderType gender, boolean hasChildren,
			String occupation, String educationLevel, String maritalStatus) {
		this.name = name;
		this.bornedAt = bornedAt;
		this.gender = gender;
		this.hasChildren = hasChildren;
		this.occupation = occupation;
		this.educationLevel = educationLevel;
		this.maritalStatus = maritalStatus;
	}

	public void setMember(Member member) {
		this.member = member;
	}
}
