package com.lifelibrarians.lifebookshelf.member.validate;

import com.lifelibrarians.lifebookshelf.member.dto.request.MemberUpdateV2RequestDto;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class MemberUpdateV2Validator implements ConstraintValidator<MemberUpdateV2Validation, MemberUpdateV2RequestDto> {

	@Override
	public boolean isValid(MemberUpdateV2RequestDto dto, ConstraintValidatorContext context) {
		if (dto == null) return false;
		
		// gender 필수 검증
		if (dto.getGender() == null) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("성별은 필수입니다")
				.addPropertyNode("gender")
				.addConstraintViolation();
			return false;
		}
		
		// 길이 검증
		if (dto.getTheme() != null && dto.getTheme().length() > 50) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("주제는 50자 이하여야 합니다")
				.addPropertyNode("theme")
				.addConstraintViolation();
			return false;
		}
		
		if (dto.getJob() != null && dto.getJob().length() > 50) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("직업군은 50자 이하여야 합니다")
				.addPropertyNode("job")
				.addConstraintViolation();
			return false;
		}
		
		return true;
	}
}