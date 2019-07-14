package com.furianrt.mydiary.usecase.update

import com.furianrt.mydiary.data.model.MyCategory
import com.furianrt.mydiary.data.repository.category.CategoryRepository
import io.reactivex.Completable
import javax.inject.Inject

class UpdateCategoryUseCase @Inject constructor(
        private val categoryRepository: CategoryRepository
) {

    fun invoke(category: MyCategory): Completable = categoryRepository.updateCategory(category)

}