package ds.meterscanner.net

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinAware
import com.github.salomonbrys.kodein.erased.instance
import com.google.gson.Gson
import ds.meterscanner.data.Prefs
import ds.meterscanner.net.model.CurrWeatherData
import io.reactivex.Observable

class NetLayer(override val kodein: Kodein) : KodeinAware {

    private val api: WeatherRestApi = instance()
    private val gson: Gson = instance()
    private val prefs: Prefs = instance()

    fun getWeather(): Observable<CurrWeatherData> {
        return api.getWeather(prefs.city)
    }

}

