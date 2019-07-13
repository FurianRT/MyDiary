package com.furianrt.mydiary.dialogs.categories.fragments.list

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyCategory
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class CategoryListPresenter @Inject constructor(
        private val dataManager: DataManager
) : CategoryListContract.Presenter() {

    override fun onButtonAddCategoryClick() {
        view?.showViewAddCategory()
    }

    override fun onButtonDeleteCategoryClick(category: MyCategory) {
        view?.showDeleteCategoryView(category)
    }

    override fun onButtonEditCategoryClick(category: MyCategory) {
        view?.showEditView(category)
    }

    override fun onViewStart() {
        addDisposable(dataManager.getAllCategories()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { categories -> view?.showCategories(categories) })
    }

    override fun onCategoryClick(category: MyCategory, noteIds: List<String>) {
        addDisposable(Observable.fromIterable(noteIds)
                .flatMapSingle { dataManager.getNote(it) }
                .flatMapSingle {
                    dataManager.updateNote(it.apply { categoryId = category.id })
                            .toSingleDefault(true)
                }
                .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                .ignoreElement()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.close() })
    }

    override fun onButtonNoCategoryClick(noteIds: List<String>) {
        addDisposable(Observable.fromIterable(noteIds)
                .flatMapSingle { dataManager.getNote(it) }
                .flatMapSingle {
                    dataManager.updateNote(it.apply { categoryId = "" })
                            .toSingleDefault(true)
                }
                .collectInto(mutableListOf<Boolean>()) { l, i -> l.add(i) }
                .ignoreElement()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.close() })
    }

    override fun onButtonCloseClick() {
        view?.close()
    }
}