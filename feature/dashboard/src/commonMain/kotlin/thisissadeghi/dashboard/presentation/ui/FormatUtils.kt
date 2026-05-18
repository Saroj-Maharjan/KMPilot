package thisissadeghi.dashboard.presentation.ui

internal fun Double.formatMoney(): String {
    val abs = if (this < 0) -this else this
    val whole = abs.toLong()
    val cents = ((abs - whole) * 100 + 0.5).toInt().coerceIn(0, 99)
    val wholeFormatted =
        whole
            .toString()
            .reversed()
            .chunked(3)
            .joinToString(",")
            .reversed()
    return "$wholeFormatted.${cents.toString().padStart(2, '0')}"
}
