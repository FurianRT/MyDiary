package com.furianrt.mydiary.view.dialogs.categories.fragments.edit

import com.furianrt.mydiary.data.model.MyCategory
import com.furianrt.mydiary.usecase.update.UpdateCategoryUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class CategoryEditPresenter @Inject constructor(
        private val updateCategory: UpdateCategoryUseCase
) : CategoryEditContract.Presenter() {

    private lateinit var mCategory: MyCategory

    override fun init(category: MyCategory) {
        mCategory = category
    }

    override fun attachView(view: CategoryEditContract.MvpView) {
        super.attachView(view)
        view.showCategory(mCategory)
    }

    override fun onButtonDoneClick(categoryName: String, categoryColor: Int) {
        if (categoryName.isEmpty()) {
            view?.showErrorEmptyName()
        } else {
            mCategory.name = categoryName
            mCategory.color = categoryColor
            addDisposable(updateCategory.invoke(mCategory)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { view?.close() })
        }
    }

    override fun onButtonCancelClick() {
        view?.close()
    }
}