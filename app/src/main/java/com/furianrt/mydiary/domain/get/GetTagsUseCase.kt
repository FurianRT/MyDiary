package com.furianrt.mydiary.domain.get

import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.data.repository.tag.TagRepository
import io.reactivex.Flowable
import javax.inject.Inject

class GetTagsUseCase @Inject constructor(
        private val tagRepository: TagRepository
) {

    fun invoke(noteId: String): Flowable<List<MyTag>> = tagRepository.getTagsForNote(noteId)

    fun invoke(): Flowable<List<MyTag>> = tagRepository.getAllTags()
}