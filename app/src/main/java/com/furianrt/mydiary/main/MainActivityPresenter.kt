package com.furianrt.mydiary.main

import android.util.Log
import com.furianrt.mydiary.data.DataManager
import com.furianrt.mydiary.data.api.Forecast
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.main.listadapter.MainContentItem
import com.furianrt.mydiary.main.listadapter.MainHeaderItem
import com.furianrt.mydiary.main.listadapter.MainListItem
import io.reactivex.Observer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList

class MainActivityPresenter(private val mDataManager: DataManager) : MainActivityContract.Presenter {

    private var mView: MainActivityContract.View? = null

    override fun attachView(view: MainActivityContract.View) {
        mView = view
    }

    override fun detachView() {
        mView = null
    }

    override fun addNote(note: MyNote) {
        mDataManager.insertNote(note)
                .subscribe()
        mView?.showAdded()
    }

    override fun deleteNote(note: MyNote) {
        mDataManager.deleteNote(note)
                .subscribe()
        mView?.showDeleted()
    }

    override fun loadNotes() {
        mDataManager.getAllNotes()
                .map { formatNotes(toMap(it)) }
                .subscribe { mView?.showNotes(it) }
    }

    private fun toMap(notes: List<MyNote>): Map<Long, ArrayList<MyNote>> {
        val map = TreeMap<Long, ArrayList<MyNote>>(Comparator<Long> { p0, p1 -> p1.compareTo(p0) })
        for (note in notes) {
            val calendar = Calendar.getInstance()
            calendar.time = Date(note.time)
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            var value = map[calendar.timeInMillis]
            if (value == null) {
                value = ArrayList()
                map[calendar.timeInMillis] = value
            }
            value.add(note)
        }
        return map
    }

    private fun formatNotes(notes: Map<Long, List<MyNote>>): ArrayList<MainListItem> {
        val list = ArrayList<MainListItem>()
        for (date in notes.keys) {
            val header = MainHeaderItem(date)
            list.add(header)
            for (note in notes[date]!!) {
                list.add(MainContentItem(note))
            }
        }
        return list
    }

    override fun onMainListItemClick(note: MyNote) {
        mDataManager.getAllNotes()
                .first(ArrayList())
                .subscribe { notes -> mView?.openNotePager(notes.indexOf(note)) }
    }
}