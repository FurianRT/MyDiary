package com.furianrt.mydiary.domain.update

import com.furianrt.mydiary.data.model.MyCategory
import com.furianrt.mydiary.data.repository.category.CategoryRepository
import com.furianrt.mydiary.data.repository.note.NoteRepository
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

class UpdateCategoryUseCase @Inject constructor(
        private val categoryRepository: CategoryRepository,
        private val noteRepository: NoteRepository
) {

    fun invoke(category: MyCategory): Completable =
            categoryRepository.updateCategory(category.apply { syncWith.clear() })
                    .andThen(noteRepository.getAllNotes())
                    .first(emptyList())
                    .flatMapObservable { Observable.fromIterable(it) }
                    .filter { it.categoryId == category.id }
                    .map { it.apply { it.syncWith.clear() } }
                    .flatMapSingle { noteRepository.updateNote(it).toSingleDefault(true) }
                    .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                    .ignoreElement()
}