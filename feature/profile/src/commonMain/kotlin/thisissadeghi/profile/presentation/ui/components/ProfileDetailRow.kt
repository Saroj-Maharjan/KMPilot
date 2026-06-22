package thisissadeghi.profile.presentation.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import kmpilot.feature.profile.generated.resources.Res
import kmpilot.feature.profile.generated.resources.profile_label_name
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme

@Composable
fun ProfileDetailRow(
    label: StringResource,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
    ) {
        XText(
            text = stringResource(label).uppercase(),
            style =
                MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    letterSpacing = 0.05f.em,
                ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(4.dp))
        XText(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Preview
@Composable
private fun ProfileDetailRowPreview() {
    XTheme {
        ProfileDetailRow(
            label = Res.string.profile_label_name,
            value = "Ali Sadeghi",
        )
    }
}
