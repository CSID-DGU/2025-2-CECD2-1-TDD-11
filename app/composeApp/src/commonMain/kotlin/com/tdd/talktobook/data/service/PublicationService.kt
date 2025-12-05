package com.tdd.talktobook.data.service

import com.tdd.talktobook.data.base.EndPoints
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Multipart
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Part
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import io.ktor.client.statement.HttpResponse

interface PublicationService {
    @Multipart
    @POST(EndPoints.Publication.PUBLICATIONS)
    suspend fun postPublication(
        @Part("title") title: String,
        @Part("preSignedCoverImageUrl") preSignedCoverImageUrl: String,
        @Part("titlePosition") titlePosition: String,
    ): HttpResponse

    @GET(EndPoints.Publication.MYPUBLICATIONS)
    suspend fun getMyPublication(
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): HttpResponse

    @GET(EndPoints.Publication.PROGRESS)
    suspend fun getPublicationProgress(
        @Path("publicationId") publicationId: Int,
    ): HttpResponse

    @DELETE(EndPoints.Publication.DELETE)
    suspend fun deletePublicationBook(
        @Path("bookId") bookId: Int,
    ): HttpResponse

    @POST(EndPoints.Publication.PUBLICATION_PDF)
    suspend fun postPublicationPdf(
        @Path("autobiographyId") autobiographyId: Int,
        @Body body: String = ""
    ): String
}
