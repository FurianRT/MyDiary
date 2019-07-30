/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.base.mvp

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BaseMvpPresenter<T : BaseMvpView> {

    protected var view: T? = null

    private val mCompositeDisposable = CompositeDisposable()

    fun addDisposable(disposable: Disposable) {
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