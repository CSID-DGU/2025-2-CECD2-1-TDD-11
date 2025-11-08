package com.lifelibrarians.lifebookshelf.interview.domain;

import com.lifelibrarians.lifebookshelf.autobiography.domain.Autobiography;
import com.lifelibrarians.lifebookshelf.chapter.domain.Chapter;
import com.lifelibrarians.lifebookshelf.member.domain.Member;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "interviews")
@Getter
@ToString(callSuper = true, exclude = {"autobiography", "chapter", "member",
		"interviewConversations"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Interview {

	/* 고유 정보 { */
	@Id
	@Column(nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private LocalDateTime createdAt;
	/* } 고유 정보 */

	/* 연관 정보 { */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "autobiography_id", nullable = false)
	private Autobiography autobiography;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chapter_id", nullable = false)
	private Chapter chapter;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "interview", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<InterviewQuestion> questions = new ArrayList<>();

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "current_question_id")
	private InterviewQuestion currentQuestion;


	@OneToMany(mappedBy = "interview")
	private Set<Conversation> interviewConversations;
	/* } 연관 정보 */

	/* 생성자 { */
	protected Interview(
			LocalDateTime createdAt,
			Autobiography autobiography,
			Chapter chapter,
			Member member,
			InterviewQuestion currentQuestion
	) {
		this.createdAt = createdAt;
		this.autobiography = autobiography;
		this.chapter = chapter;
		this.member = member;
		this.currentQuestion = currentQuestion;
	}

	public static Interview of(
			LocalDateTime createdAt,
			Autobiography autobiography,
			Chapter chapter,
			Member member,
			InterviewQuestion currentQuestion
	) {
		return new Interview(
				createdAt,
				autobiography,
				chapter,
				member,
				currentQuestion
		);
	}
	/* } 생성자 */

	/* 연관 관계 편의 메소드 { */
	public void setCurrentQuestion(InterviewQuestion interviewQuestion) {
		this.currentQuestion = interviewQuestion;
		interviewQuestion.setInterview(this);
	}

	public void setQuestions(List<InterviewQuestion> interviewQuestions) {
		this.questions = interviewQuestions;
		for (InterviewQuestion interviewQuestion : interviewQuestions) {
			interviewQuestion.setInterview(this);
		}
	}
	/* } 연관 관계 편의 메소드 */
}
