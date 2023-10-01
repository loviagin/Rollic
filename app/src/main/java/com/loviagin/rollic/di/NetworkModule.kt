package com.loviagin.rollic.di

import android.content.Context
import com.loviagin.rollic.R
import com.loviagin.rollic.data.PaymentApi
import com.loviagin.rollic.data.RemotePaymentRepository
import com.loviagin.rollic.domain.IPaymentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class NetworkModule {

    @Provides
    @Singleton
    fun providesAuthInterceptor(
        @ApplicationContext context: Context
    ): Interceptor {
        val accessToken = context.getString(R.string.liveAccessToken)
        return Interceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("x-client-access", accessToken)
                .build()
            chain.proceed(request)
        }
    }

    @Provides
    @Singleton
    fun providesRetrofit(
        @ApplicationContext context: Context,
        authInterceptor: Interceptor
    ): Retrofit {
        val baseUrl = context.getString(R.string.baseUrl)

        return Retrofit.Builder()
            .client(
                OkHttpClient.Builder()
                    .addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BODY
                    })
                    .addInterceptor(authInterceptor)
                    .build()
            )
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providesPaymentApi(
        retrofit: Retrofit
    ): PaymentApi {
        return retrofit.create(PaymentApi::class.java)
    }

    @Provides
    @Singleton
    fun providesPaymentRepository(
        paymentApi: PaymentApi
    ): IPaymentRepository {
        return RemotePaymentRepository(paymentApi)
    }
}