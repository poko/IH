package net.ecoarttech.ihplus.api

import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory



fun getGeocodeService() : GeocodeService {
    val retrofit = Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com")
            .addConverterFactory(JacksonConverterFactory.create())
            .build();
    return retrofit.create(GeocodeService::class.java);
}