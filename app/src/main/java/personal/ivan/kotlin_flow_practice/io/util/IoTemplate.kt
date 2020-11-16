package personal.ivan.kotlin_flow_practice.io.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*

/**
 * @property D origin data
 * @property R result
 */
@ExperimentalCoroutinesApi
abstract class IoTemplate<D, R> {

    // Flow of IO
    private val ioFlow: Flow<IoStatus<R>> = flow {
        // load data from database
        val dataFromDb = loadFromDb()
        val passValidationFromDb = validateFromDb(d = dataFromDb)
        if (passValidationFromDb) emit(dataFromDb!!)

        // load data from network
        val dataFromNetwork = loadFromNetwork()
        when {
            // data from network match expectation
            validateFromNetwork(d = dataFromNetwork) -> {
                saveToDb(d = dataFromNetwork)
                emit(dataFromNetwork)
            }

            // throw exception if data from network has error, also loaded from database failed
            !passValidationFromDb -> throw parseApiException(d = dataFromNetwork)
        }
    }
        .map {
            val result =
                IoStatus.Succeed(data = convertToResult(d = it)) as IoStatus<R>
            result
        }
        .onStart { emit(IoStatus.Loading()) }
        .catch {
            val exceptionInfo = handleException(exception = it)
            emit(IoStatus.Failed(exceptionInfo.first, exceptionInfo.second))
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

    abstract suspend fun loadFromDb(): D?

    abstract suspend fun validateFromDb(d: D?): Boolean

    abstract suspend fun loadFromNetwork(): D

    abstract suspend fun validateFromNetwork(d: D?): Boolean

    abstract suspend fun saveToDb(d: D)

    abstract suspend fun parseApiException(d: D): ApiException

    abstract suspend fun convertToResult(d: D): R

    abstract suspend fun handleException(exception: Throwable): Pair<Int, String>

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
data class ApiException(val code: Int, override val message: String) : Exception()