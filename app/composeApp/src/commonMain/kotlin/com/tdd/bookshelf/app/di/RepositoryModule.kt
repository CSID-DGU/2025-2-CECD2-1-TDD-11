package com.tdd.bookshelf.app.di

import com.tdd.bookshelf.data.repositoryImpl.AuthRepositoryImpl
import com.tdd.bookshelf.data.repositoryImpl.AutobiographyRepositoryImpl
import com.tdd.bookshelf.data.repositoryImpl.InterviewRepositoryImpl
import com.tdd.bookshelf.data.repositoryImpl.MemberRepositoryImpl
import com.tdd.bookshelf.data.repositoryImpl.PublicationRepositoryImpl
import com.tdd.bookshelf.data.repositoryImpl.ai.InterviewAIRepositoryImpl
import com.tdd.bookshelf.domain.repository.AuthRepository
import com.tdd.bookshelf.domain.repository.AutobiographyRepository
import com.tdd.bookshelf.domain.repository.InterviewAIRepository
import com.tdd.bookshelf.domain.repository.InterviewRepository
import com.tdd.bookshelf.domain.repository.MemberRepository
import com.tdd.bookshelf.domain.repository.PublicationRepository
import org.koin.dsl.module

val repositoryModule =
    module {
        single<AuthRepository> { AuthRepositoryImpl(get(), get()) }
        single<AutobiographyRepository> { AutobiographyRepositoryImpl(get()) }
        single<MemberRepository> { MemberRepositoryImpl(get()) }
        single<PublicationRepository> { PublicationRepositoryImpl(get()) }
        single<InterviewRepository> { InterviewRepositoryImpl(get()) }

        single<InterviewAIRepository> { InterviewAIRepositoryImpl(get()) }
    }
