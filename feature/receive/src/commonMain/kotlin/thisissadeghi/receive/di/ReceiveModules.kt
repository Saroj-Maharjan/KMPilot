package thisissadeghi.receive.di

import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module
import thisissadeghi.common.di.base.BaseFeature
import thisissadeghi.receive.presentation.ReceiveViewModel

object ReceiveModules : BaseFeature(ReceiveModules::class.simpleName.toString()) {
    override fun getKoinModules(): List<Module> =
        listOf(
            module {
                viewModelOf(::ReceiveViewModel)
            },
        )

    override fun initialize() {
        ReceiveModules
    }
}
