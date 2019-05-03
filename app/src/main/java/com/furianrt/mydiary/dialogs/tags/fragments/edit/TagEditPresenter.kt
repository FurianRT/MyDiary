package com.furianrt.mydiary.dialogs.tags.fragments.edit

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyTag
import io.reactivex.android.schedulers.AndroidSchedulers

class TagEditPresenter(
        private val dataManager: DataManager
) : TagEditContract.Presenter() {

    private class InvalidTagNameException : Throwable()

    override fun onButtonCloseClick() {
        view?.closeView()
    }

    override fun onButtonConfirmClick(tag: MyTag, newName: String) {
        if (newName.isBlank()) {
            view?.showErrorEmptyTagName()
        } else {
            addDisposable(dataManager.getAllTags()
                    .first(emptyList())
                    .flatMapCompletable { tags ->
                        if (tags.find { it.name == newName } != null) {
                            throw InvalidTagNameException()
                        } else {
                            dataManager.insertTag(tag.apply { name = newName })
                        }
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        view?.closeView()
                    }, {
                        if (it is InvalidTagNameException) {
                            view?.showErrorExistingTagName()
                        } else {
                            it.printStackTrace()
                        }
                    }))
        }
    }
}