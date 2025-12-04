package com.tdd.talktobook.data.dataSource

import io.ktor.client.statement.HttpResponse

interface PublicationDataSource {
    suspend fun postPublication(
        title: String,
        preSignedCoverImageUrl: String,
        titlePosition: String,
    ): HttpResponse

    suspend fun getMyPublication(
        page: Int,
        size: Int,
    ): HttpResponse

    suspend fun getPublicationProgress(publicationId: Int): HttpResponse

    suspend fun deletePublicationBook(bookId: Int): HttpResponse

    suspend fun postPublicationPdf(autobiographyId: Int): HttpResponse
}
