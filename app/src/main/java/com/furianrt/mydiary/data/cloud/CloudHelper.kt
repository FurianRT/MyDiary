package com.furianrt.mydiary.data.cloud

import com.furianrt.mydiary.data.model.*
import io.reactivex.Completable
import io.reactivex.Single

interface CloudHelper {
    fun saveProfile(profile: MyProfile): Completable
    fun saveNotes(notes: List<MyNote>, userId: String): Completable
    fun saveCategories(categories: List<MyCategory>, userId: String): Completable
    fun saveTags(tags: List<MyTag>, userId: String): Completable
    fun saveNoteTags(noteTags: List<NoteTag>, userId: String): Completable
    fun saveAppearances(appearances: List<MyNoteAppearance>, userId: String): Completable
    fun saveImages(images: List<MyImage>, userId: String): Completable
    fun saveImagesFiles(images: List<MyImage>, userId: String): Completable
    fun deleteNotes(notes: List<MyNote>, userId: String): Completable
    fun deleteCategories(categories: List<MyCategory>, userId: String): Completable
    fun deleteNoteTags(noteTags: List<NoteTag>, userId: String): Completable
    fun deleteTags(tags: List<MyTag>, userId: String): Completable
    fun deleteAppearances(appearances: List<MyNoteAppearance>, userId: String): Completable
    fun deleteImages(images: List<MyImage>, userId: String): Completable
    fun getProfile(userId: String): Single<MyProfile>
    fun getAllNotes(userId: String): Single<List<MyNote>>
    fun getAllCategories(userId: String): Single<List<MyCategory>>
    fun getAllTags(userId: String): Single<List<MyTag>>
    fun getAllAppearances(userId: String): Single<List<MyNoteAppearance>>
    fun getAllNoteTags(userId: String): Single<List<NoteTag>>
    fun getAllImages(userId: String): Single<List<MyImage>>
    fun loadImageFiles(images: List<MyImage>, userId: String): Completable
}