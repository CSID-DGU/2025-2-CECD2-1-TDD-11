package com.tdd.talktobook.app.di

import com.tdd.talktobook.data.service.AuthService
import com.tdd.talktobook.data.service.AutobiographyService
import com.tdd.talktobook.data.service.InterviewService
import com.tdd.talktobook.data.service.MemberService
import com.tdd.talktobook.data.service.PublicationService
import com.tdd.talktobook.data.service.ai.InterviewAIService
import com.tdd.talktobook.data.service.ai.createInterviewAIService
import com.tdd.talktobook.data.service.createAuthService
import com.tdd.talktobook.data.service.createAutobiographyService
import com.tdd.talktobook.data.service.createInterviewService
import com.tdd.talktobook.data.service.createMemberService
import com.tdd.talktobook.data.service.createPublicationService
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
