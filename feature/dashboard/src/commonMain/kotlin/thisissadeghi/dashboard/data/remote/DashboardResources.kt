package thisissadeghi.dashboard.data.remote

import io.ktor.resources.Resource
import kotlinx.serialization.Serializable

@Resource("/api/finance")
@Serializable
class DashboardResources {
    @Resource("/dashboard.json")
    @Serializable
    class GetDashboard(
        val parent: DashboardResources = DashboardResources(),
    )
}
