package thisissadeghi.send.data.datasource

import thisissadeghi.common.Either
import thisissadeghi.send.data.model.SendData

interface SendLocalDataSource {
    suspend fun getSendData(): Either<SendData>
}
