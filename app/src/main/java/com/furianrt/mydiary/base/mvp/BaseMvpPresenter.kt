package com.furianrt.mydiary.base.mvp

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