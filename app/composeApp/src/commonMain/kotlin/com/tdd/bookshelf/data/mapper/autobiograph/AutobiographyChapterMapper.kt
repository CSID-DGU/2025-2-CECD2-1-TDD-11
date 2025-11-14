package com.tdd.bookshelf.data.mapper.autobiograph

import com.tdd.bookshelf.data.base.BaseMapper
import com.tdd.bookshelf.data.entity.response.autobiography.AutobiographyChapterResponseDto
import com.tdd.bookshelf.domain.entity.response.autobiography.ChapterItemModel
import com.tdd.bookshelf.domain.entity.response.autobiography.ChapterListModel
import com.tdd.bookshelf.domain.entity.response.autobiography.SubChapterItemModel
import io.ktor.client.statement.HttpResponse
import kotlinx.coroutines.flow.Flow

object AutobiographyChapterMapper : BaseMapper() {
    fun responseToModel(apiCall: suspend () -> HttpResponse): Flow<Result<ChapterListModel>> {
        return baseMapper(
            apiCall = { apiCall() },
            successDeserializer = AutobiographyChapterResponseDto.serializer(),
            responseToModel = { response ->
                response?.let { data ->
                    ChapterListModel(
                        currentChapterId = data.currentChapterId,
                        results =
                            data.results.map { chapterItem ->
                                ChapterItemModel(
                                    chapterId = chapterItem.chapterId,
                                    chapterNumber = chapterItem.chapterNumber,
                                    chapterName = chapterItem.chapterName,
                                    chapterDescription = chapterItem.chapterDescription,
                                    chapterCreatedAt = chapterItem.chapterCreatedAt,
                                    subChapters =
                                        chapterItem.subChapters.map { subItem ->
                                            SubChapterItemModel(
                                                chapterId = subItem.chapterId,
                                                chapterNumber = subItem.chapterNumber,
                                                chapterName = subItem.chapterName,
                                                chapterDescription = subItem.chapterDescription,
                                                chapterCreatedAt = subItem.chapterCreatedAt,
                                            )
                                        },
                                )
                            },
                    )
                } ?: ChapterListModel()
            },
        )
    }
}
