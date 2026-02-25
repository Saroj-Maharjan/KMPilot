package thisissadeghi.send.data.datasource

import thisissadeghi.common.Either
import thisissadeghi.send.data.model.CoinInfo
import thisissadeghi.send.data.model.NetworkInfo
import thisissadeghi.send.data.model.SendData

class SendLocalDataSourceImpl : SendLocalDataSource {
    override suspend fun getSendData(): Either<SendData> =
        Either.Success(
            SendData(
                recipientAddress = "",
                amount = "0.00",
                selectedCoin = CoinInfo("Bitcoin", "BTC"),
                availableBalance = "Balance: 1.24 BTC (~\$78,420.00)",
                selectedNetwork = NetworkInfo("Bitcoin Network", "BTC \u2022 ERC-20"),
                networkFee = "0.00012 BTC (~\$7.54)",
                totalDeduct = "0.00012 BTC",
                estimatedArrival = "Fast (10 min)",
            ),
        )
}
