/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.delete.image

import com.furianrt.mydiary.domain.delete.DeleteImagesUseCase
import com.furianrt.mydiary.utils.MyRxUtils
import javax.inject.Inject

class DeleteImagePresenter @Inject constructor(
        private val deleteImagesUseCase: DeleteImagesUseCase,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : DeleteImageContract.Presenter() {

    override fun onButtonDeleteClick(imageNames: List<String>) {
        addDisposable(deleteImagesUseCase(imageNames)
                .observeOn(scheduler.ui())
                .subscribe { view?.closeView() })
    }

    override fun onButtonCancelClick() {
        view?.closeView()
    }
}