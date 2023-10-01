package com.loviagin.rollic.data

import com.loviagin.rollic.data.dto.CreatePayment
import com.loviagin.rollic.data.dto.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface PaymentApi {

    @POST(".")
    suspend fun createPayment(@Body createPayment: CreatePayment): Response

    @GET("{id}")
    suspend fun getPayment(@Path("id") id: String): Response
}