/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.base

import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

abstract class BasePresenter<T : BaseView> {

    protected var view: T? = null

    private val mCompositeDisposable = CompositeDisposable()

    protected fun addDisposable(disposable: Disposable) {
        mCompositeDisposable.add(disposable)
    }

    open fun attachView(view: T) {
        this.view = view
    }

    open fun detachView() {
        mCompositeDisposable.clear()
        view = null
    }
}