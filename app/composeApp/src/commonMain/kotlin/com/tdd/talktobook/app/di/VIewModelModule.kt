package com.tdd.talktobook.app.di

import com.tdd.talktobook.feature.MainViewModel
import com.tdd.talktobook.feature.auth.emailcheck.EmailCheckViewModel
import com.tdd.talktobook.feature.home.HomeViewModel
import com.tdd.talktobook.feature.interview.InterviewViewModel
import com.tdd.talktobook.feature.auth.login.LogInViewModel
import com.tdd.talktobook.feature.setting.SettingViewModel
import com.tdd.talktobook.feature.auth.signup.SignUpViewModel
import com.tdd.talktobook.feature.home.interview.PastInterviewViewModel
import com.tdd.talktobook.feature.onboarding.OnboardingViewModel
import com.tdd.talktobook.feature.publication.PublicationViewModel
import com.tdd.talktobook.feature.startprogress.StartProgressViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule =
    module {
        viewModel { MainViewModel() }
        viewModel { LogInViewModel(get(), get()) }
        viewModel { SignUpViewModel(get()) }
        viewModel { EmailCheckViewModel(get()) }
        viewModel { OnboardingViewModel(get()) }
        viewModel { StartProgressViewModel(get(), get(), get(), get(), get()) }
        viewModel { InterviewViewModel(get(), get(), get(), get(), get(), get(), get(),get(), get()) }
        viewModel { HomeViewModel(get(), get(), get(), get(), get(), get(), get()) }
        viewModel { PastInterviewViewModel(get()) }
        viewModel { SettingViewModel(get(), get(), get(), get()) }
        viewModel { PublicationViewModel(get()) }
    }
