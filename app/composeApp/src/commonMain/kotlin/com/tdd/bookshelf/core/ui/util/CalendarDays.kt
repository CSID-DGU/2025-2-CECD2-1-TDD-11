package com.tdd.bookshelf.core.ui.util

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus

fun generateCalendarDays(year: Int, month: Int): List<LocalDate> {
    val days = mutableListOf<LocalDate>()
    var date = LocalDate(year, month, 1)

    while (date.monthNumber == month) {
        days.add(date)
        date = date.plus(DatePeriod(days = 1))
    }

    return days
}

// 윤년 계산
private fun isLeapYear(year: Int): Boolean =
    (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)

fun setBeforeYearMonth(currentYear: Int, currentMonth: Int): List<Int> {
    return if (currentMonth == 1) {
        listOf(currentYear - 1, 12)
    } else {
        listOf(currentYear, currentMonth - 1)
    }
}

fun setAfterYearMonth(currentYear: Int, currentMonth: Int): List<Int> {
    return if (currentMonth == 12) {
        listOf(currentYear + 1, 1)
    } else {
        listOf(currentYear, currentMonth + 1)
    }
}