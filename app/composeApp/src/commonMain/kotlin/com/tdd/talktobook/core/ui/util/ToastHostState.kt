package com.tdd.talktobook.core.ui.util

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.tdd.talktobook.core.ui.common.type.ToastType
import kotlinx.coroutines.CoroutineScope

@Stable
class ToastHostState {
    var data by mutableStateOf<ToastData?>(null)
        private set
    var visible by mutableStateOf(false)
        private set

    private var job: Job? = null

    fun show(scope: CoroutineScope, message: String, type: ToastType) {
        job?.cancel()
        data = ToastData(message, type)
        visible = true
        job = scope.launch {
            delay(1800)
            visible = false
            delay(250)
            data = null
        }
    }
}

data class ToastData(val message: String, val type: ToastType)

@Composable
fun ToastHost(
    state: ToastHostState,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        AnimatedVisibility(
            visible = state.visible && state.data != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        ) {
            val toast = state.data ?: return@AnimatedVisibility
            ToastPill(message = toast.message, type = toast.type)
        }
    }
}

@Composable
private fun ToastPill(message: String, type: ToastType) {
    val bg = Color(0xFF9EA2A6)
    val iconBg = Color(0xFF8A8E93)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .padding(horizontal = 18.dp, vertical = 12.dp)
            .widthIn(max = 320.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            val symbol = when (type) {
                ToastType.INFO -> "!"
                ToastType.ERROR -> "×"
                ToastType.SUCCESS -> "✓"
            }
            Text(text = symbol, color = Color.White)
        }

        Spacer(Modifier.width(12.dp))

        Text(
            text = message,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
