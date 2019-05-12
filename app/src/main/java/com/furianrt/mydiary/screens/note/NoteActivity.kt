package com.furianrt.mydiary.screens.note

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.View
import android.view.WindowManager
import androidx.viewpager.widget.ViewPager
import com.anjlab.android.iab.v3.TransactionDetails
import com.furianrt.mydiary.BuildConfig
import com.furianrt.mydiary.R
import com.furianrt.mydiary.base.BaseActivity
import com.furianrt.mydiary.data.model.MyNoteWithProp
import com.furianrt.mydiary.screens.note.fragments.mainnote.edit.NoteEditFragment
import com.furianrt.mydiary.utils.KeyboardUtils
import com.furianrt.mydiary.utils.generateUniqueId
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import kotlinx.android.synthetic.main.activity_note.*
import javax.inject.Inject

class NoteActivity : BaseActivity(), NoteActivityContract.View,
        NoteEditFragment.OnNoteFragmentInteractionListener {

    companion object {
        private const val TAG = "NoteActivity"
        private const val BUNDLE_NOTE_ID = "noteId"
        private const val EXTRA_MODE = "mode"
        private const val EXTRA_CLICKED_NOTE_POSITION = "notePosition"

        enum class Mode { ADD, READ }

        fun newIntentModeAdd(context: Context) =
                Intent(context, NoteActivity::class.java).apply { putExtra(EXTRA_MODE, Companion.Mode.ADD) }

        fun newIntentModeRead(context: Context, position: Int) =
                Intent(context, NoteActivity::class.java).apply {
                    putExtra(EXTRA_MODE, Companion.Mode.READ)
                    putExtra(EXTRA_CLICKED_NOTE_POSITION, position)
                }
    }

    @Inject
    lateinit var mPresenter: NoteActivityContract.Presenter

    private var mPagerPosition = 0
    private var mMode = Companion.Mode.ADD
    private lateinit var mNoteId: String

    private lateinit var mPagerAdapter: NoteActivityPagerAdapter
    private val mOnPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {}
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
        override fun onPageSelected(position: Int) {
            mPagerPosition = position
            showImageCounter(mPagerPosition + 1, mPagerAdapter.count)
        }
    }

    private val mKeyboardListener = object : KeyboardUtils.SoftKeyboardToggleListener {
        override fun onToggleSoftKeyboard(isVisible: Boolean) {
            if (isVisible) {
                view_ad?.visibility = View.GONE
            } else {
                view_ad?.postDelayed({ view_ad?.visibility = View.VISIBLE }, 150L)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPresenterComponent(this).inject(this)
        setContentView(R.layout.activity_note)
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN)

        setSupportActionBar(toolbar_note_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

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
        pager_note.adapter = mPagerAdapter

        view_ad.loadAd(AdRequest.Builder().build())
        view_ad.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                view_ad?.visibility = View.VISIBLE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_note_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(EXTRA_CLICKED_NOTE_POSITION, pager_note.currentItem)
        outState.putString(BUNDLE_NOTE_ID, mNoteId)
    }

    override fun onBillingInitialized() {
        super.onBillingInitialized()
        if (!isItemPurshased(BuildConfig.ITEM_SYNC_SKU)) {
            showAdView()
        }
    }

    override fun onProductPurchased(productId: String, details: TransactionDetails?) {
        super.onProductPurchased(productId, details)
        hideAdView()
    }

    private fun showAdView() {
        view_ad?.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                view_ad?.visibility = View.VISIBLE
            }
        }
        view_ad?.loadAd(AdRequest.Builder().build())
    }

    private fun hideAdView() {
        view_ad?.destroy()
        view_ad?.visibility = View.GONE
    }

    override fun showNotes(notes: List<MyNoteWithProp>) {
        if (mPagerPosition >= notes.size) {
            mPagerPosition = notes.size - 1
        }
        mPagerAdapter.list = notes
        mPagerAdapter.notifyDataSetChanged()
        pager_note.setCurrentItem(mPagerPosition, false)
        showImageCounter(mPagerPosition + 1, mPagerAdapter.count)
    }

    override fun onNoteFragmentEditModeDisabled() {
        pager_note.swipeEnabled = true
        text_toolbar_title.visibility = View.VISIBLE
    }

    override fun onNoteFragmentEditModeEnabled() {
        pager_note.swipeEnabled = false
        text_toolbar_title.visibility = View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    fun savePagerPosition() {
        mPagerPosition = pager_note.currentItem
    }

    override fun closeView() {
        finish()
    }

    private fun showImageCounter(current: Int, count: Int) {
        text_toolbar_title.text = getString(R.string.counter_format, current, count)
    }

    override fun onStart() {
        super.onStart()
        mPresenter.attachView(this)
        pager_note.addOnPageChangeListener(mOnPageChangeListener)
        KeyboardUtils.addKeyboardToggleListener(this, mKeyboardListener)
        if (mMode == Companion.Mode.READ) {
            mPresenter.loadNotes()
        } else {
            mPresenter.loadNote(mNoteId)
        }
    }

    override fun onStop() {
        super.onStop()
        pager_note.removeOnPageChangeListener(mOnPageChangeListener)
        KeyboardUtils.removeKeyboardToggleListener(mKeyboardListener)
        mPresenter.detachView()
    }
}
