package com.tdd.bookshelf.data.dataSource.dataSourceImpl

import com.tdd.bookshelf.data.dataSource.AutobiographyDataSource
import com.tdd.bookshelf.data.entity.request.autobiography.PostCreateAutobiographyChapterRequestDto
import com.tdd.bookshelf.data.entity.request.autobiography.PostCreateAutobiographyRequestDto
import com.tdd.bookshelf.data.entity.request.autobiography.PostEditAutobiographyRequestDto
import com.tdd.bookshelf.data.service.AutobiographyService
import io.ktor.client.statement.HttpResponse
import org.koin.core.annotation.Single

@Single(binds = [AutobiographyDataSource::class])
class AutobiographyDataSourceImpl(
    private val autobiographyService: AutobiographyService,
) : AutobiographyDataSource {
    override suspend fun getAllAutobiographies(): HttpResponse =
        autobiographyService.getAllAutobiographies()

    override suspend fun postCreateAutobiographies(body: PostCreateAutobiographyRequestDto): HttpResponse =
        autobiographyService.postAllAutobiographies(body)

    override suspend fun getAutobiographiesDetail(autobiographyId: Int): HttpResponse =
        autobiographyService.getAutobiographiesDetail(autobiographyId)

    override suspend fun postEditAutobiographiesDetail(
        autobiographyId: Int,
        body: PostEditAutobiographyRequestDto,
    ): HttpResponse =
        autobiographyService.postEditAutobiographyDetail(autobiographyId, body)

    override suspend fun deleteAutobiography(autobiographyId: Int): HttpResponse =
        autobiographyService.deleteAutobiography(autobiographyId)

    override suspend fun getAutobiographyChapter(): HttpResponse =
        autobiographyService.getAutobiographiesChapter()

    override suspend fun postCreateChapterList(body: PostCreateAutobiographyChapterRequestDto): HttpResponse =
        autobiographyService.postAutobiographiesChapterList(body)

    override suspend fun postUpdateCurrentChapter(): HttpResponse =
        autobiographyService.updateCurrentChapter()
}
