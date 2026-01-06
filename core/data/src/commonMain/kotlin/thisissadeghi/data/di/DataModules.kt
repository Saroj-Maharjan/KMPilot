package thisissadeghi.data.di

import org.koin.core.module.Module
import thisissadeghi.common.di.base.BaseFeature

/**
 * Authentication feature module that provides all dependencies for the auth feature
 */
object DataModules : BaseFeature(DataModules::class.simpleName.toString()) {
    override fun getKoinModules(): List<Module> =
        listOf(
            binder,
            RemoteDataSourceModule,
            platformRemoteDataSourceModule,
            localDataSourceModule,
            platformLocalDataSourceModule,
        )

    override fun initialize() {
        // Simply referencing the object will trigger its initialization
        DataModules
    }
}
