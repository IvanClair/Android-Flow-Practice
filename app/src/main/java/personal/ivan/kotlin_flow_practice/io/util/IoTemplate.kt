package personal.ivan.kotlin_flow_practice.io.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

/**
 * @property N network response
 * @property C core data
 * @property R result
 */
@ExperimentalCoroutinesApi
abstract class IoTemplate<N, C, R>(private val generalErrorMessage: String) {

    companion object {
        const val ERROR_CODE_UNKNOWN = -1
    }

    // Flow of IO
    private val ioFlow: Flow<IoStatus<R>> = flow<C> {
        // fetch from data base
        val dbResponse = loadFromDatabase()
        val validCoreDataFromDatabase =
            dbResponse != null && validateCoreDataFromDatabase(c = dbResponse)
        if (validCoreDataFromDatabase) {
            emit(dbResponse!!)
        }

        // fetch from network
        val networkResponse = loadFromNetwork()
        when {
            // data from network match expectation
            networkResponse != null && validateNetworkResponse(n = networkResponse) -> {
                val coreData = fetchCoreDataFromNetworkResponse(n = networkResponse)
                saveCoreDataToDatabase(o = coreData)
                emit(coreData)
            }

            // throw exception if data from network has error, also loaded from database failed
            networkResponse != null && !validCoreDataFromDatabase -> {
                val errorInfo = parseErrorFromNetworkResponse(n = networkResponse)
                throw NetworkResponseException(code = errorInfo.first, message = errorInfo.second)
            }

            // data from network failed
            else -> throw Exception()
        }
    }
        .map { IoStatus.Succeed(data = convertCoreDataToResult(c = it)) as IoStatus<R> }
        .onStart { emit(IoStatus.Loading()) }
        .catch {
            emit(
                IoStatus.Failed(
                    code = if (it is NetworkResponseException) it.code else ERROR_CODE_UNKNOWN,
                    message = if (it is NetworkResponseException) it.message else generalErrorMessage
                )
            )
        }
        .flowOn(Dispatchers.IO)

    /**
     * Call this function to start [ioFlow] as [LiveData]
     */
    fun asLiveData(): LiveData<IoStatus<R>> = ioFlow.asLiveData()

    /**
     * Call this function to start [ioFlow]
     */
    fun getIoFlow(): Flow<IoStatus<R>> = ioFlow

    // region Override Functions

    abstract suspend fun loadFromDatabase(): C?

    abstract suspend fun validateCoreDataFromDatabase(c: C): Boolean

    abstract suspend fun loadFromNetwork(): N?

    abstract suspend fun validateNetworkResponse(n: N): Boolean

    abstract suspend fun fetchCoreDataFromNetworkResponse(n: N): C

    abstract suspend fun saveCoreDataToDatabase(o: C)

    abstract suspend fun parseErrorFromNetworkResponse(n: N): Pair<Int, String>

    abstract suspend fun convertCoreDataToResult(c: C): R

    // endregion
}

/**
 * Status of IO operation
 */
sealed class IoStatus<T> {
    class Loading<T> : IoStatus<T>()
    data class Succeed<T>(val data: T) : IoStatus<T>()
    data class Failed<T>(val code: Int, val message: String) : IoStatus<T>()
}

/**
 * Exception for IO process
 */
data class NetworkResponseException(val code: Int, override val message: String) : Exception()