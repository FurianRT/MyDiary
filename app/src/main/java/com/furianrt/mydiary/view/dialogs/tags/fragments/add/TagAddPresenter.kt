package com.furianrt.mydiary.view.dialogs.tags.fragments.add

import com.furianrt.mydiary.domain.save.AddTagToNoteUseCase
import com.furianrt.mydiary.domain.save.SaveTagUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class TagAddPresenter @Inject constructor(
        private val saveTag: SaveTagUseCase,
        private val addTagToNote: AddTagToNoteUseCase
) : TagAddContract.Presenter() {

    private lateinit var mNoteId: String

    override fun init(noteId: String) {
        mNoteId = noteId
    }

    override fun onButtonAddClick(name: String) {
        if (name.isBlank()) {
            view?.showErrorEmptyTagName()
        } else {
            addDisposable(saveTag.invoke(name)
                    .flatMapCompletable { addTagToNote.invoke(mNoteId, it) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        view?.closeView()
                    }, {
                        if (it is SaveTagUseCase.InvalidTagNameException) {
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