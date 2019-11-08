/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.model.gateway.tag

import com.furianrt.mydiary.model.entity.MyTag
import com.furianrt.mydiary.model.entity.NoteTag
import com.furianrt.mydiary.model.source.auth.AuthHelper
import com.furianrt.mydiary.model.source.cloud.CloudHelper
import com.furianrt.mydiary.model.source.database.NoteTagDao
import com.furianrt.mydiary.model.source.database.TagDao
import com.furianrt.mydiary.utils.MyRxUtils
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import javax.inject.Inject

class TagGatewayImp @Inject constructor(
        private val tagDao: TagDao,
        private val noteTagDao: NoteTagDao,
        private val cloud: CloudHelper,
        private val auth: AuthHelper,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : TagGateway {

    override fun insertTag(tag: MyTag): Completable =
            tagDao.insert(tag)
                    .subscribeOn(scheduler.io())

    override fun insertTag(tags: List<MyTag>): Completable =
            tagDao.insert(tags)
                    .subscribeOn(scheduler.io())

    override fun insertNoteTag(noteTag: NoteTag): Completable =
            noteTagDao.insert(noteTag)
                    .subscribeOn(scheduler.io())

    override fun insertNoteTag(noteTags: List<NoteTag>): Completable =
            noteTagDao.insert(noteTags)
                    .subscribeOn(scheduler.io())

    override fun updateTag(tag: MyTag): Completable =
            tagDao.update(tag.apply { syncWith.clear() })
                    .subscribeOn(scheduler.io())

    override fun updateTagsSync(tags: List<MyTag>): Completable =
            tagDao.update(tags)
                    .subscribeOn(scheduler.io())

    override fun updateNoteTagsSync(noteTags: List<NoteTag>): Completable =
            noteTagDao.update(noteTags)
                    .subscribeOn(scheduler.io())

    override fun deleteTag(tag: MyTag): Completable =
            tagDao.delete(tag.id)
                    .andThen(noteTagDao.deleteWithTagId(tag.id))
                    .subscribeOn(scheduler.io())

    override fun deleteTagsFromCloud(tags: List<MyTag>): Completable =
            cloud.deleteTags(tags, auth.getUserId())
                    .subscribeOn(scheduler.io())

    override fun deleteNoteTags(noteId: String): Completable =
            noteTagDao.deleteWithNoteId(noteId)
                    .subscribeOn(scheduler.io())

    override fun deleteNoteTag(noteId: String, tagId: String): Completable =
            noteTagDao.delete(noteId, tagId)
                    .subscribeOn(scheduler.io())

    override fun deleteNoteTagsFromCloud(noteTags: List<NoteTag>): Completable =
            cloud.deleteNoteTags(noteTags, auth.getUserId())
                    .subscribeOn(scheduler.io())

    override fun cleanupTags(): Completable =
            tagDao.cleanup()
                    .subscribeOn(scheduler.io())

    override fun cleanupNoteTags(): Completable =
            noteTagDao.cleanup()
                    .subscribeOn(scheduler.io())

    override fun getAllTags(): Flowable<List<MyTag>> =
            tagDao.getAllTags()
                    .subscribeOn(scheduler.io())

    override fun getAllTagsFromCloud(): Single<List<MyTag>> =
            cloud.getAllTags(auth.getUserId())
                    .subscribeOn(scheduler.io())

    override fun getTagsForNote(noteId: String): Flowable<List<MyTag>> =
            noteTagDao.getTagsForNote(noteId)
                    .subscribeOn(scheduler.io())

    override fun getDeletedTags(): Flowable<List<MyTag>> =
            tagDao.getDeletedTags()
                    .subscribeOn(scheduler.io())

    override fun getAllNoteTags(): Flowable<List<NoteTag>> =
            noteTagDao.getAllNoteTags()
                    .subscribeOn(scheduler.io())

    override fun getAllNoteTagsFromCloud(): Single<List<NoteTag>> =
            cloud.getAllNoteTags(auth.getUserId())
                    .subscribeOn(scheduler.io())

    override fun getDeletedNoteTags(): Flowable<List<NoteTag>> =
            noteTagDao.getDeletedNoteTags()
                    .subscribeOn(scheduler.io())

    override fun saveTagsInCloud(tags: List<MyTag>): Completable =
            cloud.saveTags(tags, auth.getUserId())
                    .subscribeOn(scheduler.io())

    override fun saveNoteTagsInCloud(noteTags: List<NoteTag>): Completable =
            cloud.saveNoteTags(noteTags, auth.getUserId())
                    .subscribeOn(scheduler.io())
}