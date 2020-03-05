package reschikov.geekbrains.androidadvancedlevel.weatherapplication.domain

import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.CurrentTable
import reschikov.geekbrains.androidadvancedlevel.weatherapplication.data.database.model.ForecastTable

class Weather (var state: Pair<CurrentTable, List<ForecastTable>>?, var error: Throwable?)