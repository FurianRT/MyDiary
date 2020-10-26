/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.graphics.drawable.Drawable
import android.os.*
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
import com.furianrt.mydiary.presentation.base.BaseActivity
import com.furianrt.mydiary.model.entity.*
import com.furianrt.mydiary.presentation.dialogs.categories.CategoriesDialog
import com.furianrt.mydiary.presentation.dialogs.delete.note.DeleteNoteDialog
import com.furianrt.mydiary.presentation.general.AppBarLayoutBehavior
import com.furianrt.mydiary.presentation.general.GlideApp
import com.furianrt.mydiary.presentation.screens.main.fragments.authentication.AuthFragment
import com.furianrt.mydiary.presentation.screens.main.fragments.drawer.DrawerMenuFragment
import com.furianrt.mydiary.presentation.screens.main.fragments.imagesettings.ImageSettingsFragment
import com.furianrt.mydiary.presentation.screens.main.fragments.imagesettings.settings.DailySettingsFragment
import com.furianrt.mydiary.presentation.screens.main.fragments.premium.PremiumFragment
import com.furianrt.mydiary.presentation.screens.main.fragments.profile.ProfileFragment
import com.furianrt.mydiary.presentation.screens.note.NoteActivity
import com.furianrt.mydiary.presentation.screens.settings.global.GlobalSettingsActivity
import com.furianrt.mydiary.utils.dpToPx
import com.furianrt.mydiary.utils.getDisplayWidth
import com.furianrt.mydiary.utils.inTransaction
import com.furianrt.mydiary.presentation.general.StickyHeaderItemDecoration
import com.furianrt.mydiary.presentation.screens.main.adapter.NoteListAdapter
import com.furianrt.mydiary.presentation.screens.main.adapter.NoteListItem
import com.furianrt.mydiary.presentation.screens.statistics.StatsActivity
import com.furianrt.mydiary.utils.getWindowInsetTop
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.play.core.review.ReviewManagerFactory
import jp.wasabeef.recyclerview.animators.LandingAnimator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_toolbar.*
import kotlinx.android.synthetic.main.bottom_sheet_main.*
import kotlinx.android.synthetic.main.empty_search_note_list.*
import kotlinx.android.synthetic.main.empty_state_note_list.*
import org.joda.time.DateTime
import java.util.TreeMap
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.math.min

