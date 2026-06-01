package thisissadeghi.dashboard.presentation.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kmpilot.feature.dashboard.generated.resources.Res
import kmpilot.feature.dashboard.generated.resources.section_savings_goals
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.dashboard.data.model.SavingsGoal
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme

@Composable
internal fun SavingsGoals(
    goals: List<SavingsGoal>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        XText(
            stringResource(Res.string.section_savings_goals),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            goals.forEach { goal -> SavingsGoalItem(goal) }
        }
    }
}

@Preview
@Composable
private fun SavingsGoalsPreview() {
    XTheme {
        SavingsGoals(
            goals =
                listOf(
                    SavingsGoal(name = "Emergency Fund", current = 3_200.0, target = 5_000.0, currency = "$", dueDate = "Dec 2026"),
                    SavingsGoal(name = "Vacation Fund", current = 1_500.0, target = 2_500.0, currency = "$", dueDate = "Aug 2026"),
                ),
        )
    }
}
