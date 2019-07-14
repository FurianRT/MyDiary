package com.furianrt.mydiary.view.dialogs.tags.fragments.list

import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.usecase.save.AddTagToNoteUseCase
import com.furianrt.mydiary.usecase.get.GetTagsUseCase
import com.furianrt.mydiary.usecase.delete.RemoveTagFromNoteUseCase
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.BiFunction
import java.util.Locale
import javax.inject.Inject

class TagListPresenter @Inject constructor(
        private val getTags: GetTagsUseCase,
        private val addTagToNote: AddTagToNoteUseCase,
        private val removeTagFromNote: RemoveTagFromNoteUseCase
) : TagListContract.Presenter() {

    private lateinit var mNoteId: String
    private var mItems = emptyList<TagListAdapter.ViewItem>()
    private var mQuery = ""

    override fun init(noteId: String) {
        mNoteId = noteId
    }

    override fun attachView(view: TagListContract.MvpView) {
        super.attachView(view)
        addDisposable(Flowable.combineLatest(getTags.invoke(), getTags.invoke(mNoteId),
                BiFunction<List<MyTag>, List<MyTag>, List<TagListAdapter.ViewItem>> { allTags, selectedTags ->
                    val items = allTags.map { TagListAdapter.ViewItem(it) }
                    items.forEach { item -> item.isChecked = selectedTags.find { it.id == item.tag.id } != null }
                    return@BiFunction items
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { items ->
                    mItems = items
                    val matchingTags = mItems.filter { item ->
                        item.tag.name.toLowerCase().contains(mQuery.toLowerCase())
                    }
                    view.showItems(matchingTags)
                })
    }

    override fun onItemCheckChange(item: TagListAdapter.ViewItem) {
        if (item.isChecked) {
            addDisposable(addTagToNote.invoke(mNoteId, item.tag.id).subscribe())
        } else {
            addDisposable(removeTagFromNote.invoke(mNoteId, item.tag.id).subscribe())
        }
    }

    override fun onButtonCloseClick() {
        view?.closeView()
    }

    override fun onButtonAddClick() {
        view?.showAddTagView(mNoteId)
    }

    override fun onButtonEditTagClick(item: TagListAdapter.ViewItem) {
        view?.showEditTagView(item.tag)
    }

    override fun onButtonDeleteTagClick(item: TagListAdapter.ViewItem) {
        view?.showDeleteTagView(item.tag)
    }

    override fun onSearchQueryChange(newText: String?) {
        mQuery = newText ?: ""
        val matchingTags = mItems.filter {
            it.tag.name.toLowerCase(Locale.getDefault())
                    .contains(mQuery.toLowerCase(Locale.getDefault()))
        }
        view?.showItems(matchingTags)
    }
}