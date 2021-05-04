package com.loguito.clase5.api

import com.loguito.clase5.models.WeatherResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface APIService {
    @GET("forecast/daily")
    fun getDailyForecast(
        @Query("q") city: String? = null,
        @Query("cnt") quantity: Int,
        @Query("appid") appID: String
    ): Call<WeatherResponse>
}