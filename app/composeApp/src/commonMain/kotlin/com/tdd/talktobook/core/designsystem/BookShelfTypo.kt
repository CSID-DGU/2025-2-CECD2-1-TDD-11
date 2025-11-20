package com.tdd.talktobook.core.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import bookshelf.composeapp.generated.resources.Res
import bookshelf.composeapp.generated.resources.omyu_pretty
import bookshelf.composeapp.generated.resources.pretendard_medium
import bookshelf.composeapp.generated.resources.pretendard_semibold
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font

@OptIn(ExperimentalResourceApi::class)
@Composable
fun setFont(): FontFamily =
    FontFamily(
        Font(Res.font.pretendard_semibold, FontWeight.SemiBold),
        Font(Res.font.pretendard_medium, FontWeight.Medium),
        Font(Res.font.omyu_pretty, FontWeight.Normal),
    )

object BookShelfTypo {
    val Head1: TextStyle
        @Composable get() =
            TextStyle(
                fontFamily = setFont(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp,
                lineHeight = 56.sp,
            )

    val Head2: TextStyle
        @Composable get() =
            TextStyle(
                fontFamily = setFont(),
                fontWeight = FontWeight.Medium,
                fontSize = 20.sp,
                lineHeight = 56.sp,
            )

    val Head3: TextStyle
        @Composable get() =
            TextStyle(
                fontFamily = setFont(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
                lineHeight = 28.sp,
            )

    val Head4: TextStyle
        @Composable get() =
            TextStyle(
                fontFamily = setFont(),
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
                lineHeight = 28.sp,
            )

    val Head10: TextStyle
        @Composable get() =
            TextStyle(
                fontFamily = setFont(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 40.sp,
            )

    val Head20: TextStyle
        @Composable get() =
            TextStyle(
                fontFamily = setFont(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 32.sp,
            )

    val Head30: TextStyle
        @Composable get() =
            TextStyle(
                fontFamily = setFont(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
            )

    val Body1: TextStyle
        @Composable get() =
            TextStyle(
                fontFamily = setFont(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                lineHeight = 24.sp,
            )

    val Body2: TextStyle
        @Composable get() =
            TextStyle(
                fontFamily = setFont(),
                fontWeight = FontWeight.Medium,
                fontSize = 16.sp,
                lineHeight = 24.sp,
            )

    val Body3: TextStyle
        @Composable get() =
            TextStyle(
                fontFamily = setFont(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )

    val Body4: TextStyle
        @Composable get() =
            TextStyle(
                fontFamily = setFont(),
                fontWeight = FontWeight.Medium,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )

    val Body10: TextStyle
        @Composable get() =
            TextStyle(
                fontFamily = setFont(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp,
            )

    val Body20: TextStyle
        @Composable get() =
            TextStyle(
                fontFamily = setFont(),
                fontWeight = FontWeight.Medium,
                fontSize = 24.sp,
            )

    val Body30: TextStyle
        @Composable get() =
            TextStyle(
                fontFamily = setFont(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp,
            )

    val Body40: TextStyle
        @Composable get() =
            TextStyle(
                fontFamily = setFont(),
                fontWeight = FontWeight.Medium,
                fontSize = 18.sp,
            )

    val Caption1: TextStyle
        @Composable get() =
            TextStyle(
                fontFamily = setFont(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                lineHeight = 18.sp,
            )

    val Caption2: TextStyle
        @Composable get() =
            TextStyle(
                fontFamily = setFont(),
                fontWeight = FontWeight.SemiBold,
                fontSize = 10.sp,
                lineHeight = 12.sp,
            )

    val Caption3: TextStyle
        @Composable get() =
            TextStyle(
                fontFamily = setFont(),
                fontWeight = FontWeight.Normal,
                fontSize = 18.sp,
                lineHeight = 28.sp,
            )

    val Caption4: TextStyle
        @Composable get() =
            TextStyle(
                fontFamily = setFont(),
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                lineHeight = 20.sp,
            )

    // Legacy
    val Black: TextStyle
        @Composable get() =
            TextStyle(
                fontFamily = setFont(),
                fontWeight = FontWeight.Black,
            )

    val Bold: TextStyle
        @Composable get() =
            TextStyle(
                fontFamily = setFont(),
                fontWeight = FontWeight.Bold,
            )

    val ExtraBold: TextStyle
        @Composable get() =
            TextStyle(
                fontFamily = setFont(),
                fontWeight = FontWeight.ExtraBold,
            )

    val ExtraLight: TextStyle
        @Composable get() =
            TextStyle(
                fontFamily = setFont(),
                fontWeight = FontWeight.ExtraLight,
            )

    val Light: TextStyle
        @Composable get() =
            TextStyle(
                fontFamily = setFont(),
                fontWeight = FontWeight.Light,
            )

    val Medium: TextStyle
        @Composable get() =
            TextStyle(
                fontFamily = setFont(),
                fontWeight = FontWeight.Medium,
            )

    val Regular: TextStyle
        @Composable get() =
            TextStyle(
                fontFamily = setFont(),
                fontWeight = FontWeight.Normal,
            )

    val SemiBold: TextStyle
        @Composable get() =
            TextStyle(
                fontFamily = setFont(),
                fontWeight = FontWeight.SemiBold,
            )

    val Thin: TextStyle
        @Composable get() =
            TextStyle(
                fontFamily = setFont(),
                fontWeight = FontWeight.Thin,
            )
}
