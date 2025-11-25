package com.tdd.talktobook.domain.repository

import com.tdd.talktobook.domain.entity.enums.AutobiographyStatusType
import com.tdd.talktobook.domain.entity.request.autobiography.CreateAutobiographyChaptersRequestModel
import com.tdd.talktobook.domain.entity.request.autobiography.CreateAutobiographyRequestModel
import com.tdd.talktobook.domain.entity.request.autobiography.EditAutobiographyDetailRequestModel
import com.tdd.talktobook.domain.entity.request.autobiography.StartProgressRequestModel
import com.tdd.talktobook.domain.entity.response.autobiography.AllAutobiographyListModel
import com.tdd.talktobook.domain.entity.response.autobiography.AutobiographiesDetailModel
import com.tdd.talktobook.domain.entity.response.autobiography.ChapterListModel
import com.tdd.talktobook.domain.entity.response.autobiography.CountMaterialsResponseModel
import com.tdd.talktobook.domain.entity.response.autobiography.CurrentInterviewProgressModel
import com.tdd.talktobook.domain.entity.response.autobiography.CurrentProgressAutobiographyModel
import com.tdd.talktobook.domain.entity.response.autobiography.InterviewAutobiographyModel
import com.tdd.talktobook.domain.entity.response.autobiography.SelectedThemeModel
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

    suspend fun getCurrentProgressAutobiography(): Flow<Result<CurrentProgressAutobiographyModel>>

    suspend fun postStartProgress(body: StartProgressRequestModel): Flow<Result<InterviewAutobiographyModel>>

    suspend fun getCountMaterials(autobiographyId: Int): Flow<Result<CountMaterialsResponseModel>>

    suspend fun getCurrentInterviewProgress(autobiographyId: Int): Flow<Result<CurrentInterviewProgressModel>>

    suspend fun saveCurrentAutobiographyStatus(currentStatue: AutobiographyStatusType): Flow<Result<Unit>>

    suspend fun patchCreateAutobiography(autobiographyId: Int): Flow<Result<Boolean>>

    suspend fun getSelectedTheme(autobiographyId: Int): Flow<Result<SelectedThemeModel>>

    suspend fun saveAutobiographyId(autobiographyId: Int): Flow<Result<Unit>>

    suspend fun getAutobiographyId(): Flow<Result<Int>>

    suspend fun getAutobiographyStatus(): Flow<Result<AutobiographyStatusType>>
}
