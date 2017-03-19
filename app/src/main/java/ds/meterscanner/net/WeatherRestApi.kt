package ds.meterscanner.net

import ds.meterscanner.net.model.CurrWeatherData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherRestApi {

    @GET("weather?units=metric")
    fun getWeather(@Query("q") city: String): Call<CurrWeatherData>

}
