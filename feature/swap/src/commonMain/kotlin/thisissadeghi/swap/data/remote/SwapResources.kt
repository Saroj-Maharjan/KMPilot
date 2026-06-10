package thisissadeghi.swap.data.remote

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Resource("/swap")
@Serializable
class SwapResources {
    @Resource("/execute")
    @Serializable
    class Execute(
        val parent: SwapResources = SwapResources(),
    )
}
