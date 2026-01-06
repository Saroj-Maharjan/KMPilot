package thisissadeghi.data.model

import thisissadeghi.common.Currency
import thisissadeghi.common.Money

/**
 * Domain model for storing voucher balance information
 */
data class VoucherBalance(
    val remaining: Money,
    val currency: Currency,
)
