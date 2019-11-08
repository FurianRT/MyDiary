/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain

import com.furianrt.mydiary.model.entity.MyNote
import com.furianrt.mydiary.model.gateway.device.DeviceGateway
import com.furianrt.mydiary.model.gateway.general.GeneralGateway
import com.furianrt.mydiary.domain.get.GetCategoriesUseCase
import com.furianrt.mydiary.domain.get.GetTagsUseCase
import com.furianrt.mydiary.domain.save.AddTagToNoteUseCase
import com.furianrt.mydiary.domain.save.SaveImagesUseCase
import com.furianrt.mydiary.domain.save.SaveNotesUseCase
import com.furianrt.mydiary.utils.generateUniqueId
import io.reactivex.Completable
import io.reactivex.Observable
import org.joda.time.DateTime
import javax.inject.Inject

class CreateTutorialNoteUseCase @Inject constructor(
        private val deviceGateway: DeviceGateway,
        private val generalGateway: GeneralGateway,
        private val saveNotes: SaveNotesUseCase,
        private val saveImages: SaveImagesUseCase,
        private val addTagToNote: AddTagToNoteUseCase,
        private val getCategories: GetCategoriesUseCase,
        private val getTags: GetTagsUseCase
) {

    fun invoke(): Completable {
        return if (generalGateway.isNeedDefaultValues()) {
            generalGateway.setNeedDefaultValues(false)
            val noteId = generateUniqueId()
            saveImages.invoke(noteId, deviceGateway.getTutorialNoteBitmap())
                    .andThen(getCategories.invoke().firstOrError())
                    .map { it.first() }
                    .flatMapCompletable { category ->
                        val note = MyNote(
                                id = noteId,
                                title = deviceGateway.getTutorialNoteTitle(),
                                content = deviceGateway.getTutorialNoteContent(),
                                time = DateTime.now().millis,
                                moodId = deviceGateway.getTutorialNoteMoodId(),
                                categoryId = category.id,
                                creationTime = DateTime.now().millis
                        )
                        saveNotes.invoke(note)
                    }
                    .andThen(getTags.invoke().firstOrError())
                    .flatMapObservable { Observable.fromIterable(it) }
                    .flatMapSingle { addTagToNote.invoke(noteId, it.id).toSingleDefault(true) }
                    .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                    .ignoreElement()
                    .onErrorComplete()
        } else {
            Completable.complete()
        }
    }
}