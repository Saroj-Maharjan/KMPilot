package thisissadeghi.assetdetail.data.datasource

import thisissadeghi.assetdetail.data.model.ActivityResponse
import thisissadeghi.assetdetail.data.model.AssetDetailResponse
import thisissadeghi.assetdetail.data.model.AssetTransaction
import thisissadeghi.assetdetail.data.model.HolderAvatar
import thisissadeghi.assetdetail.data.model.PriceHistoryResponse
import thisissadeghi.assetdetail.data.model.PricePoint
import thisissadeghi.assetdetail.data.model.TopHoldersResponse

class AssetDetailLocalDataSourceImpl : AssetDetailLocalDataSource {
    override suspend fun getDetail(assetId: String): AssetDetailResponse =
        AssetDetailResponse(
            id = "bitcoin",
            name = "Bitcoin",
            symbol = "BTC",
            price = 67420.50,
            changePercent24h = 2.84,
            marketCap = 1_320_000_000_000.0,
            volume24h = 28_400_000_000.0,
            circulatingSupply = 19_700_000.0,
            holdingAmount = 0.0842,
            holdingFiatValue = 5679.42,
            currency = "USD",
        )

    override suspend fun getPriceHistory(
        assetId: String,
        period: String,
    ): PriceHistoryResponse {
        val basePrice = 67420.50
        val dataPoints =
            when (period) {
                "1D" ->
                    listOf(
                        PricePoint("00:00", 66200.00),
                        PricePoint("01:00", 66350.00),
                        PricePoint("02:00", 66480.00),
                        PricePoint("03:00", 66120.00),
                        PricePoint("04:00", 65980.00),
                        PricePoint("05:00", 66050.00),
                        PricePoint("06:00", 66400.00),
                        PricePoint("07:00", 66750.00),
                        PricePoint("08:00", 67100.00),
                        PricePoint("09:00", 67350.00),
                        PricePoint("10:00", 67200.00),
                        PricePoint("11:00", 67580.00),
                        PricePoint("12:00", 67850.00),
                        PricePoint("13:00", 67420.00),
                        PricePoint("14:00", 67100.00),
                        PricePoint("15:00", 66950.00),
                        PricePoint("16:00", 67300.00),
                        PricePoint("17:00", 67550.00),
                        PricePoint("18:00", 67800.00),
                        PricePoint("19:00", 68100.00),
                        PricePoint("20:00", 67920.00),
                        PricePoint("21:00", 67650.00),
                        PricePoint("22:00", 67400.00),
                        PricePoint("23:00", 67420.50),
                    )
                "1W" ->
                    listOf(
                        PricePoint("Mon", 64800.00),
                        PricePoint("Tue", 65400.00),
                        PricePoint("Wed", 66200.00),
                        PricePoint("Thu", 65800.00),
                        PricePoint("Fri", 66900.00),
                        PricePoint("Sat", 67100.00),
                        PricePoint("Sun", 67420.50),
                    )
                "1M" ->
                    listOf(
                        PricePoint("Jun 1", 63500.00),
                        PricePoint("Jun 2", 63800.00),
                        PricePoint("Jun 3", 64200.00),
                        PricePoint("Jun 4", 63900.00),
                        PricePoint("Jun 5", 64500.00),
                        PricePoint("Jun 6", 65100.00),
                        PricePoint("Jun 7", 64800.00),
                        PricePoint("Jun 8", 65400.00),
                        PricePoint("Jun 9", 65800.00),
                        PricePoint("Jun 10", 66200.00),
                        PricePoint("Jun 11", 65900.00),
                        PricePoint("Jun 12", 66500.00),
                        PricePoint("Jun 13", 66800.00),
                        PricePoint("Jun 14", 66400.00),
                        PricePoint("Jun 15", 67000.00),
                        PricePoint("Jun 16", 67200.00),
                        PricePoint("Jun 17", 66900.00),
                        PricePoint("Jun 18", 67400.00),
                        PricePoint("Jun 19", 67800.00),
                        PricePoint("Jun 20", 67500.00),
                        PricePoint("Jun 21", 68100.00),
                        PricePoint("Jun 22", 67700.00),
                        PricePoint("Jun 23", 67300.00),
                        PricePoint("Jun 24", 67900.00),
                        PricePoint("Jun 25", 68200.00),
                        PricePoint("Jun 26", 67800.00),
                        PricePoint("Jun 27", 67100.00),
                        PricePoint("Jun 28", 67600.00),
                        PricePoint("Jun 29", 67900.00),
                        PricePoint("Jun 30", basePrice),
                    )
                "1Y" ->
                    listOf(
                        PricePoint("Jul '25", 42000.00),
                        PricePoint("Aug '25", 44500.00),
                        PricePoint("Sep '25", 46200.00),
                        PricePoint("Oct '25", 52800.00),
                        PricePoint("Nov '25", 58400.00),
                        PricePoint("Dec '25", 62100.00),
                        PricePoint("Jan '26", 59800.00),
                        PricePoint("Feb '26", 61500.00),
                        PricePoint("Mar '26", 63200.00),
                        PricePoint("Apr '26", 65000.00),
                        PricePoint("May '26", 66400.00),
                        PricePoint("Jun '26", basePrice),
                    )
                else -> { // "All"
                    listOf(
                        PricePoint("2021", 29000.00),
                        PricePoint("Jan '21", 33000.00),
                        PricePoint("Apr '21", 58000.00),
                        PricePoint("Jul '21", 32000.00),
                        PricePoint("Oct '21", 60000.00),
                        PricePoint("Jan '22", 47000.00),
                        PricePoint("Apr '22", 39000.00),
                        PricePoint("Jul '22", 22000.00),
                        PricePoint("Oct '22", 19000.00),
                        PricePoint("Jan '23", 16500.00),
                        PricePoint("Apr '23", 30000.00),
                        PricePoint("Jul '23", 29800.00),
                        PricePoint("Oct '23", 34000.00),
                        PricePoint("Jan '24", 42000.00),
                        PricePoint("Apr '24", 70000.00),
                        PricePoint("Jul '24", 58000.00),
                        PricePoint("Oct '24", 61000.00),
                        PricePoint("Jan '25", 96000.00),
                        PricePoint("Feb '25", 85000.00),
                        PricePoint("Mar '25", 88000.00),
                        PricePoint("Apr '25", 81000.00),
                        PricePoint("May '25", 72000.00),
                        PricePoint("Jun '25", 68000.00),
                        PricePoint("Jul '25", 42000.00),
                        PricePoint("Aug '25", 44500.00),
                        PricePoint("Sep '25", 46200.00),
                        PricePoint("Oct '25", 52800.00),
                        PricePoint("Nov '25", 58400.00),
                        PricePoint("Dec '25", 62100.00),
                        PricePoint("Jan '26", 59800.00),
                        PricePoint("Feb '26", 61500.00),
                        PricePoint("Mar '26", 63200.00),
                        PricePoint("Apr '26", 65000.00),
                        PricePoint("May '26", 66400.00),
                        PricePoint("Jun '26", 67200.00),
                        PricePoint("Jul '26", basePrice),
                        PricePoint("Now", basePrice),
                        PricePoint("", 67400.00),
                        PricePoint("", 67350.00),
                        PricePoint("", 67300.00),
                        PricePoint("", 67250.00),
                        PricePoint("", 67200.00),
                        PricePoint("", 67380.00),
                        PricePoint("", 67410.00),
                        PricePoint("", 67390.00),
                        PricePoint("", 67420.00),
                        PricePoint("", 67405.00),
                        PricePoint("", 67415.00),
                        PricePoint("", 67418.00),
                        PricePoint("", 67420.00),
                        PricePoint("", 67419.00),
                        PricePoint("", 67420.50),
                        PricePoint("", 67421.00),
                        PricePoint("", 67422.00),
                        PricePoint("", 67420.00),
                        PricePoint("", 67421.50),
                        PricePoint("", basePrice),
                    ).take(60)
                }
            }
        return PriceHistoryResponse(
            assetId = assetId,
            period = period,
            dataPoints = dataPoints,
        )
    }

