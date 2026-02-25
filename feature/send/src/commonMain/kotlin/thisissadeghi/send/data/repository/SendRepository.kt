package thisissadeghi.send.data.repository

import thisissadeghi.common.Either
import thisissadeghi.send.data.model.SendData

interface SendRepository {
    suspend fun getSendData(): Either<SendData>
}
