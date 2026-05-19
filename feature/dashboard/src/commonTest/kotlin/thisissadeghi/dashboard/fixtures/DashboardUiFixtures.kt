package thisissadeghi.dashboard.fixtures

import thisissadeghi.common.ErrorModel
import thisissadeghi.common.UiState
import thisissadeghi.dashboard.data.model.DashboardData
import thisissadeghi.dashboard.presentation.DashboardUiModel

object DashboardUiFixtures {
    // === 4 MANDATORY BASE STATES ===

    fun createUninitializedState() =
        DashboardUiModel(
            dashboardState = UiState.Uninitialized,
        )

    fun createLoadingState() =
        DashboardUiModel(
            dashboardState = UiState.Loading,
        )

    fun createSuccessState(data: DashboardData = DashboardFixtures.createDashboardData()) =
        DashboardUiModel(
            dashboardState = UiState.Success(data),
        )

    fun createErrorState(message: String = "Something went wrong") =
        DashboardUiModel(
            dashboardState = UiState.Failed(ErrorModel.Message(message)),
        )

    // === SUCCESS VARIATIONS ===

    fun createSuccessWithEmptyLists() =
        createSuccessState(
            DashboardFixtures.createDashboardDataWithEmptyLists(),
        )

    fun createSuccessWithSingleItems() =
        createSuccessState(
            DashboardFixtures.createDashboardDataWithSingleItems(),
        )

    fun createSuccessWithLargeLists(count: Int = 50) =
        createSuccessState(
            DashboardFixtures.createDashboardDataWithLargeLists(count),
        )

    fun createSuccessWithAllOverBudget() =
        createSuccessState(
            DashboardFixtures.createDashboardDataWithAllOverBudget(),
        )

    fun createSuccessWithAllOverdueBills() =
        createSuccessState(
            DashboardFixtures.createDashboardDataWithAllOverdueBills(),
        )

    fun createSuccessWithNegativePortfolioChange() =
        createSuccessState(
            DashboardFixtures.createDashboardDataWithNegativePortfolioChange(),
        )

    fun createSuccessWithNegativeInsight() =
        createSuccessState(
            DashboardFixtures.createDashboardDataWithNegativeInsight(),
        )

    fun createSuccessWithNegativeAccountChange() =
        createSuccessState(
            DashboardFixtures.createDashboardDataWithNegativeAccountChange(),
        )

    fun createSuccessWithSpecialCharacters() =
        createSuccessState(
            DashboardFixtures.createDashboardDataWithSpecialCharacters(),
        )

    fun createSuccessWithUnicode() =
        createSuccessState(
            DashboardFixtures.createDashboardDataWithUnicode(),
        )

    // === ERROR VARIATIONS ===

    fun createNetworkErrorState() =
        DashboardUiModel(
            dashboardState = UiState.Failed(DashboardFixtures.networkError),
        )

    fun createUnauthorizedErrorState() =
        DashboardUiModel(
            dashboardState = UiState.Failed(DashboardFixtures.unauthorizedError),
        )

    fun createNotFoundErrorState() =
        DashboardUiModel(
            dashboardState = UiState.Failed(DashboardFixtures.notFoundError),
        )

    fun createServerErrorState() =
        DashboardUiModel(
            dashboardState = UiState.Failed(DashboardFixtures.serverError),
        )

    fun createTimeoutErrorState() =
        DashboardUiModel(
            dashboardState = UiState.Failed(DashboardFixtures.timeoutError),
        )

    fun createServiceUnavailableErrorState() =
        DashboardUiModel(
            dashboardState = UiState.Failed(DashboardFixtures.serviceUnavailableError),
        )

    fun createSerializationErrorState() =
        DashboardUiModel(
            dashboardState = UiState.Failed(DashboardFixtures.serializationError),
        )

    fun createBadRequestErrorState() =
        DashboardUiModel(
            dashboardState = UiState.Failed(DashboardFixtures.badRequestError),
        )

    fun createCustomErrorState(error: ErrorModel) =
        DashboardUiModel(
            dashboardState = UiState.Failed(error),
        )
}
