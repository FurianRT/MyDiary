package com.furianrt.mydiary.usecase.sync

import com.furianrt.mydiary.data.repository.category.CategoryRepository
import io.reactivex.Completable
import javax.inject.Inject

class SyncCategoriesUseCase @Inject constructor(
        private val categoryRepository: CategoryRepository
) {

    class SyncCategoriesException : Throwable()

    fun invoke(email: String): Completable =
            categoryRepository.getAllCategories()
                    .first(emptyList())
                    .map { categories -> categories.filter { !it.isSync(email) } }
                    .map { categories -> categories.apply { forEach { it.syncWith.add(email) } } }
                    .flatMapCompletable { categories ->
                        Completable.concat(listOf(
                                categoryRepository.saveCategoriesInCloud(categories),
                                categoryRepository.updateCategoriesSync(categories)
                        ))
                    }
                    .andThen(categoryRepository.getDeletedCategories().first(emptyList()))
                    .flatMapCompletable { categoryRepository.deleteCategoriesFromCloud(it) }
                    .andThen(categoryRepository.getAllCategoriesFromCloud())
                    .flatMapCompletable { categoryRepository.insertCategory(it) }
                    .onErrorResumeNext { Completable.error(SyncCategoriesException()) }
}