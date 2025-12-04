package com.tdd.talktobook.data.dataSource.dataSourceImpl

import com.tdd.talktobook.data.dataSource.PublicationDataSource
import com.tdd.talktobook.data.service.PublicationService
import io.ktor.client.statement.HttpResponse
import org.koin.core.annotation.Single

@Single(binds = [PublicationDataSource::class])
class PublicationDataSourceImpl(
    private val publicationService: PublicationService,
) : PublicationDataSource {
    override suspend fun postPublication(
        title: String,
        preSignedCoverImageUrl: String,
        titlePosition: String,
    ): HttpResponse =
        publicationService.postPublication(title, preSignedCoverImageUrl, title)

    override suspend fun getMyPublication(
        page: Int,
        size: Int,
    ): HttpResponse =
        publicationService.getMyPublication(page, size)

    override suspend fun getPublicationProgress(publicationId: Int): HttpResponse =
        publicationService.getPublicationProgress(publicationId)

    override suspend fun deletePublicationBook(bookId: Int): HttpResponse =
        publicationService.deletePublicationBook(bookId)

    override suspend fun postPublicationPdf(autobiographyId: Int): HttpResponse =
        publicationService.postPublicationPdf(autobiographyId)
}
