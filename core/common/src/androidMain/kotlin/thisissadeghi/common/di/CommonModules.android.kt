package thisissadeghi.common.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import thisissadeghi.common.util.AndroidLinkHandler
import thisissadeghi.common.util.LinkHandler

/**
 * Created by Ali Sadeghi
 * on 07,May,2025
 */
actual val commonPlatformModule: Module =
    module {
        singleOf(::AndroidLinkHandler).bind<LinkHandler>()
    }
