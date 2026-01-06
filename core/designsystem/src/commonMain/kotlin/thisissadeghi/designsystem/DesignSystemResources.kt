package thisissadeghi.designsystem

import kmpilot.core.designsystem.generated.resources.Res
import kmpilot.core.designsystem.generated.resources.confirmation_no_label
import kmpilot.core.designsystem.generated.resources.confirmation_ok_label
import kmpilot.core.designsystem.generated.resources.confirmation_yes_label
import kmpilot.core.designsystem.generated.resources.email
import kmpilot.core.designsystem.generated.resources.placeholder_background
import kmpilot.core.designsystem.generated.resources.required_field_error_message
import kmpilot.core.designsystem.generated.resources.retry_label

/**
 * Created by Ali Sadeghi
 * on 28,Apr,2025
 */
object DesignSystemResources {
    object drawable {
        val placeholder_background = Res.drawable.placeholder_background
    }

    object string {
        val retry_label = Res.string.retry_label
        val confirmation_yes_label = Res.string.confirmation_yes_label
        val confirmation_no_label = Res.string.confirmation_no_label
        val email = Res.string.email
        val required_field_error_message = Res.string.required_field_error_message
        val confirmation_ok_label = Res.string.confirmation_ok_label
    }
}
