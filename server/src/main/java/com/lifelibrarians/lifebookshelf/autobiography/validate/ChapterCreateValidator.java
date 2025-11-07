package com.lifelibrarians.lifebookshelf.autobiography.validate;

import com.lifelibrarians.lifebookshelf.autobiography.dto.request.ChapterCreateRequestDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.request.ChapterRequestDto;
import com.lifelibrarians.lifebookshelf.autobiography.dto.request.SubchapterRequestDto;
import java.util.HashSet;
import java.util.Set;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ChapterCreateValidator implements
		ConstraintValidator<ChapterCreateValidation, ChapterCreateRequestDto> {

	@Override
	public boolean isValid(ChapterCreateRequestDto value, ConstraintValidatorContext context) {

		// 챕터 값 유무 및 길이 검사
		if (value.getChapters() == null || value.getChapters().isEmpty()
				|| value.getChapters().size() > 16) {
			context.buildConstraintViolationWithTemplate(
							"BIO013")
					.addConstraintViolation();
			return false;
		}

		// 챕터 번호 포맷 검사
		if (value.getChapters().stream().anyMatch(chapter -> {
			if (chapter.getNumber() == null) {
				return false;
			}
			return !chapter.getNumber().matches("^[0-9]+(\\.[0-9]+)*$");
		})) {
			context.buildConstraintViolationWithTemplate(
							"BIO012")
					.addConstraintViolation();
			return false;
		}

		Set<String> seenNumbers = new HashSet<>();
		for (ChapterRequestDto chapter : value.getChapters()) {
			// 챕터 번호 중복 검사
			if (!seenNumbers.add(chapter.getNumber())) {
				context.buildConstraintViolationWithTemplate("BIO014")
						.addConstraintViolation();
				return false;
			}
			// 챕터 이름 길이 검사
			if (chapter.getName() == null || chapter.getName().length() > 64) {
				context.buildConstraintViolationWithTemplate("BIO002")
						.addConstraintViolation();
				return false;
			}

			// 챕터 설명 길이 검사
			if (chapter.getDescription() != null && chapter.getDescription().length() > 64) {
				context.buildConstraintViolationWithTemplate("BIO016")
						.addConstraintViolation();
				return false;
			}

			Set<String> subChapterNumbers = new HashSet<>();
			if (chapter.getSubchapters() != null) {
				for (SubchapterRequestDto subChapter : chapter.getSubchapters()) {
					// 서브챕터 중복 및 부모 챕터 번호 검사
					if (!subChapterNumbers.add(subChapter.getNumber())) {
						context.buildConstraintViolationWithTemplate("BIO014")
								.addConstraintViolation();
						return false;
					}
					if (!subChapter.getNumber().startsWith(chapter.getNumber())) {
						context.buildConstraintViolationWithTemplate("BIO011")
								.addConstraintViolation();
						return false;
					}
					// 서브챕터 이름 길이 검사
					if (subChapter.getName() == null || subChapter.getName().length() > 64) {
						context.buildConstraintViolationWithTemplate("BIO002")
								.addConstraintViolation();
						return false;
					}
				}
			}
		}
		return true;
	}
}
