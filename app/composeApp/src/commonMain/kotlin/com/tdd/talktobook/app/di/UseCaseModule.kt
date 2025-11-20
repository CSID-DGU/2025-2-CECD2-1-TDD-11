package com.tdd.talktobook.app.di

import com.tdd.talktobook.domain.usecase.auth.PostEmailLogInUseCase
import com.tdd.talktobook.domain.usecase.auth.PostEmailSignUpUseCase
import com.tdd.talktobook.domain.usecase.auth.SaveTokenUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.DeleteAutobiographyUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.GetAllAutobiographyUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.GetAutobiographiesChapterListUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.GetAutobiographiesDetailUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.PostCreateAutobiographyChaptersUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.PostCreateAutobiographyUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.PostEditAutobiographyDetailUseCase
import com.tdd.talktobook.domain.usecase.autobiograph.PostUpdateCurrentChapterUseCase
import com.tdd.talktobook.domain.usecase.interview.GetInterviewConversationUseCase
import com.tdd.talktobook.domain.usecase.interview.GetInterviewQuestionListUseCase
import com.tdd.talktobook.domain.usecase.interview.PostInterviewConversationUseCase
import com.tdd.talktobook.domain.usecase.interview.PostInterviewRenewalUseCase
import com.tdd.talktobook.domain.usecase.interview.ai.PostCreateInterviewChatUseCase
import com.tdd.talktobook.domain.usecase.interview.ai.PostCreateInterviewQuestionUseCase
import com.tdd.talktobook.domain.usecase.member.GetMemberInfoUseCase
import com.tdd.talktobook.domain.usecase.member.GetMemberProfileUseCase
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

        // Autobiography
        factory { GetAllAutobiographyUseCase(get()) }
        factory { PostCreateAutobiographyUseCase(get()) }
        factory { GetAutobiographiesDetailUseCase(get()) }
        factory { PostEditAutobiographyDetailUseCase(get()) }
        factory { DeleteAutobiographyUseCase(get()) }
        factory { GetAutobiographiesChapterListUseCase(get()) }
        factory { PostCreateAutobiographyChaptersUseCase(get()) }
        factory { PostUpdateCurrentChapterUseCase(get()) }

        // Member
        factory { GetMemberInfoUseCase(get()) }
        factory { PutEditMemberInfoUseCase(get()) }
        factory { GetMemberProfileUseCase(get()) }

        // Publication
        factory { PostPublicationUseCase(get()) }
        factory { GetMyPublicationUseCase(get()) }
        factory { GetPublicationProgressUseCase(get()) }
        factory { DeletePublicationBookUseCase(get()) }

        // Interview
        factory { GetInterviewConversationUseCase(get()) }
        factory { PostInterviewRenewalUseCase(get()) }
        factory { PostInterviewConversationUseCase(get()) }
        factory { GetInterviewQuestionListUseCase(get()) }

        // AI
        // Interview
        factory { PostCreateInterviewQuestionUseCase(get()) }
        factory { PostCreateInterviewChatUseCase(get()) }
    }
