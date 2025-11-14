package com.tdd.bookshelf.domain.repository

import com.tdd.bookshelf.domain.entity.request.autobiography.CreateAutobiographyChaptersRequestModel
import com.tdd.bookshelf.domain.entity.request.autobiography.CreateAutobiographyRequestModel
import com.tdd.bookshelf.domain.entity.request.autobiography.EditAutobiographyDetailRequestModel
import com.tdd.bookshelf.domain.entity.response.autobiography.AllAutobiographyListModel
import com.tdd.bookshelf.domain.entity.response.autobiography.AutobiographiesDetailModel
import com.tdd.bookshelf.domain.entity.response.autobiography.ChapterListModel
import kotlinx.coroutines.flow.Flow

interface AutobiographyRepository {
    suspend fun getAllAutobiographies(): Flow<Result<AllAutobiographyListModel>>

    suspend fun postCreateAutobiographies(body: CreateAutobiographyRequestModel): Flow<Result<Boolean>>

    suspend fun getAutobiographiesDetail(autobiographyId: Int): Flow<Result<AutobiographiesDetailModel>>

    suspend fun postEditAutobiographiesDetail(body: EditAutobiographyDetailRequestModel): Flow<Result<Boolean>>

    suspend fun deleteAutobiography(autobiographyId: Int): Flow<Result<Boolean>>

    suspend fun getAutobiographyChapter(): Flow<Result<ChapterListModel>>

    suspend fun postCreateChapterList(body: CreateAutobiographyChaptersRequestModel): Flow<Result<Boolean>>

    suspend fun postUpdateCurrentChapter(): Flow<Result<Boolean>>
}
