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

import com.furianrt.mydiary.model.entity.MyNote
import com.furianrt.mydiary.model.entity.MyNoteWithSpans
import com.furianrt.mydiary.model.entity.MyTextSpan
import com.furianrt.mydiary.model.gateway.note.NoteGateway
import com.furianrt.mydiary.model.gateway.span.SpanGateway
import com.google.common.base.Optional
import io.reactivex.Flowable
import io.reactivex.functions.Function3
import javax.inject.Inject

class GetNotesWithSpansUseCase @Inject constructor(
        private val noteGateway: NoteGateway,
        private val spanGateway: SpanGateway
) {

    fun invoke(): Flowable<List<MyNoteWithSpans>> =
            getAllNotes().map { notes ->
                if (noteGateway.isSortDesc()) {
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
                    noteGateway.getAllNotes(),
                    spanGateway.getAllTextSpans(),
                    spanGateway.getDeletedTextSpans(),
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