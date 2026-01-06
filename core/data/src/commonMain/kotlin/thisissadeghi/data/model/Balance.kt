package thisissadeghi.data.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import thisissadeghi.common.Money

@Serializable
data class Balance(
    @Contextual
    val remaining: Money?,
)
