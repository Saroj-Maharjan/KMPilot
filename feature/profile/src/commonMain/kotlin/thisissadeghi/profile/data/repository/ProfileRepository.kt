package thisissadeghi.profile.data.repository

import thisissadeghi.common.Either
import thisissadeghi.profile.data.model.ProfileData

interface ProfileRepository {
    suspend fun getProfile(): Either<ProfileData>
}
