package com.furianrt.mydiary

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BasePresenter<in T : BaseView> {

    private val mCompositeDisposable = CompositeDisposable()

    fun addDisposable(disposable: Disposable) {
        mCompositeDisposable.add(disposable)
    }

    abstract fun attachView(view: T)

    open fun detachView() {
        mCompositeDisposable.clear()
    }
}