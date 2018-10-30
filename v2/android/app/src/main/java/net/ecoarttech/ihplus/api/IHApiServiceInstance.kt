package net.ecoarttech.ihplus.api

import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

fun get() : IHApiService {
    val retrofit = Retrofit.Builder()
            .baseUrl(NetworkConstants.SERVER_URL)
            .addConverterFactory(JacksonConverterFactory.create())
            .build();
    return retrofit.create(IHApiService::class.java);
}