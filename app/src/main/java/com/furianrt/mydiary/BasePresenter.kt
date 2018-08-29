package com.furianrt.mydiary

interface BasePresenter<in T : BaseView> {

    fun attachView(view: T)

    fun detachView()
}