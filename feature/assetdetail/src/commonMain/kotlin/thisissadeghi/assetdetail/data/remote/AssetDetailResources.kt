package thisissadeghi.assetdetail.data.remote

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Resource("/crypto")
@Serializable
class AssetDetailResources {
    @Resource("/assets/{id}/buy")
    @Serializable
    class PostBuy(
        val id: String,
        val parent: AssetDetailResources = AssetDetailResources(),
    )
}
