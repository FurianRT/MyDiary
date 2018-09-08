package com.furianrt.mydiary.note.dialogs

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyTag
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable

class TagsDialogPresenter(private val mDataManager: DataManager) : TagsDialogContract.Presenter {

    private var mView: TagsDialogContract.View? = null
    private val mCompositeDisposable = CompositeDisposable()

    override fun attachView(view: TagsDialogContract.View) {
        mView = view
    }

    override fun detachView() {
        mCompositeDisposable.clear()
        mView = null
    }

    override fun onTagClicked(tags: MutableList<MyTag>, tag: MyTag) {
        tags.find { it.id == tag.id }
                ?.isChecked = tag.isChecked
        mView?.showTags(tags)
    }

    override fun onButtonAddTagClicked(tagName: String, tags: MutableList<MyTag>) {
        if (tagName.isEmpty()) return
        val foundedTag = tags.find { it.name == tagName }
        if (foundedTag != null) {
            foundedTag.isChecked = true
            mView?.showTags(tags)
        } else {
            val tag = MyTag(tagName)
            tag.isChecked = true
            val disposable = Completable.fromAction { tags.add(tag) }
                    .andThen(mDataManager.insertTag(tag))
                    .subscribe { id ->
                        tag.id = id
                        mView?.showTags(tags)
                    }
            mCompositeDisposable.add(disposable)
        }
    }

    override fun onButtonDeleteTagClicked(tag: MyTag, tags: MutableList<MyTag>) {
        val disposable = Completable.fromCallable { tags.remove(tag) }
                .andThen(mDataManager.deleteTag(tag))
                .subscribe { mView?.showTags(tags) }
        mCompositeDisposable.add(disposable)
    }

    override fun onButtonEditTagClicked(tag: MyTag, tags: MutableList<MyTag>) {
        val disposable = Completable.fromAction { tags.find { it.id == tag.id }?.name = tag.name }
                .andThen(mDataManager.updateTag(tag))
                .subscribe { mView?.showTags(tags) }
        mCompositeDisposable.add(disposable)
    }
}