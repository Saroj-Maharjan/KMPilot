package thisissadeghi.send.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import thisissadeghi.send.data.datasource.SendLocalDataSource
import thisissadeghi.send.data.datasource.SendLocalDataSourceImpl
import thisissadeghi.send.data.repository.SendRepository
import thisissadeghi.send.data.repository.SendRepositoryImpl
import thisissadeghi.send.presentation.SendViewModel

val sendModule: Module =
    module {
        singleOf(::SendLocalDataSourceImpl).bind<SendLocalDataSource>()
        singleOf(::SendRepositoryImpl).bind<SendRepository>()
        viewModelOf(::SendViewModel)
    }
