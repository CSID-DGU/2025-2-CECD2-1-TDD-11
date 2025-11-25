package com.tdd.talktobook.data.mapper.autobiograph

import com.tdd.talktobook.data.base.BaseMapper
import com.tdd.talktobook.data.entity.response.autobiography.AutobiographyChapterResponseDto
import com.tdd.talktobook.domain.entity.response.autobiography.ChapterItemModel
import com.tdd.talktobook.domain.entity.response.autobiography.ChapterListModel
import com.tdd.talktobook.domain.entity.response.autobiography.SubChapterItemModel
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
