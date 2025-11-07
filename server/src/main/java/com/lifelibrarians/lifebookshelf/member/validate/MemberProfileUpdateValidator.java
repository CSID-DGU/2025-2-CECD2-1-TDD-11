package com.lifelibrarians.lifebookshelf.member.validate;

import com.lifelibrarians.lifebookshelf.member.dto.request.MemberProfileUpdateRequestDto;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MemberProfileUpdateValidator implements
		ConstraintValidator<MemberProfileUpdateValidation, MemberProfileUpdateRequestDto> {

	@Override
	public boolean isValid(MemberProfileUpdateRequestDto value,
			ConstraintValidatorContext context) {

		if (value.getNickname() == null || value.getNickname().isEmpty()) {
			return true;
		}

		if (value.getNickname().length() < 2 || value.getNickname().length() > 64) {
			context.buildConstraintViolationWithTemplate(
							"MEMBER003")
					.addConstraintViolation();
			return false;
		}
		return true;
	}
}
