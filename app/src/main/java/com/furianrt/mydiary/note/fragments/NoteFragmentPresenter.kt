package com.furianrt.mydiary.note.fragments

import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.data.model.NoteTag
import com.google.android.gms.maps.model.LatLng
import io.reactivex.Completable
import io.reactivex.Observable

class NoteFragmentPresenter(private val mDataManager: DataManager) : NoteFragmentContract.Presenter {

    private var mView: NoteFragmentContract.View? = null

    override fun attachView(view: NoteFragmentContract.View) {
        mView = view
    }

    override fun detachView() {
        mView = null
    }

    override fun getForecast(coords: LatLng) {
        mDataManager.getForecast(coords.latitude, coords.longitude)
                .subscribe { forecast ->
                    mView?.showForecast(forecast)
                }
    }

    override fun onTagsFieldClick(note: MyNote) {
        mDataManager.insertNote(note)
                .flatMapObservable { id ->
                    note.id = id
                    return@flatMapObservable tagsObservable(id)
                }
                .collectInto(ArrayList<MyTag>()) { list, tag ->
                    val foundedTag = list.find { it.name == tag.name }
                    if (foundedTag == null) list.add(tag)
                }
                .subscribe { tags -> mView?.showTagsDialog(tags) }
    }

    private fun tagsObservable(noteId: Long): Observable<MyTag> {
        val allTagsObservable = mDataManager.getAllTags()
                .flatMapObservable { tags -> Observable.fromIterable(tags) }

        val noteTagsObservable = mDataManager.getNoteWithTags(noteId)
                .map { it.tagNameList }
                .flatMapObservable { tagNames -> Observable.fromIterable(tagNames) }
                .map { tagName -> MyTag(tagName, true) }

        return Observable.concat(noteTagsObservable, allTagsObservable)
    }

    override fun changeNoteTags(note: MyNote, tags: List<MyTag>) {
        Completable.concatArray(mDataManager.deleteAllTagsForNote(note.id),
                mDataManager.insertAllNoteTag(toNoteTagList(note, tags)))
                .andThen( mDataManager.getNoteWithTags(note.id))
                .subscribe { noteWithTags ->
                    mView?.showTagNames(noteWithTags.tagNameList)
                }
    }

    override fun loadNoteProperties(note: MyNote) {
        mDataManager.getNoteWithTags(note.id)
                .subscribe { noteWithTags -> mView?.showTagNames(noteWithTags.tagNameList) }
    }

    private fun toNoteTagList(note: MyNote, tags: List<MyTag>): List<NoteTag> {
        val list = ArrayList<NoteTag>()
        for (tag in tags) if (tag.isChecked) list.add(NoteTag(note.id, tag.name))
        return list
    }
}
