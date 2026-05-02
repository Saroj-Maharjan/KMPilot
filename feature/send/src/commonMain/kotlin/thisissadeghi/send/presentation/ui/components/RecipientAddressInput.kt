package thisissadeghi.send.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XIconButton
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTextField

@Composable
fun RecipientAddressInput(
    value: String,
    onValueChange: (String) -> Unit,
    onPasteClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        XText(
            text = "Recipient Address",
            style =
                TextStyle(
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                ),
            modifier = Modifier.padding(bottom = 8.dp),
        )
        XTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                XText(
                    text = "Paste or scan address",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                ),
            singleLine = true,
            trailingIcon = {
                XIconButton(
                    onClick = onPasteClick,
                    contentPadding = PaddingValues(4.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = MaterialTheme.colorScheme.primary,
                        ),
                ) {
                    XIcon(
                        imageVector = Icons.Default.ContentPaste,
                        contentDescription = "Paste",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            },
        )
    }
}
