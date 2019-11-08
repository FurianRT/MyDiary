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

import com.furianrt.mydiary.model.gateway.tag.TagGateway
import io.reactivex.Completable
import javax.inject.Inject

class SyncTagsUseCase @Inject constructor(
        private val tagGateway: TagGateway
) {

    class SyncTagsException : Throwable()
    class SyncNoteTagsException : Throwable()

    fun invoke(email: String): Completable =
            Completable.concat(listOf(syncTags(email), syncNoteTags(email)))

    private fun syncTags(email: String): Completable =
            tagGateway.getAllTags()
                    .first(emptyList())
                    .map { tags -> tags.filter { !it.isSync(email) } }
                    .map { tags -> tags.apply { forEach { it.syncWith.add(email) } } }
                    .flatMapCompletable { tags ->
                        Completable.concat(listOf(
                                tagGateway.saveTagsInCloud(tags),
                                tagGateway.updateTagsSync(tags)
                        ))
                    }
                    .andThen(tagGateway.getDeletedTags().first(emptyList()))
                    .flatMapCompletable { tagGateway.deleteTagsFromCloud(it) }
                    .andThen(tagGateway.getAllTagsFromCloud())
                    .flatMapCompletable { tagGateway.insertTag(it) }
                    .onErrorResumeNext { error ->
                        error.printStackTrace()
                        Completable.error(SyncTagsException())
                    }

    private fun syncNoteTags(email: String): Completable =
            tagGateway.getAllNoteTags()
                    .first(emptyList())
                    .map { noteTags -> noteTags.filter { !it.isSync(email) } }
                    .map { noteTags -> noteTags.apply { forEach { it.syncWith.add(email) } } }
                    .flatMapCompletable { noteTags ->
                        Completable.concat(listOf(
                                tagGateway.saveNoteTagsInCloud(noteTags),
                                tagGateway.updateNoteTagsSync(noteTags)
                        ))
                    }
                    .andThen(tagGateway.getDeletedNoteTags().first(emptyList()))
                    .flatMapCompletable { tagGateway.deleteNoteTagsFromCloud(it) }
                    .andThen(tagGateway.getAllNoteTagsFromCloud())
                    .flatMapCompletable { tagGateway.insertNoteTag(it) }
                    .onErrorResumeNext { error ->
                        error.printStackTrace()
                        Completable.error(SyncNoteTagsException())
                    }
}