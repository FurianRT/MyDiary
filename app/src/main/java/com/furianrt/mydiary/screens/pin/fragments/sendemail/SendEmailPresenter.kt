package com.furianrt.mydiary.screens.pin.fragments.sendemail

import com.furianrt.mydiary.data.DataManager
import io.reactivex.android.schedulers.AndroidSchedulers

class SendEmailPresenter(
        private val dataManager: DataManager
) : SendEmailContract.Presenter() {

    override fun onButtonSendClick() {
        view?.showLoading()
        addDisposable(dataManager.sendPinResetEmail()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view?.hideLoading()
                    view?.showDoneView()
                }, {
                    it.printStackTrace()
                    view?.hideLoading()
                    view?.showErrorMessageSend()
                }))
    }

    override fun onButtonCancelClick() {
        view?.closeView()
    }
}