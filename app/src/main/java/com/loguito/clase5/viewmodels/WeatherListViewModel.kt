package com.loguito.clase5.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.loguito.clase5.BuildConfig
import com.loguito.clase5.api.APIService
import com.loguito.clase5.models.WeatherDetail
import com.loguito.clase5.models.WeatherResponse
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class WeatherListViewModel : ViewModel() {

    private val weatherList = MutableLiveData<List<WeatherDetail>>()
    private var service: APIService

    init {

        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()

        BuildConfig.BASEURL

        // TODO Inicializamos retrofit
        val retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        service = retrofit.create(APIService::class.java)
    }

    fun makeAPIRequest(city: String, quantity: Int) {
        service.getDailyForecast(city, quantity, "")
            .enqueue(object : Callback<WeatherResponse> {
                override fun onResponse(
                    call: Call<WeatherResponse>,
                    response: Response<WeatherResponse>
                ) {
                    response.body()?.let {
                        // TODO: Cuando el request se completa, notificamos a los suscriptores
                        weatherList.postValue(it.list)
                    }
                }

                override fun onFailure(call: Call<WeatherResponse>, t: Throwable) {
                    val a = ""
                }

            })
    }

    fun getWeatherList(): LiveData<List<WeatherDetail>> {
        return weatherList
    }
}