package ds.meterscanner.net

import com.github.salomonbrys.kodein.Kodein
import com.github.salomonbrys.kodein.KodeinAware
import com.github.salomonbrys.kodein.erased.instance
import ds.meterscanner.data.Prefs
import ds.meterscanner.net.model.CurrWeatherData
import ru.gildor.coroutines.retrofit.await

class NetLayer(override val kodein: Kodein) : KodeinAware {

    private val api: WeatherRestApi = instance()
    private val prefs: Prefs = instance()

    suspend fun getWeather(): CurrWeatherData = api.getWeather(prefs.city).await()

}
