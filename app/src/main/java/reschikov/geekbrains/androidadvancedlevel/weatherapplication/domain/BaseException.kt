package reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain

sealed class BaseException : Throwable() {

    data class Database(val error: String?) : BaseException()
    data class Saved(val error: String?) : BaseException()
    data class Deleted(val error: String?) : BaseException()
    class NoNetwork : BaseException()
    class NoPermission : BaseException()
}