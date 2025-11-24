package com.tdd.talktobook.app.di

import com.tdd.talktobook.data.repositoryImpl.AuthRepositoryImpl
import com.tdd.talktobook.data.repositoryImpl.AutobiographyRepositoryImpl
import com.tdd.talktobook.data.repositoryImpl.InterviewRepositoryImpl
import com.tdd.talktobook.data.repositoryImpl.MemberRepositoryImpl
import com.tdd.talktobook.data.repositoryImpl.PublicationRepositoryImpl
import com.tdd.talktobook.data.repositoryImpl.ai.InterviewAIRepositoryImpl
import com.tdd.talktobook.domain.repository.AuthRepository
import com.tdd.talktobook.domain.repository.AutobiographyRepository
import com.tdd.talktobook.domain.repository.InterviewAIRepository
import com.tdd.talktobook.domain.repository.InterviewRepository
import com.tdd.talktobook.domain.repository.MemberRepository
import com.tdd.talktobook.domain.repository.PublicationRepository
import org.koin.dsl.module

val repositoryModule =
    module {
        single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
        single<AutobiographyRepository> { AutobiographyRepositoryImpl(get(), get()) }
        single<MemberRepository> { MemberRepositoryImpl(get()) }
        single<PublicationRepository> { PublicationRepositoryImpl(get()) }
        single<InterviewRepository> { InterviewRepositoryImpl(get(), get()) }

        single<InterviewAIRepository> { InterviewAIRepositoryImpl(get()) }
    }
