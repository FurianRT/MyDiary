/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.main

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.os.Vibrator
import android.util.Log
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.ViewPropertyAnimatorListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anjlab.android.iab.v3.TransactionDetails
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.signature.ObjectKey
import com.furianrt.mydiary.BuildConfig
import com.furianrt.mydiary.R
import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.view.base.BaseActivity
import com.furianrt.mydiary.model.entity.*
import com.furianrt.mydiary.view.dialogs.categories.CategoriesDialog
import com.furianrt.mydiary.view.dialogs.delete.note.DeleteNoteDialog
import com.furianrt.mydiary.view.dialogs.rate.RateDialog
import com.furianrt.mydiary.view.general.AppBarLayoutBehavior
import com.furianrt.mydiary.view.general.GlideApp
import com.furianrt.mydiary.view.screens.main.NoteListAdapter.NoteItemView
import com.furianrt.mydiary.view.screens.main.fragments.authentication.AuthFragment
import com.furianrt.mydiary.view.screens.main.fragments.drawer.DrawerMenuFragment
import com.furianrt.mydiary.view.screens.main.fragments.imagesettings.ImageSettingsFragment
import com.furianrt.mydiary.view.screens.main.fragments.imagesettings.settings.DailySettingsFragment
import com.furianrt.mydiary.view.screens.main.fragments.premium.PremiumFragment
import com.furianrt.mydiary.view.screens.main.fragments.profile.ProfileFragment
import com.furianrt.mydiary.view.screens.note.NoteActivity
import com.furianrt.mydiary.view.screens.settings.global.GlobalSettingsActivity
import com.furianrt.mydiary.utils.dpToPx
import com.furianrt.mydiary.utils.getDisplayWidth
import com.furianrt.mydiary.utils.inTransaction
import com.furianrt.mydiary.view.general.StickyHeaderItemDecoration
import com.furianrt.mydiary.view.screens.statistics.StatsActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import jp.wasabeef.recyclerview.animators.LandingAnimator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_toolbar.*
import kotlinx.android.synthetic.main.bottom_sheet_main.*
import kotlinx.android.synthetic.main.empty_search_note_list.*
import org.joda.time.DateTime
import java.util.*
import javax.inject.Inject
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.math.min