    override suspend fun getActivity(assetId: String): ActivityResponse =
        ActivityResponse(
            assetId = assetId,
            transactions =
                listOf(
                    AssetTransaction(
                        id = "1",
                        type = "received",
                        title = "Received",
                        timestamp = "Jun 5, 2026",
                        amount = 0.0120,
                        fiatValue = 809.04,
                        currency = "USD",
                    ),
                    AssetTransaction(
                        id = "2",
                        type = "sent",
                        title = "Sent",
                        timestamp = "Jun 3, 2026",
                        amount = -0.0050,
                        fiatValue = -337.10,
                        currency = "USD",
                    ),
                    AssetTransaction(
                        id = "3",
                        type = "staked",
                        title = "Staked",
                        timestamp = "Jun 1, 2026",
                        amount = 0.0008,
                        fiatValue = 53.94,
                        currency = "USD",
                    ),
                ),
        )

    override suspend fun getHolders(assetId: String): TopHoldersResponse =
        TopHoldersResponse(
            assetId = assetId,
            holders =
                listOf(
                    HolderAvatar(id = "1", initials = "AK", colorHex = "#F5D76E"),
                    HolderAvatar(id = "2", initials = "MR", colorHex = "#4ADE80"),
                    HolderAvatar(id = "3", initials = "JP", colorHex = "#C4BA94"),
                    HolderAvatar(id = "4", initials = "SL", colorHex = "#FF6B6B"),
                    HolderAvatar(id = "5", initials = "TD", colorHex = "#F5D76E"),
                ),
            additionalCount = 42,
        )
}
