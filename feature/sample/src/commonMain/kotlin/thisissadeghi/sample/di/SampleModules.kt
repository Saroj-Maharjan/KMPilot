package thisissadeghi.sample.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import thisissadeghi.common.di.base.BaseFeature
import thisissadeghi.sample.data.datasource.SampleLocalDataSource
import thisissadeghi.sample.data.datasource.SampleLocalDataSourceImpl
import thisissadeghi.sample.data.datasource.SampleRemoteDataSource
import thisissadeghi.sample.data.datasource.SampleRemoteDataSourceImpl
import thisissadeghi.sample.data.repository.SampleRepository
import thisissadeghi.sample.data.repository.SampleRepositoryImpl
import thisissadeghi.sample.presentation.SampleViewModel

/**
 * Koin DI modules for the sample feature.
 * Demonstrates the BaseFeature pattern for auto-registration.
 */
object SampleModules : BaseFeature(SampleModules::class.simpleName.toString()) {
    override fun getKoinModules(): List<Module> =
        listOf(
            module {
                // Data layer
                singleOf(::SampleLocalDataSourceImpl).bind<SampleLocalDataSource>()
                singleOf(::SampleRemoteDataSourceImpl).bind<SampleRemoteDataSource>()
                singleOf(::SampleRepositoryImpl).bind<SampleRepository>()

                // Presentation layer
                viewModelOf(::SampleViewModel)
            },
        )

    override fun initialize() {
        SampleModules
    }
}
