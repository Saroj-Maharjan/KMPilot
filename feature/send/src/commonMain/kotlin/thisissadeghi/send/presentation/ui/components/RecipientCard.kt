package thisissadeghi.send.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kmpilot.feature.send.generated.resources.Res
import kmpilot.feature.send.generated.resources.cd_paste
import kmpilot.feature.send.generated.resources.cd_scan_qr
import kmpilot.feature.send.generated.resources.recipient_label
import kmpilot.feature.send.generated.resources.recipient_placeholder
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XIconButton
import thisissadeghi.designsystem.XText

@Composable
fun RecipientCard(
    address: String,
    onAddressChange: (String) -> Unit,
    onPasteClick: () -> Unit,
    onQrClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp)),
    ) {
        Box(
            modifier =
                Modifier
                    .align(Alignment.TopStart)
                    .width(4.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primary),
        )
        Column(modifier = Modifier.padding(20.dp)) {
            XText(
                text = stringResource(Res.string.recipient_label),
                style =
                    TextStyle(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (1.2).sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                modifier = Modifier.padding(bottom = 12.dp),
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                BasicTextField(
                    value = address,
                    onValueChange = onAddressChange,
                    modifier = Modifier.weight(1f),
                    textStyle =
                        TextStyle(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp,
                            fontStyle = FontStyle.Italic,
                        ),
                    decorationBox = { innerTextField ->
                        if (address.isEmpty()) {
                            XText(
                                text = stringResource(Res.string.recipient_placeholder),
                                style =
                                    TextStyle(
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                        fontSize = 16.sp,
                                        fontStyle = FontStyle.Italic,
                                    ),
                            )
                        }
                        innerTextField()
                    },
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    XIconButton(
                        onClick = onPasteClick,
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.primary,
                            ),
                    ) {
                        XIcon(
                            imageVector = Icons.Default.ContentPaste,
                            contentDescription = stringResource(Res.string.cd_paste),
                        )
                    }
                    XIconButton(
                        onClick = onQrClick,
                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = MaterialTheme.colorScheme.primary,
                            ),
                    ) {
                        XIcon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = stringResource(Res.string.cd_scan_qr),
                        )
                    }
                }
            }
        }
    }
}
