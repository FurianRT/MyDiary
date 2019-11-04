/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.dialogs.tags.fragments.list

import com.furianrt.mydiary.model.entity.MyNoteWithProp
import com.furianrt.mydiary.model.entity.MyTag
import com.furianrt.mydiary.domain.save.AddTagToNoteUseCase
import com.furianrt.mydiary.domain.get.GetTagsUseCase
import com.furianrt.mydiary.domain.delete.RemoveTagFromNoteUseCase
import com.furianrt.mydiary.domain.get.GetFullNotesUseCase
import com.furianrt.mydiary.utils.MyRxUtils
import com.furianrt.mydiary.presentation.dialogs.tags.fragments.list.TagListAdapter.*
import io.reactivex.Flowable
import io.reactivex.functions.Function3
import java.util.Locale
import javax.inject.Inject

class TagListPresenter @Inject constructor(
        private val getTags: GetTagsUseCase,
        private val addTagToNote: AddTagToNoteUseCase,
        private val removeTagFromNote: RemoveTagFromNoteUseCase,
        private val getFullNotes: GetFullNotesUseCase,
        private val scheduler: MyRxUtils.BaseSchedulerProvider
) : TagListContract.Presenter() {

    private lateinit var mNoteId: String
    private var mItems = emptyList<ViewItem>()
    private var mQuery = ""

    override fun init(noteId: String) {
        mNoteId = noteId
    }

    override fun attachView(view: TagListContract.MvpView) {
        super.attachView(view)
        addDisposable(Flowable.combineLatest(
                getTags.invoke(),
                getTags.invoke(mNoteId).firstOrError().toFlowable(),
                getFullNotes.invoke().firstOrError().toFlowable(),
                Function3<List<MyTag>, List<MyTag>, List<MyNoteWithProp>, List<ViewItem>>
                { allTags, selectedTags, notes ->
                    val items = allTags.map { ViewItem(it) }
                    items.forEach { item ->
                        item.isChecked = selectedTags.find { it.id == item.tag.id } != null
                        item.count = notes.count { note -> note.tags.find { it.id == item.tag.id } != null }
                    }
                    return@Function3 items
                })
                .observeOn(scheduler.ui())
                .subscribe { items ->
                    mItems = items
                    val matchingTags = mItems
                            .filter { item -> item.tag.name.toLowerCase().contains(mQuery.toLowerCase()) }
                            .sortedWith(Comparator { b, a -> compareValuesBy(a, b, { it.isChecked }, { it.count }) })
                    view.showItems(matchingTags)
                })
    }

    override fun onItemCheckChange(item: ViewItem) {
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

    override fun onButtonEditTagClick(item: ViewItem) {
        view?.showEditTagView(item.tag)
    }

    override fun onButtonDeleteTagClick(item: ViewItem) {
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