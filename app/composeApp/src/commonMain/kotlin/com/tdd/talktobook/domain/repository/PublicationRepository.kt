package com.tdd.talktobook.domain.repository

import com.tdd.talktobook.domain.entity.request.default.GetQueryDefaultModel
import com.tdd.talktobook.domain.entity.request.publication.PostPublicationModel
import com.tdd.talktobook.domain.entity.response.publication.PublicationProgressModel
import com.tdd.talktobook.domain.entity.response.publication.PublishMyListModel
import kotlinx.coroutines.flow.Flow

interface PublicationRepository {
    suspend fun postPublication(request: PostPublicationModel): Flow<Result<Boolean>>

    suspend fun getMyPublication(request: GetQueryDefaultModel): Flow<Result<PublishMyListModel>>

    suspend fun getPublicationProgress(request: Int): Flow<Result<PublicationProgressModel>>

    suspend fun deletePublicationBook(bookId: Int): Flow<Result<Boolean>>
}
