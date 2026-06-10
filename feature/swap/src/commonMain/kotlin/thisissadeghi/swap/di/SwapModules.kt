package thisissadeghi.swap.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import thisissadeghi.swap.data.datasource.SwapLocalDataSource
import thisissadeghi.swap.data.datasource.SwapLocalDataSourceImpl
import thisissadeghi.swap.data.datasource.SwapRemoteDataSource
import thisissadeghi.swap.data.datasource.SwapRemoteDataSourceImpl
import thisissadeghi.swap.data.repository.SwapRepository
import thisissadeghi.swap.data.repository.SwapRepositoryImpl
import thisissadeghi.swap.presentation.SwapViewModel

val swapModule: Module =
    module {
        singleOf(::SwapLocalDataSourceImpl).bind<SwapLocalDataSource>()
        singleOf(::SwapRemoteDataSourceImpl).bind<SwapRemoteDataSource>()
        singleOf(::SwapRepositoryImpl).bind<SwapRepository>()
        viewModelOf(::SwapViewModel)
    }
