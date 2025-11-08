package com.lifelibrarians.lifebookshelf.autobiography.validate;

import com.lifelibrarians.lifebookshelf.autobiography.dto.request.AutobiographyCreateRequestDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.request.InterviewQuestionRequestDto;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class AutobiographyCreateValidator implements
		ConstraintValidator<AutobiographyCreateValidation, AutobiographyCreateRequestDto> {

	@Override
	public boolean isValid(AutobiographyCreateRequestDto value, ConstraintValidatorContext context) {

		// 자서전 제목 길이 제한 테스트
		if (value.getTitle() == null || value.getTitle().isEmpty() || value.getTitle().length() > 64) {
			context.buildConstraintViolationWithTemplate(
							"BIO005")
					.addConstraintViolation();
			return false;
		}

		// 자서전 내용 길이 제한 테스트
		if (value.getContent() == null || value.getContent().isEmpty()
				|| value.getContent().length() > 30000) {
			context.buildConstraintViolationWithTemplate(
							"BIO006")
					.addConstraintViolation();
			return false;
		}

		// 인터뷰 질문 순서 중복 테스트
		if (value.getInterviewQuestions() != null) {
			for (int i = 0; i < value.getInterviewQuestions().size(); i++) {
				for (int j = i + 1; j < value.getInterviewQuestions().size(); j++) {
					if (value.getInterviewQuestions().get(i).getOrder()
							== value.getInterviewQuestions().get(j)
							.getOrder()) {
						context.buildConstraintViolationWithTemplate(
										"INTERVIEW010")
								.addConstraintViolation();
						return false;
					}
				}
			}
		}

		// 인터뷰 질문 길이 제한 테스트
		if (value.getInterviewQuestions() != null) {
			for (InterviewQuestionRequestDto interviewQuestion : value.getInterviewQuestions()) {
				if (interviewQuestion.getQuestionText() == null || interviewQuestion.getQuestionText()
						.isEmpty()
						|| interviewQuestion.getQuestionText().length() > 64) {
					context.buildConstraintViolationWithTemplate(
									"INTERVIEW005")
							.addConstraintViolation();
					return false;
				}
			}
		}
		return true;
	}
}
