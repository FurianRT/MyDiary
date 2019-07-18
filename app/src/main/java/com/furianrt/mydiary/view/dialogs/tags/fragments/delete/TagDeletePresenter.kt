package com.furianrt.mydiary.view.dialogs.tags.fragments.delete

import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.domain.delete.DeleteTagUseCase
import io.reactivex.android.schedulers.AndroidSchedulers
import javax.inject.Inject

class TagDeletePresenter @Inject constructor(
        private val deleteTag: DeleteTagUseCase
) : TagDeleteContract.Presenter() {

    private lateinit var mTag: MyTag

    override fun init(tag: MyTag) {
        mTag = tag
    }

    override fun attachView(view: TagDeleteContract.MvpView) {
        super.attachView(view)
        view.showTagName(mTag.name)
    }

    override fun onButtonDeleteClick() {
        addDisposable(deleteTag.invoke(mTag)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.closeView() })
    }

    override fun onButtonCancelClick() {
        view?.closeView()
    }
}