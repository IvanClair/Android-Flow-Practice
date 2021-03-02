package personal.ivan.kotlin_flow_practice.io.error_handler

import personal.ivan.kotlin_flow_practice.io.util.IoException
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException



fun Int.convertHttpError(): String =
    when (this) {
        400 -> "something went wrong with API"
        404 -> "cannot find out what you want"
        500 -> "server error"
        502 -> "protocol issue"
        else -> "unexpected"
    }
