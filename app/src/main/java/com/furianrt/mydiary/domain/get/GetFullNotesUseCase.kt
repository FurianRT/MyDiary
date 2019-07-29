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

import com.furianrt.mydiary.data.model.*
import com.furianrt.mydiary.data.repository.image.ImageRepository
import com.furianrt.mydiary.data.repository.location.LocationRepository
import com.furianrt.mydiary.data.repository.note.NoteRepository
import com.furianrt.mydiary.data.repository.tag.TagRepository
import com.google.common.base.Optional
import io.reactivex.Flowable
import io.reactivex.functions.Function6
import javax.inject.Inject

//Следит почти за всеми таблицами. Использовать только когда дейстительно необходимо!
class GetFullNotesUseCase @Inject constructor(
        private val noteRepository: NoteRepository,
        private val tagRepository: TagRepository,
        private val imageRepository: ImageRepository,
        private val locationRepository: LocationRepository
) {

    fun invoke(): Flowable<List<MyNoteWithProp>> =
            getAllNotes().map { notes ->
                if (noteRepository.isSortDesc()) {
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
                    noteRepository.getAllNotesWithProp(),
                    tagRepository.getAllNoteTags(),
                    tagRepository.getAllTags(),
                    imageRepository.getAllImages(),
                    locationRepository.getAllNoteLocations(),
                    locationRepository.getAllDbLocations(),
                    Function6<List<MyNoteWithProp>, List<NoteTag>, List<MyTag>, List<MyImage>,
                            List<NoteLocation>, List<MyLocation>, List<MyNoteWithProp>>
                    { notes, noteTags, tags, images, noteLocations, locations ->
                        notes.map { note ->
                            val noteTagsForNote = noteTags.filter { it.noteId == note.note.id }
                            note.tags = tags.filter { tag ->
                                noteTagsForNote.find { it.tagId == tag.id } != null
                            }

                            note.images = images.filter { it.noteId == note.note.id }

                            val noteLocationsForNote = noteLocations.filter { it.noteId == note.note.id }
                            note.locations = locations.filter { location ->
                                noteLocationsForNote.find { it.locationId == location.id } != null
                            }

                            return@map note
                        }
                    }
            )
}