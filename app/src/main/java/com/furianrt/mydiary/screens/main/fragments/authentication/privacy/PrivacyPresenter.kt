package com.furianrt.mydiary.screens.main.fragments.authentication.privacy

import com.furianrt.mydiary.data.DataManager
import io.reactivex.android.schedulers.AndroidSchedulers

class PrivacyPresenter(
        private val dataManager: DataManager
) : PrivacyContract.Presenter() {

    companion object {
        private const val PRIVACY_LINK = "https://docs.google.com/document/d/1jW6ik5s4digfAURnKo48kx_oXUryP7QOc-mFNBstxtA/edit?usp=sharing/"
    }

    override fun onButtonAcceptClick(email: String, password: String) {
        view?.showLoading()
        addDisposable(dataManager.signUp(email, password)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    view?.hideLoading()
                    view?.showMessageSuccessRegistration()
                }, {
                    view?.hideLoading()
                    it.printStackTrace()
                    view?.showErrorNetworkConnection()
                }))
    }

    override fun onButtonCancelClick() {
        view?.close()
    }

    override fun onPrivacyLinkClick() {
        view?.openLink(PRIVACY_LINK)
    }
}