package thisissadeghi.receive.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import thisissadeghi.receive.presentation.ReceiveViewModel

val receiveModule: Module =
    module {
        viewModelOf(::ReceiveViewModel)
    }
