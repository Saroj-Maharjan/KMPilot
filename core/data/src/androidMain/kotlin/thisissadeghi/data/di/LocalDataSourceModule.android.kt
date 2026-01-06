package thisissadeghi.data.di

import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

actual val platformLocalDataSourceModule =
    module {
        single<String>(qualifier = DataStorePathStringQualifier) {
            androidApplication().filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath
        }
    }
