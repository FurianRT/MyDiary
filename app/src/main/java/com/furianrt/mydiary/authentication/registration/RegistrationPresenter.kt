package com.furianrt.mydiary.authentication.registration

import com.furianrt.mydiary.data.DataManager

class RegistrationPresenter(
        private val mDataManager: DataManager
) : RegistrationContract.Presenter() {

    override fun onButtonCancelClick() {
        view?.close()
    }
}