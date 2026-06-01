package thisissadeghi.common.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module
import thisissadeghi.common.di.base.BaseFeature
import thisissadeghi.common.locale.LanguageController

/**
 * Created by Ali Sadeghi
 * on 07,May,2025
 */

expect val commonPlatformModule: Module

val localeModule: Module =
    module {
        singleOf(::LanguageController)
    }

object CommonModules : BaseFeature(CommonModules::class.simpleName.toString()) {
    override fun getKoinModules(): List<Module> =
        listOf(
            commonPlatformModule,
            localeModule,
        )

    override fun initialize() {
        // Simply referencing the object will trigger its initialization
        CommonModules
    }
}
