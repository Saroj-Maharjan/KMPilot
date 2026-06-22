package thisissadeghi.profile.data.datasource

import thisissadeghi.common.Either
import thisissadeghi.profile.data.model.ProfileData

interface ProfileDataSource {
    suspend fun getProfile(): Either<ProfileData>
}
