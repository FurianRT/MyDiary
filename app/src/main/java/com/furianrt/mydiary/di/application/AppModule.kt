package com.furianrt.mydiary.di.application

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.graphics.BitmapFactory
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
import com.furianrt.mydiary.data.model.*
import com.furianrt.mydiary.data.prefs.PreferencesHelper
import com.furianrt.mydiary.data.prefs.PreferencesHelperImp
import com.furianrt.mydiary.data.room.NoteDatabase
import com.furianrt.mydiary.data.storage.StorageHelper
import com.furianrt.mydiary.data.storage.StorageHelperImp
import com.furianrt.mydiary.utils.generateUniqueId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.joda.time.DateTime
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Module
class AppModule(private val app: Application) {

    companion object {
        private const val TAG = "AppModule"
        private const val DATABASE_NAME = "Notes.db"
        private const val WEATHER_API_BASE_URL = "https://api.openweathermap.org/data/2.5/"
        private const val IMAGE_API_BASE_URL = "https://pixabay.com/api/"
    }

    @Provides
    @AppScope
    fun provideContext(): Context = app

    @Provides
    @AppScope
    fun provideStorageHelper(context: Context): StorageHelper = StorageHelperImp(context)

    @Provides
    @AppScope
    fun providePreferencesHelper(context: Context): PreferencesHelper = PreferencesHelperImp(context)

    @Provides
    @AppScope
    fun provideCloudHelper(
            firestore: FirebaseFirestore,
            firebaseStorage: FirebaseStorage
    ): CloudHelper = CloudHelperImp(firestore, firebaseStorage)

