package com.furianrt.mydiary.note.dialogs.moods

import com.furianrt.mydiary.data.DataManager

class MoodsDialogPresenter(private val mDataManager: DataManager) : MoodsDialogContract.Presenter() {

    override fun onViewCreate() {
        addDisposable( mDataManager.getAllMoods()
                .subscribe { moods -> view?.showMoods(moods) })
    }
}