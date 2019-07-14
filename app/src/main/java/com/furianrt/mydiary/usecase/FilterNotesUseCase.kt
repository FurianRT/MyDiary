package com.furianrt.mydiary.usecase

import com.furianrt.mydiary.data.model.MyCategory
import com.furianrt.mydiary.data.model.MyLocation
import com.furianrt.mydiary.data.model.MyNoteWithProp
import com.furianrt.mydiary.data.model.MyTag
import org.joda.time.LocalDate
import javax.inject.Inject

class FilterNotesUseCase @Inject constructor() {

    fun invoke(
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
                it.note.title.toLowerCase().contains(query)
                        || it.note.content.toLowerCase().contains(query)
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