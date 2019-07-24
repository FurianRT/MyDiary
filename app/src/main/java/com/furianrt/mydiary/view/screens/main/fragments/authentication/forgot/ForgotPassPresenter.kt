package com.furianrt.mydiary.view.screens.main.fragments.authentication.forgot

import com.furianrt.mydiary.domain.send.SendPassResetEmailUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class ForgotPassPresenter @Inject constructor(
        private val sendPassResetEmail: SendPassResetEmailUseCase
) : ForgotPassContract.Presenter() {

    override fun onButtonCancelClick() {
        view?.close()
    }

    override fun onButtonSendClick(email: String) {
        view?.showLoading()
        addDisposable(sendPassResetEmail.invoke(email)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view?.hideLoading()
                    view?.showEmailSent()
                }, { error ->
                    view?.hideLoading()
                    when (error) {
                        is SendPassResetEmailUseCase.EmptyEmailException ->
                            view?.showErrorEmptyEmail()
                        is SendPassResetEmailUseCase.EmailFormatException ->
                            view?.showErrorEmailFormat()
                        else -> {
                            error.printStackTrace()
                            view?.showErrorNetworkConnection()
                        }
                    }
                }))
    }
}