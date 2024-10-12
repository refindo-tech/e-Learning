package com.example.kessekolah.data.response

sealed class ResponseMessage<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : ResponseMessage<T>(data)
    class Error<T>(message: String, data: T? = null) : ResponseMessage<T>(data, message)
    class Loading<T>(data: T? = null) : ResponseMessage<T>(data)
}


