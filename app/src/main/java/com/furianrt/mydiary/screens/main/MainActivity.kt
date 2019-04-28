package com.furianrt.mydiary.screens.main

import android.Manifest
import android.animation.Animator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
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
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.furianrt.mydiary.R
import com.furianrt.mydiary.base.BaseActivity
import com.furianrt.mydiary.data.model.*
import com.furianrt.mydiary.dialogs.delete.note.DeleteNoteDialog
import com.furianrt.mydiary.general.AppBarLayoutBehavior
import com.furianrt.mydiary.general.GlideApp
import com.furianrt.mydiary.general.HeaderItemDecoration
import com.furianrt.mydiary.screens.main.fragments.authentication.AuthFragment
import com.furianrt.mydiary.screens.main.fragments.imagesettings.ImageSettingsFragment
import com.furianrt.mydiary.screens.main.fragments.premium.PremiumFragment
import com.furianrt.mydiary.screens.main.fragments.profile.ProfileFragment
import com.furianrt.mydiary.screens.main.fragments.profile.signout.SignOutFragment
import com.furianrt.mydiary.screens.main.listadapter.MainListAdapter
import com.furianrt.mydiary.screens.main.listadapter.MainListItem
import com.furianrt.mydiary.screens.note.NoteActivity
import com.furianrt.mydiary.screens.settings.global.GlobalSettingsActivity
import com.furianrt.mydiary.services.sync.SyncService
import com.furianrt.mydiary.utils.getThemePrimaryColor
import com.furianrt.mydiary.utils.getThemePrimaryDarkColor
import com.furianrt.mydiary.utils.inTransaction
import com.furianrt.mydiary.utils.isNetworkAvailable
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.api.widget.Widget
import jp.wasabeef.recyclerview.animators.LandingAnimator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_toolbar.*
import kotlinx.android.synthetic.main.bottom_sheet_main.*
import kotlinx.android.synthetic.main.nav_header_main.*
import kotlinx.android.synthetic.main.nav_header_main.view.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import javax.inject.Inject

