package com.furianrt.mydiary.dialogs.tags.fragments.list

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.data.model.NoteTag
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction

class TagListPresenter(
        private val dataManager: DataManager
) : TagListContract.Presenter() {

    private var mTags = emptyList<MyTag>()
    private var mQuery = ""

    override fun onViewResume(noteId: String) {
        addDisposable(Flowable.combineLatest(dataManager.getAllTags(), dataManager.getTagsForNote(noteId),
                BiFunction<List<MyTag>, List<MyTag>, List<MyTag>> { allTags, selectedTags ->
                    ArrayList<MyTag>(allTags).apply {
                        forEach { tag -> tag.isChecked = selectedTags.find { it.id == tag.id } != null }
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { tags ->
                    mTags = tags
                    view?.showTags(mTags.filter { it.name.toLowerCase().contains(mQuery.toLowerCase()) })
                })
    }

    override fun onItemCheckChange(noteId: String, tag: MyTag, checked: Boolean) {
        if (checked) {
            addDisposable(dataManager.insertNoteTag(NoteTag(noteId, tag.id)).subscribe())
        } else {
            addDisposable(dataManager.deleteNoteTag(noteId, tag.id).subscribe())
        }
    }

    override fun onButtonCloseClick() {
        view?.closeView()
    }

    override fun onButtonAddClick() {
        view?.showAddTagView()
    }

    override fun onButtonEditTagClick(tag: MyTag) {
        view?.showEditTagView(tag)
    }

    override fun onButtonDeleteTagClick(tag: MyTag) {
        view?.showDeleteTagView(tag)
    }

    override fun onSearchQueryChange(newText: String?) {
        mQuery = newText ?: ""
        view?.showTags(mTags.filter { it.name.toLowerCase().contains(mQuery.toLowerCase()) })
    }
}