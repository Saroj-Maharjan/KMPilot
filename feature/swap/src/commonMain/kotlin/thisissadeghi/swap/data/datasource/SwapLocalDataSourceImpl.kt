package thisissadeghi.swap.data.datasource

import thisissadeghi.swap.data.model.SwapAsset
import thisissadeghi.swap.data.model.SwapQuoteResponse

class SwapLocalDataSourceImpl : SwapLocalDataSource {
    private val bitcoin =
        SwapAsset(
            id = "btc",
            name = "Bitcoin",
            symbol = "BTC",
            avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuBpHT28Fs4C0tG05J2CuuCE6GQCSiKmkrV6IzsdFXgP6oYIteEbk_T70nFunJ33x_MkMlVu9o3Tb7rBhJNO6VxJjsiZSirb9TxL_tpo2B4K4hfeVqgUB6KWBD-sOfho6WkY8H6HKbfpZXc_BFLm1k8PvCli2Cr4wWHv8igKIslADh_v9WuOv-5rfaGYVHc1bxpmYGsejrFXPN0AoFsSSqYu13xJEa7N2jSHIS6O8qQQvF1Qy5W80ZBaefai5fKbPRvLJ_LI8zwkCXfw",
            balance = 0.4821,
        )

    private val ethereum =
        SwapAsset(
            id = "eth",
            name = "Ethereum",
            symbol = "ETH",
            avatarUrl = "https://lh3.googleusercontent.com/aida-public/AB6AXuDWNlb5SAhLn5Lhv0veM6p7wv1oHG6Ugg1koHMhHQW0NZumQozHIz06njFMJK2wYFQneahd3-pqxKSWfJ94Wb21FuFmgVNu6Eazjy8-lRRSzJoXLGwfSX2q9fcjX5UYlaa5BFrOkSal2UFOmrKNOLMuFghRB2vVfWxSJfqhura-0l63mVz7tj86ZyPhW1mU94PnT9PvwGhyRLIaZALBobf8LJZfDUBPtP1jZKfpW5PdTxoTZlkG0Dd1piXzdlDCfa_ANWpzNcz9GvAH",
            balance = 8.5994,
        )

    private val btcToEthQuote =
        SwapQuoteResponse(
            fromAsset = bitcoin,
            toAsset = ethereum,
            exchangeRate = 17.84,
            rateDisplay = "1 BTC ≈ 17.84 ETH",
            networkFee = "$7.20",
            slippageTolerance = "0.5%",
            estimatedTotal = "0.4821 BTC",
        )

    private val ethToBtcQuote =
        SwapQuoteResponse(
            fromAsset = ethereum,
            toAsset = bitcoin,
            exchangeRate = 1.0 / 17.84,
            rateDisplay = "1 ETH ≈ 0.0560 BTC",
            networkFee = "$7.20",
            slippageTolerance = "0.5%",
            estimatedTotal = "8.5994 ETH",
        )

    override fun getSwapQuote(
        fromAssetId: String?,
        toAssetId: String?,
    ): SwapQuoteResponse = if (fromAssetId == "eth" || toAssetId == "btc") ethToBtcQuote else btcToEthQuote
}
