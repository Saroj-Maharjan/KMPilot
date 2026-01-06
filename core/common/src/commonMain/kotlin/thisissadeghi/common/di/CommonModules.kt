package thisissadeghi.common.di

import org.koin.core.module.Module
import thisissadeghi.common.di.base.BaseFeature

/**
 * Created by Ali Sadeghi
 * on 07,May,2025
 */

expect val commonPlatformModule: Module

object CommonModules : BaseFeature(CommonModules::class.simpleName.toString()) {
    override fun getKoinModules(): List<Module> =
        listOf(
            commonPlatformModule,
        )

    override fun initialize() {
        // Simply referencing the object will trigger its initialization
        CommonModules
    }
}
