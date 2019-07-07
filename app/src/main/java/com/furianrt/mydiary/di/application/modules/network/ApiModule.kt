package com.furianrt.mydiary.di.application.modules.network

import android.util.Log
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.furianrt.mydiary.BuildConfig
import com.furianrt.mydiary.data.api.forecast.WeatherApiService
import com.furianrt.mydiary.data.api.images.ImageApiService
import com.furianrt.mydiary.di.application.component.AppScope
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class ApiModule {

    companion object {
        private const val TAG = "ApiModule"
        private const val WEATHER_API_BASE_URL = "https://api.openweathermap.org/data/2.5/"
        private const val IMAGE_API_BASE_URL = "https://pixabay.com/api/"
    }

    @Provides
    @ForecastApi
    @AppScope
    fun provideForecastParamInterceptor(): Interceptor = object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val url = chain.request()
                    .url
                    .newBuilder()
                    .addQueryParameter("appid", BuildConfig.WEATHER_API_KEY)
                    .addQueryParameter("units", "metric")
                    .build()
            val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()
            return chain.proceed(request)
        }
    }

    @Provides
    @ImageApi
    @AppScope
    fun provideImageParamInterceptor(): Interceptor = object : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val url = chain.request()
                    .url
                    .newBuilder()
                    .addQueryParameter("key", BuildConfig.IMAGE_API_KEY)
                    .addQueryParameter("image_type", "photo")
                    .addQueryParameter("orientation", "horizontal")
                    .addQueryParameter("min_width", "1280")
                    .addQueryParameter("min_height", "720")
                    .addQueryParameter("order", "latest")
                    .addQueryParameter("safesearch", "true")
                    .build()
            val request = chain.request()
                    .newBuilder()
                    .url(url)
                    .build()
            return chain.proceed(request)
        }
    }

    @Provides
    @AppScope
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
            HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
                override fun log(message: String) {
                    Log.e(TAG, message)
                }
            }).apply { level = HttpLoggingInterceptor.Level.BODY }

    @Provides
    @AppScope
    fun provideStethoInterceptor(): StethoInterceptor = StethoInterceptor()

    @Provides
    @ForecastApi
    @AppScope
    fun provideForecastOkHttpClient(
            logInterceptor: HttpLoggingInterceptor,
            @ForecastApi paramInterceptor: Interceptor,
            stethoInterceptor: StethoInterceptor
    ): OkHttpClient = if (BuildConfig.DEBUG) {
        OkHttpClient.Builder()
                .addInterceptor(logInterceptor)
                .addInterceptor(paramInterceptor)
                .addNetworkInterceptor(stethoInterceptor)
                .build()
    } else {
        OkHttpClient.Builder()
                .addInterceptor(paramInterceptor)
                .build()
    }

    @Provides
    @ImageApi
    @AppScope
    fun provideImageOkHttpClient(
            logInterceptor: HttpLoggingInterceptor,
            @ImageApi paramInterceptor: Interceptor,
            stethoInterceptor: StethoInterceptor
    ): OkHttpClient = if (BuildConfig.DEBUG) {
        OkHttpClient.Builder()
                .addInterceptor(logInterceptor)
                .addInterceptor(paramInterceptor)
                .addNetworkInterceptor(stethoInterceptor)
                .build()
    } else {
        OkHttpClient.Builder()
                .addInterceptor(paramInterceptor)
                .build()
    }

    @Provides
    @ForecastApi
    @AppScope
    fun provideForecastRetrofit(@ForecastApi okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(WEATHER_API_BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @ImageApi
    @AppScope
    fun provideImageRetrofit(@ImageApi okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(IMAGE_API_BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @AppScope
    fun provideWeatherApiService(@ForecastApi retrofit: Retrofit): WeatherApiService =
            retrofit.create(WeatherApiService::class.java)

    @Provides
    @AppScope
    fun provideImageApiService(@ImageApi retrofit: Retrofit): ImageApiService =
            retrofit.create(ImageApiService::class.java)
}