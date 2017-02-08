package ds.meterscanner.di

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import ds.meterscanner.BuildConfig
import ds.meterscanner.net.WeatherRestApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    @Singleton
    fun gson() = GsonBuilder().create()

    @Provides
    @Singleton
    @Named("APPID")
    fun weatherApiKey() = "3a2aae4aa5a564852f108fa99754e5f1"

    @Provides
    @Singleton
    @Named("host")
    fun weatherApiUrl() = "http://api.openweathermap.org/data/2.5/"

    @Provides
    @Singleton
    fun okHttpClient(@Named("APPID") weatherApi: String): OkHttpClient {
        val builder = OkHttpClient.Builder()
        if (BuildConfig.DEBUG) {
            builder.addNetworkInterceptor(StethoInterceptor())

            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

            builder.addInterceptor(loggingInterceptor)
        }

        builder.addInterceptor { chain ->
            var request = chain.request()
            val url = request.url().newBuilder().addQueryParameter("APPID", weatherApi).build()
            request = request.newBuilder().url(url).build()
            return@addInterceptor chain.proceed(request)
        }

        return builder.build()
    }

    @Provides
    @Singleton
    fun retrofit(@Named("host") host: String, client: OkHttpClient) = Retrofit.Builder()
        .baseUrl(host)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()

    @Provides
    @Singleton
    fun weatherRestApi(retrofit: Retrofit) = retrofit.create(WeatherRestApi::class.java)

}