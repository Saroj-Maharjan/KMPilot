package thisissadeghi.data.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import thisissadeghi.common.Money

@Serializable
class TransactionsSum(
    @Contextual
    @SerialName("sum_amount")
    val sum: Money?,
)
