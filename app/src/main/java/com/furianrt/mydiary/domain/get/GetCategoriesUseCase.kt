package com.furianrt.mydiary.domain.get

import com.furianrt.mydiary.data.model.MyCategory
import com.furianrt.mydiary.data.repository.category.CategoryRepository
import io.reactivex.Flowable
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
        private val categoryRepository: CategoryRepository
) {

    fun invoke(): Flowable<List<MyCategory>> = categoryRepository.getAllCategories()
}