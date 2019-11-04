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

import com.furianrt.mydiary.model.entity.*
import com.furianrt.mydiary.model.gateway.image.ImageGateway
import com.furianrt.mydiary.model.gateway.location.LocationGateway
import com.furianrt.mydiary.model.gateway.note.NoteGateway
import com.furianrt.mydiary.model.gateway.span.SpanGateway
import com.furianrt.mydiary.model.gateway.tag.TagGateway
import com.furianrt.mydiary.utils.MyRxUtils
import com.google.common.base.Optional
import io.reactivex.Flowable
import io.reactivex.functions.Function9
import java.util.concurrent.TimeUnit
import javax.inject.Inject

//Следит почти за всеми таблицами. Использовать только когда дейстительно необходимо!
class GetFullNotesUseCase @Inject constructor(
        private val noteGateway: NoteGateway,
        private val tagGateway: TagGateway,
        private val imageGateway: ImageGateway,
        private val locationGateway: LocationGateway,
        private val spanGateway: SpanGateway,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) {

    fun invoke(): Flowable<List<MyNoteWithProp>> =
            getAllNotes().map { notes ->
                if (noteGateway.isSortDesc()) {
                    notes.sortedByDescending { it.note.time }
                } else {
                    notes.sortedBy { it.note.time }
                }
            }

    fun invoke(noteId: String): Flowable<Optional<MyNoteWithProp>> =
            getAllNotes().map { notes ->
                Optional.fromNullable(notes.find { it.note.id == noteId })
            }

    private fun getAllNotes(): Flowable<List<MyNoteWithProp>> =
            Flowable.combineLatest(
                    noteGateway.getAllNotesWithProp(),
                    tagGateway.getAllNoteTags(),
                    tagGateway.getAllTags(),
                    imageGateway.getAllImages(),
                    locationGateway.getAllNoteLocations(),
                    locationGateway.getAllDbLocations(),
                    spanGateway.getAllTextSpans(),
                    spanGateway.getDeletedTextSpans(),
                    imageGateway.getDeletedImages(),
                    Function9<List<MyNoteWithProp>, List<NoteTag>, List<MyTag>, List<MyImage>,
                            List<NoteLocation>, List<MyLocation>, List<MyTextSpan>, List<MyTextSpan>,
                            List<MyImage>, List<MyNoteWithProp>>
                    { notes, noteTags, tags, images, noteLocations, locations, spans, deletedSpans, deletedImages ->
                        notes.map { note ->
                            val noteTagsForNote = noteTags.filter { it.noteId == note.note.id }
                            note.tags = tags.filter { tag ->
                                noteTagsForNote.find { it.tagId == tag.id } != null
                            }

                            note.images = images.filter { it.noteId == note.note.id }
                            note.deletedImages = deletedImages.filter { it.noteId == note.note.id }

                            val noteLocationsForNote = noteLocations.filter { it.noteId == note.note.id }
                            note.locations = locations.filter { location ->
                                noteLocationsForNote.find { it.locationId == location.id } != null
                            }

                            note.textSpans = spans.filter { it.noteId == note.note.id }
                            note.deletedTextSpans = deletedSpans.filter { it.noteId == note.note.id }

                            return@map note
                        }
                    }
            ).debounce(100L, TimeUnit.MILLISECONDS, scheduler.computation())
}