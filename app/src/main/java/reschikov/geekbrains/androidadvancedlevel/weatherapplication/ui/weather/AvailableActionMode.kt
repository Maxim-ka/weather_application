package reschikov.geekbrains.androidadvancedlevel.weatherapplication.ui.weather

import androidx.appcompat.view.ActionMode

interface AvailableActionMode : ActionMode.Callback {
    fun checkAndClose()
}