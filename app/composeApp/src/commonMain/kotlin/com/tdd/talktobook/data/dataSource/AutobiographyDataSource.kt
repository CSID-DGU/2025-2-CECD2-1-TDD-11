package com.tdd.talktobook.data.dataSource

import com.tdd.talktobook.data.entity.request.autobiography.PostCreateAutobiographyChapterRequestDto
import com.tdd.talktobook.data.entity.request.autobiography.PostCreateAutobiographyRequestDto
import com.tdd.talktobook.data.entity.request.autobiography.PostEditAutobiographyRequestDto
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

    suspend fun getCurrentProgressAutobiography(): HttpResponse

    suspend fun postStartProgress(theme: String, reason: String): HttpResponse

    suspend fun getCountMaterials(autobiographyId: Int): HttpResponse
}
