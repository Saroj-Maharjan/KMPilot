package thisissadeghi.assetdetail.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import thisissadeghi.assetdetail.data.datasource.AssetDetailLocalDataSource
import thisissadeghi.assetdetail.data.datasource.AssetDetailLocalDataSourceImpl
import thisissadeghi.assetdetail.data.datasource.AssetDetailRemoteDataSource
import thisissadeghi.assetdetail.data.datasource.AssetDetailRemoteDataSourceImpl
import thisissadeghi.assetdetail.data.repository.AssetDetailRepository
import thisissadeghi.assetdetail.data.repository.AssetDetailRepositoryImpl
import thisissadeghi.assetdetail.presentation.AssetDetailViewModel

val assetdetailModule: Module =
    module {
        singleOf(::AssetDetailLocalDataSourceImpl).bind<AssetDetailLocalDataSource>()
        singleOf(::AssetDetailRemoteDataSourceImpl).bind<AssetDetailRemoteDataSource>()
        singleOf(::AssetDetailRepositoryImpl).bind<AssetDetailRepository>()
        viewModelOf(::AssetDetailViewModel)
    }
