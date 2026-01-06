package thisissadeghi.designsystem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kmpilot.core.designsystem.generated.resources.Res
import kmpilot.core.designsystem.generated.resources.placeholder_background

@Composable
fun CountryRowItem(
    label: String,
    flagUrl: String,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    CountryRowItem(
        label = label,
        isSelected = isSelected,
        onClick = onClick,
        flag = { CountryFlag(flagUrl) },
    )
}

@Composable
fun CountryRowItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    flag: @Composable () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier =
            Modifier
                .then(if (isSelected) Modifier.background(Color.LightGray) else Modifier)
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(vertical = 12.dp),
    ) {
        Spacer(modifier = Modifier.width(8.dp))

        flag()

        Spacer(modifier = Modifier.width(8.dp))

        Text(
            text = label,
            maxLines = 1,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun CountryFlag(
    url: String,
    modifier: Modifier = Modifier,
) {
    Card(
        shape = MaterialTheme.shapes.small,
        border =
            BorderStroke(
                width = 1.dp,
                color = Color.Black,
            ),
    ) {
        AsyncImage(
            url = url,
            loadingResId = Res.drawable.placeholder_background,
            contentScale = ContentScale.Crop,
            modifier = modifier.then(DefaultFlagModifier),
        )
    }
}

val DefaultFlagModifier: Modifier
    @Composable get() =
        Modifier
            .width(36.dp)
            .aspectRatio(5 / 3f)
            .clip(MaterialTheme.shapes.small)
