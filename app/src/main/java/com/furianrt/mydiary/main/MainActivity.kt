package com.furianrt.mydiary.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcelable
import android.os.Vibrator
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.furianrt.mydiary.BaseActivity
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyHeaderImage
import com.furianrt.mydiary.data.model.MyNoteWithProp
import com.furianrt.mydiary.general.AppBarLayoutBehavior
import com.furianrt.mydiary.general.GlideApp
import com.furianrt.mydiary.general.HeaderItemDecoration
import com.furianrt.mydiary.main.listadapter.MainListAdapter
import com.furianrt.mydiary.main.listadapter.MainListItem
import com.furianrt.mydiary.note.NoteActivity
import com.furianrt.mydiary.settings.global.GlobalSettingsActivity
import com.furianrt.mydiary.utils.getThemePrimaryColor
import com.furianrt.mydiary.utils.getThemePrimaryDarkColor
import com.google.android.material.appbar.AppBarLayout
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.api.widget.Widget
import jp.wasabeef.recyclerview.animators.LandingAnimator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_toolbar.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.util.*
import javax.inject.Inject

class MainActivity : BaseActivity(), MainActivityContract.View,
        MainListAdapter.OnMainListItemInteractionListener, View.OnClickListener {

    companion object {
        private const val TAG = "MainActivity"
        private const val BUNDLE_RECYCLER_VIEW_STATE = "recyclerState"
        private const val BUNDLE_SELECTED_LIST_ITEMS = "selectedListItems"
        private const val STORAGE_PERMISSIONS_REQUEST_CODE = 1
        private const val ACTIVITY_SETTING_REQUEST_CODE = 2
    }

    @Inject
    lateinit var mPresenter: MainActivityContract.Presenter

    private lateinit var mAdapter: MainListAdapter
    private var mRecyclerViewState: Parcelable? = null
    private var mBackPressCount = 0
    private var mNeedToOpenActionBar = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPresenterComponent(this).inject(this)
        setContentView(R.layout.activity_main)

        savedInstanceState?.let {
            mRecyclerViewState = it.getParcelable(BUNDLE_RECYCLER_VIEW_STATE)
            val selectedListItems = it.getParcelableArrayList<MyNoteWithProp>(BUNDLE_SELECTED_LIST_ITEMS)
            mPresenter.onRestoreInstanceState(selectedListItems)
            if (selectedListItems != null && selectedListItems.isNotEmpty()) {
                activateSelection()
            }
        }

        setupUi()
    }

    override fun onStart() {
        super.onStart()
        Log.e(TAG, "onStart")
        mPresenter.attachView(this)
        mAdapter.listener = this
        mPresenter.onViewStart()
    }

    override fun showHeaderImages(images: List<MyHeaderImage>) {
        Log.e(TAG, "showHeaderImages")
        if (mNeedToOpenActionBar) {
            enableActionBarExpanding()
        }

        mNeedToOpenActionBar = true

        GlideApp.with(this)
                .load(images.first().url)
                .into(image_toolbar_main)
    }

    override fun showEmptyHeaderImage() {
        Log.e(TAG, "showEmptyHeaderImage")
        disableActionBarExpanding()
    }

    private fun disableActionBarExpanding() {
        val appBarParams = collapsing_toolbar_main.layoutParams as AppBarLayout.LayoutParams
        appBarParams.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or
                AppBarLayout.LayoutParams.SCROLL_FLAG_EXIT_UNTIL_COLLAPSED
        collapsing_toolbar_main.layoutParams = appBarParams

        app_bar_layout.setExpanded(false)

        val coordParams = app_bar_layout.layoutParams as CoordinatorLayout.LayoutParams
        (coordParams.behavior as AppBarLayoutBehavior).shouldScroll = false
    }

    private fun enableActionBarExpanding() {
        val appBarParams = collapsing_toolbar_main.layoutParams as AppBarLayout.LayoutParams
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

        app_bar_layout.setExpanded(true)

        val coordParams = app_bar_layout.layoutParams as CoordinatorLayout.LayoutParams
        (coordParams.behavior as AppBarLayoutBehavior).shouldScroll = true
    }

    private fun setupUi() {
        setSupportActionBar(toolbar_main)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val toggle =
                ActionBarDrawerToggle(this, drawer, toolbar_main, R.string.open, R.string.close)
        drawer.addDrawerListener(toggle)
        toggle.syncState()

        fab_menu.setOnClickListener(this)
        fab_menu.setMenuButtonColorRippleResId(R.color.colorPrimary)

        fab_delete.setOnClickListener(this)
        fab_folder.setOnClickListener(this)
        fab_place.setOnClickListener(this)
        image_toolbar_main.setOnClickListener(this)

        mAdapter = MainListAdapter(is24TimeFormat = mPresenter.is24TimeFormat())

        list_main.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(HeaderItemDecoration(this, mAdapter))
            adapter = mAdapter
            itemAnimator = LandingAnimator()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.fab_delete -> mPresenter.onMenuDeleteClick()
            R.id.image_toolbar_main -> mPresenter.onButtonSetMainImageClick()
            R.id.fab_menu -> mPresenter.onFabMenuClick()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_all_notes -> {
                mPresenter.onMenuAllNotesClick()
                true
            }
            R.id.menu_image -> {
                mPresenter.onButtonSetMainImageClick()
                true
            }
            R.id.menu_settings -> {
                mPresenter.onButtonSettingsClick()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun showSettingsView() {
        val intent = Intent(this, GlobalSettingsActivity::class.java)
        startActivityForResult(intent, ACTIVITY_SETTING_REQUEST_CODE)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.activity_main_toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putParcelable(BUNDLE_RECYCLER_VIEW_STATE, list_main.layoutManager?.onSaveInstanceState())
        outState?.putParcelableArrayList(BUNDLE_SELECTED_LIST_ITEMS, mPresenter.onSaveInstanceState())
        super.onSaveInstanceState(outState)
    }

    override fun showViewNewNote() {
        mPresenter.detachView()
        val intent = Intent(this, NoteActivity::class.java)
        intent.putExtra(NoteActivity.EXTRA_MODE, NoteActivity.Companion.Mode.ADD)
        startActivity(intent)
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

    override fun showAdded() {
        Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show()
    }

    override fun showDeleted() {
        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
    }

    override fun showNotes(notes: List<MainListItem>, selectedNotes: ArrayList<MyNoteWithProp>) {
        Log.e(TAG, "showNotes")

        mAdapter.submitList(notes.toMutableList())
        mAdapter.selectedNotes = selectedNotes
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
            vibrator.vibrate(30L)
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

    override fun openNotePager(position: Int) {
        val intent = Intent(this, NoteActivity::class.java)
        intent.putExtra(NoteActivity.EXTRA_CLICKED_NOTE_POSITION, position)
        intent.putExtra(NoteActivity.EXTRA_MODE, NoteActivity.Companion.Mode.READ)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTIVITY_SETTING_REQUEST_CODE) {
            recreate()
        }
    }

    override fun onBackPressed() {
        when {
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

    override fun onPause() {
        super.onPause()
        mNeedToOpenActionBar = false
        mBackPressCount = 0
    }

    override fun onStop() {
        super.onStop()
        Log.e(TAG, "onStop")
        mAdapter.listener = null
        mPresenter.detachView()
    }
}
