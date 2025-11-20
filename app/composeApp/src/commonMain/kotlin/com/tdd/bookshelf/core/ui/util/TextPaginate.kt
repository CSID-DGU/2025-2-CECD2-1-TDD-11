package com.tdd.bookshelf.core.ui.util

import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints

fun paginateText(
    fullText: String,
    textMeasurer: TextMeasurer,
    maxWidthPx: Int,
    maxHeightPx: Int,
    textStyle: TextStyle,
): List<String> {
    if (fullText.isEmpty()) return emptyList()

    val pages = mutableListOf<String>()
    var startIndex = 0

    while (startIndex < fullText.length) {
        var low = startIndex + 1
        var high = fullText.length
        var best = low

        while (low <= high) {
            val mid = (low + high) / 2
            val candidate = fullText.substring(startIndex, mid)

            val result =
                textMeasurer.measure(
                    text = candidate,
                    style = textStyle,
                    constraints =
                        Constraints(
                            maxWidth = maxWidthPx,
                            maxHeight = maxHeightPx,
                        ),
                )

            if (!result.hasVisualOverflow && result.size.height <= maxHeightPx) {
                best = mid
                low = mid + 1
            } else {
                high = mid - 1
            }
        }

        val rawPageText = fullText.substring(startIndex, best)
        val lastSpace = rawPageText.lastIndexOf(' ')
        val pageText =
            if (lastSpace > 0 && best < fullText.length) {
                rawPageText.substring(0, lastSpace)
            } else {
                rawPageText
            }

        pages += pageText
        startIndex += pageText.length
    }

    return pages
}
