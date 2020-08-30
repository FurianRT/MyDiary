/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.di.application.modules.network

import com.facebook.stetho.okhttp3.StethoInterceptor
import com.furianrt.mydiary.BuildConfig
import com.furianrt.mydiary.model.source.api.forecast.WeatherApiService
import com.furianrt.mydiary.model.source.api.images.ImagesApiService
import com.furianrt.mydiary.di.application.component.AppScope
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
object ApiModule {

    private const val WEATHER_API_BASE_URL = "https://api.openweathermap.org/data/2.5/"
    private const val IMAGE_API_BASE_URL = "https://pixabay.com/api/"

    @JvmStatic
    @Provides
    @ForecastApi
    @AppScope
    fun provideForecastParamInterceptor(): Interceptor = Interceptor { chain ->
        chain.proceed(chain.request()
                .newBuilder()
                .url(chain.request()
                        .url()
                        .newBuilder()
                        .addQueryParameter("appid", BuildConfig.WEATHER_API_KEY)
                        .addQueryParameter("units", "metric")
                        .build())
                .build())
    }

    @JvmStatic
    @Provides
    @ImageApi
    @AppScope
    fun provideImageParamInterceptor(): Interceptor = Interceptor { chain ->
        chain.proceed(chain.request()
                .newBuilder()
                .url(chain.request()
                        .url()
                        .newBuilder()
                        .addQueryParameter("key", BuildConfig.IMAGE_API_KEY)
                        .addQueryParameter("image_type", "photo")
                        .addQueryParameter("orientation", "horizontal")
                        .addQueryParameter("min_width", "1280")
                        .addQueryParameter("min_height", "720")
                        .addQueryParameter("order", "latest")
                        .addQueryParameter("safesearch", "true")
                        .build())
                .build())
    }

    @JvmStatic
    @Provides
    @AppScope
    fun provideStethoInterceptor(): StethoInterceptor = StethoInterceptor()

    @JvmStatic
    @Provides
    @ForecastApi
    @AppScope
    fun provideForecastOkHttpClient(
            @ForecastApi paramInterceptor: Interceptor,
            stethoInterceptor: StethoInterceptor
    ): OkHttpClient = if (BuildConfig.DEBUG) {
        OkHttpClient.Builder()
                .addInterceptor(paramInterceptor)
                .addNetworkInterceptor(stethoInterceptor)
                .build()
    } else {
        OkHttpClient.Builder()
                .addInterceptor(paramInterceptor)
                .build()
    }

    @JvmStatic
    @Provides
    @ImageApi
    @AppScope
    fun provideImageOkHttpClient(
            @ImageApi paramInterceptor: Interceptor,
            stethoInterceptor: StethoInterceptor
    ): OkHttpClient = if (BuildConfig.DEBUG) {
        OkHttpClient.Builder()
                .addInterceptor(paramInterceptor)
                .addNetworkInterceptor(stethoInterceptor)
                .build()
    } else {
        OkHttpClient.Builder()
                .addInterceptor(paramInterceptor)
                .build()
    }

    @JvmStatic
    @Provides
    @ForecastApi
    @AppScope
    fun provideForecastRetrofit(@ForecastApi okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(WEATHER_API_BASE_URL)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @JvmStatic
    @Provides
    @ImageApi
    @AppScope
    fun provideImageRetrofit(@ImageApi okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(IMAGE_API_BASE_URL)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @JvmStatic
    @Provides
    @AppScope
    fun provideWeatherApiService(@ForecastApi retrofit: Retrofit): WeatherApiService =
            retrofit.create(WeatherApiService::class.java)

    @JvmStatic
    @Provides
    @AppScope
    fun provideImageApiService(@ImageApi retrofit: Retrofit): ImagesApiService =
            retrofit.create(ImagesApiService::class.java)
}