class MainActivity : BaseActivity(R.layout.activity_main), MainActivityContract.MvpView,
        NoteListAdapter.OnMainListItemInteractionListener,
        DailySettingsFragment.OnImageSettingsInteractionListener,
        DeleteNoteDialog.OnDeleteNoteConfirmListener, CategoriesDialog.OnCategorySelectedListener,
        PremiumFragment.OnPremiumFragmentInteractionListener,
        DrawerMenuFragment.OnDrawerMenuInteractionListener {

    companion object {
        private const val TAG = "MainActivity"
        private const val BUNDLE_RECYCLER_VIEW_STATE = "recycler_state"
        private const val BUNDLE_ROOT_LAYOUT_OFFSET = "root_layout_offset"
        private const val BUNDLE_BOTTOM_SHEET_STATE = "bottom_sheet_state"
        private const val BUNDLE_SEARCH_QUERY = "query"
        private const val BUNDLE_STATUS_BAR_HEIGHT = "status_bar_height"
        private const val ACTIVITY_SETTING_REQUEST_CODE = 2
        private const val ITEM_LONG_CLICK_VIBRATION_DURATION = 30L
        private const val BOTTOM_SHEET_EXPAND_DELAY = 500L
        private const val ANIMATION_IMAGE_SETTINGS_FADE_OUT_DURATION = 350L
        private const val ANIMATION_IMAGE_SETTINGS_FADE_OUT_OFFSET = 2000L
        private const val ANIMATION_NO_SEARCH_RESULT_DURATION = 200L
        private const val ANIMATION_NO_SEARCH_RESULT_SIZE = 1.4f
        private const val ANIMATION_EMPTY_SATE_DURATION = 500L
        private val TOOLBAR_HEIGHT = dpToPx(56f)
    }

    @Inject
    lateinit var mPresenter: MainActivityContract.Presenter

    private lateinit var mAdapter: NoteListAdapter
    private lateinit var mBottomSheet: BottomSheetBehavior<FrameLayout>
    private lateinit var mOnDrawerListener: ActionBarDrawerToggle
    private var mSearchQuery = ""
    private var mRecyclerViewState: Parcelable? = null
    private var mBackPressCount = 0
    private var mMenu: Menu? = null
    private var mIsAppBarExpandEnabled = false
    private val mHandler = Handler()
    private var mStatusBarHeight = 0
    private val mBottomSheetOpenRunnable: Runnable = Runnable {
        mBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private val mQuickScrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            val topChildPosition = (recyclerView.layoutManager as LinearLayoutManager?)
                    ?.findFirstVisibleItemPosition() ?: 0
            if (topChildPosition > 0) {
                fab_quick_scroll.show()
            } else {
                fab_quick_scroll.hide()
            }
        }
    }

    private val mBottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            view_actionbar?.layoutParams?.height = (mStatusBarHeight * slideOffset).toInt()
            view_actionbar?.requestLayout()
        }
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                supportFragmentManager.findFragmentByTag(AuthFragment.TAG)?.let {
                    (it as AuthFragment).clearFocus()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(this).inject(this)
        super.onCreate(savedInstanceState)
        loadOwnedPurchasesFromGoogle()

        mPresenter.onRestoreInstanceState(savedInstanceState)

        setSupportActionBar(toolbar_main)
        supportActionBar?.let { toolbar ->
            toolbar.setDisplayShowTitleEnabled(false)
            toolbar.setDisplayHomeAsUpEnabled(true)
            toolbar.setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        layout_main.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN

        val drawerWidth = min(getDisplayWidth() - TOOLBAR_HEIGHT, TOOLBAR_HEIGHT * 5)
        container_main_drawer.layoutParams.width = drawerWidth

        drawer.touchEventChildId = R.id.calendar_search

        mOnDrawerListener = object : ActionBarDrawerToggle(this, drawer, toolbar_main, R.string.open, R.string.close) {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                layout_main_root.translationX = slideOffset * drawerView.width * 0.3f
            }
        }

        if (supportFragmentManager.findFragmentByTag(DrawerMenuFragment.TAG) == null) {
            supportFragmentManager.inTransaction {
                add(R.id.container_main_drawer, DrawerMenuFragment(), DrawerMenuFragment.TAG)
            }
        }

        mBottomSheet = BottomSheetBehavior.from(main_sheet_container)

        savedInstanceState?.let { state ->
            mRecyclerViewState = state.getParcelable(BUNDLE_RECYCLER_VIEW_STATE)
            layout_main_root.translationX = state.getFloat(BUNDLE_ROOT_LAYOUT_OFFSET, 0f)
            mSearchQuery = state.getString(BUNDLE_SEARCH_QUERY, "")
            mBottomSheet.state = state.getInt(BUNDLE_BOTTOM_SHEET_STATE, BottomSheetBehavior.STATE_COLLAPSED)
            if (mBottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
                view_actionbar.layoutParams.height = state.getInt(BUNDLE_STATUS_BAR_HEIGHT, 0)
            }
        }

        button_change_filters.setOnClickListener { mPresenter.onButtonChangeFiltersClick() }

        fab_menu.setOnClickListener { mPresenter.onFabMenuClick() }
        fab_delete.setOnClickListener { mPresenter.onButtonDeleteClick() }
        fab_folder.setOnClickListener { mPresenter.onButtonFolderClick() }

        image_toolbar_main.setOnClickListener { mPresenter.onMainImageClick() }
        button_main_image_settings.setOnClickListener {
            analytics.sendEvent(MyAnalytics.EVENT_HEADER_IMAGE_SETTINGS)
            mPresenter.onButtonImageSettingsClick()
        }

        mAdapter = NoteListAdapter(this)
        list_main.layoutManager = LinearLayoutManager(this)
        list_main.adapter = mAdapter
        list_main.setHasFixedSize(true)
        list_main.itemAnimator = LandingAnimator()
        list_main.setItemViewCacheSize(15)
        list_main.addItemDecoration(StickyHeaderItemDecoration(list_main, mAdapter))

        fab_quick_scroll.hide()
        fab_quick_scroll.setOnClickListener {
            list_main.scrollToPosition(0)
            if (mIsAppBarExpandEnabled) {
                app_bar_layout.setExpanded(true, true)
            }
        }

        layout_main.setOnApplyWindowInsetsListener { _, insets ->
            mStatusBarHeight = insets.systemWindowInsetTop

            val toolbarParams = toolbar_main.layoutParams as ViewGroup.MarginLayoutParams
            toolbarParams.topMargin = mStatusBarHeight

            val sheetParams = layout_main_sheet_wrapper.layoutParams as ViewGroup.MarginLayoutParams
            sheetParams.topMargin = mStatusBarHeight

            (supportFragmentManager.findFragmentByTag(DrawerMenuFragment.TAG) as DrawerMenuFragment?)
                    ?.onApplyWindowInsets(insets)

            return@setOnApplyWindowInsetsListener insets
        }
    }

    override fun onTagCheckStateChange(tag: MyTag, checked: Boolean) {
        mPresenter.onTagFilterChange(tag, checked)
    }

    override fun onCategoryCheckStateChange(category: MyCategory, checked: Boolean) {
        mPresenter.onCategoryFilterChange(category, checked)
    }

    override fun onLocationCheckStateChange(location: MyLocation, checked: Boolean) {
        mPresenter.onLocationFilterChange(location, checked)
    }

    override fun onMoodCheckStateChange(mood: MyMood, checked: Boolean) {
        mPresenter.onMoodFilterChange(mood, checked)
    }

    override fun onNoTagsCheckStateChange(checked: Boolean) {
        mPresenter.onNoTagsFilterChange(checked)
    }

    override fun onNoCategoryCheckStateChange(checked: Boolean) {
        mPresenter.onNoCategoryFilterChange(checked)
    }

    override fun onNoMoodCheckStateChange(checked: Boolean) {
        mPresenter.onNoMoodFilterChange(checked)
    }

    override fun onNoLocationCheckStateChange(checked: Boolean) {
        mPresenter.onNoLocationFilterChange(checked)
    }

    override fun onSearchDatesSelected(startDate: Long?, endDate: Long?) {
        mPresenter.onDateFilterChange(startDate, endDate)
    }

    override fun onBillingInitialized() {
        super.onBillingInitialized()
        if (!getIsItemPurchased(BuildConfig.ITEM_PREMIUM_SKU)) {
            showAdView()
        }
    }

    override fun onProductPurchased(productId: String, details: TransactionDetails?) {
        super.onProductPurchased(productId, details)
        if (productId == BuildConfig.ITEM_PREMIUM_SKU) {
            closeBottomSheet()
            hideAdView()
        }
    }

    override fun getIsBillingInitialized(): Boolean = isBillingInitialized()

    override fun getIsItemPurchased(productId: String): Boolean = isItemPurchased(productId)

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

    override fun showHeaderImage(image: MyHeaderImage) {
        Log.e(TAG, "showHeaderImage")
        GlideApp.with(this)
                .load(image.url)
                .transition(DrawableTransitionOptions.withCrossFade())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                .signature(ObjectKey(image.id))
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?,
                                              isFirstResource: Boolean): Boolean {
                        disableActionBarExpanding(false)
                        return true
                    }
                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?,
                                                 dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        if (!mIsAppBarExpandEnabled) {
                            enableActionBarExpanding(true)
                        }
                        return false
                    }
                })
                .into(image_toolbar_main)
    }

    override fun showEmptyHeaderImage(hasError: Boolean) {
        Log.e(TAG, "showEmptyHeaderImage")
        if (hasError) {
            analytics.sendEvent(MyAnalytics.EVENT_DAILY_IMAGE_LOAD_ERROR)
        }
        disableActionBarExpanding()
    }

    private fun disableActionBarExpanding(animate: Boolean = false) {
        val appBarParams =
                collapsing_toolbar_main.layoutParams as AppBarLayout.LayoutParams
        appBarParams.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or
                AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
        collapsing_toolbar_main.layoutParams = appBarParams
        app_bar_layout.setExpanded(false, animate)
        val coordParams =
                app_bar_layout.layoutParams as CoordinatorLayout.LayoutParams
        (coordParams.behavior as AppBarLayoutBehavior).shouldScroll = false
        mIsAppBarExpandEnabled = false
    }

    private fun enableActionBarExpanding(expand: Boolean) {
        val appBarParams =
                collapsing_toolbar_main.layoutParams as AppBarLayout.LayoutParams
        val orientation = resources.configuration.orientation
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            appBarParams.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or
                    AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS or
                    AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS_COLLAPSED
            collapsing_toolbar_main.layoutParams = appBarParams
        } else {
            appBarParams.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or
                    AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
            collapsing_toolbar_main.layoutParams = appBarParams
        }
        if (expand) {
            app_bar_layout.setExpanded(true)
        }
        val coordParams =
                app_bar_layout.layoutParams as CoordinatorLayout.LayoutParams
        (coordParams.behavior as AppBarLayoutBehavior).shouldScroll = true
        mIsAppBarExpandEnabled = true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            when (item.itemId) {
                R.id.menu_all_notes -> {
                    mPresenter.onMenuAllNotesClick()
                    true
                }
                /*R.id.menu_statistics -> {
                    mPresenter.onButtonStatisticsClick()
                    true
                }*/
                R.id.menu_image -> {
                    analytics.sendEvent(MyAnalytics.EVENT_HEADER_IMAGE_SETTINGS)
                    mPresenter.onButtonImageSettingsClick()
                    true
                }
                R.id.menu_settings -> {
                    analytics.sendEvent(MyAnalytics.EVENT_MAIN_SETTINGS)
                    mPresenter.onButtonSettingsClick()
                    true
                }
                R.id.menu_sort -> {
                    mPresenter.onButtonSortClick()
                    true
                }
                else -> super.onOptionsItemSelected(item)
            }

    override fun showDeleteConfirmationDialog(noteIds: List<String>) {
        DeleteNoteDialog.newInstance(noteIds).apply {
            setOnDeleteConfirmListener(this@MainActivity)
        }.show(supportFragmentManager, DeleteNoteDialog.TAG)
    }

    override fun onDialogButtonDeleteClick() {
        mPresenter.onButtonDeleteConfirmClick()
    }

    override fun showImageOptions() {
        layout_main_image_settings.visibility = View.VISIBLE
        layout_main_image_settings.startAnimation(AlphaAnimation(1f, 0f).apply {
            duration = ANIMATION_IMAGE_SETTINGS_FADE_OUT_DURATION
            startOffset = ANIMATION_IMAGE_SETTINGS_FADE_OUT_OFFSET
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationRepeat(animation: Animation?) {}
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationEnd(animation: Animation?) {
                    layout_main_image_settings.visibility = View.GONE
                }
            })
        })
    }

    override fun showViewImageSettings() {
        analytics.sendEvent(MyAnalytics.EVENT_DAILY_IMAGE_OPEN)
        if (supportFragmentManager.findFragmentByTag(ImageSettingsFragment.TAG) == null) {
            supportFragmentManager.inTransaction {
                replace(R.id.main_sheet_container, ImageSettingsFragment(), ImageSettingsFragment.TAG)
                Log.e(TAG, "ImageSettingsFragment added")
            }
        }
        mHandler.postDelayed(mBottomSheetOpenRunnable, BOTTOM_SHEET_EXPAND_DELAY)
    }

    override fun showStatisticsView() {
        startActivity(Intent(this, StatsActivity::class.java))
    }

    override fun setSortDesc() {
        mMenu?.findItem(R.id.menu_sort)?.setTitle(R.string.sort_old_first)
    }

    override fun setSortAsc() {
        mMenu?.findItem(R.id.menu_sort)?.setTitle(R.string.sort_new_first)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        mMenu = menu
        menuInflater.inflate(R.menu.activity_main_toolbar_menu, menu)

        val searchView = menu?.findItem(R.id.menu_search)?.actionView as SearchView?

        searchView?.maxWidth = dpToPx(1000f)

        if (mSearchQuery.isNotEmpty()) {
            disableActionBarExpanding()
            searchView?.isIconified = false
            searchView?.setQuery(mSearchQuery, false)
        }

        searchView?.setOnSearchClickListener {
            analytics.sendEvent(MyAnalytics.EVENT_SEARCH_WORD_OPENED)
            disableActionBarExpanding(true)
        }

        searchView?.setOnCloseListener {
            enableActionBarExpanding(false)
            return@setOnCloseListener false
        }

        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                mSearchQuery = newText ?: ""
                mPresenter.onSearchQueryChange(mSearchQuery)
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(BUNDLE_RECYCLER_VIEW_STATE, list_main.layoutManager?.onSaveInstanceState())
        outState.putFloat(BUNDLE_ROOT_LAYOUT_OFFSET, layout_main_root.translationX)
        outState.putInt(BUNDLE_BOTTOM_SHEET_STATE, mBottomSheet.state)
        outState.putString(BUNDLE_SEARCH_QUERY, mSearchQuery)
        outState.putInt(BUNDLE_STATUS_BAR_HEIGHT, mStatusBarHeight)
        mPresenter.onSaveInstanceState(outState)
    }

    override fun showViewNewNote(noteId: String) {
        analytics.sendEvent(MyAnalytics.EVENT_NOTE_ADDED)
        startActivity(NoteActivity.newIntentModeAdd(this, noteId))
    }

    override fun showNotes(notes: List<MyNoteWithProp>, selectedNoteIds: Set<String>, scrollToTop: Boolean) {
        Log.e(TAG, "showNotes")
        mAdapter.selectedNoteIds.clear()
        mAdapter.selectedNoteIds.addAll(selectedNoteIds)
        mAdapter.submitList(toNoteViewItem(notes.map { it.copy() }))
        mRecyclerViewState?.let {
            list_main.layoutManager?.onRestoreInstanceState(it)
            mRecyclerViewState = null
        }

        if (selectedNoteIds.isEmpty()) {
            fab_menu.close(true)
        } else {
            fab_menu.open(true)
        }

        text_selected_note_counter.text = if (selectedNoteIds.isNotEmpty()) {
            getString(R.string.counter_format, selectedNoteIds.size, notes.size)
        } else {
            ""
        }

        if (scrollToTop) {
            list_main.scrollToPosition(0)
        }
    }

    private fun toNoteViewItem(notes: List<MyNoteWithProp>): ArrayList<NoteItemView> {
        val sortAsc = notes.size > 1 && notes[0].note.time < notes[1].note.time
        val map = TreeMap<Long, ArrayList<MyNoteWithProp>>(Comparator<Long> { p0, p1 ->
            if (sortAsc) {
                p0.compareTo(p1)
            } else {
                p1.compareTo(p0)
            }
        })
        for (note in notes) {
            val dateTime = DateTime(note.note.time).dayOfMonth()
                    .withMinimumValue()
                    .withTimeAtStartOfDay()
                    .withMillisOfDay(0)
            var value = map[dateTime.millis]
            if (value == null) {
                value = ArrayList()
                map[dateTime.millis] = value
            }
            value.add(note)
        }

        val list = ArrayList<NoteItemView>()
        for (date in map.keys) {
            val header = NoteItemView(type = NoteItemView.TYPE_HEADER, time = date)
            list.add(header)
            val values = if (sortAsc) {
                map.getValue(date).sortedBy { it.note.time }
            } else {
                map.getValue(date).sortedByDescending { it.note.time }
            }

            for (note in values) {
                if (note.images.isEmpty()) {
                    list.add(NoteItemView(NoteItemView.TYPE_NOTE_WITH_TEXT, note))
                } else {
                    list.add(NoteItemView(NoteItemView.TYPE_NOTE_WITH_IMAGE, note))
                }
            }
        }
        return list
    }

    override fun showEmptyNoteList() {
        mAdapter.submitList(emptyList())
        if (empty_state.visibility != View.VISIBLE && (empty_state.animation == null || empty_state.animation.hasEnded())) {
            empty_state.visibility = View.VISIBLE
            app_bar_layout.setExpanded(false, true)
            empty_state.translationY = empty_state.height.toFloat()
            empty_state.animate()
                    .translationY(0f)
                    .setDuration(ANIMATION_EMPTY_SATE_DURATION)
                    .interpolator = DecelerateInterpolator()
        }
    }

    override fun hideEmptyNoteList() {
        if (empty_state.visibility != View.GONE) {
            empty_state.visibility = View.GONE
        }
    }

    override fun showNoSearchResults() {
        if (empty_search.visibility != View.VISIBLE) {
            empty_search.visibility = View.VISIBLE
            ViewCompat.animate(empty_search)
                    .alpha(1f)
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(ANIMATION_NO_SEARCH_RESULT_DURATION)
                    .setInterpolator(DecelerateInterpolator())
                    .setListener(null)
                    .start()
        }
    }

    override fun hideNoSearchResults() {
        if (empty_search.visibility != View.GONE) {
            ViewCompat.animate(empty_search)
                    .alpha(0f)
                    .scaleX(ANIMATION_NO_SEARCH_RESULT_SIZE)
                    .scaleY(ANIMATION_NO_SEARCH_RESULT_SIZE)
                    .setDuration(ANIMATION_NO_SEARCH_RESULT_DURATION)
                    .setInterpolator(AccelerateInterpolator())
                    .setListener(object : ViewPropertyAnimatorListener {
                        override fun onAnimationStart(view: View?) {}
                        override fun onAnimationCancel(view: View?) {
                            onAnimationEnd(view)
                        }

                        override fun onAnimationEnd(view: View?) {
                            view?.visibility = View.GONE
                        }
                    })
                    .start()
        }
    }

    override fun onMainListItemClick(note: MyNoteWithProp) {
        mPresenter.onMainListItemClick(note)
    }

    override fun onMainListItemLongClick(note: MyNoteWithProp) {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            @Suppress("DEPRECATION")
            vibrator.vibrate(ITEM_LONG_CLICK_VIBRATION_DURATION)
        }
        mPresenter.onMainListItemLongClick(note)
    }

    override fun showNotePager(position: Int, noteId: String) {
        analytics.sendEvent(MyAnalytics.EVENT_NOTE_OPENED)
        startActivity(NoteActivity.newIntentModeRead(this, noteId, position))
    }

    override fun showSettingsView() {
        val intent = Intent(this, GlobalSettingsActivity::class.java)
        startActivityForResult(intent, ACTIVITY_SETTING_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTIVITY_SETTING_REQUEST_CODE) {
            recreate()
        }
    }

    override fun showAnonymousProfile() {
        mAdapter.syncEmail = null
        mAdapter.notifyDataSetChanged()
    }

    override fun showProfile(profile: MyProfile) {
        if (isItemPurchased(BuildConfig.ITEM_PREMIUM_SKU)) {
            mAdapter.syncEmail = profile.email
        } else {
            mAdapter.syncEmail = null
        }
        mAdapter.notifyDataSetChanged()
    }

    fun closeBottomSheet() {
        mBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
    }

    override fun onBackPressed() {
        val authFragment =
                supportFragmentManager.findFragmentByTag(AuthFragment.TAG) as? AuthFragment
        val profileFragment =
                supportFragmentManager.findFragmentByTag(ProfileFragment.TAG) as? ProfileFragment
        when {
            authFragment != null && !authFragment.isBackStackEmpty() -> super.onBackPressed()
            profileFragment != null && !profileFragment.isBackStackEmpty() -> super.onBackPressed()
            supportFragmentManager.backStackEntryCount > 0 -> super.onBackPressed()
            mBottomSheet.state == BottomSheetBehavior.STATE_EXPANDED ->
                mBottomSheet.state = BottomSheetBehavior.STATE_COLLAPSED
            drawer.isDrawerOpen(GravityCompat.START) ->
                drawer.closeDrawer(GravityCompat.START, true)
            fab_menu.isOpened -> {
                mPresenter.onFabMenuClick()
            }
            ++mBackPressCount < 2 ->
                Toast.makeText(this, getString(R.string.click_again_to_exit), Toast.LENGTH_SHORT).show()
            else -> super.onBackPressed()
        }
    }

    override fun showCategoriesView(noteIds: List<String>) {
        CategoriesDialog.newInstance(noteIds).apply {
            setOnCategorySelectedListener(this@MainActivity)
        }.show(supportFragmentManager, CategoriesDialog.TAG)
    }

    override fun onCategorySelected() {
        mPresenter.onCategorySelected()
    }

    override fun onClearFilters() {
        mPresenter.onClearFilters()
    }

    override fun onButtonPurchaseClick(productId: String) {
        if (isOneTimePurchaseSupported()) {
            purchaseItem(productId)
        } else {
            Toast.makeText(this, getString(R.string.not_available), Toast.LENGTH_SHORT).show()
        }
    }

    override fun showChangeFilters() {
        drawer.openDrawer(GravityCompat.START, true)
    }

    override fun onDailyImageLoadStateChange() {
        mPresenter.onDailyImageLoadStateChange()
    }

    override fun showRateProposal() {
        if (supportFragmentManager.findFragmentByTag(RateDialog.TAG) == null) {
            RateDialog().show(supportFragmentManager, RateDialog.TAG)
        }
    }

    override fun onStart() {
        super.onStart()
        mBottomSheet.addBottomSheetCallback(mBottomSheetCallback)
        mAdapter.listener = this
        drawer.addDrawerListener(mOnDrawerListener)
        list_main.addOnScrollListener(mAdapter.preloader)
        list_main.addOnScrollListener(mQuickScrollListener)
        (supportFragmentManager.findFragmentByTag(DeleteNoteDialog.TAG) as? DeleteNoteDialog)
                ?.setOnDeleteConfirmListener(this)
        supportFragmentManager.findFragmentByTag(CategoriesDialog.TAG)?.let {
            (it as CategoriesDialog).setOnCategorySelectedListener(this)
        }

        mPresenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        mPresenter.detachView()
        mBottomSheet.removeBottomSheetCallback(mBottomSheetCallback)
        mBackPressCount = 0
        mAdapter.listener = null
        drawer.removeDrawerListener(mOnDrawerListener)
        list_main.removeOnScrollListener(mAdapter.preloader)
        list_main.removeOnScrollListener(mQuickScrollListener)
        mHandler.removeCallbacks(mBottomSheetOpenRunnable)
    }
}
