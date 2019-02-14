package com.furianrt.mydiary.note.dialogs.tags

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.utils.generateUniqueId
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers

class TagsDialogPresenter(private val mDataManager: DataManager) : TagsDialogContract.Presenter() {

    private lateinit var mTags: ArrayList<MyTag>

    override fun setTags(tags: ArrayList<MyTag>) {
        mTags = tags
    }

    override fun onViewCreate() {
        view?.showTags(mTags)
    }

    override fun getTags() = mTags

    override fun onTagClicked(tag: MyTag) {
        mTags.find { it.id == tag.id }
                ?.isChecked = tag.isChecked
        view?.showTags(mTags)
    }

    override fun onButtonAddTagClicked(tagName: String) {
        if (tagName.isEmpty()) return
        val foundedTag = mTags.find { it.name == tagName }
        if (foundedTag != null) {
            foundedTag.isChecked = true
            view?.showTags(mTags)
        } else {
            val tag = MyTag(generateUniqueId(), tagName)
            tag.isChecked = true
            addDisposable(Completable.fromAction { mTags.add(tag) }
                    .andThen(mDataManager.insertTag(tag))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { view?.showTags(mTags) })
        }
    }

    override fun onButtonDeleteTagClicked(tag: MyTag) {
        addDisposable(Completable.fromCallable { mTags.remove(tag) }
                .andThen(mDataManager.deleteTag(tag))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.showTags(mTags) })
    }

    override fun onButtonEditTagClicked(tag: MyTag) {
        addDisposable(Completable.fromAction { mTags.find { it.id == tag.id }?.name = tag.name }
                .andThen(mDataManager.updateTag(tag))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { view?.showTags(mTags) })
    }
}