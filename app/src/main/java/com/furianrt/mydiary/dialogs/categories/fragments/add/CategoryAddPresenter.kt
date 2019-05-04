package com.furianrt.mydiary.dialogs.categories.fragments.add

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyCategory

class CategoryAddPresenter(
        private val dataManager: DataManager
) : CategoryAddContract.Presenter() {

    override fun onButtonDoneClick(category: MyCategory, categoryName: String, categoryColor: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onButtonCancelClick() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}