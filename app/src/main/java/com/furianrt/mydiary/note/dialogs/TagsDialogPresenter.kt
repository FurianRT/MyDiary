package com.furianrt.mydiary.note.dialogs

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyTag
import io.reactivex.Completable

class TagsDialogPresenter(private val mDataManager: DataManager) : TagsDialogContract.Presenter {

    private var mView: TagsDialogContract.View? = null

    override fun attachView(view: TagsDialogContract.View) {
        mView = view
    }

    override fun detachView() {
        mView = null
    }

    override fun onTagClicked(tags: MutableList<MyTag>, tag: MyTag) {
        tags.find { it.id == tag.id }
                ?.isChecked = tag.isChecked
        mView?.showTags(tags)

        /*tags.find { it.id == tag.id }
                ?.isChecked = tag.isChecked*/
    }

    override fun onButtonAddTagClicked(tagName: String, tags: MutableList<MyTag>) {
        val foundedTag = tags.find { it.name == tagName }
        if (foundedTag != null) {
            foundedTag.isChecked = true
            mView?.showTags(tags)
        } else {
            val tag = MyTag(tagName)
            tag.isChecked = true
            Completable.fromAction { tags.add(tag) }
                    .andThen(mDataManager.insertTag(tag))
                    .subscribe { id ->
                        tag.id = id
                        mView?.showTags(tags)
                    }
        }
    }

    override fun onButtonDeleteTagClicked(tag: MyTag, tags: MutableList<MyTag>) {
        Completable.fromCallable { tags.remove(tag) }
                .andThen(mDataManager.deleteTag(tag))
                .subscribe { mView?.showTags(tags) }
    }

    override fun onButtonEditTagClicked(tag: MyTag, tags: MutableList<MyTag>) {
        Completable.fromAction { tags.find { it.id == tag.id }?.name = tag.name }
                .andThen(mDataManager.updateTag(tag))
                .subscribe { mView?.showTags(tags) }
    }
}