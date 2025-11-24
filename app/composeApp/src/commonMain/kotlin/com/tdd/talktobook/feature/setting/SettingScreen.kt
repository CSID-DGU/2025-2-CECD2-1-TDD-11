package com.tdd.talktobook.feature.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.tdd.talktobook.BuildKonfig
import com.tdd.talktobook.core.designsystem.BackGround1
import com.tdd.talktobook.core.designsystem.BackGround4
import com.tdd.talktobook.core.designsystem.BookShelfTypo
import com.tdd.talktobook.core.designsystem.Gray5
import com.tdd.talktobook.core.designsystem.Main1
import com.tdd.talktobook.core.designsystem.SettingAge
import com.tdd.talktobook.core.designsystem.SettingCurrentVersion
import com.tdd.talktobook.core.designsystem.SettingDelete
import com.tdd.talktobook.core.designsystem.SettingGender
import com.tdd.talktobook.core.designsystem.SettingLogOut
import com.tdd.talktobook.core.designsystem.SettingOccupation
import com.tdd.talktobook.core.designsystem.SettingPolicy
import com.tdd.talktobook.core.designsystem.SettingTitle
import com.tdd.talktobook.core.ui.common.content.ItemContentRow
import com.tdd.talktobook.core.ui.common.content.TopBarContent
import com.tdd.talktobook.core.ui.util.openUrl
import com.tdd.talktobook.domain.entity.response.member.MemberInfoResponseModel
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.koin.compose.viewmodel.koinViewModel

@Composable
internal fun SettingScreen(
    goBackPage: () -> Unit,
) {
    val viewModel: SettingViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val interactionSource = remember { MutableInteractionSource() }

    val policyUrl = BuildKonfig.POLICY_URL

    SettingContent(
        interactionSource = interactionSource,
        onClickBack = { goBackPage() },
        memberInfo = uiState.memberInfo,
        onClickDelete = { viewModel.deleteUser() },
        onClickPolicy = { openUrl(policyUrl) },
        onClickLogOut = {}
    )
}

@Composable
private fun SettingContent(
    interactionSource: MutableInteractionSource,
    onClickBack: () -> Unit,
    memberInfo: MemberInfoResponseModel,
    onClickPolicy: () -> Unit,
    onClickLogOut: () -> Unit,
    onClickDelete: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(BackGround4),
    ) {
        TopBarContent(
            content = SettingTitle,
            interactionSource = interactionSource,
            iconVisible = true,
            onClickIcon = onClickBack
        )

        SettingProfileBox(
            ageGroup = memberInfo.ageGroup,
            gender = memberInfo.gender,
            occupation = memberInfo.occupation
        )

        Spacer(modifier = Modifier.padding(top = 10.dp))

        ItemContentRow(
            iconImgUrl = "files/ic_policy.svg",
            content = SettingPolicy,
            onClickNext = onClickPolicy
        )

        ItemContentRow(
            iconImgUrl = "files/ic_version.svg",
            content = SettingCurrentVersion,
            isNextVisible = false
        )

        Text(
            text = SettingLogOut,
            color = Gray5,
            style = BookShelfTypo.Body2,
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 20.dp)
                .clickable(
                    onClick = onClickLogOut,
                    interactionSource = interactionSource,
                    indication = null
                )
        )

        Text(
            text = SettingDelete,
            color = Gray5,
            style = BookShelfTypo.Body2.copy(
                textDecoration = TextDecoration.Underline
            ),
            modifier = Modifier
                .padding(vertical = 16.dp, horizontal = 20.dp)
                .clickable(
                    onClick = onClickDelete,
                    interactionSource = interactionSource,
                    indication = null
                )
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun SettingProfileBox(
    ageGroup: String,
    gender: String,
    occupation: String,
) {
    Row(
        modifier =
            Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(5.dp))
                .background(BackGround1)
                .border(1.dp, Main1, RoundedCornerShape(5.dp)),
    ) {
//        Text(
//            text = buildAnnotatedString {
//                withStyle(
//                    style = BookShelfTypo.Body1.toSpanStyle().copy(
//                        color = Black1
//                    )
//                ) {
//                    append(SettingEmail)
//                }
//
//                withStyle(
//                    style = BookShelfTypo.Caption3.toSpanStyle().copy(
//                        color = Black1
//                    )
//                ) {
//                    append(email)
//                }
//            },
//            modifier = Modifier
//                .padding(top = 12.dp, start = 15.dp)
//        )

        Row(
            modifier = Modifier
                .padding(top = 15.dp, start = 15.dp, bottom = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = BookShelfTypo.Caption1.toSpanStyle().copy(
                            color = Gray5
                        )
                    ) {
                        append(SettingAge)
                    }

                    withStyle(
                        style = BookShelfTypo.Caption4.toSpanStyle().copy(
                            color = Gray5
                        )
                    ) {
                        append(ageGroup)
                    }
                },
                modifier = Modifier
                    .padding(end = 12.dp)
            )

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = BookShelfTypo.Caption1.toSpanStyle().copy(
                            color = Gray5
                        )
                    ) {
                        append(SettingGender)
                    }

                    withStyle(
                        style = BookShelfTypo.Caption4.toSpanStyle().copy(
                            color = Gray5
                        )
                    ) {
                        append(gender)
                    }
                },
                modifier = Modifier
                    .padding(end = 12.dp)
            )

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = BookShelfTypo.Caption1.toSpanStyle().copy(
                            color = Gray5
                        )
                    ) {
                        append(SettingOccupation)
                    }

                    withStyle(
                        style = BookShelfTypo.Caption4.toSpanStyle().copy(
                            color = Gray5
                        )
                    ) {
                        append(occupation)
                    }
                },
                modifier = Modifier
                    .padding(end = 12.dp)
            )
        }
    }
}