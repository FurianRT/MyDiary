package com.furianrt.mydiary.usecase.save

import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.data.repository.tag.TagRepository
import com.furianrt.mydiary.utils.generateUniqueId
import io.reactivex.Single
import javax.inject.Inject

class SaveTagUseCase @Inject constructor(
        private val tagRepository: TagRepository
) {

    class InvalidTagNameException : Throwable()

    fun invoke(name: String): Single<String> {
        val id = generateUniqueId()
        return tagRepository.getAllTags()
                .first(emptyList())
                .flatMapCompletable { tags ->
                    if (tags.find { it.name == name } != null) {
                        throw InvalidTagNameException()
                    } else {
                        tagRepository.insertTag(MyTag(id, name))
                    }
                }
                .andThen(Single.just(id))
    }
}