package com.furianrt.mydiary.usecase.update

import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.data.repository.tag.TagRepository
import io.reactivex.Completable
import javax.inject.Inject

class UpdateTagUseCase @Inject constructor(
        private val tagRepository: TagRepository
) {

    class InvalidTagNameException : Throwable()

    fun invoke(tag: MyTag): Completable =
            tagRepository.getAllTags()
                    .first(emptyList())
                    .flatMapCompletable { tags ->
                        if (tags.find { it.name == tag.name } != null) {
                            throw InvalidTagNameException()
                        } else {
                            tagRepository.updateTag(tag.apply { syncWith.clear() })
                        }
                    }
}