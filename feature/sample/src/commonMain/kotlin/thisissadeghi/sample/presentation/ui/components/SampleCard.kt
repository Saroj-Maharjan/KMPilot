package thisissadeghi.sample.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thisissadeghi.designsystem.XCard
import thisissadeghi.designsystem.XText
import thisissadeghi.sample.data.model.SampleItem

private val GhostGray = Color(0xFFE5E4E7)
private val TextMutedGray = Color(0xFF7D7887)
private val TitleDark = Color(0xFF323036)

/**
 * Premium card component for displaying a sample item.
 * Features a crimson left accent bar, bold title, muted description, and ghost index number.
 */
@Composable
fun SampleCard(
    item: SampleItem,
    index: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    XCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Left crimson accent bar
            Box(
                modifier =
                    Modifier
                        .width(3.dp)
                        .fillMaxHeight()
                        .background(MaterialTheme.colorScheme.primary),
            )

            // Item content
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(horizontal = 16.dp, vertical = 16.dp),
            ) {
                XText(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TitleDark,
                )
                XText(
                    text = item.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextMutedGray,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }

            // Ghost index number — oversized, decorative
            XText(
                text = (index + 1).toString().padStart(2, '0'),
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                color = GhostGray,
                modifier = Modifier.padding(end = 16.dp),
            )
        }
    }
}
