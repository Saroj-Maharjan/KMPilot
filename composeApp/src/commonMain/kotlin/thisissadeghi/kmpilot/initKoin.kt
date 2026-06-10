package thisissadeghi.kmpilot

import org.koin.core.KoinApplication
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.KoinAppDeclaration
import org.koin.dsl.bind
import org.koin.dsl.module
import thisissadeghi.assetdetail.di.assetdetailModule
import thisissadeghi.common.di.commonModule
import thisissadeghi.dashboard.di.dashboardModule
import thisissadeghi.data.config.BuildOptionProvider
import thisissadeghi.data.di.dataModule
import thisissadeghi.receive.di.receiveModule
import thisissadeghi.send.di.sendModule
import thisissadeghi.swap.di.swapModule

private val appModule =
    module {
        singleOf(::BuildOptionProviderImpl).bind<BuildOptionProvider>()
    }

fun initKoin(appDeclaration: KoinAppDeclaration = {}): KoinApplication =
    startKoin {
        appDeclaration()
        modules(
            appModule,
            commonModule,
            dataModule,
            dashboardModule,
            sendModule,
            receiveModule,
            assetdetailModule,
            swapModule,
        )
    }
