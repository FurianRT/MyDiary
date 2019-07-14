package com.furianrt.mydiary.view.screens.pin.fragments.sendemail

import com.furianrt.mydiary.usecase.send.SendPinResetEmailUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class SendEmailPresenter @Inject constructor(
        private val sendPinResetEmail: SendPinResetEmailUseCase
) : SendEmailContract.Presenter() {

    override fun onButtonSendClick() {
        view?.showLoading()
        addDisposable(sendPinResetEmail.invoke()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view?.hideLoading()
                    view?.showDoneView()
                }, { error ->
                    error.printStackTrace()
                    view?.hideLoading()
                    view?.showErrorMessageSend()
                }))
    }

    override fun onButtonCancelClick() {
        view?.closeView()
    }
}