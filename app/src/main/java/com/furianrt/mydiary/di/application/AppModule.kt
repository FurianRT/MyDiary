package com.furianrt.mydiary.di.application

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.DataManagerImp
import com.furianrt.mydiary.data.api.forecast.WeatherApiService
import com.furianrt.mydiary.data.api.images.ImageApiService
import com.furianrt.mydiary.data.cloud.CloudHelper
import com.furianrt.mydiary.data.cloud.CloudHelperImp
import com.furianrt.mydiary.data.model.MyCategory
import com.furianrt.mydiary.data.prefs.PreferencesHelper
import com.furianrt.mydiary.data.prefs.PreferencesHelperImp
import com.furianrt.mydiary.data.room.NoteDatabase
import com.furianrt.mydiary.data.storage.StorageHelper
import com.furianrt.mydiary.data.storage.StorageHelperImp
import com.furianrt.mydiary.utils.generateUniqueId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class AppModule(private val app: Application) {

    companion object {
        private const val TAG = "AppModule"
        private const val DATABASE_NAME = "Notes.db"
        private const val WEATHER_BASE_URL = "https://api.openweathermap.org/data/2.5/"
        private const val IMAGE_BASE_URL = "https://pixabay.com/api/"
    }

    @Provides
    @AppScope
    fun provideContext(): Context = app

    @Provides
    @AppScope
    fun provideStorageHelper(context: Context): StorageHelper = StorageHelperImp(context)

    @Provides
    @AppScope
    fun providePreferencesHelper(context: Context): PreferencesHelper =
            PreferencesHelperImp(context)

    @Provides
    @AppScope
    fun provideCloudHelper(firestore: FirebaseFirestore, firebaseStorage: FirebaseStorage): CloudHelper =
            CloudHelperImp(firestore, firebaseStorage)

    @Provides
    @AppScope
    fun provideFirestore() = FirebaseFirestore
            .getInstance()
            .apply {
                firestoreSettings = FirebaseFirestoreSettings.Builder(firestoreSettings)
                        .setPersistenceEnabled(false)
                        .build()
            }


    @Provides
    @AppScope
    fun provideFirebaseStorage() = FirebaseStorage.getInstance()

    @Provides
    @DatabaseInfo
    @AppScope
    fun provideDatabaseName() = DATABASE_NAME

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
    fun provideRxScheduler(): Scheduler = Schedulers.io()

    @Provides
    @AppScope
    fun provideDataManager(database: NoteDatabase, prefs: PreferencesHelper, storage: StorageHelper,
                           weatherApi: WeatherApiService, imageApi: ImageApiService,
                           cloudHelper: CloudHelper, rxScheduler: Scheduler): DataManager =
            DataManagerImp(database, prefs, storage, weatherApi, imageApi, cloudHelper, rxScheduler)

    @Provides
    @ForecastApi
    @AppScope
    fun provideForecastParamInterceptor(): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val url = original.url()
                    .newBuilder()
                    .addQueryParameter("appid", app.getString(R.string.weather_api_key))
                    .addQueryParameter("units", "metric")
                    .build()
            val request = original.newBuilder()
                    .url(url)
                    .build()
            return@Interceptor chain.proceed(request)
        }
    }

    @Provides
    @ImageApi
    @AppScope
    fun provideImageParamInterceptor(): Interceptor {
        return Interceptor { chain ->
            val original = chain.request()
            val url = original.url()
                    .newBuilder()
                    .addQueryParameter("key", app.getString(R.string.image_api_key))
                    .addQueryParameter("image_type", "photo")
                    .addQueryParameter("orientation", "horizontal")
                    .addQueryParameter("category", "backgrounds")
                    .addQueryParameter("min_width", "1280")
                    .addQueryParameter("min_height", "720")
                    .addQueryParameter("order", "latest")
                    .addQueryParameter("safesearch", "true")
                    .build()
            val request = original.newBuilder()
                    .url(url)
                    .build()
            return@Interceptor chain.proceed(request)
        }
    }

    @Provides
    @AppScope
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor { message -> Log.e(TAG, message) }
        return logging.setLevel(HttpLoggingInterceptor.Level.BASIC)
    }

    @Provides
    @ForecastApi
    @AppScope
    fun provideForecastOkHttpClient(logInterceptor: HttpLoggingInterceptor,
                                    @ForecastApi paramInterceptor: Interceptor): OkHttpClient =
            OkHttpClient.Builder()
                    .addInterceptor(logInterceptor)
                    .addInterceptor(paramInterceptor)
                    .build()

    @Provides
    @ImageApi
    @AppScope
    fun provideImageOkHttpClient(logInterceptor: HttpLoggingInterceptor,
                                 @ImageApi paramInterceptor: Interceptor): OkHttpClient =
            OkHttpClient.Builder()
                    .addInterceptor(logInterceptor)
                    .addInterceptor(paramInterceptor)
                    .build()

    @Provides
    @ForecastApi
    @AppScope
    fun provideForecastRetrofit(@ForecastApi okHttpClient: OkHttpClient): Retrofit =
            Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(WEATHER_BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

    @Provides
    @ImageApi
    @AppScope
    fun provideImageRetrofit(@ImageApi okHttpClient: OkHttpClient): Retrofit =
            Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(IMAGE_BASE_URL)
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

    private fun setInitialData(db: SupportSQLiteDatabase) {
        val cv = ContentValues()
        val moodNames = app.resources.getStringArray(R.array.moods)
        val moodIcons = arrayOf(
                app.resources.getResourceEntryName(R.drawable.ic_mood_angry),
                app.resources.getResourceEntryName(R.drawable.ic_mood_awful),
                app.resources.getResourceEntryName(R.drawable.ic_mood_bad),
                app.resources.getResourceEntryName(R.drawable.ic_mood_neutral),
                app.resources.getResourceEntryName(R.drawable.ic_mood_good),
                app.resources.getResourceEntryName(R.drawable.ic_mood_great)
        )
        for (i in 0 until moodNames.size) {
            cv.put("name_mood", moodNames[i])
            cv.put("icon_mood", moodIcons[i])
            db.insert("Moods", 0, cv)
        }
        cv.clear()
        val tagNames = app.resources.getStringArray(R.array.tags)
        for (tagName in tagNames) {
            cv.put("id_tag", generateUniqueId())
            cv.put("name_tag", tagName)
            cv.put("is_tag_sync", false)
            cv.put("is_tag_deleted", false)
            db.insert("Tags", 0, cv)
        }
        cv.clear()
        val categoryNames = app.resources.getStringArray(R.array.categories)
        for (categoryName in categoryNames) {
            cv.put("name_category", categoryName)
            cv.put("color", MyCategory.DEFAULT_COLOR)
            cv.put("is_category_sync", false)
            cv.put("is_category_deleted", false)
            db.insert("Categories", 0, cv)
        }
        cv.clear()
    }
}