package personal.ivan.kotlin_flow_practice.io.error_handler

import personal.ivan.kotlin_flow_practice.io.util.ApiException
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

fun Throwable.parseException(): Pair<Int, String> {
    // record API issue such as send exception to crashlytics
    return when (this) {
        // HTTP error
        is HttpException -> Pair(code(), code().convertHttpError())
        // API error
        is ApiException -> Pair(code, message)
        // Unknown host
        is UnknownHostException -> Pair(-2, "plz check your network")
        // Socket timeout
        is SocketTimeoutException -> Pair(-3, "Your network issue")
        // unexpected
        else -> Pair(-1, "unexpected")
    }
}

fun Int.convertHttpError(): String =
    when (this) {
        400 -> "something went wrong with API"
        404 -> "cannot find out what you want"
        500 -> "server error"
        502 -> "protocol issue"
        else -> "unexpected"
    }
