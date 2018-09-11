package com.furianrt.mydiary.di.application

import android.app.Application
import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.ContentValues
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
import com.furianrt.mydiary.data.storage.StorageHelper
import com.furianrt.mydiary.data.storage.StorageHelperImp
import dagger.Module
import dagger.Provides
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


@Module
class AppModule(private val app: Application) {

    @Provides
    @AppScope
    fun provideApplication() = app

    @Provides
    @AppScope
    fun provideContext(): Context = app

    @Provides
    @AppScope
    fun provideStorageHelper(context: Context) : StorageHelper = StorageHelperImp(context)

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
    fun provideRoomCallback() = object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    super.onCreate(db)
                    setInitialData(db)
                }
            }

    @Provides
    @AppScope
    fun provideNoteDatabase(context: Context, @DatabaseInfo databaseName: String,
                               callback: RoomDatabase.Callback) =
            Room.databaseBuilder(context, NoteDatabase::class.java, databaseName)
                    .addCallback(callback)
                    .build()

    @Provides
    @AppScope
    fun provideDataManager(database: NoteDatabase, prefs: PreferencesHelper, storage: StorageHelper,
                           weatherApi: WeatherApiService): DataManager =
            DataManagerImp(database, prefs, storage, weatherApi)

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

    private fun setInitialData(db: SupportSQLiteDatabase) {
        val cv = ContentValues()
        cv.put("name_mood", app.getString(R.string.mood_great))
        db.insert("Moods", 0, cv)
        cv.put("name_mood", app.getString(R.string.mood_good))
        db.insert("Moods", 0, cv)
        cv.put("name_mood", app.getString(R.string.mood_normal))
        db.insert("Moods", 0, cv)
        cv.put("name_mood", app.getString(R.string.mood_bad))
        db.insert("Moods", 0, cv)
        cv.put("name_mood", app.getString(R.string.mood_terrible))
        db.insert("Moods", 0, cv)
        cv.clear()
        cv.put("name_tag", app.getString(R.string.tag_dream))
        db.insert("Tags", 0, cv)
        cv.put("name_tag", app.getString(R.string.tag_idea))
        db.insert("Tags", 0, cv)
        cv.put("name_tag", app.getString(R.string.tag_plans))
        db.insert("Tags", 0, cv)
        cv.clear()
        cv.put("name_category", app.getString(R.string.package_love))
        db.insert("Categories", 0, cv)
        cv.put("name_category", app.getString(R.string.package_friends))
        db.insert("Categories", 0, cv)
        cv.put("name_category", app.getString(R.string.package_business))
        db.insert("Categories", 0, cv)
        cv.put("name_category", app.getString(R.string.package_todo))
        db.insert("Categories", 0, cv)
        cv.put("name_category", app.getString(R.string.package_entertainment))
        db.insert("Categories", 0, cv)
    }
}