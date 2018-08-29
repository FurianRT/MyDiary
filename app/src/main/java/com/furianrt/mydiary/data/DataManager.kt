package com.furianrt.mydiary.data

import com.furianrt.mydiary.data.api.Forecast
import com.furianrt.mydiary.data.model.MyNote
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface DataManager {

    fun insertNote(note: MyNote) : Single<Long>

    fun updateNote(note: MyNote) : Completable

    fun deleteNote(note: MyNote) : Completable

    fun getAllNotes(): Flowable<List<MyNote>>

    fun contains(noteId: Long): Single<Boolean>

    fun getForecast(lat: Double, lon: Double): Single<Forecast>
}