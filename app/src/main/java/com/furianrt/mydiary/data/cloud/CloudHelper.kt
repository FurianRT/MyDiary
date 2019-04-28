package com.furianrt.mydiary.data.cloud

import com.furianrt.mydiary.data.model.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single

interface CloudHelper {
    fun isSignedIn(): Boolean
    fun signUp(email: String, password: String): Single<MyProfile>
    fun signIn(email: String, password: String): Single<MyProfile>
    fun signOut(): Completable
    fun updatePassword(oldPassword: String, newPassword: String): Completable
    fun updateProfile(profile: MyProfile): Completable
    fun isProfileExists(email: String): Single<Boolean>
    fun observeSignOut(): Observable<Boolean>
    fun saveNotes(notes: List<MyNote>): Completable
    fun saveCategories(categories: List<MyCategory>): Completable
    fun saveTags(tags: List<MyTag>): Completable
    fun saveNoteTags(noteTags: List<NoteTag>): Completable
    fun saveAppearances(appearances: List<MyNoteAppearance>): Completable
    fun saveImages(images: List<MyImage>): Completable
    fun saveImagesFiles(images: List<MyImage>): Completable
    fun deleteNotes(notes: List<MyNote>): Completable
    fun deleteCategories(categories: List<MyCategory>): Completable
    fun deleteNoteTags(noteTags: List<NoteTag>): Completable
    fun deleteTags(tags: List<MyTag>): Completable
    fun deleteAppearances(appearances: List<MyNoteAppearance>): Completable
    fun deleteImages(images: List<MyImage>): Completable
    fun getAllNotes(): Single<List<MyNote>>
    fun getAllCategories(): Single<List<MyCategory>>
    fun getAllTags(): Single<List<MyTag>>
    fun getAllAppearances(): Single<List<MyNoteAppearance>>
    fun getAllNoteTags(): Single<List<NoteTag>>
    fun getAllImages(): Single<List<MyImage>>
    fun loadImageFiles(images: List<MyImage>): Completable
}