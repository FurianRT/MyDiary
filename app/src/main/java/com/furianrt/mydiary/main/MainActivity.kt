package com.furianrt.mydiary.main

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.widget.Toast
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.general.HeaderItemDecoration
import com.furianrt.mydiary.main.listadapter.MainListAdapter
import com.furianrt.mydiary.main.listadapter.MainListItem
import com.furianrt.mydiary.note.EXTRA_MODE
import com.furianrt.mydiary.note.Mode
import com.furianrt.mydiary.note.NoteActivity
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject

const val EXTRA_CLICKED_NOTE_POSITION = "notePosition"

private const val RECYCLER_VIEW_POSITION = "recyclerPosition"

class MainActivity : AppCompatActivity(), MainActivityContract.View,
        MainListAdapter.OnMainListItemInteractionListener {

    @Inject
    lateinit var mPresenter: MainActivityContract.Presenter

    private val mAdapter = MainListAdapter(this)

    private var mRecyclerViewState: Parcelable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPresenterComponent(this).inject(this)
        setContentView(R.layout.activity_main)

        mPresenter.attachView(this)

        mPresenter.loadNotes()

        fab.setOnClickListener {
            val intent = Intent(this, NoteActivity::class.java)
            intent.putExtra(EXTRA_MODE, Mode.ADD)
            startActivity(intent)
        }

        list_main.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(HeaderItemDecoration(this, mAdapter))
            adapter = mAdapter
            savedInstanceState?.let {
                mRecyclerViewState = it.getParcelable(RECYCLER_VIEW_POSITION)
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putParcelable(RECYCLER_VIEW_POSITION, list_main.layoutManager?.onSaveInstanceState())
        super.onSaveInstanceState(outState)
    }

    override fun showAdded() {
        Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show()
    }

    override fun showDeleted() {
        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
    }

    override fun showNotes(notes: List<MainListItem>?) {
        mAdapter.submitList(notes)
        mRecyclerViewState?.let {
            list_main.layoutManager?.onRestoreInstanceState(mRecyclerViewState)
            mRecyclerViewState = null
        }
    }

    override fun onMainListItemClick(note: MyNote) {
        mPresenter.onMainListItemClick(note)
    }

    override fun openNotePager(position: Int) {
        val intent = Intent(this, NoteActivity::class.java)
        intent.putExtra(EXTRA_CLICKED_NOTE_POSITION, position)
        intent.putExtra(EXTRA_MODE, Mode.READ)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }
}
