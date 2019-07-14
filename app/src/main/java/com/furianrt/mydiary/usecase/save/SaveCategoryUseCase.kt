package com.furianrt.mydiary.usecase.save

import com.furianrt.mydiary.data.model.MyCategory
import com.furianrt.mydiary.data.repository.category.CategoryRepository
import com.furianrt.mydiary.utils.generateUniqueId
import io.reactivex.Single
import javax.inject.Inject

class SaveCategoryUseCase @Inject constructor(
        private val categoryRepository: CategoryRepository
) {

    fun invoke(name: String, color: Int): Single<String> {
        val id = generateUniqueId()
        return categoryRepository.insertCategory(MyCategory(id, name, color))
                .andThen(Single.just(id))
    }
}