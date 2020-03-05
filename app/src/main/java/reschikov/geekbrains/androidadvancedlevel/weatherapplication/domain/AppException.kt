package reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain

sealed class AppException : Throwable() {

    data class Database(val error: String?) : AppException()
    data class Saved(val error: String?) : AppException()
    class NoPermission : AppException()
}