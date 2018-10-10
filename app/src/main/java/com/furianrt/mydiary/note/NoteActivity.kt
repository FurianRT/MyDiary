package com.furianrt.mydiary.note

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.WindowManager
import androidx.viewpager.widget.ViewPager
import com.furianrt.mydiary.BaseActivity
import com.furianrt.mydiary.LOG_TAG
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyNoteWithProp
import com.furianrt.mydiary.main.EXTRA_CLICKED_NOTE_POSITION
import com.furianrt.mydiary.note.fragments.notefragment.edit.NoteEditFragment
import kotlinx.android.synthetic.main.activity_note.*
import javax.inject.Inject

const val EXTRA_MODE = "mode"

enum class Mode { ADD, READ }

class NoteActivity : BaseActivity(), NoteActivityContract.View,
        NoteEditFragment.OnNoteFragmentInteractionListener {

    @Inject
    lateinit var mPresenter: NoteActivityContract.Presenter

    private var mPagerPosition = 0
    private var mMode = Mode.ADD

    private lateinit var mPagerAdapter: NoteActivityPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPresenterComponent(this).inject(this)
        setContentView(R.layout.activity_note)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        mPagerPosition = savedInstanceState?.getInt(EXTRA_CLICKED_NOTE_POSITION, 0)
                ?: intent.getIntExtra(EXTRA_CLICKED_NOTE_POSITION, 0)

        mMode = intent.getSerializableExtra(EXTRA_MODE) as Mode

        mPagerAdapter = NoteActivityPagerAdapter(supportFragmentManager, mMode)

        setupUi()
    }

    override fun onStart() {
        super.onStart()
        mPresenter.attachView(this)
        mPresenter.loadNotes(mMode)
    }

    private fun setupUi() {
        setSupportActionBar(toolbar_note_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)


        pager_note.adapter = mPagerAdapter
        pager_note.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {

            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

            }

            override fun onPageSelected(position: Int) {
                mPagerPosition = position
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_note_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState?.putInt(EXTRA_CLICKED_NOTE_POSITION, pager_note.currentItem)
    }

    override fun showNotes(notes: List<MyNoteWithProp>) {
        mPagerAdapter.list = notes
        Log.e(LOG_TAG, "note_list_notify")
        mPagerAdapter.notifyDataSetChanged()
        pager_note.setCurrentItem(mPagerPosition, false)
    }

    override fun onNoteFragmentEditModeDisabled() {
        pager_note.swipeEnabled = true
    }

    override fun onNoteFragmentEditModeEnabled() {
        pager_note.swipeEnabled = false
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStop() {
        super.onStop()
        mPresenter.detachView()
    }

    fun savePagerPosition() {
        mPagerPosition = pager_note.currentItem
    }
}
