package com.furianrt.mydiary.view.dialogs.tags.fragments.edit

import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.usecase.update.UpdateTagUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class TagEditPresenter @Inject constructor(
        private val updateTag: UpdateTagUseCase
) : TagEditContract.Presenter() {

    private lateinit var mTag: MyTag

    override fun init(tag: MyTag) {
        mTag = tag
    }

    override fun attachView(view: TagEditContract.MvpView) {
        super.attachView(view)
        view.showTagName(mTag.name)
    }

    override fun onButtonCloseClick() {
        view?.closeView()
    }

    override fun onButtonConfirmClick(newTagName: String) {
        if (newTagName.isBlank()) {
            view?.showErrorEmptyTagName()
        } else {
            addDisposable(updateTag.invoke(mTag.apply { name = newTagName })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        view?.closeView()
                    }, {
                        if (it is UpdateTagUseCase.InvalidTagNameException) {
                            view?.showErrorExistingTagName()
                        } else {
                            it.printStackTrace()
                        }
                    }))
        }
    }
}