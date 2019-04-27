package com.furianrt.mydiary.note

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.WindowManager
import androidx.viewpager.widget.ViewPager
import com.furianrt.mydiary.R
import com.furianrt.mydiary.base.BaseActivity
import com.furianrt.mydiary.data.model.MyNoteWithProp
import com.furianrt.mydiary.note.fragments.notefragment.edit.NoteEditFragment
import com.furianrt.mydiary.utils.generateUniqueId
import kotlinx.android.synthetic.main.activity_note.*
import javax.inject.Inject

class NoteActivity : BaseActivity(), NoteActivityContract.View,
        NoteEditFragment.OnNoteFragmentInteractionListener {

    companion object {
        private const val TAG = "NoteActivity"
        private const val BUNDLE_NOTE_ID = "noteId"
        private const val EXTRA_MODE = "mode"
        private const val EXTRA_CLICKED_NOTE_POSITION = "notePosition"

        enum class Mode { ADD, READ } //todo вместо это сделать разные методы у презентера

        fun newIntentModeAdd(context: Context) =
                Intent(context, NoteActivity::class.java).apply { putExtra(EXTRA_MODE, Mode.ADD) }

        fun newIntentModeRead(context: Context, position: Int) =
                Intent(context, NoteActivity::class.java).apply {
                    putExtra(EXTRA_MODE, Mode.READ)
                    putExtra(EXTRA_CLICKED_NOTE_POSITION, position)
                }
    }

    @Inject
    lateinit var mPresenter: NoteActivityContract.Presenter

    private var mPagerPosition = 0
    private var mMode = Mode.ADD
    private lateinit var mNoteId: String

    private lateinit var mPagerAdapter: NoteActivityPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPresenterComponent(this).inject(this)
        setContentView(R.layout.activity_note)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        mPagerPosition = savedInstanceState?.getInt(EXTRA_CLICKED_NOTE_POSITION, 0)
                ?: intent.getIntExtra(EXTRA_CLICKED_NOTE_POSITION, 0)

        if (savedInstanceState != null) {
            mPagerPosition = savedInstanceState.getInt(EXTRA_CLICKED_NOTE_POSITION, 0)
            mNoteId = savedInstanceState.getString(BUNDLE_NOTE_ID)
                    ?: throw IllegalArgumentException()
        } else {
            mPagerPosition = intent.getIntExtra(EXTRA_CLICKED_NOTE_POSITION, 0)
            mNoteId = generateUniqueId()
        }

        mMode = intent.getSerializableExtra(EXTRA_MODE) as Mode

        mPagerAdapter = NoteActivityPagerAdapter(supportFragmentManager, mMode)

        setupUi()
    }

    private fun setupUi() {
        setSupportActionBar(toolbar_note_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        pager_note.adapter = mPagerAdapter
        pager_note.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {}
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {}
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
        outState?.putString(BUNDLE_NOTE_ID, mNoteId)
    }

    override fun showNotes(notes: List<MyNoteWithProp>) {
        mPagerAdapter.list = notes
        Log.e(TAG, "note_list_notify")
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
        finish()
        return true
    }

    fun savePagerPosition() {
        mPagerPosition = pager_note.currentItem
    }

    override fun onResume() {
        super.onResume()
        mPresenter.attachView(this)
        if (mMode == Mode.READ) {
            mPresenter.loadNotes()
        } else {
            mPresenter.loadNote(mNoteId)
        }
    }

    override fun onPause() {
        super.onPause()
        mPresenter.detachView()
    }
}
