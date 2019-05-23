package com.furianrt.mydiary.screens.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Parcelable
import android.os.Vibrator
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.anjlab.android.iab.v3.TransactionDetails
import com.furianrt.mydiary.BuildConfig
import com.furianrt.mydiary.R
import com.furianrt.mydiary.base.BaseActivity
import com.furianrt.mydiary.data.model.*
import com.furianrt.mydiary.dialogs.categories.CategoriesDialog
import com.furianrt.mydiary.dialogs.delete.note.DeleteNoteDialog
import com.furianrt.mydiary.general.Analytics
import com.furianrt.mydiary.general.AppBarLayoutBehavior
import com.furianrt.mydiary.general.GlideApp
import com.furianrt.mydiary.general.HeaderItemDecoration
import com.furianrt.mydiary.screens.main.adapter.NoteListAdapter
import com.furianrt.mydiary.screens.main.adapter.NoteListItem
import com.furianrt.mydiary.screens.main.fragments.authentication.AuthFragment
import com.furianrt.mydiary.screens.main.fragments.drawer.DrawerMenuFragment
import com.furianrt.mydiary.screens.main.fragments.drawer.adapter.SearchListAdapter
import com.furianrt.mydiary.screens.main.fragments.imagesettings.ImageSettingsFragment
import com.furianrt.mydiary.screens.main.fragments.premium.PremiumFragment
import com.furianrt.mydiary.screens.main.fragments.profile.ProfileFragment
import com.furianrt.mydiary.screens.note.NoteActivity
import com.furianrt.mydiary.screens.settings.global.GlobalSettingsActivity
import com.furianrt.mydiary.utils.*
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.api.widget.Widget
import jp.wasabeef.recyclerview.animators.LandingAnimator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_toolbar.*
import kotlinx.android.synthetic.main.bottom_sheet_main.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

