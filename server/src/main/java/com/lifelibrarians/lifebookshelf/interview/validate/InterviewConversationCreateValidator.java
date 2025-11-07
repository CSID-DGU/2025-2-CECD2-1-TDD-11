package com.lifelibrarians.lifebookshelf.interview.validate;

import com.lifelibrarians.lifebookshelf.interview.dto.request.InterviewConversationCreateRequestDto;
import com.lifelibrarians.lifebookshelf.interview.dto.request.InterviewConversationDto;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class InterviewConversationCreateValidator implements
		ConstraintValidator<InterviewConversationCreateValidation, InterviewConversationCreateRequestDto> {

	@Override
	public boolean isValid(InterviewConversationCreateRequestDto value,
			ConstraintValidatorContext context) {

		if (value.getConversations().isEmpty() || value.getConversations().size() > 20) {
			context.buildConstraintViolationWithTemplate("INTERVIEW003")
					.addConstraintViolation();
			return false;
		}

		for (InterviewConversationDto conversation : value.getConversations()) {
			if (conversation.getContent() == null || conversation.getContent().isEmpty()
					|| conversation.getContent().length() > 512) {
				context.buildConstraintViolationWithTemplate("INTERVIEW004")
						.addConstraintViolation();
				return false;
			}
		}
		return true;
	}
}
