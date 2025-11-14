package com.tdd.bookshelf.app.di

import com.tdd.bookshelf.feature.MainViewModel
import com.tdd.bookshelf.feature.detailchapter.DetailChapterViewModel
import com.tdd.bookshelf.feature.home.HomeViewModel
import com.tdd.bookshelf.feature.interview.InterviewViewModel
import com.tdd.bookshelf.feature.login.LogInViewModel
import com.tdd.bookshelf.feature.my.MyViewModel
import com.tdd.bookshelf.feature.signup.SignUpViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val viewModelModule =
    module {
        viewModel { MainViewModel() }
        viewModel { LogInViewModel(get(), get()) }
        viewModel { SignUpViewModel(get(), get()) }
        viewModel { InterviewViewModel(get(), get()) }
        viewModel { HomeViewModel(get(), get(), get(), get(), get()) }
        viewModel { DetailChapterViewModel(get()) }
        viewModel { MyViewModel(get(), get(), get()) }
    }
