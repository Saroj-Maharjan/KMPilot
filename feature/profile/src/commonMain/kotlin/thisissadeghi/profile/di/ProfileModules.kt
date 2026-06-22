package thisissadeghi.profile.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import thisissadeghi.profile.data.datasource.ProfileDataSource
import thisissadeghi.profile.data.datasource.ProfileDataSourceImpl
import thisissadeghi.profile.data.repository.ProfileRepository
import thisissadeghi.profile.data.repository.ProfileRepositoryImpl
import thisissadeghi.profile.presentation.ProfileViewModel

val profileModule: Module =
    module {
        singleOf(::ProfileDataSourceImpl).bind<ProfileDataSource>()
        singleOf(::ProfileRepositoryImpl).bind<ProfileRepository>()
        viewModelOf(::ProfileViewModel)
    }
