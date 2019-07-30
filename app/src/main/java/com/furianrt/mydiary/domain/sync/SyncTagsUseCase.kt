/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.sync

import com.furianrt.mydiary.data.repository.tag.TagRepository
import io.reactivex.Completable
import javax.inject.Inject

class SyncTagsUseCase @Inject constructor(
        private val tagRepository: TagRepository
) {

    class SyncTagsException : Throwable()
    class SyncNoteTagsException : Throwable()

    fun invoke(email: String): Completable =
            Completable.concat(listOf(syncTags(email), syncNoteTags(email)))

    private fun syncTags(email: String): Completable =
            tagRepository.getAllTags()
                    .first(emptyList())
                    .map { tags -> tags.filter { !it.isSync(email) } }
                    .map { tags -> tags.apply { forEach { it.syncWith.add(email) } } }
                    .flatMapCompletable { tags ->
                        Completable.concat(listOf(
                                tagRepository.saveTagsInCloud(tags),
                                tagRepository.updateTagsSync(tags)
                        ))
                    }
                    .andThen(tagRepository.getDeletedTags().first(emptyList()))
                    .flatMapCompletable { tagRepository.deleteTagsFromCloud(it) }
                    .andThen(tagRepository.getAllTagsFromCloud())
                    .flatMapCompletable { tagRepository.insertTag(it) }
                    .onErrorResumeNext { Completable.error(SyncTagsException()) }

    private fun syncNoteTags(email: String): Completable =
            tagRepository.getAllNoteTags()
                    .first(emptyList())
                    .map { noteTags -> noteTags.filter { !it.isSync(email) } }
                    .map { noteTags -> noteTags.apply { forEach { it.syncWith.add(email) } } }
                    .flatMapCompletable { noteTags ->
                        Completable.concat(listOf(
                                tagRepository.saveNoteTagsInCloud(noteTags),
                                tagRepository.updateNoteTagsSync(noteTags)
                        ))
                    }
                    .andThen(tagRepository.getDeletedNoteTags().first(emptyList()))
                    .flatMapCompletable { tagRepository.deleteNoteTagsFromCloud(it) }
                    .andThen(tagRepository.getAllNoteTagsFromCloud())
                    .flatMapCompletable { tagRepository.insertNoteTag(it) }
                    .onErrorResumeNext { Completable.error(SyncNoteTagsException()) }
}