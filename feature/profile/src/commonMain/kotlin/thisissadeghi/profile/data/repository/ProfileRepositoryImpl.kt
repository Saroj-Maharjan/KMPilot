package thisissadeghi.profile.data.repository

import thisissadeghi.common.Either
import thisissadeghi.profile.data.datasource.ProfileDataSource
import thisissadeghi.profile.data.model.ProfileData

class ProfileRepositoryImpl(
    private val dataSource: ProfileDataSource,
) : ProfileRepository {
    override suspend fun getProfile(): Either<ProfileData> = dataSource.getProfile()
}
