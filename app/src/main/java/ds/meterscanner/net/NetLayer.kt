package ds.meterscanner.net

import L
import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinAware
import com.google.gson.Gson
import ds.meterscanner.data.Prefs
import ds.meterscanner.net.model.CurrWeatherData
import io.reactivex.Observable
import com.github.salomonbrys.kodein.instance as inject

class NetLayer(override val kodein: Kodein) : KodeinAware {

    private val api: WeatherRestApi = inject()
    private val gson: Gson = inject()
    private val prefs: Prefs = inject()

    init {
        L.i("::: NetLayer initialized")
    }

    fun getWeather(): Observable<CurrWeatherData> {
        return api.getWeather(prefs.city)
    }

}

