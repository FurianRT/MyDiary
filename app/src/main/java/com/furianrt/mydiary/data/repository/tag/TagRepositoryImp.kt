package com.furianrt.mydiary.data.repository.tag

import com.furianrt.mydiary.data.auth.AuthHelper
import com.furianrt.mydiary.data.cloud.CloudHelper
import com.furianrt.mydiary.data.database.NoteTagDao
import com.furianrt.mydiary.data.database.TagDao
import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.data.model.NoteTag
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Scheduler
import io.reactivex.Single
import javax.inject.Inject

class TagRepositoryImp @Inject constructor(
        private val tagDao: TagDao,
        private val noteTagDao: NoteTagDao,
        private val cloud: CloudHelper,
        private val auth: AuthHelper,
        private val rxScheduler: Scheduler
) : TagRepository {

    override fun insertTag(tag: MyTag): Completable =
            tagDao.insert(tag)
                    .subscribeOn(rxScheduler)

    override fun insertTag(tags: List<MyTag>): Completable =
            tagDao.insert(tags)
                    .subscribeOn(rxScheduler)

    override fun insertNoteTag(noteTag: NoteTag): Completable =
            noteTagDao.insert(noteTag)
                    .subscribeOn(rxScheduler)

    override fun insertNoteTag(noteTags: List<NoteTag>): Completable =
            noteTagDao.insert(noteTags)
                    .subscribeOn(rxScheduler)

    override fun updateTag(tag: MyTag): Completable =
            tagDao.update(tag.apply { syncWith.clear() })
                    .subscribeOn(rxScheduler)

    override fun updateTagsSync(tags: List<MyTag>): Completable =
            tagDao.update(tags)
                    .subscribeOn(rxScheduler)

    override fun updateNoteTagsSync(noteTags: List<NoteTag>): Completable =
            noteTagDao.update(noteTags)
                    .subscribeOn(rxScheduler)

    override fun deleteTag(tag: MyTag): Completable =
            tagDao.delete(tag.id)
                    .andThen(noteTagDao.deleteWithTagId(tag.id))
                    .subscribeOn(rxScheduler)

    override fun deleteTagsFromCloud(tags: List<MyTag>): Completable =
            cloud.deleteTags(tags, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun deleteNoteTags(noteId: String): Completable =
            noteTagDao.deleteWithNoteId(noteId)
                    .subscribeOn(rxScheduler)

    override fun deleteNoteTag(noteId: String, tagId: String): Completable =
            noteTagDao.delete(noteId, tagId)
                    .subscribeOn(rxScheduler)

    override fun deleteNoteTagsFromCloud(noteTags: List<NoteTag>): Completable =
            cloud.deleteNoteTags(noteTags, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun cleanupTags(): Completable =
            tagDao.cleanup()
                    .subscribeOn(rxScheduler)

    override fun cleanupNoteTags(): Completable =
            noteTagDao.cleanup()
                    .subscribeOn(rxScheduler)

    override fun getAllTags(): Flowable<List<MyTag>> =
            tagDao.getAllTags()
                    .subscribeOn(rxScheduler)

    override fun getAllTagsFromCloud(): Single<List<MyTag>> =
            cloud.getAllTags(auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun getTagsForNote(noteId: String): Flowable<List<MyTag>> =
            noteTagDao.getTagsForNote(noteId)
                    .subscribeOn(rxScheduler)

    override fun getDeletedTags(): Flowable<List<MyTag>> =
            tagDao.getDeletedTags()
                    .subscribeOn(rxScheduler)

    override fun getAllNoteTags(): Flowable<List<NoteTag>> =
            noteTagDao.getAllNoteTags()
                    .subscribeOn(rxScheduler)

    override fun getAllNoteTagsFromCloud(): Single<List<NoteTag>> =
            cloud.getAllNoteTags(auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun getDeletedNoteTags(): Flowable<List<NoteTag>> =
            noteTagDao.getDeletedNoteTags()
                    .subscribeOn(rxScheduler)

    override fun saveTagsInCloud(tags: List<MyTag>): Completable =
            cloud.saveTags(tags, auth.getUserId())
                    .subscribeOn(rxScheduler)

    override fun saveNoteTagsInCloud(noteTags: List<NoteTag>): Completable =
            cloud.saveNoteTags(noteTags, auth.getUserId())
                    .subscribeOn(rxScheduler)
}