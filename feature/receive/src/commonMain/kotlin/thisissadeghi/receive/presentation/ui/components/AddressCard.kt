package thisissadeghi.receive.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XIconButton
import thisissadeghi.designsystem.XText

@Composable
fun AddressCard(
    walletAddress: String,
    onCopyClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
                .padding(32.dp),
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            XText(
                text = "Your Bitcoin address",
                modifier = Modifier.padding(bottom = 24.dp),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            AddressPill(walletAddress = walletAddress, onCopyClick = onCopyClick)
        }
    }
}

@Composable
private fun AddressPill(
    walletAddress: String,
    onCopyClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(24.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
                .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        XText(
            text = walletAddress,
            modifier = Modifier.weight(1f),
            fontFamily = FontFamily.Monospace,
            fontSize = 12.sp,
            letterSpacing = (-0.025).em,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        XIconButton(
            onClick = onCopyClick,
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                ),
        ) {
            XIcon(imageVector = Icons.Default.ContentCopy, modifier = Modifier.size(20.dp))
        }
    }
}
