package ds.meterscanner.net

import ds.meterscanner.net.model.CurrWeatherData
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherRestApi {

    @GET("weather?units=metric")
    fun getWeather(@Query("q") city: String): Observable<CurrWeatherData>

}
