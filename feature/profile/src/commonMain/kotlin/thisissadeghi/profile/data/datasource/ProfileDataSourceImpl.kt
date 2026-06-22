package thisissadeghi.profile.data.datasource

import thisissadeghi.common.Either
import thisissadeghi.profile.data.model.ProfileData

class ProfileDataSourceImpl : ProfileDataSource {
    override suspend fun getProfile(): Either<ProfileData> =
        Either.Success(
            ProfileData(
                name = "Ali Sadeghi",
                email = "alisadeghi.dev@gmail.com",
                memberTier = "Gold Private Banking",
                biometricEnabled = false,
            ),
        )
}