class MainActivity : BaseActivity(R.layout.activity_main), MainActivityContract.View,
        NoteListAdapter.OnMainListItemInteractionListener,
        DailySettingsFragment.OnImageSettingsInteractionListener,
        DeleteNoteDialog.OnDeleteNoteConfirmListener, CategoriesDialog.OnCategorySelectedListener,
        PremiumFragment.OnPremiumFragmentInteractionListener,
        DrawerMenuFragment.OnDrawerMenuInteractionListener,
        MainBottomSheetHolder {

    companion object {
        private const val TAG = "MainActivity"
        private const val BUNDLE_RECYCLER_VIEW_STATE = "recycler_state"
        private const val BUNDLE_ROOT_LAYOUT_OFFSET = "root_layout_offset"
        private const val BUNDLE_BOTTOM_SHEET_STATE = "bottom_sheet_state"
        private const val BUNDLE_SEARCH_QUERY = "query"
        private const val BUNDLE_STATUS_BAR_HEIGHT = "status_bar_height"
        private const val ACTIVITY_SETTING_REQUEST_CODE = 2
        private const val LIST_ITEM_CACHE_SIZE = 15
        private const val ITEM_LONG_CLICK_VIBRATION_DURATION = 30L
        private const val BOTTOM_SHEET_EXPAND_DELAY = 500L
        private const val ANIMATION_IMAGE_SETTINGS_FADE_OUT_DURATION = 350L
        private const val ANIMATION_IMAGE_SETTINGS_FADE_OUT_OFFSET = 2000L
        private const val ANIMATION_NO_SEARCH_RESULT_DURATION = 200L
        private const val ANIMATION_NO_SEARCH_RESULT_SIZE = 1.4f
        private const val ANIMATION_EMPTY_SATE_DURATION = 500L
        private const val REQUEST_CODE_NOTE_ACTIVITY = 23
        private val TOOLBAR_HEIGHT = dpToPx(56f)

        @JvmStatic
        fun getLauncherIntent(applicationContext: Context): Intent =
                Intent(applicationContext, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    action = Intent.ACTION_MAIN
                    addCategory(Intent.CATEGORY_LAUNCHER)
                }
    }

    @Inject
    lateinit var presenter: MainActivityContract.Presenter

    private lateinit var mAdapter: NoteListAdapter
    private lateinit var mBottomSheet: BottomSheetBehavior<FrameLayout>
    private lateinit var mOnDrawerListener: ActionBarDrawerToggle
    private var mSearchQuery = ""
    private var mRecyclerViewState: Parcelable? = null
    private var mBackPressCount = 0
    private var mMenu: Menu? = null
    private var mIsAppBarExpandEnabled = false
    private val mHandler = Handler(Looper.getMainLooper())
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
            if (newState == BottomSheetBehavior.STATE_COLLAPSED || newState == BottomSheetBehavior.STATE_HIDDEN) {
                supportFragmentManager.findFragmentByTag(AuthFragment.TAG)?.let { fragment ->
                    supportFragmentManager.inTransaction { remove(fragment) }
                }
                supportFragmentManager.findFragmentByTag(ProfileFragment.TAG)?.let { fragment ->
                    supportFragmentManager.inTransaction { remove(fragment) }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(this).inject(this)
        super.onCreate(savedInstanceState)

        if (resources.getBoolean(R.bool.portrait_only)) {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }

        presenter.onRestoreInstanceState(savedInstanceState)

        setSupportActionBar(toolbar_main)
        supportActionBar?.let { toolbar ->
            toolbar.setDisplayShowTitleEnabled(false)
            toolbar.setDisplayHomeAsUpEnabled(true)
            toolbar.setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
              window.setDecorFitsSystemWindows(false)
          } else {
              layout_main.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
          }*/

        val drawerWidth = min(getDisplayWidth() - TOOLBAR_HEIGHT, TOOLBAR_HEIGHT * 5)
        container_main_drawer.layoutParams.width = drawerWidth

        drawer.touchEventChildId = R.id.calendar_search

        mOnDrawerListener = object : ActionBarDrawerToggle(this, drawer, toolbar_main, R.string.open, R.string.close) {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                layout_main_root.translationX = slideOffset * drawerView.width * 0.2f
            }
        }

        if (supportFragmentManager.findFragmentByTag(DrawerMenuFragment.TAG) == null) {
            supportFragmentManager.inTransaction {
                add(R.id.container_main_drawer, DrawerMenuFragment(), DrawerMenuFragment.TAG)
            }
        }

        mBottomSheet = BottomSheetBehavior.from(main_sheet_container)
        closeBottomSheet()

        savedInstanceState?.let { state ->
            mRecyclerViewState = state.getParcelable(BUNDLE_RECYCLER_VIEW_STATE)
            layout_main_root.translationX = state.getFloat(BUNDLE_ROOT_LAYOUT_OFFSET, 0f)
            mSearchQuery = state.getString(BUNDLE_SEARCH_QUERY, "")
            mBottomSheet.state = state.getInt(BUNDLE_BOTTOM_SHEET_STATE, BottomSheetBehavior.STATE_HIDDEN)
            if (mBottomSheet.state == BottomSheetBehavior.STATE_EXPANDED) {
                view_actionbar.layoutParams.height = state.getInt(BUNDLE_STATUS_BAR_HEIGHT, 0)
            }
        }

        button_change_filters.setOnClickListener { presenter.onButtonChangeFiltersClick() }
        button_first_note.setOnClickListener { presenter.onFabMenuClick() }
        fab_menu.setOnClickListener { presenter.onFabMenuClick() }
        fab_delete.setOnClickListener { presenter.onButtonDeleteClick() }
        fab_folder.setOnClickListener { presenter.onButtonFolderClick() }
        image_toolbar_main.setOnClickListener { presenter.onMainImageClick() }
        button_main_image_settings.setOnClickListener {
            analytics.sendEvent(MyAnalytics.EVENT_HEADER_IMAGE_SETTINGS)
            presenter.onButtonImageSettingsClick()
        }

        mAdapter = NoteListAdapter(this)
        list_main.layoutManager = LinearLayoutManager(this)
        list_main.adapter = mAdapter
        list_main.setHasFixedSize(true)
        list_main.itemAnimator = LandingAnimator()
        list_main.setItemViewCacheSize(LIST_ITEM_CACHE_SIZE)
        list_main.addItemDecoration(StickyHeaderItemDecoration(list_main, mAdapter))

        fab_quick_scroll.hide()
        fab_quick_scroll.setOnClickListener {
            list_main.scrollToPosition(0)
            if (mIsAppBarExpandEnabled) {
                app_bar_layout.setExpanded(true, true)
            }
        }

        layout_main.setOnApplyWindowInsetsListener { _, insets ->
            mStatusBarHeight = insets.getWindowInsetTop()

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
        presenter.onTagFilterChange(tag, checked)
    }

    override fun onCategoryCheckStateChange(category: MyCategory, checked: Boolean) {
        presenter.onCategoryFilterChange(category, checked)
    }

    override fun onLocationCheckStateChange(location: MyLocation, checked: Boolean) {
        presenter.onLocationFilterChange(location, checked)
    }

    override fun onMoodCheckStateChange(mood: MyMood, checked: Boolean) {
        presenter.onMoodFilterChange(mood, checked)
    }

    override fun onNoTagsCheckStateChange(checked: Boolean) {
        presenter.onNoTagsFilterChange(checked)
    }

    override fun onNoCategoryCheckStateChange(checked: Boolean) {
        presenter.onNoCategoryFilterChange(checked)
    }

    override fun onNoMoodCheckStateChange(checked: Boolean) {
        presenter.onNoMoodFilterChange(checked)
    }

    override fun onNoLocationCheckStateChange(checked: Boolean) {
        presenter.onNoLocationFilterChange(checked)
    }

    override fun onSearchDatesSelected(startDate: Long?, endDate: Long?) {
        presenter.onDateFilterChange(startDate, endDate)
    }

    override fun onProductPurchased(productId: String, details: TransactionDetails?) {
        super.onProductPurchased(productId, details)
        if (productId == BuildConfig.ITEM_PREMIUM_SKU) {
            closeBottomSheet()
        }
    }

    override fun getIsBillingInitialized(): Boolean = isBillingInitialized()

    override fun loadOwnedPurchases(): Boolean = loadOwnedPurchasesFromGoogle()

    override fun getIsItemPurchased(productId: String): Boolean = isItemPurchased(productId)

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
                    presenter.onMenuAllNotesClick()
                    true
                }
                /*R.id.menu_statistics -> {
                    presenter.onButtonStatisticsClick()
                    true
                }*/
                R.id.menu_image -> {
                    analytics.sendEvent(MyAnalytics.EVENT_HEADER_IMAGE_SETTINGS)
                    presenter.onButtonImageSettingsClick()
                    true
                }
                R.id.menu_settings -> {
                    analytics.sendEvent(MyAnalytics.EVENT_MAIN_SETTINGS)
                    presenter.onButtonSettingsClick()
                    true
                }
                R.id.menu_sort -> {
                    presenter.onButtonSortClick()
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
        presenter.onButtonDeleteConfirmClick()
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
                presenter.onSearchQueryChange(mSearchQuery)
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
        presenter.onSaveInstanceState(outState)
    }

    override fun showViewNewNote(noteId: String) {
        analytics.sendEvent(MyAnalytics.EVENT_NOTE_ADDED)
        startActivityForResult(
                NoteActivity.newIntentModeAdd(this, noteId),
                REQUEST_CODE_NOTE_ACTIVITY
        )
    }

    override fun showNotes(notes: List<MyNoteWithProp>, selectedNoteIds: Set<String>, scrollToTop: Boolean) {
        Log.e(TAG, "showNotes")
        mAdapter.submitList(toNoteViewItem(notes.map { it.copy() }, selectedNoteIds)) {
            mRecyclerViewState?.let {
                list_main.layoutManager?.onRestoreInstanceState(it)
                mRecyclerViewState = null
            }

            if (scrollToTop) {
                list_main.scrollToPosition(0)
            }
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
    }

    private fun toNoteViewItem(notes: List<MyNoteWithProp>, selectedIds: Set<String>): List<NoteListItem> {
        val sortAsc = notes.size > 1 && notes[0].note.time < notes[1].note.time
        val map = TreeMap<Long, ArrayList<MyNoteWithProp>> { p0, p1 ->
            if (sortAsc) {
                p0.compareTo(p1)
            } else {
                p1.compareTo(p0)
            }
        }
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

        val result = mutableListOf<NoteListItem>()
        for (date in map.keys) {
            val header = NoteListItem.Date(date)
            result.add(header)
            val values = if (sortAsc) {
                map.getValue(date).sortedBy { it.note.time }
            } else {
                map.getValue(date).sortedByDescending { it.note.time }
            }

            for (note in values) {
                if (note.images.isEmpty()) {
                    result.add(NoteListItem.WithText(note, selectedIds.contains(note.note.id)))
                } else {
                    result.add(NoteListItem.WithImage(note, selectedIds.contains(note.note.id)))
                }
            }
        }
        return result
    }

    override fun showEmptyNoteList() {
        mAdapter.submitList(emptyList())
        if (empty_state.visibility != View.VISIBLE && (empty_state.animation == null || empty_state.animation.hasEnded())) {
            anim_image_empty_note_list.playAnimation()
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
            anim_image_empty_note_list.cancelAnimation()
            empty_state.visibility = View.GONE
        }
    }

    override fun showNoSearchResults() {
        if (empty_search.visibility != View.VISIBLE) {
            empty_search.visibility = View.VISIBLE
            anim_image_empty_search.playAnimation()
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
            anim_image_empty_search.cancelAnimation()
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
        presenter.onMainListItemClick(note)
    }

    override fun onMainListItemLongClick(note: MyNoteWithProp) {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator?
        if (vibrator?.hasVibrator() == true) {
            @Suppress("DEPRECATION")
            vibrator.vibrate(ITEM_LONG_CLICK_VIBRATION_DURATION)
        }
        presenter.onMainListItemLongClick(note)
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
        if (requestCode == ACTIVITY_SETTING_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
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

    override fun closeBottomSheet() {
        mBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
    }

    override fun onBackPressed() {
        val authFragment = supportFragmentManager.findFragmentByTag(AuthFragment.TAG) as? AuthFragment?
        val profileFragment = supportFragmentManager.findFragmentByTag(ProfileFragment.TAG) as? ProfileFragment?
        when {
            authFragment != null && !authFragment.isBackStackEmpty() -> super.onBackPressed()
            profileFragment != null && !profileFragment.isBackStackEmpty() -> super.onBackPressed()
            supportFragmentManager.backStackEntryCount > 0 -> super.onBackPressed()
            mBottomSheet.state == BottomSheetBehavior.STATE_EXPANDED ->
                mBottomSheet.state = BottomSheetBehavior.STATE_HIDDEN
            drawer.isDrawerOpen(GravityCompat.START) ->
                drawer.closeDrawer(GravityCompat.START, true)
            fab_menu.isOpened -> {
                presenter.onFabMenuClick()
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
        presenter.onCategorySelected()
    }

    override fun onClearFilters() {
        presenter.onClearFilters()
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
        presenter.onDailyImageLoadStateChange()
    }

    override fun showRateProposal() {
        val manager = ReviewManagerFactory.create(this)
        manager.requestReviewFlow().addOnCompleteListener { reviewFlow ->
            if (reviewFlow.isSuccessful) {
                manager.launchReviewFlow(this, reviewFlow.result).addOnCompleteListener {
                    presenter.onUserReviewComplete()
                }
            }
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

        presenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        presenter.detachView()
        mBottomSheet.removeBottomSheetCallback(mBottomSheetCallback)
        mBackPressCount = 0
        mAdapter.listener = null
        drawer.removeDrawerListener(mOnDrawerListener)
        list_main.removeOnScrollListener(mAdapter.preloader)
        list_main.removeOnScrollListener(mQuickScrollListener)
        mHandler.removeCallbacks(mBottomSheetOpenRunnable)
    }
}
