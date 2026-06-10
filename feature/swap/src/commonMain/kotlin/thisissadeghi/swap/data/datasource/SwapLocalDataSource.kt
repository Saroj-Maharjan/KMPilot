package thisissadeghi.swap.data.datasource

import thisissadeghi.swap.data.model.SwapQuoteResponse

interface SwapLocalDataSource {
    fun getSwapQuote(
        fromAssetId: String? = null,
        toAssetId: String? = null,
    ): SwapQuoteResponse
}
