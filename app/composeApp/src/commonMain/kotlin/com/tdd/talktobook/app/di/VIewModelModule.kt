package com.tdd.talktobook.app.di

import com.tdd.talktobook.feature.MainViewModel
import com.tdd.talktobook.feature.detailchapter.DetailChapterViewModel
import com.tdd.talktobook.feature.home.HomeViewModel
import com.tdd.talktobook.feature.interview.InterviewViewModel
import com.tdd.talktobook.feature.login.LogInViewModel
import com.tdd.talktobook.feature.my.MyViewModel
import com.tdd.talktobook.feature.signup.SignUpViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule =
    module {
        viewModel { MainViewModel() }
        viewModel { LogInViewModel(get(), get(), get(), get()) }
        viewModel { SignUpViewModel(get(), get()) }
        viewModel { InterviewViewModel(get(), get()) }
        viewModel { HomeViewModel(get(), get(), get(), get(), get(), get()) }
        viewModel { DetailChapterViewModel(get()) }
        viewModel { MyViewModel(get(), get(), get()) }
    }