class MainActivity : BaseActivity(), MainActivityContract.View,
        NoteListAdapter.OnMainListItemInteractionListener,
        ImageSettingsFragment.OnImageSettingsInteractionListener,
        DeleteNoteDialog.OnDeleteNoteConfirmListener, CategoriesDialog.OnCategorySelectedListener,
        PremiumFragment.OnPremiumFragmentInteractionListener,
        DrawerMenuFragment.OnDrawerMenuInteractionListener,
        SearchListAdapter.OnSearchListInteractionListener {

    companion object {
        private const val TAG = "MainActivity"
        private const val BUNDLE_RECYCLER_VIEW_STATE = "recycler_state"
        private const val BUNDLE_SELECTED_LIST_ITEMS = "selected_list_items"
        private const val BUNDLE_ROOT_LAYOUT_OFFSET = "root_layout_offset"
        private const val BUNDLE_BOTTOM_SHEET_STATE = "bottom_sheet_state"
        private const val STORAGE_PERMISSIONS_REQUEST_CODE = 1
        private const val ACTIVITY_SETTING_REQUEST_CODE = 2
        private const val ITEM_LONG_CLICK_VIBRATION_DURATION = 30L
        private const val BOTTOM_SHEET_EXPAND_DELAY = 300L
        private const val ANIMATION_IMAGE_SETTINGS_FADE_OUT_DURATION = 350L
        private const val ANIMATION_IMAGE_SETTINGS_FADE_OUT_OFFSET = 2000L
    }

    @Inject
    lateinit var mPresenter: MainActivityContract.Presenter

    private lateinit var mAdapter: NoteListAdapter
    private lateinit var mBottomSheet: BottomSheetBehavior<FrameLayout>
    private lateinit var mOnDrawerListener: ActionBarDrawerToggle
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

        setSupportActionBar(toolbar_main)
        supportActionBar?.let {
            it.setDisplayShowTitleEnabled(false)
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_menu)
        }

        val drawerWidth = Math.min(getDisplayWidth() - dpToPx(56f), dpToPx(56f) * 5)
        container_main_drawer.layoutParams.width = drawerWidth

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
            val selectedListItems = it.getStringArrayList(BUNDLE_SELECTED_LIST_ITEMS)
            mPresenter.onRestoreInstanceState(selectedListItems?.toSet())
            if (selectedListItems != null && selectedListItems.isNotEmpty()) {
                activateSelection()
            }
            layout_main_root.translationX = it.getFloat(BUNDLE_ROOT_LAYOUT_OFFSET, 0f)
            mBottomSheet.state = it.getInt(BUNDLE_BOTTOM_SHEET_STATE, BottomSheetBehavior.STATE_COLLAPSED)
        }

        fab_menu.setOnClickListener { mPresenter.onFabMenuClick() }
        fab_delete.setOnClickListener { mPresenter.onButtonDeleteClick() }
        fab_folder.setOnClickListener { mPresenter.onButtonFolderClick() }
        fab_place.setOnClickListener {
            if (BuildConfig.DEBUG) {
                consumePurchase(ITEM_TEST_SKU)
            }
            //todo
        }

        image_toolbar_main.setOnClickListener { mPresenter.onMainImageClick() }
        button_main_image_settings.setOnClickListener {
            Analytics.sendEvent(this, Analytics.EVENT_HEADER_IMAGE_SETTINGS)
            mPresenter.onButtonImageSettingsClick()
        }

        mAdapter = NoteListAdapter(is24TimeFormat = mPresenter.is24TimeFormat())
        list_main.layoutManager = LinearLayoutManager(this)
        list_main.addItemDecoration(HeaderItemDecoration(list_main, mAdapter))
        list_main.adapter = mAdapter
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

    override fun onBillingInitialized() {
        super.onBillingInitialized()
        supportFragmentManager.findFragmentByTag(DrawerMenuFragment.TAG)?.let {
            (it as DrawerMenuFragment).onBillingInitialized()
        }
        if (!getIsItemPurshased(BuildConfig.ITEM_SYNC_SKU) && !getIsItemPurshased(ITEM_TEST_SKU)) {
            showAdView()
        }
    }

    override fun onProductPurchased(productId: String, details: TransactionDetails?) {
        super.onProductPurchased(productId, details)
        if (productId == BuildConfig.ITEM_SYNC_SKU || productId == ITEM_TEST_SKU) {
            hideAdView()
            supportFragmentManager.findFragmentByTag(DrawerMenuFragment.TAG)?.let {
                (it as DrawerMenuFragment).onProductPurchased(productId)
            }
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

    override fun showEmptyHeaderImage() {
        Log.e(TAG, "showEmptyHeaderImage")
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_all_notes -> {
                mPresenter.onMenuAllNotesClick()
                true
            }
            R.id.menu_image -> {
                Analytics.sendEvent(this, Analytics.EVENT_HEADER_IMAGE_SETTINGS)
                mPresenter.onButtonImageSettingsClick()
                true
            }
            R.id.menu_settings -> {
                Analytics.sendEvent(this, Analytics.EVENT_MAIN_SETTINGS)
                mPresenter.onButtonSettingsClick()
                true
            }
            R.id.menu_sort -> {
                mPresenter.onButtonSortClick()
                true
            }
            android.R.id.home -> {
                drawer.openDrawer(drawer, true)
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

        searchView?.setOnSearchClickListener {
            Analytics.sendEvent(this, Analytics.EVENT_SEARCH_WORD_OPENED)
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
                mPresenter.onSearchQueryChange(newText ?: "")
                return true
            }
        })

        return super.onCreateOptionsMenu(menu)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(BUNDLE_RECYCLER_VIEW_STATE, list_main.layoutManager?.onSaveInstanceState())
        outState.putStringArrayList(BUNDLE_SELECTED_LIST_ITEMS, ArrayList(mPresenter.onSaveInstanceState()))
        outState.putFloat(BUNDLE_ROOT_LAYOUT_OFFSET, layout_main_root.translationX)
        outState.putInt(BUNDLE_BOTTOM_SHEET_STATE, mBottomSheet.state)
        super.onSaveInstanceState(outState)
    }

    override fun showViewNewNote() {
        Analytics.sendEvent(this, Analytics.EVENT_NOTE_ADDED)
        startActivity(NoteActivity.newIntentModeAdd(this))
    }

    override fun requestStoragePermissions() {
        val readExtStorage = Manifest.permission.READ_EXTERNAL_STORAGE
        val camera = Manifest.permission.CAMERA
        if (EasyPermissions.hasPermissions(this, readExtStorage, camera)) {
            mPresenter.onStoragePermissionsGranted()
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.storage_permission_request),
                    STORAGE_PERMISSIONS_REQUEST_CODE, readExtStorage, camera)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @AfterPermissionGranted(STORAGE_PERMISSIONS_REQUEST_CODE)
    override fun showImageExplorer() {
        val widget = Widget.newDarkBuilder(this)
                .statusBarColor(getThemePrimaryDarkColor(this))
                .toolBarColor(getThemePrimaryColor(this))
                .navigationBarColor(Color.BLACK)
                .title(R.string.album)
                .build()
        Album.image(this)
                .singleChoice()
                .columnCount(3)
                .filterMimeType {
                    when (it) {
                        "jpeg" -> true
                        "jpg " -> true
                        else -> false
                    }
                }
                .afterFilterVisibility(false)
                .camera(true)
                .widget(widget)
                .onResult { albImg ->
                    if (albImg.isNotEmpty()) mNeedToOpenActionBar = true
                    mPresenter.onHeaderImagesPicked(albImg.map { it.path })
                }
                .start()
    }

    override fun showNotes(notes: List<NoteListItem>, selectedNoteIds: Set<String>) {
        Log.e(TAG, "showNotes")
        mAdapter.selectedNoteIds.clear()
        mAdapter.selectedNoteIds.addAll(selectedNoteIds)
        mAdapter.submitList(notes.toMutableList())
        empty_state.visibility = if (notes.isEmpty()) {
            app_bar_layout.setExpanded(false, true)
            View.VISIBLE
        } else {
            View.GONE
        }
        mRecyclerViewState?.let {
            list_main.layoutManager?.onRestoreInstanceState(it)
            mRecyclerViewState = null
        }
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
        Analytics.sendEvent(this, Analytics.EVENT_NOTE_OPENED)
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
        if (isItemPurshased(BuildConfig.ITEM_SYNC_SKU) || isItemPurshased(ITEM_TEST_SKU)) {
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

    override fun onButtonPurshaseClick(productId: String) {
        purshaseItem(productId)
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
