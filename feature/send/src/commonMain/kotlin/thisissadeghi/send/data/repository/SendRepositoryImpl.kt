package thisissadeghi.send.data.repository

import thisissadeghi.common.Either
import thisissadeghi.send.data.datasource.SendLocalDataSource
import thisissadeghi.send.data.model.SendData

class SendRepositoryImpl(
    private val dataSource: SendLocalDataSource,
) : SendRepository {
    override suspend fun getSendData(): Either<SendData> = dataSource.getSendData()
}
