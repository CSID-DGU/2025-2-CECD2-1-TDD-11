package com.tdd.bookshelf.feature

import androidx.compose.foundation.Image
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
import androidx.compose.ui.unit.dp
import com.tdd.bookshelf.core.designsystem.Blue500
import com.tdd.bookshelf.core.designsystem.BookShelfTypo
import com.tdd.bookshelf.core.designsystem.Gray400
import com.tdd.bookshelf.core.designsystem.White0
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

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
            navIcon = BottomNavType.getBottomNavIcon(BottomNavType.HOME),
            isSelected = (type == BottomNavType.HOME),
            type = BottomNavType.HOME,
            onClick = { onClick(BottomNavType.getDestination(BottomNavType.HOME)) },
            interactionSource = interactionSource,
        )

        BottomNavItem(
            navIcon = BottomNavType.getBottomNavIcon(BottomNavType.MY),
            isSelected = (type == BottomNavType.MY),
            type = BottomNavType.MY,
            onClick = { onClick(BottomNavType.getDestination(BottomNavType.MY)) },
            interactionSource = interactionSource,
        )
    }
}

@Composable
private fun BottomNavItem(
    navIcon: DrawableResource,
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
        Image(
            painter = painterResource(navIcon),
            contentDescription = "nav icon",
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
