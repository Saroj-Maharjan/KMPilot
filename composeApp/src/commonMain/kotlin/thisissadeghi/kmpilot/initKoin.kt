package thisissadeghi.kmpilot

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module
import thisissadeghi.common.di.CommonModules
import thisissadeghi.common.di.base.FeatureRegistry
import thisissadeghi.data.config.BuildOptionProvider
import thisissadeghi.data.di.DataModules
import thisissadeghi.receive.di.ReceiveModules
import thisissadeghi.sample.di.SampleModules
import thisissadeghi.send.di.SendModules

private val appModule =
    module {
        singleOf(::BuildOptionProviderImpl).bind<BuildOptionProvider>()
    }

private fun initializeFeatures() {
    // Initialize each feature
    CommonModules.initialize()
    DataModules.initialize()
    SampleModules.initialize()
    SendModules.initialize()
    ReceiveModules.initialize()
}

fun initKoin(appDeclaration: KoinAppDeclaration = {}): KoinApplication {
    initializeFeatures()

    return startKoin {
        appDeclaration()
        modules(getAllModules())
    }
}

private fun getAllModules(): List<Module> {
    val modulesList = mutableListOf<Module>()
    modulesList.add(appModule)
    modulesList.addAll(FeatureRegistry.getAllKoinModules())
    return modulesList
}
