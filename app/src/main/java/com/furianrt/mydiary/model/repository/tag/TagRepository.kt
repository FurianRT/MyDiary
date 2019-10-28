/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.repository.tag

import com.furianrt.mydiary.model.entity.MyTag
import com.furianrt.mydiary.model.entity.NoteTag
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single

interface TagRepository {
    fun insertTag(tag: MyTag): Completable
    fun insertTag(tags: List<MyTag>): Completable
    fun insertNoteTag(noteTag: NoteTag): Completable
    fun insertNoteTag(noteTags: List<NoteTag>): Completable
    fun updateTag(tag: MyTag): Completable
    fun updateTagsSync(tags: List<MyTag>): Completable
    fun updateNoteTagsSync(noteTags: List<NoteTag>): Completable
    fun deleteTag(tag: MyTag): Completable
    fun deleteTagsFromCloud(tags: List<MyTag>): Completable
    fun deleteNoteTags(noteId: String): Completable
    fun deleteNoteTag(noteId: String, tagId: String): Completable
    fun deleteNoteTagsFromCloud(noteTags: List<NoteTag>): Completable
    fun cleanupTags(): Completable
    fun cleanupNoteTags(): Completable
    fun getAllTags(): Flowable<List<MyTag>>
    fun getAllTagsFromCloud(): Single<List<MyTag>>
    fun getDeletedTags(): Flowable<List<MyTag>>
    fun getTagsForNote(noteId: String): Flowable<List<MyTag>>
    fun getAllNoteTags(): Flowable<List<NoteTag>>
    fun getAllNoteTagsFromCloud(): Single<List<NoteTag>>
    fun getDeletedNoteTags(): Flowable<List<NoteTag>>
    fun saveTagsInCloud(tags: List<MyTag>): Completable
    fun saveNoteTagsInCloud(noteTags: List<NoteTag>): Completable
}