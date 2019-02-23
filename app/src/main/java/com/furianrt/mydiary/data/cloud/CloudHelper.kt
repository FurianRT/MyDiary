package com.furianrt.mydiary.data.cloud

import com.furianrt.mydiary.data.model.*
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

interface CloudHelper {

    fun isProfileExists(email: String): Single<Boolean>
    fun getProfile(email: String): Maybe<MyProfile>
    fun createProfile(profile: MyProfile): Completable
    fun saveNotes(notes: List<MyNote>, profile: MyProfile): Completable
    fun saveCategories(categories: List<MyCategory>, profile: MyProfile): Completable
    fun saveTags(tags: List<MyTag>, profile: MyProfile): Completable
    fun saveNoteTags(noteTags: List<NoteTag>, profile: MyProfile): Completable
    fun saveAppearances(appearances: List<MyNoteAppearance>, profile: MyProfile): Completable
    fun saveImages(images: List<MyImage>, profile: MyProfile): Completable
    fun deleteNotes(notes: List<MyNote>, profile: MyProfile): Completable
    fun deleteCategories(categories: List<MyCategory>, profile: MyProfile): Completable
    fun deleteNoteTags(noteTags: List<NoteTag>, profile: MyProfile): Completable
    fun deleteTags(tags: List<MyTag>, profile: MyProfile): Completable
    fun deleteAppearances(appearances: List<MyNoteAppearance>, profile: MyProfile): Completable
    fun deleteImages(images: List<MyImage>, profile: MyProfile): Completable
    fun getAllNotes(profile: MyProfile): Single<List<MyNote>>
    fun getAllCategories(profile: MyProfile): Single<List<MyCategory>>
    fun getAllTags(profile: MyProfile): Single<List<MyTag>>
    fun getAllAppearances(profile: MyProfile): Single<List<MyNoteAppearance>>
    fun getAllNoteTags(profile: MyProfile): Single<List<NoteTag>>
    fun getAllImages(profile: MyProfile): Single<List<MyImage>>
}