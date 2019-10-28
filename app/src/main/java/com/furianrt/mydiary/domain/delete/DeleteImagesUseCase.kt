/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.domain.delete

import com.furianrt.mydiary.model.repository.image.ImageRepository
import io.reactivex.Completable
import javax.inject.Inject

class DeleteImagesUseCase @Inject constructor(
        private val imageRepository: ImageRepository
) {

    fun invoke(imageNames: List<String>): Completable =
            imageRepository.deleteImage(imageNames)
}