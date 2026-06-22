package thisissadeghi.profile.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kmpilot.feature.profile.generated.resources.Res
import kmpilot.feature.profile.generated.resources.mail
import kmpilot.feature.profile.generated.resources.person
import kmpilot.feature.profile.generated.resources.profile_edit_changes_hint
import kmpilot.feature.profile.generated.resources.profile_edit_field_email
import kmpilot.feature.profile.generated.resources.profile_edit_field_full_name
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTextField
import thisissadeghi.designsystem.XTheme

@Composable
fun ProfileInputForm(
    name: String,
    email: String,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        XTextField(
            value = name,
            onValueChange = onNameChange,
            label = { XText(stringResource(Res.string.profile_edit_field_full_name)) },
            leadingIcon = {
                XIcon(
                    painter = painterResource(Res.drawable.person),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(24.dp),
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    cursorColor = MaterialTheme.colorScheme.primary,
                ),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
        )
        XTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { XText(stringResource(Res.string.profile_edit_field_email)) },
            leadingIcon = {
                XIcon(
                    painter = painterResource(Res.drawable.mail),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            shape = RoundedCornerShape(24.dp),
            colors =
                OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                    focusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    cursorColor = MaterialTheme.colorScheme.primary,
                ),
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(56.dp),
        )
        XText(
            text = stringResource(Res.string.profile_edit_changes_hint),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, start = 16.dp, end = 16.dp),
        )
    }
}

@Preview
@Composable
private fun ProfileInputFormPreview() {
    XTheme {
        ProfileInputForm(
            name = "Ali Sadeghi",
            email = "ali@example.com",
            onNameChange = {},
            onEmailChange = {},
        )
    }
}
