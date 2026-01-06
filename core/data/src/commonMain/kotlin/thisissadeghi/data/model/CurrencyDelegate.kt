package thisissadeghi.data.model

import kotlinx.serialization.Serializable

/**
 * DO NOT use this class in other models.
 * This model is a trigger for serialization to generate currency serializer.
 */
@Deprecated(
    message = "Avoid using delegate class in other classes",
    replaceWith = ReplaceWith("Currency", "thisissadeghi.common.Currency"),
    level = DeprecationLevel.WARNING,
)
@Serializable
data class CurrencyDelegate(
    val id: Int?,
    val symbol: String?,
)
