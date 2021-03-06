/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.tags.fragments.delete

import com.furianrt.mydiary.model.entity.MyTag
import com.furianrt.mydiary.domain.delete.DeleteTagUseCase
import com.furianrt.mydiary.utils.MyRxUtils
import javax.inject.Inject

class TagDeletePresenter @Inject constructor(
        private val deleteTagUseCase: DeleteTagUseCase,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : TagDeleteContract.Presenter() {

    private lateinit var mTag: MyTag

    override fun init(tag: MyTag) {
        mTag = tag
    }

    override fun attachView(view: TagDeleteContract.View) {
        super.attachView(view)
        view.showTagName(mTag.name)
    }

    override fun onButtonDeleteClick() {
        addDisposable(deleteTagUseCase(mTag)
                .observeOn(scheduler.ui())
                .subscribe { view?.closeView() })
    }

    override fun onButtonCancelClick() {
        view?.closeView()
    }
}