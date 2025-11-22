package com.tdd.talktobook.feature

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import bookshelf.composeapp.generated.resources.Res
import coil3.compose.AsyncImage
import com.tdd.talktobook.core.designsystem.Blue500
import com.tdd.talktobook.core.designsystem.BookShelfTypo
import com.tdd.talktobook.core.designsystem.Gray400
import com.tdd.talktobook.core.designsystem.White0
import org.jetbrains.compose.resources.ExperimentalResourceApi

@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource,
    type: BottomNavType = BottomNavType.HOME,
    onClick: (String) -> Unit = {},
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .background(White0),
        horizontalArrangement = Arrangement.Absolute.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        BottomNavItem(
            navIcon = BottomNavType.getBottomNavIcon(BottomNavType.PUBLICATION),
            isSelected = (type == BottomNavType.PUBLICATION),
            type = BottomNavType.PUBLICATION,
            onClick = { onClick(BottomNavType.getDestination(BottomNavType.PUBLICATION)) },
            interactionSource = interactionSource,
        )

        BottomNavItem(
            navIcon = BottomNavType.getBottomNavIcon(BottomNavType.HOME),
            isSelected = (type == BottomNavType.HOME),
            type = BottomNavType.HOME,
            onClick = { onClick(BottomNavType.getDestination(BottomNavType.HOME)) },
            interactionSource = interactionSource,
        )

        BottomNavItem(
            navIcon = BottomNavType.getBottomNavIcon(BottomNavType.INTERVIEW),
            isSelected = (type == BottomNavType.INTERVIEW),
            type = BottomNavType.INTERVIEW,
            onClick = { onClick(BottomNavType.getDestination(BottomNavType.INTERVIEW)) },
            interactionSource = interactionSource,
        )
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
private fun BottomNavItem(
    navIcon: String,
    isSelected: Boolean = false,
    type: BottomNavType = BottomNavType.DEFAULT,
    onClick: () -> Unit = {},
    interactionSource: MutableInteractionSource,
) {
    Column(
        modifier =
            Modifier
                .padding(vertical = 7.dp)
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = Res.getUri(navIcon),
            contentDescription = "nav icon",
            colorFilter =  if (isSelected) ColorFilter.tint(Blue500) else ColorFilter.tint(Gray400),
            modifier =
                Modifier
                    .size(25.dp),
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = type.navName,
            style = BookShelfTypo.Regular,
            color = if (isSelected) Blue500 else Gray400,
        )
    }
}