    @Provides
    @AppScope
    fun provideFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance()
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
            createDefaultProperties(db)
            createTutorialNote(db)
        }
    }

    @Provides
    @AppScope
    fun provideNoteDatabase(
            context: Context,
            @DatabaseInfo databaseName: String,
            callback: RoomDatabase.Callback
    ) = Room.databaseBuilder(context, NoteDatabase::class.java, databaseName)
            .addCallback(callback)
            .build()

    @Provides
    @AppScope
    fun provideRxScheduler(): Scheduler = Schedulers.io()

    @Provides
    @AppScope
    fun provideDataManager(
            database: NoteDatabase,
            prefs: PreferencesHelper,
            storage: StorageHelper,
            weatherApi: WeatherApiService,
            imageApi: ImageApiService,
            cloudHelper: CloudHelper,
            rxScheduler: Scheduler
    ): DataManager = DataManagerImp(database, prefs, storage, weatherApi, imageApi, cloudHelper, rxScheduler)

    @Provides
    @ForecastApi
    @AppScope
    fun provideForecastParamInterceptor(): Interceptor = Interceptor { chain ->
        val url = chain.request()
                .url()
                .newBuilder()
                .addQueryParameter("appid", app.getString(R.string.weather_api_key))
                .addQueryParameter("units", "metric")
                .build()
        val request = chain.request()
                .newBuilder()
                .url(url)
                .build()
        return@Interceptor chain.proceed(request)
    }

    @Provides
    @ImageApi
    @AppScope
    fun provideImageParamInterceptor(): Interceptor = Interceptor { chain ->
        val url = chain.request()
                .url()
                .newBuilder()
                .addQueryParameter("key", app.getString(R.string.image_api_key))
                .addQueryParameter("image_type", "photo")
                .addQueryParameter("orientation", "horizontal")
                .addQueryParameter("travel", "backgrounds")
                .addQueryParameter("min_width", "1280")
                .addQueryParameter("min_height", "720")
                .addQueryParameter("order", "latest")
                .addQueryParameter("safesearch", "true")
                .build()
        val request = chain.request()
                .newBuilder()
                .url(url)
                .build()
        return@Interceptor chain.proceed(request)
    }

    @Provides
    @AppScope
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor =
            HttpLoggingInterceptor { message -> Log.e(TAG, message) }
                    .setLevel(HttpLoggingInterceptor.Level.BASIC)

    @Provides
    @ForecastApi
    @AppScope
    fun provideForecastOkHttpClient(
            logInterceptor: HttpLoggingInterceptor,
            @ForecastApi paramInterceptor: Interceptor
    ): OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(logInterceptor)
            .addInterceptor(paramInterceptor)
            .build()

    @Provides
    @ImageApi
    @AppScope
    fun provideImageOkHttpClient(
            logInterceptor: HttpLoggingInterceptor,
            @ImageApi paramInterceptor: Interceptor
    ): OkHttpClient = OkHttpClient.Builder()
            .addInterceptor(logInterceptor)
            .addInterceptor(paramInterceptor)
            .build()

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

    private fun createDefaultProperties(db: SupportSQLiteDatabase) {
        with(ContentValues()) {
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
                put(MyMood.FIELD_NAME, moodNames[i])
                put(MyMood.FIELD_ICON, moodIcons[i])
                db.insert(MyMood.TABLE_NAME, SQLiteDatabase.CONFLICT_IGNORE, this)
            }
            clear()
            val tagNames = app.resources.getStringArray(R.array.tags)
            for (i in 0 until tagNames.size) {
                put(MyTag.FIELD_ID, "default_tag_$i")
                put(MyTag.FIELD_NAME, tagNames[i])
                put(MyTag.FIELD_SYNC_WITH, Gson().toJson(listOf(MyTag.DEFAULT_SYNC_EMAIL)))
                put(MyTag.FIELD_IS_DELETED, false)
                db.insert(MyTag.TABLE_NAME, SQLiteDatabase.CONFLICT_IGNORE, this)
            }
            clear()
            val categoryNames = app.resources.getStringArray(R.array.categories)
            for (i in 0 until categoryNames.size) {
                put(MyCategory.FIELD_ID, "default_category_$i")
                put(MyCategory.FIELD_NAME, categoryNames[i])
                put(MyCategory.FIELD_COLOR, MyCategory.DEFAULT_COLOR)
                put(MyCategory.FIELD_SYNC_WITH, Gson().toJson(listOf(MyCategory.DEFAULT_SYNC_EMAIL)))
                put(MyCategory.FIELD_IS_DELETED, false)
                db.insert(MyCategory.TABLE_NAME, SQLiteDatabase.CONFLICT_IGNORE, this)
            }
            clear()
        }
    }

    private fun createTutorialNote(db: SupportSQLiteDatabase) {
        val note = MyNote(
                id = generateUniqueId(),
                title = app.getString(R.string.tutorial_note_title),
                content = app.getString(R.string.tutorial_note_content),
                time = DateTime.now().millis,
                moodId = app.resources.getStringArray(R.array.moods).size,
                categoryId = "default_category_4",
                creationTime = DateTime.now().millis,
                locationName = null,
                forecast = null
        )

        val noteAppearance = MyNoteAppearance(appearanceId = note.id)

        val noteTag1 = NoteTag(noteId = note.id, tagId = "default_tag_0")
        val noteTag2 = NoteTag(noteId = note.id, tagId = "default_tag_1")

        val imageFile = provideStorageHelper(app).copyBitmapToStorage(
                BitmapFactory.decodeResource(app.resources, R.drawable.tutorial_header_image),
                generateUniqueId()
        )

        val image = MyImage(
                name = imageFile.name,
                uri = imageFile.toURI().toString(),
                noteId = note.id,
                addedTime = DateTime.now().millis,
                editedTime = DateTime.now().millis
        )

        with(ContentValues()) {
            put(MyNote.FIELD_ID, note.id)
            put(MyNote.FIELD_TITLE, note.title)
            put(MyNote.FIELD_CONTENT, note.content)
            put(MyNote.FIELD_TIME, note.time)
            put(MyNote.FIELD_MOOD, note.moodId)
            put(MyNote.FIELD_CATEGORY, note.categoryId)
            put(MyNote.FIELD_CREATION_TIME, note.creationTime)
            put(MyNote.FIELD_SYNC_WITH, Gson().toJson(note.syncWith))
            put(MyNote.FIELD_IS_DELETED, note.isDeleted)
            db.insert(MyNote.TABLE_NAME, SQLiteDatabase.CONFLICT_IGNORE, this)
            clear()

            put(MyNoteAppearance.FIELD_ID, noteAppearance.appearanceId)
            put(MyNoteAppearance.FIELD_ID, noteAppearance.appearanceId)
            put(MyNoteAppearance.FIELD_SYNC_WITH, Gson().toJson(noteAppearance.syncWith))
            put(MyNoteAppearance.FIELD_IS_DELETED, noteAppearance.isDeleted)
            db.insert(MyNoteAppearance.TABLE_NAME, SQLiteDatabase.CONFLICT_IGNORE, this)
            clear()

            put(NoteTag.FIELD_NOTE_ID, noteTag1.noteId)
            put(NoteTag.FIELD_TAG_ID, noteTag1.tagId)
            put(NoteTag.FIELD_SYNC_WITH, Gson().toJson(noteTag1.syncWith))
            put(NoteTag.FIELD_IS_DELETED, noteTag1.isDeleted)
            db.insert(NoteTag.TABLE_NAME, SQLiteDatabase.CONFLICT_IGNORE, this)

            put(NoteTag.FIELD_TAG_ID, noteTag2.tagId)
            db.insert(NoteTag.TABLE_NAME, SQLiteDatabase.CONFLICT_IGNORE, this)
            clear()

            put(MyImage.FIELD_NAME, image.name)
            put(MyImage.FIELD_URI, image.uri)
            put(MyImage.FIELD_ID_NOTE, image.noteId)
            put(MyImage.FIELD_ADDED_TIME, image.addedTime)
            put(MyImage.FIELD_EDITED_TIME, image.editedTime)
            put(MyImage.FIELD_ORDER, image.order)
            put(MyImage.FIELD_SYNC_WITH, Gson().toJson(image.syncWith))
            put(MyImage.FIELD_FILE_SYNC_WITH, Gson().toJson(image.fileSyncWith))
            put(MyImage.FIELD_IS_DELETED, image.isDeleted)
            db.insert(MyImage.TABLE_NAME, SQLiteDatabase.CONFLICT_IGNORE, this)
            clear()
        }
    }
}