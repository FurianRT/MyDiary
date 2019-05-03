package com.furianrt.mydiary.dialogs.tags.fragments.add

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.data.model.NoteTag
import com.furianrt.mydiary.utils.generateUniqueId
import io.reactivex.android.schedulers.AndroidSchedulers

class TagAddPresenter(
        private val dataManager: DataManager
) : TagAddContract.Presenter() {

    private class InvalidTagNameException : Throwable()

    override fun onButtonAddClick(noteId: String, name: String) {
        if (name.isBlank()) {
            view?.showErrorEmptyTagName()
        } else {
            val tag = MyTag(generateUniqueId(), name)
            addDisposable(dataManager.getAllTags()
                    .first(emptyList())
                    .flatMapCompletable { tags ->
                        if (tags.find { it.name == tag.name } != null) {
                            throw InvalidTagNameException()
                        } else {
                            dataManager.insertTag(tag)
                        }
                    }
                    .andThen(dataManager.insertNoteTag(NoteTag(noteId, tag.id)))
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

    override fun onButtonCloseClick() {
        view?.closeView()
    }
}