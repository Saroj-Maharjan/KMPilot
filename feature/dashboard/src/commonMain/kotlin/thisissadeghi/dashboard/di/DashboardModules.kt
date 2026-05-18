package thisissadeghi.dashboard.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import thisissadeghi.common.di.base.BaseFeature
import thisissadeghi.dashboard.data.datasource.DashboardLocalDataSource
import thisissadeghi.dashboard.data.datasource.DashboardLocalDataSourceImpl
import thisissadeghi.dashboard.data.datasource.DashboardRemoteDataSource
import thisissadeghi.dashboard.data.datasource.DashboardRemoteDataSourceImpl
import thisissadeghi.dashboard.data.repository.DashboardRepository
import thisissadeghi.dashboard.data.repository.DashboardRepositoryImpl
import thisissadeghi.dashboard.presentation.DashboardViewModel

/**
 * Koin DI modules for the dashboard feature.
 * Demonstrates the BaseFeature pattern for auto-registration.
 */
object DashboardModules : BaseFeature(DashboardModules::class.simpleName.toString()) {
    override fun getKoinModules(): List<Module> =
        listOf(
            module {
                // Data layer
                singleOf(::DashboardLocalDataSourceImpl).bind<DashboardLocalDataSource>()
                singleOf(::DashboardRemoteDataSourceImpl).bind<DashboardRemoteDataSource>()
                singleOf(::DashboardRepositoryImpl).bind<DashboardRepository>()

                // Presentation layer
                viewModelOf(::DashboardViewModel)
            },
        )

    override fun initialize() {
        DashboardModules
    }
}
