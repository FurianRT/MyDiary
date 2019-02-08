package com.furianrt.mydiary

import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class BasePresenter<T : BaseView> {

    protected var view: T? = null

    private val mCompositeDisposable = CompositeDisposable()

    fun addDisposable(disposable: Disposable) {
        mCompositeDisposable.add(disposable)
    }

    fun attachView(view: T) {
        this.view = view
    }

    fun detachView() {
        mCompositeDisposable.clear()
        view = null
    }
}