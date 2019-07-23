package com.furianrt.mydiary.domain.get

import com.furianrt.mydiary.data.model.MyCategory
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.repository.category.CategoryRepository
import com.furianrt.mydiary.data.repository.note.NoteRepository
import com.google.common.base.Optional
import io.reactivex.Flowable
import io.reactivex.functions.BiFunction
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
        private val categoryRepository: CategoryRepository,
        private val noteRepository: NoteRepository
) {

    fun invoke(): Flowable<List<MyCategory>> = categoryRepository.getAllCategories()

    fun invoke(noteId: String): Flowable<Optional<MyCategory>> =
            Flowable.combineLatest(noteRepository.getNoteAsList(noteId),
                    categoryRepository.getAllCategories(),
                    BiFunction<List<MyNote>, List<MyCategory>, Optional<MyCategory>> { note, categories ->
                        if (categories.isEmpty() || note.isEmpty()) {
                            Optional.absent()
                        } else {
                            Optional.fromNullable(categories.find { it.id == note.first().categoryId })
                        }
                    })
}