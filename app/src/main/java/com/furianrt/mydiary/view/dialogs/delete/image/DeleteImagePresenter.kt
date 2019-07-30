/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.dialogs.delete.image

import com.furianrt.mydiary.domain.delete.DeleteImagesUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class DeleteImagePresenter @Inject constructor(
        private val deleteImages: DeleteImagesUseCase
) : DeleteImageContract.Presenter() {

    override fun onButtonDeleteClick(imageNames: List<String>) {
        addDisposable(deleteImages.invoke(imageNames)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.closeView() })
    }

    override fun onButtonCancelClick() {
        view?.closeView()
    }
}