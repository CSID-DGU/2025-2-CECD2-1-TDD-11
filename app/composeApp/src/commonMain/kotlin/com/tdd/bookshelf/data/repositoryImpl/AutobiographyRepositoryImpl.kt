package com.tdd.bookshelf.data.repositoryImpl

import com.tdd.bookshelf.data.dataSource.AutobiographyDataSource
import com.tdd.bookshelf.data.mapper.autobiograph.AllAutobiographyMapper
import com.tdd.bookshelf.data.mapper.autobiograph.AutobiographiesDetailMapper
import com.tdd.bookshelf.data.mapper.autobiograph.AutobiographyChapterMapper
import com.tdd.bookshelf.data.mapper.autobiograph.CreateAutobiographyChaptersMapper.toDto
import com.tdd.bookshelf.data.mapper.autobiograph.CreateAutobiographyMapper.toDto
import com.tdd.bookshelf.data.mapper.autobiograph.EditAutobiographyDetailMapper.toDto
import com.tdd.bookshelf.data.mapper.base.DefaultBooleanMapper
import com.tdd.bookshelf.domain.entity.request.autobiography.CreateAutobiographyChaptersRequestModel
import com.tdd.bookshelf.domain.entity.request.autobiography.CreateAutobiographyRequestModel
import com.tdd.bookshelf.domain.entity.request.autobiography.EditAutobiographyDetailRequestModel
import com.tdd.bookshelf.domain.entity.response.autobiography.AllAutobiographyListModel
import com.tdd.bookshelf.domain.entity.response.autobiography.AutobiographiesDetailModel
import com.tdd.bookshelf.domain.entity.response.autobiography.ChapterListModel
import com.tdd.bookshelf.domain.repository.AutobiographyRepository
import kotlinx.coroutines.flow.Flow
import org.koin.core.annotation.Single

@Single(binds = [AutobiographyRepository::class])
class AutobiographyRepositoryImpl(
    private val autobiographyDataSource: AutobiographyDataSource,
) : AutobiographyRepository {
    override suspend fun getAllAutobiographies(): Flow<Result<AllAutobiographyListModel>> =
        AllAutobiographyMapper.responseToModel(apiCall = { autobiographyDataSource.getAllAutobiographies() })

    override suspend fun postCreateAutobiographies(body: CreateAutobiographyRequestModel): Flow<Result<Boolean>> =
        DefaultBooleanMapper.responseToModel(apiCall = {
            autobiographyDataSource.postCreateAutobiographies(
                body.toDto(),
            )
        })

    override suspend fun getAutobiographiesDetail(autobiographyId: Int): Flow<Result<AutobiographiesDetailModel>> =
        AutobiographiesDetailMapper.responseToModel(apiCall = {
            autobiographyDataSource.getAutobiographiesDetail(
                autobiographyId,
            )
        })

    override suspend fun postEditAutobiographiesDetail(
        body: EditAutobiographyDetailRequestModel,
    ): Flow<Result<Boolean>> =
        DefaultBooleanMapper.responseToModel(apiCall = {
            autobiographyDataSource.postEditAutobiographiesDetail(
                body.autobiographyId,
                body.toDto(),
            )
        })

    override suspend fun deleteAutobiography(autobiographyId: Int): Flow<Result<Boolean>> =
        DefaultBooleanMapper.responseToModel(apiCall = {
            autobiographyDataSource.deleteAutobiography(
                autobiographyId,
            )
        })

    override suspend fun getAutobiographyChapter(): Flow<Result<ChapterListModel>> =
        AutobiographyChapterMapper.responseToModel(apiCall = { autobiographyDataSource.getAutobiographyChapter() })

    override suspend fun postCreateChapterList(body: CreateAutobiographyChaptersRequestModel): Flow<Result<Boolean>> =
        DefaultBooleanMapper.responseToModel(apiCall = {
            autobiographyDataSource.postCreateChapterList(
                body.toDto(),
            )
        })

    override suspend fun postUpdateCurrentChapter(): Flow<Result<Boolean>> =
        DefaultBooleanMapper.responseToModel(apiCall = { autobiographyDataSource.postUpdateCurrentChapter() })
}
