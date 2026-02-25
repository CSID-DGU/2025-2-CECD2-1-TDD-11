package com.tdd.talktobook.core.ui.util

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.minus
import kotlinx.datetime.plus

fun generateCalendarDays(
    year: Int,
    month: Int,
): List<LocalDate> {
    val days = mutableListOf<LocalDate>()
    var date = LocalDate(year, month, 1)

    while (date.monthNumber == month) {
        days.add(date)
        date = date.plus(DatePeriod(days = 1))
    }

    return days
}

// 이번 달 일수 계산
fun LocalDate.daysInMonth(): Int {
    val firstDayNextMonth =
        if (this.monthNumber == 12) {
            LocalDate(this.year + 1, 1, 1)
        } else {
            LocalDate(this.year, this.monthNumber + 1, 1)
        }

    return firstDayNextMonth
        .minus(1, DateTimeUnit.DAY)
        .dayOfMonth
}

// 윤년 계산
private fun isLeapYear(year: Int): Boolean =
    (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)

fun setBeforeYearMonth(
    currentYear: Int,
    currentMonth: Int,
): List<Int> {
    return if (currentMonth == 1) {
        listOf(currentYear - 1, 12)
    } else {
        listOf(currentYear, currentMonth - 1)
    }
}

fun setAfterYearMonth(
    currentYear: Int,
    currentMonth: Int,
): List<Int> {
    return if (currentMonth == 12) {
        listOf(currentYear + 1, 1)
    } else {
        listOf(currentYear, currentMonth + 1)
    }
}
