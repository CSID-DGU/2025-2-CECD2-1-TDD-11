package com.tdd.bookshelf.core.designsystem

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import bookshelf.composeapp.generated.resources.Res
import bookshelf.composeapp.generated.resources.pretendard_black
import bookshelf.composeapp.generated.resources.pretendard_bold
import bookshelf.composeapp.generated.resources.pretendard_extrabold
import bookshelf.composeapp.generated.resources.pretendard_extralight
import bookshelf.composeapp.generated.resources.pretendard_light
import bookshelf.composeapp.generated.resources.pretendard_medium
import bookshelf.composeapp.generated.resources.pretendard_regular
import bookshelf.composeapp.generated.resources.pretendard_semibold
import bookshelf.composeapp.generated.resources.pretendard_thin
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font

@OptIn(ExperimentalResourceApi::class)
@Composable
fun setFont(): FontFamily =
    FontFamily(
        Font(Res.font.pretendard_black, FontWeight.Black),
        Font(Res.font.pretendard_bold, FontWeight.Bold),
        Font(Res.font.pretendard_extrabold, FontWeight.ExtraBold),
        Font(Res.font.pretendard_extralight, FontWeight.ExtraLight),
        Font(Res.font.pretendard_light, FontWeight.Light),
        Font(Res.font.pretendard_medium, FontWeight.Medium),
        Font(Res.font.pretendard_regular, FontWeight.Normal),
        Font(Res.font.pretendard_semibold, FontWeight.SemiBold),
        Font(Res.font.pretendard_thin, FontWeight.Thin),
    )

object BookShelfTypo {
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
