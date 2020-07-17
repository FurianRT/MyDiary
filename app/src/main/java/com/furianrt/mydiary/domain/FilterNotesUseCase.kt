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

import com.furianrt.mydiary.model.entity.MyCategory
import com.furianrt.mydiary.model.entity.MyLocation
import com.furianrt.mydiary.model.entity.MyNoteWithProp
import com.furianrt.mydiary.model.entity.MyTag
import org.joda.time.LocalDate
import java.util.*
import javax.inject.Inject

class FilterNotesUseCase @Inject constructor() {

    operator fun invoke(
            notes: List<MyNoteWithProp>,
            tagIds: Set<String>,
            categoryIds: Set<String>,
            moodIds: Set<Int>,
            locationNames: Set<String>,
            startDate: LocalDate?,
            endDate: LocalDate?,
            query: String
    ): List<MyNoteWithProp> = notes.asSequence()
            .filter {
                it.note.title.toLowerCase(Locale.getDefault()).contains(query)
                        || it.note.content.toLowerCase(Locale.getDefault()).contains(query)
            }
            .filter { note ->
                if (tagIds.isEmpty()) {
                    return@filter true
                }
                if (note.tags.isEmpty()) {
                    return@filter tagIds.contains(MyTag.TABLE_NAME)
                }
                if (tagIds.none { it != MyTag.TABLE_NAME }) {
                    return@filter false
                }
                tagIds.filter { it != MyTag.TABLE_NAME }
                        .forEach { tagId ->
                            if (note.tags.find { it.id == tagId } == null) {
                                return@filter false
                            }
                        }
                return@filter true
            }
            .filter { note ->
                categoryIds.isEmpty()
                        || categoryIds.find { it == note.category?.id ?: MyCategory.TABLE_NAME } != null
            }
            .filter { note ->
                moodIds.isEmpty()
                        || moodIds.find { it == note.mood?.id ?: -1 } != null
            }
            .filter { note ->
                if (locationNames.isEmpty()) {
                    return@filter true
                }
                if (note.locations.isEmpty()) {
                    return@filter locationNames.contains(MyLocation.TABLE_NAME)
                }
                val names = locationNames.filter { it != MyLocation.TABLE_NAME }
                if (names.isEmpty()) {
                    return@filter false
                }
                locationNames.filter { it != MyLocation.TABLE_NAME }
                        .forEach { locationName ->
                            if (note.locations.find { it.name == locationName } == null) {
                                return@filter false
                            }
                        }
                return@filter true
            }
            .filter { note ->
                when {
                    startDate == null && endDate == null -> true
                    startDate != null && endDate == null -> LocalDate(note.note.time) == startDate
                    else -> {
                        val noteDate = LocalDate(note.note.time)
                        noteDate >= startDate && noteDate <= endDate
                    }
                }
            }
            .toList()
}