package personal.ivan.kotlin_flow_practice.io.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import retrofit2.HttpException

/**
 * Help to process IO
 */
abstract class IoTemplate<T> {

    // Flow of IO
    private val ioFlow: Flow<IoStatus<T>> = flow {
        // load data from database
        getFromDb()?.also { emit(it) }

        // load data from network
        val data = getFromNetwork()
        val exception = validateData(data = data)
        if (exception == null) data.also { saveData(data = it) }.also { emit(it) }
        else throw exception
    }
        .map { originData -> IoStatus.Success(data = originData.also { handleData(data = it) }) as IoStatus<T> }
        .catch { exception -> emit(IoStatus.Fail(exception = exception.parseException())) }
        .onStart { emit(IoStatus.Loading()) }
        .flowOn(Dispatchers.IO)

    /**
     * Call this function to start [ioFlow] as [LiveData]
     */
    fun asLiveData(): LiveData<IoStatus<T>> = ioFlow.asLiveData()

    /**
     * Call this function to start [ioFlow]
     */
    fun getIoFlow(): Flow<IoStatus<T>> = ioFlow

    // region Override Functions

    abstract suspend fun getFromDb(): T?

    abstract suspend fun getFromNetwork(): T

    abstract suspend fun validateData(data: T): IoException?

    abstract suspend fun saveData(data: T)

    abstract suspend fun handleData(data: T)

    // endregion
}

/**
 * Status of IO process
 */
sealed class IoStatus<T> {
    class Loading<T> : IoStatus<T>()
    data class Success<T>(val data: T) : IoStatus<T>()
    data class Fail<T>(val exception: IoException) : IoStatus<T>()
}

/**
 * Exception information
 */
data class IoException(val code: Int, override val message: String) : Exception() {
    companion object {
        fun defaultException(): IoException =
            IoException(code = -1, message = IoException::class.java.simpleName)
    }
}

/**
 * Parse exception from IO process
 */
fun Throwable.parseException(): IoException =
    when (this) {
        // HTTP error
        is HttpException -> IoException(code = code(), message = message())
        // API error
        is IoException -> this
        // unexpected
        else -> IoException(code = -1, message = javaClass.simpleName)
    }