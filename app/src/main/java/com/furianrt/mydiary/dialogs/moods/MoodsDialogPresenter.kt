package com.furianrt.mydiary.dialogs.moods

import com.furianrt.mydiary.data.DataManager
import io.reactivex.android.schedulers.AndroidSchedulers

class MoodsDialogPresenter(
        private val dataManager: DataManager
) : MoodsDialogContract.Presenter() {

    override fun onViewCreate() {
        addDisposable( dataManager.getAllMoods()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { moods -> view?.showMoods(moods) })
    }
}