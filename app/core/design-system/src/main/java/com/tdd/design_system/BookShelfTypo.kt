package com.tdd.design_system

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val fontFamily = FontFamily(
    Font(R.font.pretendard_semibold, FontWeight.SemiBold),
    Font(R.font.pretendard_medium, FontWeight.Medium),
    Font(R.font.omyu_pretty, FontWeight.Normal)
)

object BookShelfTypo {
    val head10: TextStyle = TextStyle(
        fontSize = 40.sp,
        fontFamily = fontFamily,
        fontWeight = FontWeight.SemiBold
    )

    val head20: TextStyle = TextStyle(
        fontSize = 32.sp,
        fontFamily = fontFamily,
        fontWeight = FontWeight.SemiBold
    )

    val head30: TextStyle = TextStyle(
        fontSize = 24.sp,
        fontFamily = fontFamily,
        fontWeight = FontWeight.SemiBold
    )

    val body10: TextStyle = TextStyle(
        fontSize = 24.sp,
        fontFamily = fontFamily,
        fontWeight = FontWeight.SemiBold
    )

    val body20: TextStyle = TextStyle(
        fontSize = 24.sp,
        fontFamily = fontFamily,
        fontWeight = FontWeight.Medium
    )

    val body30: TextStyle = TextStyle(
        fontSize = 18.sp,
        fontFamily = fontFamily,
        fontWeight = FontWeight.SemiBold
    )

    val body40: TextStyle = TextStyle(
        fontSize = 18.sp,
        fontFamily = fontFamily,
        fontWeight = FontWeight.Medium
    )

    val body50: TextStyle = TextStyle(
        fontSize = 14.sp,
        fontFamily = fontFamily,
        fontWeight = FontWeight.SemiBold
    )

    val body60: TextStyle = TextStyle(
        fontSize = 14.sp,
        fontFamily = fontFamily,
        fontWeight = FontWeight.Medium
    )

    val caption10: TextStyle = TextStyle(
        fontSize = 32.sp,
        fontFamily = fontFamily,
        fontWeight = FontWeight.Normal
    )

    val caption20: TextStyle = TextStyle(
        fontSize = 28.sp,
        fontFamily = fontFamily,
        fontWeight = FontWeight.Normal
    )

    val caption30: TextStyle = TextStyle(
        fontSize = 22.sp,
        fontFamily = fontFamily,
        fontWeight = FontWeight.Normal
    )

    val caption40: TextStyle = TextStyle(
        fontSize = 16.sp,
        fontFamily = fontFamily,
        fontWeight = FontWeight.Normal
    )
}