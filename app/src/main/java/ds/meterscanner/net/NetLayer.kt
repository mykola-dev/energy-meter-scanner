package ds.meterscanner.net

import L
import com.google.gson.Gson
import ds.meterscanner.data.Prefs
import ds.meterscanner.net.model.CurrWeatherData
import io.reactivex.Observable
import javax.inject.Inject

class NetLayer @Inject constructor () {

    @Inject lateinit var api: WeatherRestApi
    @Inject lateinit var gson: Gson
    @Inject lateinit var prefs: Prefs

    init {
        L.i("::: NetLayer initialized")
    }

    fun getWeather(): Observable<CurrWeatherData> {
        return api.getWeather(prefs.city)
    }

}

