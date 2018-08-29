package com.furianrt.mydiary.di.application

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import android.util.Log
import com.furianrt.mydiary.LOG_TAG
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.DataManagerImp
import com.furianrt.mydiary.data.api.WeatherApiService
import com.furianrt.mydiary.data.prefs.PreferencesHelper
import com.furianrt.mydiary.data.prefs.PreferencesHelperImp
import com.furianrt.mydiary.data.room.NoteDatabase
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.HttpUrl


@Module
class AppModule(private val app: Application) {

    @Provides
    @AppScope
    fun provideApplication() = app

    @Provides
    @AppScope
    fun provideContext(): Context = app

    @Provides
    @PreferenceInfo
    @AppScope
    fun providePrefFolderName() = "myPreferences"

    @Provides
    @AppScope
    fun providePreferencesHelper(context: Context, @PreferenceInfo name: String): PreferencesHelper =
            PreferencesHelperImp(context, name)

    @Provides
    @DatabaseInfo
    @AppScope
    fun provideDatabaseName() = "Notes.db"

    @Provides
    @AppScope
    fun provideContactDatabase(context: Context, @DatabaseInfo databaseName: String) =
            Room.databaseBuilder(context, NoteDatabase::class.java, databaseName)
                    .build()

    @Provides
    @AppScope
    fun provideDataManager(database: NoteDatabase, prefs: PreferencesHelper,
                           weatherApi: WeatherApiService): DataManager =
            DataManagerImp(database, prefs, weatherApi)

    @Provides
    @AppScope
    fun provideParamInterceptor(): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val originalUrl = original.url()
            val url = originalUrl.newBuilder()
                    .addQueryParameter("appid", app.getString(R.string.weather_api_key))
                    .addEncodedQueryParameter("units", "metric")
                    .build()
            val requestBuilder = original.newBuilder().url(url)
            val request = requestBuilder.build()
             chain.proceed(request)
        }
    }

    @Provides
    @AppScope
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor { message -> Log.e(LOG_TAG, message) }
        return logging.setLevel(HttpLoggingInterceptor.Level.BASIC)
    }

    @Provides
    @AppScope
    fun provideOkHttpClient(logInterceptor: HttpLoggingInterceptor, paramInterceptor: Interceptor):
            OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(logInterceptor)
            .addInterceptor(paramInterceptor)
            .build()

    @Provides
    @AppScope
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit =
            Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl("https://api.openweathermap.org/data/2.5/")
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

    @Provides
    @AppScope
    fun provideWeatherApiService(retrofit: Retrofit): WeatherApiService =
            retrofit.create(WeatherApiService::class.java)
}