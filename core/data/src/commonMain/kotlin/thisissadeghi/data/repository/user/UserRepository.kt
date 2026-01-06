package thisissadeghi.data.repository.user

import thisissadeghi.common.Either
import thisissadeghi.data.model.Balance

/**
 * Repository for user-related data
 */
interface UserRepository {
    // Balance
    suspend fun getBalance(): Either<Balance>
}
