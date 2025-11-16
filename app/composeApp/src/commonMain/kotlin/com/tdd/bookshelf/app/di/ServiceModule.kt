package com.tdd.bookshelf.app.di

import com.tdd.bookshelf.data.service.AuthService
import com.tdd.bookshelf.data.service.AutobiographyService
import com.tdd.bookshelf.data.service.InterviewService
import com.tdd.bookshelf.data.service.MemberService
import com.tdd.bookshelf.data.service.PublicationService
import com.tdd.bookshelf.data.service.ai.InterviewAIService
import com.tdd.bookshelf.data.service.ai.createInterviewAIService
import com.tdd.bookshelf.data.service.createAuthService
import com.tdd.bookshelf.data.service.createAutobiographyService
import com.tdd.bookshelf.data.service.createInterviewService
import com.tdd.bookshelf.data.service.createMemberService
import com.tdd.bookshelf.data.service.createPublicationService
import de.jensklingenberg.ktorfit.Ktorfit
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan
class ServiceModule {
    @Single
    fun provideAuthService(
        @NoAuthKtor ktorfit: Ktorfit,
    ): AuthService = ktorfit.createAuthService()

    @Single
    fun provideAutobiographyService(
        @BookShelfKtor ktorfit: Ktorfit,
    ): AutobiographyService = ktorfit.createAutobiographyService()

    @Single
    fun provideMemberService(
        @BookShelfKtor ktorfit: Ktorfit,
    ): MemberService = ktorfit.createMemberService()

    @Single
    fun providePublicationService(
        @BookShelfKtor ktorfit: Ktorfit,
    ): PublicationService = ktorfit.createPublicationService()

    @Single
    fun provideInterviewService(
        @BookShelfKtor ktorfit: Ktorfit,
    ): InterviewService = ktorfit.createInterviewService()

    // AI
    @Single
    fun provideInterviewAIService(
        @BookShelfKtorAI ktorfit: Ktorfit,
    ): InterviewAIService = ktorfit.createInterviewAIService()
}
