package com.furianrt.mydiary.screens.main

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.os.Vibrator
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
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
import com.anjlab.android.iab.v3.TransactionDetails
import com.furianrt.mydiary.BuildConfig
import com.furianrt.mydiary.R
import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.base.BaseActivity
import com.furianrt.mydiary.data.model.*
import com.furianrt.mydiary.dialogs.categories.CategoriesDialog
import com.furianrt.mydiary.dialogs.delete.note.DeleteNoteDialog
import com.furianrt.mydiary.dialogs.rate.RateDialog
import com.furianrt.mydiary.general.AppBarLayoutBehavior
import com.furianrt.mydiary.general.GlideApp
import com.furianrt.mydiary.general.HeaderItemDecoration
import com.furianrt.mydiary.screens.main.adapter.NoteListAdapter
import com.furianrt.mydiary.screens.main.adapter.NoteListItem
import com.furianrt.mydiary.screens.main.fragments.authentication.AuthFragment
import com.furianrt.mydiary.screens.main.fragments.drawer.DrawerMenuFragment
import com.furianrt.mydiary.screens.main.fragments.imagesettings.ImageSettingsFragment
import com.furianrt.mydiary.screens.main.fragments.imagesettings.settings.DailySettingsFragment
import com.furianrt.mydiary.screens.main.fragments.premium.PremiumFragment
import com.furianrt.mydiary.screens.main.fragments.profile.ProfileFragment
import com.furianrt.mydiary.screens.note.NoteActivity
import com.furianrt.mydiary.screens.settings.global.GlobalSettingsActivity
import com.furianrt.mydiary.utils.dpToPx
import com.furianrt.mydiary.utils.getDisplayWidth
import com.furianrt.mydiary.utils.inTransaction
import com.furianrt.mydiary.utils.isNetworkAvailable
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import jp.wasabeef.recyclerview.animators.LandingAnimator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_toolbar.*
import kotlinx.android.synthetic.main.bottom_sheet_main.*
import kotlinx.android.synthetic.main.empty_search_note_list.*
import javax.inject.Inject
import kotlin.math.min

