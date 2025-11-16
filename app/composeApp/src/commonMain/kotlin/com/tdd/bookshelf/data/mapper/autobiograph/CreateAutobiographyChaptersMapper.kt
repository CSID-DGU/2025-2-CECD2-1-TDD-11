package com.tdd.bookshelf.data.mapper.autobiograph

import com.tdd.bookshelf.data.base.BaseMapper
import com.tdd.bookshelf.data.entity.request.autobiography.PostCreateAutobiographyChapterRequestDto
import com.tdd.bookshelf.domain.entity.request.autobiography.CreateAutobiographyChaptersRequestModel

object CreateAutobiographyChaptersMapper : BaseMapper() {
    fun CreateAutobiographyChaptersRequestModel.toDto() =
        PostCreateAutobiographyChapterRequestDto(
            chapters =
                chapters.map { chapterItem ->
                    PostCreateAutobiographyChapterRequestDto.ChapterItem(
                        number = chapterItem.number,
                        name = chapterItem.name,
                        description = chapterItem.description,
                        subchapters =
                            chapterItem.subchapters.map { subChapterItem ->
                                PostCreateAutobiographyChapterRequestDto.ChapterItem.SubChapterItem(
                                    number = subChapterItem.number,
                                    name = subChapterItem.name,
                                    description = subChapterItem.description,
                                )
                            },
                    )
                },
        )
}
