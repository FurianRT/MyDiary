/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.source.cloud

import com.furianrt.mydiary.model.entity.*
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

interface CloudSource {
    fun saveProfile(profile: MyProfile): Completable
    fun saveNotes(notes: List<MyNote>, userId: String): Completable
    fun saveCategories(categories: List<MyCategory>, userId: String): Completable
    fun saveTags(tags: List<MyTag>, userId: String): Completable
    fun saveNoteTags(noteTags: List<NoteTag>, userId: String): Completable
    fun saveNoteLocations(noteLocations: List<NoteLocation>, userId: String): Completable
    fun saveAppearances(appearances: List<MyNoteAppearance>, userId: String): Completable
    fun saveLocations(locations: List<MyLocation>, userId: String): Completable
    fun saveForecasts(forecasts: List<MyForecast>, userId: String): Completable
    fun saveImages(images: List<MyImage>, userId: String): Completable
    fun saveImagesFiles(images: List<MyImage>, userId: String): Completable
    fun saveTextSpans(textSpans: List<MyTextSpan>, userId: String): Completable
    fun deleteNotes(notes: List<MyNote>, userId: String): Completable
    fun deleteCategories(categories: List<MyCategory>, userId: String): Completable
    fun deleteNoteTags(noteTags: List<NoteTag>, userId: String): Completable
    fun deleteNoteLocations(noteLocations: List<NoteLocation>, userId: String): Completable
    fun deleteLocations(locations: List<MyLocation>, userId: String): Completable
    fun deleteForecasts(forecasts: List<MyForecast>, userId: String): Completable
    fun deleteTags(tags: List<MyTag>, userId: String): Completable
    fun deleteAppearances(appearances: List<MyNoteAppearance>, userId: String): Completable
    fun deleteImages(images: List<MyImage>, userId: String): Completable
    fun deleteTextSpans(textSpans: List<MyTextSpan>, userId: String): Completable
    fun getProfile(userId: String): Maybe<MyProfile>
    fun getAllNotes(userId: String): Single<List<MyNote>>
    fun getAllCategories(userId: String): Single<List<MyCategory>>
    fun getAllTags(userId: String): Single<List<MyTag>>
    fun getAllAppearances(userId: String): Single<List<MyNoteAppearance>>
    fun getAllNoteTags(userId: String): Single<List<NoteTag>>
    fun getAllNoteLocations(userId: String): Single<List<NoteLocation>>
    fun getAllLocations(userId: String): Single<List<MyLocation>>
    fun getAllForecasts(userId: String): Single<List<MyForecast>>
    fun getAllImages(userId: String): Single<List<MyImage>>
    fun getAllTextSpans(userId: String): Single<List<MyTextSpan>>
    fun loadImageFiles(images: List<MyImage>, userId: String): Completable
}