class MainActivity : BaseActivity(), MainActivityContract.MvpView,
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
        private const val ACTIVITY_SETTING_REQUEST_CODE = 2
        private const val ITEM_LONG_CLICK_VIBRATION_DURATION = 30L
        private const val BOTTOM_SHEET_EXPAND_DELAY = 500L
        private const val ANIMATION_IMAGE_SETTINGS_FADE_OUT_DURATION = 350L
        private const val ANIMATION_IMAGE_SETTINGS_FADE_OUT_OFFSET = 2000L
        private const val ANIMATION_NO_SEARCH_RESULT_DURATION = 200L
        private const val ANIMATION_NO_SEARCH_RESULT_SIZE = 1.4f
    }

    @Inject
    lateinit var mPresenter: MainActivityContract.Presenter

    private lateinit var mAdapter: NoteListAdapter
    private lateinit var mBottomSheet: BottomSheetBehavior<FrameLayout>
    private lateinit var mOnDrawerListener: ActionBarDrawerToggle
    private var mSearchQury = ""
    private var mRecyclerViewState: Parcelable? = null
    private var mBackPressCount = 0
    private var mNeedToOpenActionBar = true
    private var mMenu: Menu? = null
    private val mHandler = Handler()
    private val mBottomSheetOpenRunnable: Runnable = Runnable {
        mBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPresenterComponent(this).inject(this)
        setContentView(R.layout.activity_main)

        mPresenter.onRestoreInstanceState(savedInstanceState)

        setSupportActionBar(toolbar_main)
        supportActionBar?.let {
            it.setDisplayShowTitleEnabled(false)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        val drawerWidth = min(getDisplayWidth() - dpToPx(56f), dpToPx(56f) * 5)
        container_main_drawer.layoutParams.width = drawerWidth

        drawer.touchEventChildId = R.id.calendar_search

        mOnDrawerListener = object : ActionBarDrawerToggle(this, drawer, toolbar_main, R.string.open, R.string.close) {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                layout_main_root.translationX = slideOffset * drawerView.width
            }
        }

        if (supportFragmentManager.findFragmentByTag(DrawerMenuFragment.TAG) == null) {
            supportFragmentManager.inTransaction {
                add(R.id.container_main_drawer, DrawerMenuFragment(), DrawerMenuFragment.TAG)
            }
        }

        mBottomSheet = BottomSheetBehavior.from(main_sheet_container)
        mBottomSheet.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    supportFragmentManager.findFragmentByTag(ImageSettingsFragment.TAG)?.let {
                        supportFragmentManager.inTransaction { remove(it) }
                    }
                    supportFragmentManager.findFragmentByTag(PremiumFragment.TAG)?.let {
                        supportFragmentManager.inTransaction { remove(it) }
                    }
                    supportFragmentManager.findFragmentByTag(ProfileFragment.TAG)?.let {
                        supportFragmentManager.inTransaction { remove(it) }
                    }
                    supportFragmentManager.findFragmentByTag(AuthFragment.TAG)?.let {
                        (it as AuthFragment).clearFocus()
                        supportFragmentManager.inTransaction { remove(it) }
                    }
                }
            }
        })

        savedInstanceState?.let {
            mRecyclerViewState = it.getParcelable(BUNDLE_RECYCLER_VIEW_STATE)
            layout_main_root.translationX = it.getFloat(BUNDLE_ROOT_LAYOUT_OFFSET, 0f)
            mBottomSheet.state = it.getInt(BUNDLE_BOTTOM_SHEET_STATE, BottomSheetBehavior.STATE_COLLAPSED)
            mSearchQury = it.getString(BUNDLE_SEARCH_QUERY, "")
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

        mAdapter = NoteListAdapter(is24TimeFormat = mPresenter.is24TimeFormat())
        list_main.layoutManager = LinearLayoutManager(this)
        list_main.addItemDecoration(HeaderItemDecoration(list_main, mAdapter))
        list_main.adapter = mAdapter
        list_main.setHasFixedSize(true)
        list_main.itemAnimator = LandingAnimator()
    }

    override fun onTagChackStateChange(tag: MyTag, checked: Boolean) {
        mPresenter.onTagFilterChange(tag, checked)
    }

    override fun onCategoryChackStateChange(category: MyCategory, checked: Boolean) {
        mPresenter.onCategoryFilterChange(category, checked)
    }

    override fun onLocationChackStateChange(location: MyLocation, checked: Boolean) {
        mPresenter.onLocationFilterChange(location, checked)
    }

    override fun onMoodChackStateChange(mood: MyMood, checked: Boolean) {
        mPresenter.onMoodFilterChange(mood, checked)
    }

    override fun onNoTagsChackStateChange(checked: Boolean) {
        mPresenter.onNoTagsFilterChange(checked)
    }

    override fun onNoCategoryChackStateChange(checked: Boolean) {
        mPresenter.onNoCategoryFilterChange(checked)
    }

    override fun onNoMoodChackStateChange(checked: Boolean) {
        mPresenter.onNoMoodFilterChange(checked)
    }

    override fun onNoLocationChackStateChange(checked: Boolean) {
        mPresenter.onNoLocationFilterChange(checked)
    }

    override fun onSearchDatesSelected(startDate: Long?, endDate: Long?) {
        mPresenter.onDateFilterChange(startDate, endDate)
    }

    override fun onBillingInitialized() {
        super.onBillingInitialized()
        if (!getIsItemPurshased(BuildConfig.ITEM_PREMIUM_SKU)/* && !getIsItemPurshased(ITEM_TEST_SKU)*/) {
            showAdView()
        }
    }

    override fun onProductPurchased(productId: String, details: TransactionDetails?) {
        super.onProductPurchased(productId, details)
        if (productId == BuildConfig.ITEM_PREMIUM_SKU/* || productId == ITEM_TEST_SKU*/) {
            closeBottomSheet()
            hideAdView()
        }
    }

    override fun getIsBillingInitialized(): Boolean = isBillingInitialized()

    override fun getIsItemPurshased(productId: String): Boolean = isItemPurshased(productId)

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
        enableActionBarExpanding(mNeedToOpenActionBar)
        mNeedToOpenActionBar = true
        GlideApp.with(this)
                .load(image.url)
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
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_all_notes -> {
                mPresenter.onMenuAllNotesClick()
                true
            }
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

    override fun setSortDesc() {
        mMenu?.findItem(R.id.menu_sort)?.setTitle(R.string.sort_new_first)
    }

    override fun setSortAsc() {
        mMenu?.findItem(R.id.menu_sort)?.setTitle(R.string.sort_old_first)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        mMenu = menu
        menuInflater.inflate(R.menu.activity_main_toolbar_menu, menu)

        val searchView = menu?.findItem(R.id.menu_search)?.actionView as SearchView?

        if (mSearchQury.isNotEmpty()) {
            disableActionBarExpanding()
            searchView?.isIconified = false
            searchView?.setQuery(mSearchQury, false)
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
                mSearchQury = newText ?: ""
                mPresenter.onSearchQueryChange(mSearchQury)
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(BUNDLE_RECYCLER_VIEW_STATE, list_main.layoutManager?.onSaveInstanceState())
        outState.putFloat(BUNDLE_ROOT_LAYOUT_OFFSET, layout_main_root.translationX)
        outState.putInt(BUNDLE_BOTTOM_SHEET_STATE, mBottomSheet.state)
        outState.putString(BUNDLE_SEARCH_QUERY, mSearchQury)
        mPresenter.onSaveInstanceState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun showViewNewNote() {
        analytics.sendEvent(MyAnalytics.EVENT_NOTE_ADDED)
        startActivity(NoteActivity.newIntentModeAdd(this))
    }

    override fun showNotes(notes: List<NoteListItem>, selectedNoteIds: Set<String>) {
        Log.e(TAG, "showNotes")
        mAdapter.selectedNoteIds.clear()
        mAdapter.selectedNoteIds.addAll(selectedNoteIds)
        mAdapter.submitList(notes.toMutableList())
        mRecyclerViewState?.let {
            list_main.layoutManager?.onRestoreInstanceState(it)
            mRecyclerViewState = null
        }
    }

    override fun showEmptyNoteList() {
        empty_state.visibility = View.VISIBLE
        list_main.visibility = View.INVISIBLE
        app_bar_layout.setExpanded(false, true)
    }

    override fun hideEmptyNoteList() {
        empty_state.visibility = View.GONE
        list_main.visibility = View.VISIBLE
    }

    override fun showNoSearchResults() {
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

    override fun hideNoSearchResults() {
        ViewCompat.animate(empty_search)
                .alpha(0f)
                .scaleX(ANIMATION_NO_SEARCH_RESULT_SIZE)
                .scaleY(ANIMATION_NO_SEARCH_RESULT_SIZE)
                .setDuration(ANIMATION_NO_SEARCH_RESULT_DURATION)
                .setInterpolator(AccelerateInterpolator())
                .setListener(object : ViewPropertyAnimatorListener {
                    override fun onAnimationCancel(view: View?) {
                        view?.visibility = View.GONE
                    }
                    override fun onAnimationStart(view: View?) {}
                    override fun onAnimationEnd(view: View?) {
                        view?.visibility = View.GONE
                    }
                })
                .start()
    }

    override fun onMainListItemClick(note: MyNoteWithProp, position: Int) {
        mPresenter.onMainListItemClick(note, position)
    }

    override fun onMainListItemLongClick(note: MyNoteWithProp, position: Int) {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            @Suppress("DEPRECATION")
            vibrator.vibrate(ITEM_LONG_CLICK_VIBRATION_DURATION)
        }
        mPresenter.onMainListItemLongClick(note, position)
    }

    override fun activateSelection() {
        fab_menu.open(true)
    }

    override fun deactivateSelection() {
        fab_menu.close(true)
    }

    override fun updateItemSelection(selectedNoteIds: Set<String>) {
        mAdapter.selectedNoteIds.clear()
        mAdapter.selectedNoteIds.addAll(selectedNoteIds)
        mAdapter.notifyDataSetChanged()
    }

    override fun showNotePager(position: Int, note: MyNoteWithProp) {
        analytics.sendEvent(MyAnalytics.EVENT_NOTE_OPENED)
        startActivity(NoteActivity.newIntentModeRead(this, position))
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
        if (isItemPurshased(BuildConfig.ITEM_PREMIUM_SKU)/* || isItemPurshased(ITEM_TEST_SKU)*/) {
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

    override fun networkAvailable() = isNetworkAvailable()

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

    override fun onButtonPurshaseClick(productId: String) {
        purshaseItem(productId)
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
        mPresenter.attachView(this)
        mAdapter.listener = this
        drawer.addDrawerListener(mOnDrawerListener)
        (supportFragmentManager.findFragmentByTag(DeleteNoteDialog.TAG) as? DeleteNoteDialog)
                ?.setOnDeleteConfirmListener(this)
        supportFragmentManager.findFragmentByTag(CategoriesDialog.TAG)?.let {
            (it as CategoriesDialog).setOnCategorySelectedListener(this)
        }
    }

    override fun onStop() {
        super.onStop()
        mNeedToOpenActionBar = false
        mBackPressCount = 0
        mAdapter.listener = null
        drawer.removeDrawerListener(mOnDrawerListener)
        mHandler.removeCallbacks(mBottomSheetOpenRunnable)
        mPresenter.detachView()
    }
}
