package thisissadeghi.receive.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thisissadeghi.designsystem.XButton
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme

@Composable
fun AssetSelectorCard(
    coinName: String,
    networkName: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    XButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(64.dp),
        shape = CircleShape,
        colors =
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
            ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        contentPadding = PaddingValues(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(40.dp)
                            .background(XTheme.Colors.Bitcoin, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    XText(
                        text = "₿",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    )
                }
                Column(horizontalAlignment = Alignment.Start) {
                    XText(
                        text = coinName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    XText(
                        text = networkName,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
            XIcon(
                imageVector = Icons.Default.ExpandMore,
                modifier = Modifier.padding(end = 8.dp),
                tint = MaterialTheme.colorScheme.outline,
            )
        }
    }
}
