package com.tdd.talktobook.app.di

import com.tdd.talktobook.feature.MainViewModel
import com.tdd.talktobook.feature.auth.emailcheck.EmailCheckViewModel
import com.tdd.talktobook.feature.detailchapter.DetailChapterViewModel
import com.tdd.talktobook.feature.home.HomeViewModel
import com.tdd.talktobook.feature.interview.InterviewViewModel
import com.tdd.talktobook.feature.auth.login.LogInViewModel
import com.tdd.talktobook.feature.my.MyViewModel
import com.tdd.talktobook.feature.auth.signup.SignUpViewModel
import com.tdd.talktobook.feature.home.interview.PastInterviewViewModel
import com.tdd.talktobook.feature.onboarding.OnboardingViewModel
import com.tdd.talktobook.feature.publication.PublicationViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule =
    module {
        viewModel { MainViewModel() }
        viewModel { LogInViewModel(get(), get()) }
        viewModel { SignUpViewModel(get()) }
        viewModel { EmailCheckViewModel(get()) }
        viewModel { OnboardingViewModel() }
        viewModel { InterviewViewModel(get(), get()) }
        viewModel { HomeViewModel(get(), get(), get(), get(), get()) }
        viewModel { PastInterviewViewModel() }
        viewModel { DetailChapterViewModel(get()) }
        viewModel { MyViewModel(get(), get(), get()) }
        viewModel { PublicationViewModel() }
    }
