package com.tdd.feature.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.tdd.design_system.BookShelfTypo
import com.tdd.design_system.Gray5
import com.tdd.design_system.Main3
import com.tdd.design_system.White4
import com.tdd.feature.BottomNavType

@Composable
fun BottomNavBar(
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource,
    type: BottomNavType = BottomNavType.CHAPTER,
    onClick: (String) -> Unit = {},
) {

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(70.dp)
            .background(White4),
        horizontalArrangement = Arrangement.spacedBy(60.dp, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BottomNavType.entries.take(3).forEach { navItem ->
            BottomNavItem(
                interactionSource = interactionSource,
                onClick = onClick,
                type = navItem,
                isSelected = (type == navItem),
                navIcon = BottomNavType.getBottomNavIcon(navItem, (type == navItem))
            )
        }
    }
}

@Composable
fun BottomNavItem(
    navIcon: Int = -1,
    isSelected: Boolean = false,
    type: BottomNavType = BottomNavType.DEFAULT,
    onClick: (String) -> Unit = {},
    interactionSource: MutableInteractionSource,
) {
    Column(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { onClick(BottomNavType.getDestination(type)) }
            ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = navIcon),
            contentDescription = "nav",
            tint = Color.Unspecified,
            modifier = Modifier
                .size(25.dp)
        )

        Text(
            text = type.navName,
            style = BookShelfTypo.body30,
            color = if (isSelected) Main3 else Gray5
        )
    }
}