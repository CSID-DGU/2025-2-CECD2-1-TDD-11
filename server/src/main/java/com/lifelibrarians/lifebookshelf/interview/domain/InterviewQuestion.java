package com.lifelibrarians.lifebookshelf.interview.domain;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "interviewQuestions")
@Getter
@ToString(callSuper = true, exclude = {"interview"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InterviewQuestion {

	/* 고유 정보 { */
	@Id
	@Column(nullable = false, updatable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, name = "\"order\"")
	private Integer order;

	@Column(nullable = false)
	private String questionText;

	@Column(nullable = false)
	private LocalDateTime createdAt;
	/* } 고유 정보 */

	/* 연관 정보 { */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "interview_id", nullable = false)
	private Interview interview;
	/* } 연관 정보 */

	/* 생성자 { */
	protected InterviewQuestion(
			Integer order, String questionText,
			LocalDateTime createdAt,
			Interview interview
	) {
		this.order = order;
		this.questionText = questionText;
		this.createdAt = createdAt;
		this.interview = interview;
	}

	public static InterviewQuestion of(Integer order, String questionText,
			LocalDateTime createdAt,
			Interview interview
	) {
		return new InterviewQuestion(order, questionText, createdAt, interview);
	}

	public void setInterview(Interview interview) {
		this.interview = interview;
	}
	/* } 생성자 */
}
