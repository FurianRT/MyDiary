/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.get

import com.furianrt.mydiary.model.entity.MyImage
import com.furianrt.mydiary.model.entity.MyNote
import com.furianrt.mydiary.model.entity.MyNoteAppearance
import com.furianrt.mydiary.model.entity.MyNoteWithImages
import com.furianrt.mydiary.model.gateway.image.ImageGateway
import com.google.common.base.Optional
import io.reactivex.Flowable
import io.reactivex.functions.Function4
import javax.inject.Inject

class GetNotesWithImagesUseCase @Inject constructor(
        private val getNotesUseCase: GetNotesUseCase,
        private val imageGateway: ImageGateway,
        private val getAppearanceUseCase: GetAppearanceUseCase,
        private val getImagesUseCase: GetImagesUseCase
) {

    operator fun invoke(): Flowable<List<MyNoteWithImages>> =
            Flowable.combineLatest(
                    getNotesUseCase(),
                    getImagesUseCase(),
                    imageGateway.getDeletedImages(),
                    getAppearanceUseCase(),
                    Function4<List<MyNote>, List<MyImage>, List<MyImage>, List<MyNoteAppearance>, List<MyNoteWithImages>>
                    { notes, images, deletedImages, appearances ->
                        notes.map { note ->
                            MyNoteWithImages(
                                    note,
                                    appearances.find { it.appearanceId == note.id },
                                    images.filter { it.noteId == note.id },
                                    deletedImages.filter { it.noteId == note.id }
                            )
                        }
                    }
            )

    operator fun invoke(noteId: String): Flowable<Optional<MyNoteWithImages>> =
            Flowable.combineLatest(
                    getNotesUseCase(noteId),
                    getImagesUseCase(noteId),
                    imageGateway.getDeletedImages(noteId),
                    getAppearanceUseCase(noteId),
                    Function4<Optional<MyNote>, List<MyImage>, List<MyImage>, Optional<MyNoteAppearance>, Optional<MyNoteWithImages>>
                    { note, images, deletedImages, appearance ->
                        if (note.isPresent) {
                            Optional.of(MyNoteWithImages(
                                    note.get(),
                                    appearance.orNull(),
                                    images,
                                    deletedImages
                            ))
                        } else {
                            Optional.absent()
                        }
                    }
            )
}