package me.xhsun.gw2leo.storage.error

class HTTPError(code: Int, message: String) : Exception("HTTP error: $message") {
    val code = code
}