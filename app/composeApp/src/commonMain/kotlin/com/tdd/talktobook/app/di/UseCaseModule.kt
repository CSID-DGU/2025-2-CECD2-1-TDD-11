package com.tdd.talktobook.app.di

import com.tdd.talktobook.domain.usecase.auth.DeleteLocalAllDataUseCase
import com.tdd.talktobook.domain.usecase.auth.DeleteLocalTokenUseCase
import com.tdd.talktobook.domain.usecase.auth.DeleteUserUseCase
import com.tdd.talktobook.domain.usecase.auth.GetAccessTokenUseCase
import com.tdd.talktobook.domain.usecase.auth.GetRefreshTokenUseCase
import com.tdd.talktobook.domain.usecase.auth.LogOutUseCase
import com.tdd.talktobook.domain.usecase.auth.PostEmailLogInUseCase
import com.tdd.talktobook.domain.usecase.auth.PostEmailSignUpUseCase
import com.tdd.talktobook.domain.usecase.auth.PostEmailVerifyUseCase
import com.tdd.talktobook.domain.usecase.auth.ReissueTokenUseCase
import com.tdd.talktobook.domain.usecase.auth.SaveTokenUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.ChangeAutobiographyStatusUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.DeleteAutobiographyUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.GetAllAutobiographyUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.GetAutobiographiesChapterListUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.GetAutobiographiesDetailUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.GetAutobiographyIdUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.GetAutobiographyStatusUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.GetCountMaterialsUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.GetCurrentInterviewProgressUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.GetCurrentProgressAutobiographyUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.GetSelectedThemeUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.PatchCreateAutobiographyUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.PostCoShowProgressUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.PostCreateAutobiographyChaptersUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.PostEditAutobiographyDetailUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.PostStartProgressUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.PostUpdateCurrentChapterUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.SaveAutobiographyIdUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.SaveCurrentAutobiographyStatusUseCase
import com.tdd.talktobook.domain.usecase.interview.GetCoShowInterviewConversationUseCase
import com.tdd.talktobook.domain.usecase.interview.GetInterviewConversationUseCase
import com.tdd.talktobook.domain.usecase.interview.GetInterviewIdUseCase
import com.tdd.talktobook.domain.usecase.interview.GetInterviewQuestionListUseCase
import com.tdd.talktobook.domain.usecase.interview.GetInterviewSummariesUseCase
import com.tdd.talktobook.domain.usecase.interview.PostInterviewConversationUseCase
import com.tdd.talktobook.domain.usecase.interview.PostInterviewRenewalUseCase
import com.tdd.talktobook.domain.usecase.interview.SaveInterviewIdUseCase
import com.tdd.talktobook.domain.usecase.interview.ai.PostChatInterviewUseCase
import com.tdd.talktobook.domain.usecase.interview.ai.PostStartInterviewUseCase
import com.tdd.talktobook.domain.usecase.member.GetMemberInfoUseCase
import com.tdd.talktobook.domain.usecase.member.PutEditMemberInfoUseCase
import com.tdd.talktobook.domain.usecase.publication.DeletePublicationBookUseCase
import com.tdd.talktobook.domain.usecase.publication.GetMyPublicationUseCase
import com.tdd.talktobook.domain.usecase.publication.GetPublicationProgressUseCase
import com.tdd.talktobook.domain.usecase.publication.PostPublicationUseCase
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.dsl.module

@Module
@ComponentScan("com.tdd.bookshelf.domain")
class UseCaseModule

val useCaseModule =
    module {
        // Auth
        factory { PostEmailLogInUseCase(get()) }
        factory { SaveTokenUseCase(get()) }
        factory { PostEmailSignUpUseCase(get()) }
        factory { PostEmailVerifyUseCase(get()) }
        factory { DeleteUserUseCase(get()) }
        factory { LogOutUseCase(get()) }
        factory { ReissueTokenUseCase(get()) }
        factory { GetAccessTokenUseCase(get()) }
        factory { GetRefreshTokenUseCase(get()) }

        // Autobiography
        factory { GetAllAutobiographyUseCase(get()) }
        factory { GetAutobiographiesDetailUseCase(get()) }
        factory { PostEditAutobiographyDetailUseCase(get()) }
        factory { DeleteAutobiographyUseCase(get()) }
        factory { GetAutobiographiesChapterListUseCase(get()) }
        factory { PostCreateAutobiographyChaptersUseCase(get()) }
        factory { PostUpdateCurrentChapterUseCase(get()) }
        factory { GetCurrentProgressAutobiographyUseCase(get()) }
        factory { PostStartProgressUseCase(get()) }
        factory { PostCoShowProgressUseCase(get()) }
        factory { GetCountMaterialsUseCase(get()) }
        factory { GetCurrentInterviewProgressUseCase(get()) }
        factory { SaveCurrentAutobiographyStatusUseCase(get()) }
        factory { PatchCreateAutobiographyUseCase(get()) }
        factory { GetSelectedThemeUseCase(get()) }
        factory { SaveAutobiographyIdUseCase(get()) }
        factory { GetAutobiographyIdUseCase(get()) }
        factory { GetAutobiographyStatusUseCase(get()) }
        factory { SaveInterviewIdUseCase(get()) }
        factory { GetInterviewIdUseCase(get()) }
        factory { DeleteLocalAllDataUseCase(get()) }
        factory { DeleteLocalTokenUseCase(get()) }
        factory { ChangeAutobiographyStatusUseCase(get()) }

        // Member
        factory { GetMemberInfoUseCase(get()) }
        factory { PutEditMemberInfoUseCase(get()) }

        // Publication
        factory { PostPublicationUseCase(get()) }
        factory { GetMyPublicationUseCase(get()) }
        factory { GetPublicationProgressUseCase(get()) }
        factory { DeletePublicationBookUseCase(get()) }

        // Interview
        factory { GetInterviewConversationUseCase(get()) }
        factory { GetCoShowInterviewConversationUseCase(get()) }
        factory { PostInterviewRenewalUseCase(get()) }
        factory { PostInterviewConversationUseCase(get()) }
        factory { GetInterviewQuestionListUseCase(get()) }
        factory { GetInterviewSummariesUseCase(get()) }

        // AI
        // Interview
        factory { PostStartInterviewUseCase(get()) }
        factory { PostChatInterviewUseCase(get()) }
    }