class MainActivity : BaseActivity(), MainActivityContract.View,
        MainListAdapter.OnMainListItemInteractionListener, View.OnClickListener,
        SignOutFragment.OnSignOutFragmentInteractionListener,
        ImageSettingsFragment.OnImageSettingsInteractionListener,
        DeleteNoteDialog.OnDeleteNoteConfirmListener {

    companion object {
        private const val TAG = "MainActivity"
        private const val BUNDLE_RECYCLER_VIEW_STATE = "recyclerState"
        private const val BUNDLE_SELECTED_LIST_ITEMS = "selectedListItems"
        private const val BUNDLE_ROOT_LAYOUT_OFFSET = "rootLayoutOffset"
        private const val BUNDLE_BOTTOM_SHEET_STATE = "bottomSheetState"
        private const val STORAGE_PERMISSIONS_REQUEST_CODE = 1
        private const val ACTIVITY_SETTING_REQUEST_CODE = 2
        private const val ITEM_LONG_CLICK_VIBRATION_DURATION = 30L
        private const val ANIMATION_IMAGE_SETTINGS_FADE_OUT_DURATION = 350L
        private const val ANIMATION_IMAGE_SETTINGS_FADE_OUT_OFFSET = 2000L
        private const val ANIMATION_PROGRESS_FADE_OUT_OFFSET = 2000L
        private const val ANIMATION_PROGRESS_DURATION = 500L
        private const val BOTTOM_SHEET_EXPAND_DELAY = 300L
    }

    @Inject
    lateinit var mPresenter: MainActivityContract.Presenter

    private lateinit var mAdapter: MainListAdapter
    private var mRecyclerViewState: Parcelable? = null
    private lateinit var mBottomSheet: BottomSheetBehavior<FrameLayout>
    private var mBackPressCount = 0
    private var mNeedToOpenActionBar = true
    private var mMenu: Menu? = null
    private val mHandler = Handler()
    private val mBottomSheetOpenRunnable: Runnable = Runnable {
        mBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }
    private val mProgressAnimationListener = object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {}
        override fun onAnimationCancel(animation: Animator?) {}
        override fun onAnimationStart(animation: Animator?) {}
        override fun onAnimationEnd(animation: Animator?) {
            nav_view.getHeaderView(0).progress_sync.visibility = View.GONE
            nav_view.getHeaderView(0).button_sync.isEnabled = true
            nav_view.getHeaderView(0).button_sync.text = getString(R.string.nav_header_main_button_sync)
        }
    }
    private lateinit var mOnDrawerListener: ActionBarDrawerToggle

    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            SyncService.getProgressMessage(intent)?.let {
                if (it.hasError) {
                    button_sync.text = it.message
                    nav_view.getHeaderView(0).progress_sync.progress = 100f
                    nav_view.getHeaderView(0).progress_sync.finishLoad()
                    animateProgressAlpha()
                } else {
                    if (nav_view.getHeaderView(0).progress_sync.isFinish) {
                        nav_view.getHeaderView(0).progress_sync.reset()
                    }
                    nav_view.getHeaderView(0).button_sync.isEnabled = false
                    nav_view.getHeaderView(0).progress_sync.alpha = 0.35f
                    nav_view.getHeaderView(0).progress_sync.visibility = View.VISIBLE
                    nav_view.getHeaderView(0).progress_sync.progress = it.progress.toFloat()
                    val progressText = "${it.progress}% ${it.message}"
                    if (it.taskIndex == SyncProgressMessage.SYNC_FINISHED) {
                        nav_view.getHeaderView(0).progress_sync.finishLoad()
                        animateProgressAlpha()
                    }
                    nav_view.getHeaderView(0).button_sync.text = progressText
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPresenterComponent(this).inject(this)
        setContentView(R.layout.activity_main)
        mBottomSheet = BottomSheetBehavior.from(main_sheet_container)
        mBottomSheet.setBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
                    supportFragmentManager.findFragmentByTag(PremiumFragment.TAG)?.let {
                        supportFragmentManager.popBackStack() //todo странная фигня
                    }
                    supportFragmentManager.findFragmentByTag(ProfileFragment.TAG)?.let {
                        supportFragmentManager.inTransaction { remove(it) }
                    }
                    supportFragmentManager.findFragmentByTag(AuthFragment.TAG)?.let {
                        (it as AuthFragment).clearFocus()
                        supportFragmentManager.inTransaction { remove(it) }
                    }
                    supportFragmentManager.findFragmentByTag(ImageSettingsFragment.TAG)?.let {
                        supportFragmentManager.inTransaction { remove(it) }
                    }
                }
            }
        })
        savedInstanceState?.let {
            mRecyclerViewState = it.getParcelable(BUNDLE_RECYCLER_VIEW_STATE)
            val selectedListItems = it.getParcelableArrayList<MyNoteWithProp>(BUNDLE_SELECTED_LIST_ITEMS)
            mPresenter.onRestoreInstanceState(selectedListItems)
            if (selectedListItems != null && selectedListItems.isNotEmpty()) {
                activateSelection()
            }
            layout_main_root.translationX = it.getFloat(BUNDLE_ROOT_LAYOUT_OFFSET, 0f)
            mBottomSheet.state = it.getInt(BUNDLE_BOTTOM_SHEET_STATE, BottomSheetBehavior.STATE_COLLAPSED)
        }
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mBroadcastReceiver, IntentFilter(Intent.ACTION_SYNC))
        setupUi()
    }

    private fun setupUi() {
        setSupportActionBar(toolbar_main)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)

        mAdapter = MainListAdapter(is24TimeFormat = mPresenter.is24TimeFormat())
        list_main.layoutManager = LinearLayoutManager(this)
        list_main.addItemDecoration(HeaderItemDecoration(list_main, mAdapter))
        list_main.adapter = mAdapter
        list_main.itemAnimator = LandingAnimator()
    }

    private fun animateProgressAlpha() {
        nav_view.getHeaderView(0)
                .progress_sync
                .animate()
                .alpha(0f)
                .setDuration(ANIMATION_PROGRESS_DURATION)
                .setStartDelay(ANIMATION_PROGRESS_FADE_OUT_OFFSET)
                .setListener(mProgressAnimationListener)
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

    private fun disableActionBarExpanding() {
        val appBarParams =
                collapsing_toolbar_main.layoutParams as AppBarLayout.LayoutParams
        appBarParams.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or
                AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
        collapsing_toolbar_main.layoutParams = appBarParams
        app_bar_layout.setExpanded(false)
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

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab_delete -> mPresenter.onButtonDeleteClick()
            R.id.image_toolbar_main -> mPresenter.onMainImageClick()
            R.id.fab_menu -> mPresenter.onFabMenuClick()
            R.id.button_sync -> mPresenter.onButtonSyncClick()
            R.id.button_profile_settings -> mPresenter.onButtonProfileClick()
            R.id.layout_profile_name -> mPresenter.onButtonProfileClick()
            R.id.image_profile -> mPresenter.onButtonProfileClick()
            R.id.button_main_image_settings -> mPresenter.onButtonImageSettingsClick()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_all_notes -> {
                mPresenter.onMenuAllNotesClick()
                true
            }
            R.id.menu_image -> {
                mPresenter.onButtonImageSettingsClick()
                true
            }
            R.id.menu_settings -> {
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

    override fun showDeleteConfirmationDialog(notes: List<MyNote>) {
        DeleteNoteDialog.newInstance(notes).apply {
            setOnDeleteConfirmListener(this@MainActivity)
        }.show(supportFragmentManager, DeleteNoteDialog.TAG)
    }

    override fun onDialogButtonDeleteClick(notes: List<MyNote>) {
        mPresenter.onButtonDeleteConfirmClick(notes)
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
        mBottomSheet.state = BottomSheetBehavior.STATE_EXPANDED
    }

    override fun setSortDesc() {
        mMenu?.findItem(R.id.menu_sort)?.setTitle(R.string.sort_new_first)
    }

    override fun setSortAsc() {
        mMenu?.findItem(R.id.menu_sort)?.setTitle(R.string.sort_old_first)
    }

    override fun showProfileSettings() {
        if (supportFragmentManager.findFragmentByTag(ProfileFragment.TAG) == null) {
            supportFragmentManager.inTransaction {
                replace(R.id.main_sheet_container, ProfileFragment(), ProfileFragment.TAG)
            }
        }
        mHandler.postDelayed(mBottomSheetOpenRunnable, BOTTOM_SHEET_EXPAND_DELAY)
    }

    override fun showLoginView() {
        if (supportFragmentManager.findFragmentByTag(AuthFragment.TAG) == null) {
            supportFragmentManager.inTransaction {
                replace(R.id.main_sheet_container, AuthFragment(), AuthFragment.TAG)
            }
        }
        mHandler.postDelayed(mBottomSheetOpenRunnable, BOTTOM_SHEET_EXPAND_DELAY)
    }

    override fun showPremiumView() {
        if (supportFragmentManager.findFragmentByTag(PremiumFragment.TAG) == null) {
            supportFragmentManager.inTransaction {
                add(R.id.main_sheet_container, PremiumFragment(), PremiumFragment.TAG)
            }
        }
        mHandler.postDelayed(mBottomSheetOpenRunnable, BOTTOM_SHEET_EXPAND_DELAY)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        mMenu = menu
        menuInflater.inflate(R.menu.activity_main_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putParcelable(BUNDLE_RECYCLER_VIEW_STATE, list_main.layoutManager?.onSaveInstanceState())
        outState?.putParcelableArrayList(BUNDLE_SELECTED_LIST_ITEMS, mPresenter.onSaveInstanceState())
        outState?.putFloat(BUNDLE_ROOT_LAYOUT_OFFSET, layout_main_root.translationX)
        outState?.putInt(BUNDLE_BOTTOM_SHEET_STATE, mBottomSheet.state)
        super.onSaveInstanceState(outState)
    }

    override fun showViewNewNote() {
        startActivity(NoteActivity.newIntentModeAdd(this))
    }

    override fun requestStoragePermissions() {
        val readExtStorage = Manifest.permission.READ_EXTERNAL_STORAGE
        val camera = Manifest.permission.CAMERA
        if (EasyPermissions.hasPermissions(this, readExtStorage, camera)) {
            mPresenter.onStoragePermissionsGranted()
        } else {
            EasyPermissions.requestPermissions(this,
                    getString(R.string.storage_permission_request),
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
                .navigationBarColor(ContextCompat.getColor(this, R.color.black))
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

    override fun showNotes(notes: List<MainListItem>, selectedNotes: ArrayList<MyNoteWithProp>) {
        Log.e(TAG, "showNotes")
        mAdapter.selectedNotes = selectedNotes
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

    override fun showNotesTotal(count: Int) {
        nav_view.getHeaderView(0).text_notes_total.text = count.toString()
    }

    override fun showNotesCountToday(count: Int) {
        nav_view.getHeaderView(0).text_notes_today.text = count.toString()
    }

    override fun showImageCount(count: Int) {
        nav_view.getHeaderView(0).text_image_total.text = count.toString()
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

    override fun updateItemSelection(selectedNotes: ArrayList<MyNoteWithProp>) {
        mAdapter.selectedNotes = selectedNotes
        mAdapter.notifyDataSetChanged()
    }

    override fun openNotePager(position: Int, note: MyNoteWithProp) {
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

    override fun onSignOut() {
        mPresenter.onSignOut()
    }

    override fun showAnonymousProfile() {
        mAdapter.profile = null
        mAdapter.notifyDataSetChanged()
        nav_view.getHeaderView(0).text_email.text = getString(R.string.nav_header_main_anonymous)
        nav_view.getHeaderView(0).text_profile_description.text =
                getString(R.string.nav_header_main_sing_in)
    }

    override fun showRegularProfile(profile: MyProfile) {
        mAdapter.profile = null
        mAdapter.notifyDataSetChanged()
        //todo Добавить загрузку картинки профиля
        nav_view.getHeaderView(0).text_email.text = profile.email
        nav_view.getHeaderView(0).text_profile_description.text =
                getString(R.string.nav_header_main_regular)
    }

    override fun showPremiumProfile(profile: MyProfile) {
        mAdapter.profile = profile
        mAdapter.notifyDataSetChanged()
        //todo Добавить загрузку картинки профиля
        nav_view.getHeaderView(0).text_email.text = profile.email
        nav_view.getHeaderView(0).text_profile_description.text =
                getString(R.string.nav_header_main_premium)
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

    override fun startSyncService() {
        val serviceIntent = Intent(this, SyncService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    override fun networkAvailable() = isNetworkAvailable()

    override fun onStart() {
        super.onStart()
        mOnDrawerListener = object : ActionBarDrawerToggle(this, drawer, toolbar_main, R.string.open, R.string.close) {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
                super.onDrawerSlide(drawerView, slideOffset)
                layout_main_root.translationX = slideOffset * drawerView.width
            }
        }
        drawer.addDrawerListener(mOnDrawerListener)
        fab_menu.setOnClickListener(this)
        fab_delete.setOnClickListener(this)
        fab_folder.setOnClickListener(this)
        fab_place.setOnClickListener(this)
        image_toolbar_main.setOnClickListener(this)
        button_main_image_settings.setOnClickListener(this)
        nav_view.getHeaderView(0).button_sync.setOnClickListener(this)
        nav_view.getHeaderView(0).button_profile_settings.setOnClickListener(this)
        nav_view.getHeaderView(0).layout_profile_name.setOnClickListener(this)
        nav_view.getHeaderView(0).image_profile.setOnClickListener(this)
    }

    override fun onStop() {
        super.onStop()
        drawer.removeDrawerListener(mOnDrawerListener)
    }

    override fun onResume() {
        super.onResume()
        mPresenter.attachView(this)
        mPresenter.onViewResume()
        mAdapter.listener = this
        (supportFragmentManager.findFragmentByTag(DeleteNoteDialog.TAG) as? DeleteNoteDialog)
                ?.setOnDeleteConfirmListener(this)
    }

    override fun onPause() {
        super.onPause()
        mNeedToOpenActionBar = false
        mBackPressCount = 0
        mAdapter.listener = null
        mHandler.removeCallbacks(mBottomSheetOpenRunnable)
        mPresenter.detachView()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver)
    }
}
