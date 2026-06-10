package thisissadeghi.assetdetail.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import thisissadeghi.assetdetail.data.model.HolderAvatar
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme

private const val MAX_VISIBLE_AVATARS = 5
private val AVATAR_SIZE = 40.dp
private val AVATAR_STEP = 28.dp // 40 - 12 overlap

@Composable
fun AvatarStack(
    holders: List<HolderAvatar>,
    additionalCount: Int,
    modifier: Modifier = Modifier,
) {
    val visibleHolders = holders.take(MAX_VISIBLE_AVATARS)
    val showOverflow = additionalCount > 0 || holders.size > MAX_VISIBLE_AVATARS
    val totalItems = visibleHolders.size + if (showOverflow) 1 else 0

    val totalWidth =
        if (totalItems > 0) {
            AVATAR_STEP * (totalItems - 1) + AVATAR_SIZE
        } else {
            0.dp
        }

    Box(
        modifier =
            modifier
                .width(totalWidth)
                .height(AVATAR_SIZE),
    ) {
        visibleHolders.forEachIndexed { index, holder ->
            AvatarCircle(
                initials = holder.initials,
                backgroundColor = parseHexColor(holder.colorHex),
                textColor = MaterialTheme.colorScheme.primary,
                modifier =
                    Modifier
                        .offset(x = AVATAR_STEP * index)
                        .size(AVATAR_SIZE),
            )
        }
        if (showOverflow) {
            val overflowIndex = visibleHolders.size
            OverflowBadge(
                count = additionalCount + (holders.size - visibleHolders.size),
                modifier =
                    Modifier
                        .offset(x = AVATAR_STEP * overflowIndex)
                        .size(AVATAR_SIZE),
            )
        }
    }
}

@Composable
private fun AvatarCircle(
    initials: String,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .clip(CircleShape)
                .background(backgroundColor, CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.background, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        XText(
            text = initials,
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = textColor,
        )
    }
}

@Composable
private fun OverflowBadge(
    count: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer, CircleShape)
                .border(2.dp, MaterialTheme.colorScheme.background, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        XText(
            text = "+$count",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

private fun parseHexColor(hex: String): Color {
    val cleaned = hex.trimStart('#')
    return try {
        Color(cleaned.toLong(16) or 0xFF000000L)
    } catch (e: NumberFormatException) {
        Color.Gray
    }
}

@Preview
@Composable
private fun AvatarStackPreview() {
    XTheme {
        AvatarStack(
            holders =
                listOf(
                    HolderAvatar("1", "AK", "#4A90D9"),
                    HolderAvatar("2", "MW", "#E74C3C"),
                    HolderAvatar("3", "PR", "#2ECC71"),
                    HolderAvatar("4", "JL", "#9B59B6"),
                    HolderAvatar("5", "ST", "#F39C12"),
                ),
            additionalCount = 42,
        )
    }
}
