package com.tdd.bookshelf.data.dataSource

import com.tdd.bookshelf.data.entity.request.autobiography.PostCreateAutobiographyChapterRequestDto
import com.tdd.bookshelf.data.entity.request.autobiography.PostCreateAutobiographyRequestDto
import com.tdd.bookshelf.data.entity.request.autobiography.PostEditAutobiographyRequestDto
import io.ktor.client.statement.HttpResponse

interface AutobiographyDataSource {
    suspend fun getAllAutobiographies(): HttpResponse

    suspend fun postCreateAutobiographies(body: PostCreateAutobiographyRequestDto): HttpResponse

    suspend fun getAutobiographiesDetail(autobiographyId: Int): HttpResponse

    suspend fun postEditAutobiographiesDetail(
        autobiographyId: Int,
        body: PostEditAutobiographyRequestDto,
    ): HttpResponse

    suspend fun deleteAutobiography(autobiographyId: Int): HttpResponse

    suspend fun getAutobiographyChapter(): HttpResponse

    suspend fun postCreateChapterList(body: PostCreateAutobiographyChapterRequestDto): HttpResponse

    suspend fun postUpdateCurrentChapter(): HttpResponse
}
