package me.xhsun.gw2leo.storage.error

class HTTPError(code: Int, message: String) : Exception(message) {
    val code = code
}