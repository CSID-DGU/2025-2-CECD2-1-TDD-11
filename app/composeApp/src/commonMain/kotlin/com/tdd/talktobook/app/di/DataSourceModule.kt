package com.tdd.talktobook.app.di

import com.tdd.talktobook.data.dataSource.AuthDataSource
import com.tdd.talktobook.data.dataSource.AutobiographyDataSource
import com.tdd.talktobook.data.dataSource.InterviewDataSource
import com.tdd.talktobook.data.dataSource.MemberDataSource
import com.tdd.talktobook.data.dataSource.PublicationDataSource
import com.tdd.talktobook.data.dataSource.ai.InterviewAIDataSource
import com.tdd.talktobook.data.dataSource.dataSourceImpl.AuthDataSourceImpl
import com.tdd.talktobook.data.dataSource.dataSourceImpl.AutobiographyDataSourceImpl
import com.tdd.talktobook.data.dataSource.dataSourceImpl.InterviewDataSourceImpl
import com.tdd.talktobook.data.dataSource.dataSourceImpl.MemberDataSourceImpl
import com.tdd.talktobook.data.dataSource.dataSourceImpl.PublicationDataSourceImpl
import com.tdd.talktobook.data.dataSource.dataSourceImpl.ai.InterviewAIDataSourceImpl
import org.koin.dsl.module

val dataSourceModule =
    module {
        single<AuthDataSource> { AuthDataSourceImpl(get()) }
        single<AutobiographyDataSource> { AutobiographyDataSourceImpl(get()) }
        single<MemberDataSource> { MemberDataSourceImpl(get()) }
        single<PublicationDataSource> { PublicationDataSourceImpl(get()) }
        single<InterviewDataSource> { InterviewDataSourceImpl(get()) }

        single<InterviewAIDataSource> { InterviewAIDataSourceImpl(get()) }
    }
