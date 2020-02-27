package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather

interface Spreadable {
    fun toShare(collectable: Collectable)
    fun getTitle() : String
}