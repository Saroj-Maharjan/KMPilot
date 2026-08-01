package thisissadeghi.receive.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kmpilot.feature.receive.generated.resources.Res
import kmpilot.feature.receive.generated.resources.copy_address_label
import kmpilot.feature.receive.generated.resources.share_label
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.designsystem.XButton
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XText

@Composable
fun ReceiveBottomBar(
    onShareClick: () -> Unit,
    onCopyClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .padding(horizontal = 16.dp)
                .padding(top = 16.dp)
                // Bar background bleeds to the screen edge; pad content clear of the nav bar.
                // exclude(ime): when the keyboard is open the shell already lifts the whole NavHost
                // by the IME inset, so drop the nav-bar pad here to avoid a double gap above the keyboard.
                .windowInsetsPadding(WindowInsets.navigationBars.exclude(WindowInsets.ime)),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        XButton(
            onClick = onShareClick,
            modifier =
                Modifier
                    .weight(1f)
                    .height(56.dp),
            shape = RoundedCornerShape(24.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        ) {
            XIcon(imageVector = Icons.Default.Share, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.size(8.dp))
            XText(text = stringResource(Res.string.share_label), fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
        XButton(
            onClick = onCopyClick,
            modifier =
                Modifier
                    .weight(1f)
                    .height(56.dp)
                    .shadow(
                        elevation = 4.dp,
                        shape = RoundedCornerShape(24.dp),
                        ambientColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    ),
            shape = RoundedCornerShape(24.dp),
            colors =
                ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                ),
        ) {
            XIcon(imageVector = Icons.Default.ContentCopy, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.size(8.dp))
            XText(text = stringResource(Res.string.copy_address_label), fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}
