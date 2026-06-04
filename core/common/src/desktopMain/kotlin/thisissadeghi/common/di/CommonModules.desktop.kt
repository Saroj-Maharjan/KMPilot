package thisissadeghi.common.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module
import thisissadeghi.common.locale.DesktopLanguagePreferenceStore
import thisissadeghi.common.locale.LanguagePreferenceStore
import thisissadeghi.common.util.DesktopLinkHandler
import thisissadeghi.common.util.LinkHandler

internal actual val commonPlatformModule: Module =
    module {
        singleOf(::DesktopLinkHandler).bind<LinkHandler>()
        singleOf(::DesktopLanguagePreferenceStore).bind<LanguagePreferenceStore>()
    }
