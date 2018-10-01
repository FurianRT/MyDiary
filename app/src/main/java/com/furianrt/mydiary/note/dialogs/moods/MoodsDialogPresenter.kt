package com.furianrt.mydiary.note.dialogs.moods

import android.util.Log
import com.furianrt.mydiary.LOG_TAG
import com.furianrt.mydiary.data.DataManager
import io.reactivex.disposables.CompositeDisposable

class MoodsDialogPresenter(private val mDataManager: DataManager) : MoodsDialogContract.Presenter {

    private var mView: MoodsDialogContract.View? = null
    private val mCompositeDisposable = CompositeDisposable()

    override fun attachView(view: MoodsDialogContract.View) {
        mView = view
    }

    override fun detachView() {
        mCompositeDisposable.clear()
        mView = null
    }

    override fun onViewCreate() {
        Log.e(LOG_TAG, "onViewCreated")

        val disposable = mDataManager.getAllMoods()
                .subscribe { moods -> mView?.showMoods(moods) }

        mCompositeDisposable.add(disposable)
    }
}