package com.furianrt.mydiary.main

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
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

const val EXTRA_POSITION = "position"

class MainActivity : AppCompatActivity(), MainActivityContract.View,
        MainListAdapter.OnMainListItemInteractionListener {

    @Inject
    lateinit var mPresenter: MainActivityContract.Presenter

    private val mAdapter = MainListAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPresenterComponent(this).inject(this)
        setContentView(R.layout.activity_main)

        mPresenter.attachView(this)

        mPresenter.loadNotes()

        fab.setOnClickListener {
            //mPresenter.addNote(MyNote("", "", Date().time))
            val intent = Intent(this, NoteActivity::class.java)
            intent.putExtra(EXTRA_MODE, Mode.ADD)
            startActivity(intent)
        }

        list_main.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(HeaderItemDecoration(this, mAdapter))
            adapter = mAdapter
        }
    }

    override fun showAdded() {
        Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show()
    }

    override fun showDeleted() {
        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
    }

    override fun showNotes(notes: List<MainListItem>?) {
        mAdapter.submitList(notes)
    }

    override fun onMainListItemClick(note: MyNote) {
        mPresenter.onMainListItemClick(note)
    }

    override fun openNotePager(position: Int) {
        val intent = Intent(this, NoteActivity::class.java)
        intent.putExtra(EXTRA_POSITION, position)
        intent.putExtra(EXTRA_MODE, Mode.READ)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }
}
