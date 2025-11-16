package com.tdd.bookshelf.app.di

import com.tdd.bookshelf.feature.MainViewModel
import com.tdd.bookshelf.feature.auth.emailcheck.EmailCheckViewModel
import com.tdd.bookshelf.feature.detailchapter.DetailChapterViewModel
import com.tdd.bookshelf.feature.home.HomeViewModel
import com.tdd.bookshelf.feature.interview.InterviewViewModel
import com.tdd.bookshelf.feature.auth.login.LogInViewModel
import com.tdd.bookshelf.feature.my.MyViewModel
import com.tdd.bookshelf.feature.auth.signup.SignUpViewModel
import com.tdd.bookshelf.feature.home.interview.PastInterviewViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule =
    module {
        viewModel { MainViewModel() }
        viewModel { LogInViewModel(get(), get()) }
        viewModel { SignUpViewModel(get(), get()) }
        viewModel { EmailCheckViewModel() }
        viewModel { InterviewViewModel(get(), get()) }
        viewModel { HomeViewModel(get(), get(), get(), get(), get()) }
        viewModel { PastInterviewViewModel() }
        viewModel { DetailChapterViewModel(get()) }
        viewModel { MyViewModel(get(), get(), get()) }
    }
