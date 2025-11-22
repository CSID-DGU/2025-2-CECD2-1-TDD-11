package com.tdd.talktobook.data.service

import com.tdd.talktobook.data.base.EndPoints
import com.tdd.talktobook.data.entity.request.autobiography.PostCreateAutobiographyChapterRequestDto
import com.tdd.talktobook.data.entity.request.autobiography.PostCreateAutobiographyRequestDto
import com.tdd.talktobook.data.entity.request.autobiography.PostEditAutobiographyRequestDto
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Multipart
import de.jensklingenberg.ktorfit.http.PATCH
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.Part
import de.jensklingenberg.ktorfit.http.Path
import io.ktor.client.statement.HttpResponse

interface AutobiographyService {
    @GET(EndPoints.Autobiography.AUTOBIOGRAPHIES)
    suspend fun getAllAutobiographies(): HttpResponse

    @POST(EndPoints.Autobiography.AUTOBIOGRAPHIES)
    suspend fun postAllAutobiographies(
        @Body body: PostCreateAutobiographyRequestDto,
    ): HttpResponse

    @GET(EndPoints.Autobiography.AUTOBIOGRAPHIESDETAIL)
    suspend fun getAutobiographiesDetail(
        @Path("autobiographyId") autobiographyId: Int,
    ): HttpResponse

    @POST(EndPoints.Autobiography.AUTOBIOGRAPHIESDETAIL)
    suspend fun postEditAutobiographyDetail(
        @Path("autobiographyId") autobiographyId: Int,
        @Body body: PostEditAutobiographyRequestDto,
    ): HttpResponse

    @DELETE(EndPoints.Autobiography.AUTOBIOGRAPHIESDETAIL)
    suspend fun deleteAutobiography(
        @Path("autobiographyId") autobiographyId: Int,
    ): HttpResponse

    @GET(EndPoints.Autobiography.AUTOBIOGRAPHIESCHAPTER)
    suspend fun getAutobiographiesChapter(): HttpResponse

    @POST(EndPoints.Autobiography.AUTOBIOGRAPHIESCHAPTER)
    suspend fun postAutobiographiesChapterList(
        @Body body: PostCreateAutobiographyChapterRequestDto,
    ): HttpResponse

    @POST(EndPoints.Autobiography.UPDATECURRENTCHAPTER)
    suspend fun updateCurrentChapter(): HttpResponse

    @GET(EndPoints.Autobiography.CURRENT_PROGRESS_AUTOBIOGRAPHIES)
    suspend fun getCurrentProgressAutobiography(): HttpResponse

    @Multipart
    @POST(EndPoints.Autobiography.START_PROGRESS)
    suspend fun postStartProgress(
        @Part("theme") theme: String,
        @Part("reason") reason: String
    ): HttpResponse

    @GET(EndPoints.Autobiography.COUNT_MATERIALS)
    suspend fun getCountMaterials(
        @Path("autobiographyId") autobiographyId: Int
    ): HttpResponse

    @GET(EndPoints.Autobiography.CURRENT_INTERVIEW_PROGRESS)
    suspend fun getCurrentInterviewProgress(
        @Path("autobiographyId") autobiographyId: Int
    ): HttpResponse

    @PATCH(EndPoints.Autobiography.CREATE_AUTOBIOGRAPHY)
    suspend fun patchCreateAutobiography(
        @Path("autobiographyId") autobiographyId: Int
    ): HttpResponse
}
