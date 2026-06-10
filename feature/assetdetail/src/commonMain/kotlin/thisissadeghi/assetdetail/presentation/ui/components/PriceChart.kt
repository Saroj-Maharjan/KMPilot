package thisissadeghi.assetdetail.presentation.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kmpilot.feature.assetdetail.generated.resources.Res
import kmpilot.feature.assetdetail.generated.resources.chart_unavailable
import org.jetbrains.compose.resources.stringResource
import thisissadeghi.assetdetail.data.model.PriceHistoryResponse
import thisissadeghi.assetdetail.data.model.PricePoint
import thisissadeghi.common.UiState
import thisissadeghi.common.ext.formatDecimals
import thisissadeghi.designsystem.XText
import thisissadeghi.designsystem.XTheme
import thisissadeghi.designsystem.motion.rememberReducedMotion
import thisissadeghi.designsystem.motion.shimmer

@Composable
fun PriceChart(
    priceHistoryState: UiState<PriceHistoryResponse>,
    modifier: Modifier = Modifier,
) {
    val reducedMotion = rememberReducedMotion()
    val primary = MaterialTheme.colorScheme.primary
    val background = MaterialTheme.colorScheme.background
    val onSurfaceVariant = MaterialTheme.colorScheme.onSurfaceVariant
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant

    Column(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
    ) {
        when (priceHistoryState) {
            is UiState.Loading, UiState.Uninitialized -> {
                // Shimmer skeleton placeholder
                val shimmerHighlight =
                    Color(
                        red = surfaceVariant.red,
                        green = surfaceVariant.green,
                        blue = surfaceVariant.blue,
                        alpha = 0.5f,
                    )
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .padding(top = 16.dp),
                ) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(12.dp))
                                .then(
                                    if (reducedMotion) {
                                        Modifier.background(surfaceVariant)
                                    } else {
                                        Modifier.shimmer(
                                            baseColor = surfaceVariant,
                                            highlightColor = shimmerHighlight,
                                        )
                                    },
                                ),
                    )
                }
            }

            is UiState.Failed -> {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .padding(top = 16.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    XText(
                        text = stringResource(Res.string.chart_unavailable),
                        style = MaterialTheme.typography.labelMedium,
                        color = onSurfaceVariant,
                    )
                }
            }

            is UiState.Success -> {
                val dataPoints = priceHistoryState.value.dataPoints
                if (dataPoints.isEmpty()) {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .padding(top = 16.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        XText(
                            text = stringResource(Res.string.chart_unavailable),
                            style = MaterialTheme.typography.labelMedium,
                            color = onSurfaceVariant,
                        )
                    }
                } else {
                    val prices = dataPoints.map { it.price }
                    val minPrice = prices.min()
                    val maxPrice = prices.max()
                    val priceRange = maxPrice - minPrice

                    // Y-axis labels + chart body
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(180.dp)
                                .padding(top = 16.dp),
                    ) {
                        // Y-axis labels (left 40dp)
                        Column(
                            modifier =
                                Modifier
                                    .width(40.dp)
                                    .fillMaxHeight(),
                            verticalArrangement = Arrangement.SpaceBetween,
                        ) {
                            val step = priceRange / 3
                            for (i in 3 downTo 0) {
                                val labelPrice = minPrice + i * step
                                XText(
                                    text = "$${(labelPrice / 1000).formatDecimals(0)}k",
                                    style =
                                        MaterialTheme.typography.labelSmall.copy(
                                            fontSize = 10.sp,
                                        ),
                                    color = onSurfaceVariant,
                                )
                            }
                        }

                        // Chart canvas
                        Canvas(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .padding(start = 40.dp),
                        ) {
                            val width = size.width
                            val height = size.height
                            val pointCount = dataPoints.size

                            fun xForIndex(index: Int): Float = if (pointCount > 1) index.toFloat() / (pointCount - 1) * width else width / 2

                            fun yForPrice(price: Double): Float =
                                if (priceRange > 0) {
                                    height - ((price - minPrice) / priceRange * height).toFloat()
                                } else {
                                    height / 2
                                }

                            // Gradient area path
                            val areaPath =
                                Path().apply {
                                    moveTo(xForIndex(0), yForPrice(dataPoints[0].price))
                                    for (i in 1 until pointCount) {
                                        lineTo(xForIndex(i), yForPrice(dataPoints[i].price))
                                    }
                                    lineTo(xForIndex(pointCount - 1), height)
                                    lineTo(0f, height)
                                    close()
                                }
                            drawPath(
                                path = areaPath,
                                brush =
                                    Brush.verticalGradient(
                                        colors =
                                            listOf(
                                                primary.copy(alpha = 0.4f),
                                                background.copy(alpha = 0f),
                                            ),
                                    ),
                            )

                            // Line path
                            val linePath =
                                Path().apply {
                                    moveTo(xForIndex(0), yForPrice(dataPoints[0].price))
                                    for (i in 1 until pointCount) {
                                        lineTo(xForIndex(i), yForPrice(dataPoints[i].price))
                                    }
                                }
                            drawPath(
                                path = linePath,
                                color = primary,
                                style =
                                    Stroke(
                                        width = 2.dp.toPx(),
                                        cap = StrokeCap.Round,
                                    ),
                            )
                        }
                    }

                    // X-axis labels
                    if (dataPoints.size >= 2) {
                        Row(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(start = 40.dp, top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            XText(
                                text = dataPoints.first().timestamp.take(5),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = onSurfaceVariant,
                            )
                            XText(
                                text = dataPoints.last().timestamp.take(5),
                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                color = onSurfaceVariant,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
private fun PriceChartPreview() {
    XTheme {
        PriceChart(
            priceHistoryState =
                UiState.Success(
                    PriceHistoryResponse(
                        assetId = "bitcoin",
                        period = "1D",
                        dataPoints =
                            listOf(
                                PricePoint("09:00", 65000.0),
                                PricePoint("12:00", 66500.0),
                                PricePoint("15:00", 64800.0),
                                PricePoint("18:00", 67200.0),
                                PricePoint("21:00", 67420.0),
                            ),
                    ),
                ),
        )
    }
}

@Preview
@Composable
private fun PriceChartLoadingPreview() {
    XTheme {
        PriceChart(priceHistoryState = UiState.Loading)
    }
}
