package com.tdd.talktobook.data.repositoryImpl

import com.tdd.talktobook.data.dataSource.AutobiographyDataSource
import com.tdd.talktobook.data.dataStore.LocalDataStore
import com.tdd.talktobook.data.entity.request.autobiography.GetCoShowGenerateRequestDto
import com.tdd.talktobook.data.mapper.autobiograph.AllAutobiographyMapper
import com.tdd.talktobook.data.mapper.autobiograph.AutobiographiesDetailMapper
import com.tdd.talktobook.data.mapper.autobiograph.AutobiographyChapterMapper
import com.tdd.talktobook.data.mapper.autobiograph.CreateAutobiographyChaptersMapper.toDto
import com.tdd.talktobook.data.mapper.autobiograph.EditAutobiographyDetailMapper.toDto
import com.tdd.talktobook.data.mapper.autobiograph.GetCountMaterialsMapper
import com.tdd.talktobook.data.mapper.autobiograph.GetCurrentInterviewProgressMapper
import com.tdd.talktobook.data.mapper.autobiograph.GetCurrentProgressMapper
import com.tdd.talktobook.data.mapper.autobiograph.GetSelectedThemeMapper
import com.tdd.talktobook.data.mapper.autobiograph.PostStartProgressMapper
import com.tdd.talktobook.data.mapper.base.DefaultBooleanMapper
import com.tdd.talktobook.domain.entity.enums.AutobiographyStatusType
import com.tdd.talktobook.domain.entity.request.autobiography.ChangeAutobiographyStatusRequestModel
import com.tdd.talktobook.domain.entity.request.autobiography.CreateAutobiographyChaptersRequestModel
import com.tdd.talktobook.domain.entity.request.autobiography.CreateAutobiographyRequestModel
import com.tdd.talktobook.domain.entity.request.autobiography.EditAutobiographyDetailRequestModel
import com.tdd.talktobook.domain.entity.request.autobiography.GetCoShowGenerateRequestModel
import com.tdd.talktobook.domain.entity.request.autobiography.StartProgressRequestModel
import com.tdd.talktobook.domain.entity.response.autobiography.AllAutobiographyListModel
import com.tdd.talktobook.domain.entity.response.autobiography.AutobiographiesDetailModel
import com.tdd.talktobook.domain.entity.response.autobiography.ChapterListModel
import com.tdd.talktobook.domain.entity.response.autobiography.CountMaterialsResponseModel
import com.tdd.talktobook.domain.entity.response.autobiography.CurrentInterviewProgressModel
import com.tdd.talktobook.domain.entity.response.autobiography.CurrentProgressAutobiographyModel
import com.tdd.talktobook.domain.entity.response.autobiography.InterviewAutobiographyModel
import com.tdd.talktobook.domain.entity.response.autobiography.SelectedThemeModel
import com.tdd.talktobook.domain.repository.AutobiographyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.koin.core.annotation.Single

@Single(binds = [AutobiographyRepository::class])
class AutobiographyRepositoryImpl(
    private val autobiographyDataSource: AutobiographyDataSource,
    private val localDataStore: LocalDataStore,
) : AutobiographyRepository {
    override suspend fun getAllAutobiographies(): Flow<Result<AllAutobiographyListModel>> =
        AllAutobiographyMapper.responseToModel(apiCall = { autobiographyDataSource.getAllAutobiographies() })

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

    override suspend fun getCurrentProgressAutobiography(): Flow<Result<CurrentProgressAutobiographyModel>> =
        GetCurrentProgressMapper.responseToModel(apiCall = { autobiographyDataSource.getCurrentProgressAutobiography() })

    override suspend fun postStartProgress(body: StartProgressRequestModel): Flow<Result<InterviewAutobiographyModel>> =
        PostStartProgressMapper.responseToModel(apiCall = {
            autobiographyDataSource.postStartProgress(
                body.theme,
                body.reason,
            )
        })

    override suspend fun postCoShowStartProgress(body: StartProgressRequestModel): Flow<Result<InterviewAutobiographyModel>> =
        PostStartProgressMapper.responseToModel(apiCall = {
            autobiographyDataSource.postCoShowStartProgress(body.theme, body.reason)
        })

    override suspend fun getCountMaterials(autobiographyId: Int): Flow<Result<CountMaterialsResponseModel>> =
        GetCountMaterialsMapper.responseToModel(apiCall = {
            autobiographyDataSource.getCountMaterials(
                autobiographyId,
            )
        })

    override suspend fun getCurrentInterviewProgress(autobiographyId: Int): Flow<Result<CurrentInterviewProgressModel>> =
        GetCurrentInterviewProgressMapper.responseToModel(apiCall = {
            autobiographyDataSource.getCurrentInterviewProgress(
                autobiographyId,
            )
        })

    override suspend fun saveCurrentAutobiographyStatus(currentStatue: AutobiographyStatusType): Flow<Result<Unit>> =
        flow { localDataStore.saveCurrentAutobiographyStatus(currentStatue.type) }

    override suspend fun patchCreateAutobiography(body: CreateAutobiographyRequestModel): Flow<Result<Boolean>> =
        DefaultBooleanMapper.responseToModel(apiCall = {
            autobiographyDataSource.patchCreateAutobiography(
                body.autobiographyId, body.name,
            )
        })

    override suspend fun getSelectedTheme(autobiographyId: Int): Flow<Result<SelectedThemeModel>> =
        GetSelectedThemeMapper.responseToModel(apiCall = {
            autobiographyDataSource.getSelectedTheme(
                autobiographyId,
            )
        })

    override suspend fun saveAutobiographyId(autobiographyId: Int): Flow<Result<Unit>> =
        flow { localDataStore.saveCurrentAutobiographyId(autobiographyId) }

    override suspend fun getAutobiographyId(): Flow<Result<Int>> =
        flow {
            localDataStore.currentAutobiographyId.collect { id ->
                if (id != null) {
                    emit(Result.success(id))
                } else {
                    emit(Result.failure(Exception("[dataStore] autobiographyId is null")))
                }
            }
        }

    override suspend fun getAutobiographyStatus(): Flow<Result<AutobiographyStatusType>> =
        flow {
            localDataStore.currentAutobiographyStatus.collect { status ->
                if (status != null) {
                    emit(Result.success(AutobiographyStatusType.getType(status)))
                } else {
                    emit(Result.failure(Exception("[dataStore] autobiography status is null")))
                }
            }
        }

    override suspend fun patchChangeStatus(body: ChangeAutobiographyStatusRequestModel): Flow<Result<Boolean>> =
        DefaultBooleanMapper.responseToModel(apiCall = { autobiographyDataSource.patchChangeStatus(body.autobiographyId, body.status.type) })

    override suspend fun getCoShowGenerate(body: GetCoShowGenerateRequestModel): Flow<Result<Boolean>> =
        DefaultBooleanMapper.responseToModel(apiCall = { autobiographyDataSource.getCoShowGenerate(body.autobiographyId, GetCoShowGenerateRequestDto(body.name)) })

}
