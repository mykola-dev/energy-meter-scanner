package ds.meterscanner.net.model

data class CurrWeatherData(val coord: Coord,
                           val weather: List<Weather>,
                           val base: String,
                           val main: Main,
                           val wind: Wind,
                           val clouds: Clouds,
                           val dt: Long,
                           val sys: Sys,
                           val id: Long,
                           val name: String,
                           val cod: Long) {

	data class Coord(val lon: Double,
	                 val lat: Double)

	data class Weather(val id: Long,
	                   val main: String,
	                   val description: String,
	                   val icon: String)

	data class Main(val temp: Double,
	                val pressure: Double,
	                val humidity: Long,
	                val temp_min: Double)

	data class Wind(val speed: Double,
	                val deg: Float)

	data class Clouds(val all: Long)

	data class Sys(
			val type: Long,
			val id: Long,
			val message: Double,
			val country: String,
			val sunrise: Long,
			val sunset: Long
	              )

}