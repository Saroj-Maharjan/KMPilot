package thisissadeghi.data.di

import kotlinx.serialization.json.Json
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import thisissadeghi.data.datasource.local.AuthenticationLocalDataSource
import thisissadeghi.data.datasource.local.AuthenticationLocalDataSourceImpl
import thisissadeghi.data.repository.user.UserRepository
import thisissadeghi.data.repository.user.UserRepositoryImpl

/**
 * Data layer dependency injection bindings
 */
val binder =
    module {
        // JSON serializer
        single {
            Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
                isLenient = true
            }
        }

        // Authentication Local DataSource (used by login feature)
        singleOf(::AuthenticationLocalDataSourceImpl).bind<AuthenticationLocalDataSource>()

        // User
        singleOf(::UserRepositoryImpl).bind<UserRepository>()
    }
