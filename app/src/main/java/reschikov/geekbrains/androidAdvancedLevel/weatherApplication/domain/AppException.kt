package reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain

sealed class AppException : Throwable() {

    data class Response(val error: String) : AppException()
    data class Database(val error: String?) : AppException()
    data class Saved(val error: String?) : AppException()
    data class Deleted(val error: String?) : AppException()
    class NoNetwork : AppException()
    class NoPermission : AppException()
}