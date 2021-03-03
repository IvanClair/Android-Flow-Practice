package personal.ivan.kotlin_flow_practice.io.util

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import retrofit2.HttpException

/**
 * Help to process IO
 *
 * @property A API response
 * @property M map to the result you want
 */
abstract class IoTemplate<A, M> {

    // Flow of IO
    private val ioFlow: Flow<IoStatus<M>> = flow {
        // load data from database
        loadFromDb()?.also { emit(value = it) }

        // load data from network
        val apiRs = loadFromNetwork()
        apiRs
            .takeIf { validateApiRs(a = it) }
            // API success
            ?.also { saveData(a = it) }
            ?.also { emit(value = it) }
        // API returns error
            ?: throw parseApiException(a = apiRs)
    }
        .map {
            val result = IoStatus.Success(data = convert(a = it)) as IoStatus<M>
            result
        }
        .catch { e -> emit(value = IoStatus.Fail(exception = e.parseException())) }
        .onStart { emit(value = IoStatus.Loading(loading = true)) }
        .onCompletion { emit(value = IoStatus.Loading(loading = false)) }
        .flowOn(Dispatchers.IO)

    /**
     * Call this function to start [ioFlow] as [LiveData]
     */
    fun asLiveData(): LiveData<IoStatus<M>> = ioFlow.asLiveData()

    // region Override Functions

    abstract suspend fun loadFromDb(): A?

    abstract suspend fun loadFromNetwork(): A

    abstract suspend fun validateApiRs(a: A): Boolean

    abstract suspend fun saveData(a: A)

    abstract suspend fun parseApiException(a: A): IoException

    abstract suspend fun convert(a: A): M

    // endregion
}

/**
 * Status of IO process
 */
sealed class IoStatus<T> {
    data class Loading<T>(val loading: Boolean) : IoStatus<T>()
    data class Success<T>(val data: T) : IoStatus<T>()
    data class Fail<T>(val exception: IoException) : IoStatus<T>()
}

// region Exception

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

// endregion