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
import com.furianrt.mydiary.model.entity.MyNoteWithImages
import com.furianrt.mydiary.model.gateway.image.ImageGateway
import com.furianrt.mydiary.model.gateway.note.NoteGateway
import com.google.common.base.Optional
import io.reactivex.Flowable
import io.reactivex.functions.Function3
import javax.inject.Inject

class GetNotesWithImagesUseCase @Inject constructor(
        private val noteGateway: NoteGateway,
        private val imageGateway: ImageGateway
) {

    operator fun invoke(): Flowable<List<MyNoteWithImages>> =
            Flowable.combineLatest(
                    noteGateway.getAllNotesWithImages(),
                    imageGateway.getAllImages(),
                    imageGateway.getDeletedImages(),
                    Function3<List<MyNoteWithImages>, List<MyImage>, List<MyImage>, List<MyNoteWithImages>>
                    { notes, images, deletedImages ->
                        notes.map { note ->
                            note.images = images.filter { it.noteId == note.note.id }
                            note.deletedImages = deletedImages.filter { it.noteId == note.note.id }
                            return@map note
                        }
                    }
            ).map { notes ->
                if (noteGateway.isSortDesc()) {
                    notes.sortedByDescending { it.note.time }
                } else {
                    notes.sortedBy { it.note.time }
                }
            }

    operator fun invoke(noteId: String): Flowable<Optional<MyNoteWithImages>> =
            Flowable.combineLatest(
                    noteGateway.getNoteWithImagesAsList(noteId),
                    imageGateway.getImagesForNote(noteId),
                    imageGateway.getDeletedImages(noteId),
                    Function3<List<MyNoteWithImages>, List<MyImage>, List<MyImage>, Optional<MyNoteWithImages>>
                    { notes, images, deletedImages ->
                        Optional.fromNullable(notes
                                .map { note ->
                                    note.images = images
                                    note.deletedImages = deletedImages
                                    return@map note
                                }
                                .find { it.note.id == noteId })
                    }
            )
}