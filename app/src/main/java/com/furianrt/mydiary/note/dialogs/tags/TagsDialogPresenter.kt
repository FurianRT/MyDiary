package com.furianrt.mydiary.note.dialogs.tags

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.utils.generateUniqueId
import io.reactivex.Completable

class TagsDialogPresenter(private val mDataManager: DataManager) : TagsDialogContract.Presenter {

    private var mView: TagsDialogContract.View? = null

    private lateinit var mTags: ArrayList<MyTag>

    override fun attachView(view: TagsDialogContract.View) {
        mView = view
    }

    override fun detachView() {
        super.detachView()
        mView = null
    }

    override fun setTags(tags: ArrayList<MyTag>) {
        mTags = tags
    }

    override fun onViewCreate() {
        mView?.showTags(mTags)
    }

    override fun getTags() = mTags

    override fun onTagClicked(tag: MyTag) {
        mTags.find { it.id == tag.id }
                ?.isChecked = tag.isChecked
        mView?.showTags(mTags)
    }

    override fun onButtonAddTagClicked(tagName: String) {
        if (tagName.isEmpty()) return
        val foundedTag = mTags.find { it.name == tagName }
        if (foundedTag != null) {
            foundedTag.isChecked = true
            mView?.showTags(mTags)
        } else {
            val tag = MyTag(generateUniqueId(), tagName)
            tag.isChecked = true
            addDisposable(Completable.fromAction { mTags.add(tag) }
                    .andThen(mDataManager.insertTag(tag))
                    .subscribe { mView?.showTags(mTags) })
        }
    }

    override fun onButtonDeleteTagClicked(tag: MyTag) {
        addDisposable(Completable.fromCallable { mTags.remove(tag) }
                .andThen(mDataManager.deleteTag(tag))
                .subscribe { mView?.showTags(mTags) })
    }

    override fun onButtonEditTagClicked(tag: MyTag) {
        addDisposable(Completable.fromAction { mTags.find { it.id == tag.id }?.name = tag.name }
                .andThen(mDataManager.updateTag(tag))
                .subscribe { mView?.showTags(mTags) })
    }
}