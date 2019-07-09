package com.furianrt.mydiary.dialogs.tags.fragments.delete

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyTag
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class TagDeletePresenter @Inject constructor(
        private val dataManager: DataManager
) : TagDeleteContract.Presenter() {

    override fun onButtonDeleteClick(tag: MyTag) {
        addDisposable(dataManager.deleteTag(tag)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.closeView() })
    }

    override fun onButtonCancelClick() {
        view?.closeView()
    }
}