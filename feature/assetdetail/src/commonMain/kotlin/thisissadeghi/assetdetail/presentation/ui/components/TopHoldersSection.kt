package thisissadeghi.assetdetail.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kmpilot.feature.assetdetail.generated.resources.Res
import kmpilot.feature.assetdetail.generated.resources.action_join_group
import kmpilot.feature.assetdetail.generated.resources.holders_unavailable
import kmpilot.feature.assetdetail.generated.resources.section_top_holders
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.assetdetail.data.model.HolderAvatar
import thisissadeghi.assetdetail.data.model.TopHoldersResponse
import thisissadeghi.common.UiState
import thisissadeghi.designsystem.DesignSystemResources
import thisissadeghi.designsystem.XIcon
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme
import thisissadeghi.designsystem.motion.rememberReducedMotion
import thisissadeghi.designsystem.motion.shimmer

@Composable
fun TopHoldersSection(
    holdersState: UiState<TopHoldersResponse>,
    onJoinGroupClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val reducedMotion = rememberReducedMotion()
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 48.dp),
    ) {
        XText(
            text = stringResource(Res.string.section_top_holders),
            style =
                MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        when (holdersState) {
            is UiState.Loading, UiState.Uninitialized -> {
                val shimmerHighlight =
                    androidx.compose.ui.graphics.Color(
                        red = surfaceVariant.red,
                        green = surfaceVariant.green,
                        blue = surfaceVariant.blue,
                        alpha = 0.5f,
                    )
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .then(
                                if (reducedMotion) {
                                    Modifier.background(surfaceVariant)
                                } else {
                                    Modifier.shimmer(
                                        baseColor = surfaceVariant,
                                        highlightColor = shimmerHighlight,
                                    )
                                },
                            ),
                )
            }

            is UiState.Failed -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .background(surfaceVariant, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center,
                ) {
                    XText(
                        text = stringResource(Res.string.holders_unavailable),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            is UiState.Success -> {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AvatarStack(
                        holders = holdersState.value.holders,
                        additionalCount = holdersState.value.additionalCount,
                    )

                    Row(
                        modifier =
                            Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .clickable { onJoinGroupClick() }
                                .padding(horizontal = 8.dp, vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        XText(
                            text = stringResource(Res.string.action_join_group),
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary,
                        )
                        XIcon(
                            painter = painterResource(DesignSystemResources.drawable.chevron_right),
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun TopHoldersSectionPreview() {
    XTheme {
        TopHoldersSection(
            holdersState =
                UiState.Success(
                    TopHoldersResponse(
                        assetId = "bitcoin",
                        holders =
                            listOf(
                                HolderAvatar("1", "AK", "#4A90D9"),
                                HolderAvatar("2", "MW", "#E74C3C"),
                                HolderAvatar("3", "PR", "#2ECC71"),
                            ),
                        additionalCount = 42,
                    ),
                ),
            onJoinGroupClick = {},
        )
    }
}
