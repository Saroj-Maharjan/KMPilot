package thisissadeghi.send.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import thisissadeghi.common.di.base.BaseFeature
import thisissadeghi.send.data.datasource.SendLocalDataSource
import thisissadeghi.send.data.datasource.SendLocalDataSourceImpl
import thisissadeghi.send.data.repository.SendRepository
import thisissadeghi.send.data.repository.SendRepositoryImpl
import thisissadeghi.send.presentation.SendViewModel

object SendModules : BaseFeature(SendModules::class.simpleName.toString()) {
    override fun getKoinModules(): List<Module> =
        listOf(
            module {
                singleOf(::SendLocalDataSourceImpl).bind<SendLocalDataSource>()
                singleOf(::SendRepositoryImpl).bind<SendRepository>()
                viewModelOf(::SendViewModel)
            },
        )

    override fun initialize() {
        SendModules
    }
}
