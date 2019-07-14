package com.furianrt.mydiary.usecase.get

import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.data.repository.image.ImageRepository
import io.reactivex.Flowable
import javax.inject.Inject

class GetImagesUseCase @Inject constructor(
        private val imageRepository: ImageRepository
) {

    fun invoke(noteId: String): Flowable<List<MyImage>> =
            imageRepository.getImagesForNote(noteId)
}