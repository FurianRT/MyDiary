/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.get

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