package ds.meterscanner.net

import ds.meterscanner.data.Prefs
import ds.meterscanner.net.model.CurrWeatherData
import ru.gildor.coroutines.retrofit.await

class NetLayer(
    private val api: WeatherRestApi,
    private val prefs: Prefs
) {

    suspend fun getWeather(): CurrWeatherData = api.getWeather(prefs.city).await()

}
