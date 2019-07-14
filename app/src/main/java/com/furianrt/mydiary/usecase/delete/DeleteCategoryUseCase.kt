package com.furianrt.mydiary.usecase.delete

import com.furianrt.mydiary.data.model.MyCategory
import com.furianrt.mydiary.data.repository.category.CategoryRepository
import io.reactivex.Completable
import javax.inject.Inject

class DeleteCategoryUseCase @Inject constructor(
        private val categoryRepository: CategoryRepository
) {

    fun invoke(category: MyCategory): Completable =
            categoryRepository.deleteCategory(category)
}