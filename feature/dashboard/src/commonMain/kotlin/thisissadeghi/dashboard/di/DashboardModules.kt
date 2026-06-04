package thisissadeghi.dashboard.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import thisissadeghi.dashboard.data.datasource.DashboardRemoteDataSource
import thisissadeghi.dashboard.data.datasource.DashboardRemoteDataSourceImpl
import thisissadeghi.dashboard.data.repository.DashboardRepository
import thisissadeghi.dashboard.data.repository.DashboardRepositoryImpl
import thisissadeghi.dashboard.presentation.DashboardViewModel

/**
 * Koin DI module for the dashboard feature.
 */
val dashboardModule: Module =
    module {
        // Data layer
        singleOf(::DashboardRemoteDataSourceImpl).bind<DashboardRemoteDataSource>()
        singleOf(::DashboardRepositoryImpl).bind<DashboardRepository>()

        // Presentation layer
        viewModelOf(::DashboardViewModel)
    }
