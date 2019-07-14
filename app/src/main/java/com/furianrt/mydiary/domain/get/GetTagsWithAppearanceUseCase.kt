package com.furianrt.mydiary.domain.get

import com.furianrt.mydiary.data.model.MyNoteAppearance
import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.data.model.pojo.TagsAndAppearance
import com.furianrt.mydiary.data.repository.appearance.AppearanceRepository
import com.furianrt.mydiary.data.repository.tag.TagRepository
import io.reactivex.Flowable
import io.reactivex.functions.BiFunction
import javax.inject.Inject

class GetTagsWithAppearanceUseCase @Inject constructor(
        private val tagRepository: TagRepository,
        private val appearanceRepository: AppearanceRepository
) {

    fun invoke(noteId: String): Flowable<TagsAndAppearance> =
            Flowable.combineLatest(tagRepository.getTagsForNote(noteId),
                    appearanceRepository.getNoteAppearance(noteId),
                    BiFunction<List<MyTag>, MyNoteAppearance, TagsAndAppearance> { tags, appearance ->
                        TagsAndAppearance(tags, appearance)
                    })
}