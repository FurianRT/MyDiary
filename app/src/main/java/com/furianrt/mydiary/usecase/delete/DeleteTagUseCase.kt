package com.furianrt.mydiary.usecase.delete

import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.data.repository.tag.TagRepository
import io.reactivex.Completable
import javax.inject.Inject

class DeleteTagUseCase @Inject constructor(
        private val tagRepository: TagRepository
) {

    fun invoke(tag: MyTag): Completable = tagRepository.deleteTag(tag)
}