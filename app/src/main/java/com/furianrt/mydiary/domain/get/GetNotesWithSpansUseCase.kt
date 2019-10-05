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

import com.furianrt.mydiary.data.entity.MyNote
import com.furianrt.mydiary.data.entity.MyNoteWithSpans
import com.furianrt.mydiary.data.entity.MyTextSpan
import com.furianrt.mydiary.data.repository.note.NoteRepository
import com.furianrt.mydiary.data.repository.span.SpanRepository
import com.google.common.base.Optional
import io.reactivex.Flowable
import io.reactivex.functions.BiFunction
import io.reactivex.functions.Function3
import javax.inject.Inject

class GetNotesWithSpansUseCase @Inject constructor(
        private val noteRepository: NoteRepository,
        private val spanRepository: SpanRepository
) {

    fun invoke(): Flowable<List<MyNoteWithSpans>> =
            getAllNotes().map { notes ->
                if (noteRepository.isSortDesc()) {
                    notes.sortedByDescending { it.note.time }
                } else {
                    notes.sortedBy { it.note.time }
                }
            }

    fun invoke(noteId: String): Flowable<Optional<MyNoteWithSpans>> =
            getAllNotes().map { note ->
                Optional.fromNullable(note.find { it.note.id == noteId })
            }

    private fun getAllNotes(): Flowable<List<MyNoteWithSpans>> =
            Flowable.combineLatest(
                    noteRepository.getAllNotes(),
                    spanRepository.getAllTextSpans(),
                    spanRepository.getDeletedTextSpans(),
                    Function3<List<MyNote>, List<MyTextSpan>, List<MyTextSpan>, List<MyNoteWithSpans>>
                    { notes, spans, deletedSpans ->
                        notes.map { note ->
                            MyNoteWithSpans(
                                    note,
                                    spans.filter { it.noteId == note.id },
                                    deletedSpans.filter { it.noteId == note.id }
                            )
                        }
                    }
            )
}