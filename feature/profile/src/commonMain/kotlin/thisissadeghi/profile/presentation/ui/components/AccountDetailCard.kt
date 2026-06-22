package thisissadeghi.profile.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import kmpilot.feature.profile.generated.resources.Res
import kmpilot.feature.profile.generated.resources.profile_label_email
import kmpilot.feature.profile.generated.resources.profile_label_member_tier
import kmpilot.feature.profile.generated.resources.profile_label_name
import kmpilot.feature.profile.generated.resources.profile_section_account_details
import kmpilot.feature.profile.generated.resources.stars_fill
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.designsystem.XHorizontalDivider
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme

@Composable
fun AccountDetailCard(
    name: String,
    email: String,
    tier: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.padding(top = 24.dp)) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 1.dp, shape = RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(24.dp))
                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(24.dp))
                    .padding(20.dp),
        ) {
            XText(
                text = stringResource(Res.string.profile_section_account_details),
                style =
                    MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.1f.em,
                    ),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            ProfileDetailRow(label = Res.string.profile_label_name, value = name)
            XHorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            ProfileDetailRow(label = Res.string.profile_label_email, value = email)
            XHorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
            ProfileMemberTierRow(tier = tier)
        }
    }
}

@Composable
private fun ProfileMemberTierRow(
    tier: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
    ) {
        XText(
            text = stringResource(Res.string.profile_label_member_tier).uppercase(),
            style =
                MaterialTheme.typography.labelSmall.copy(
                    fontSize = 10.sp,
                    letterSpacing = 0.05f.em,
                ),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.size(4.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            XIcon(
                painter = painterResource(Res.drawable.stars_fill),
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp),
            )
            Spacer(Modifier.width(6.dp))
            XText(
                text = tier,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

@Preview
@Composable
private fun AccountDetailCardPreview() {
    XTheme {
        AccountDetailCard(
            name = "Ali Sadeghi",
            email = "ali@example.com",
            tier = "Gold Private Banking",
        )
    }
}
