package com.furianrt.mydiary.dialogs.categories.fragments.delete

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyCategory
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class CategoryDeletePresenter @Inject constructor(
        private val dataManager: DataManager
) : CategoryDeleteContract.Presenter() {

    override fun onButtonDeleteClick(category: MyCategory) {
        addDisposable(dataManager.deleteCategory(category)
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe { view?.closeView() })
    }

    override fun onButtonCancelClick() {
        view?.closeView()
    }
}