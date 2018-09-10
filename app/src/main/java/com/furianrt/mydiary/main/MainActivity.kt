package com.furianrt.mydiary.main

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.content.ContextCompat
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.view.ActionMode
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.general.HeaderItemDecoration
import com.furianrt.mydiary.main.listadapter.MainListAdapter
import com.furianrt.mydiary.main.listadapter.MainListItem
import com.furianrt.mydiary.note.EXTRA_MODE
import com.furianrt.mydiary.note.Mode
import com.furianrt.mydiary.note.NoteActivity
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.AlbumConfig
import com.yanzhenjie.album.api.widget.Widget
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.util.*
import javax.inject.Inject


const val EXTRA_CLICKED_NOTE_POSITION = "notePosition"

private const val RECYCLER_VIEW_POSITION = "recyclerPosition"
private const val STORAGE_PERMISSIONS_REQUEST_CODE = 2

class MainActivity : AppCompatActivity(), MainActivityContract.View,
        MainListAdapter.OnMainListItemInteractionListener, ActionMode.Callback {

    @Inject
    lateinit var mPresenter: MainActivityContract.Presenter

    private val mAdapter = MainListAdapter(this)

    private var mRecyclerViewState: Parcelable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getPresenterComponent(this).inject(this)
        setContentView(R.layout.activity_main)

        savedInstanceState?.let {
            mRecyclerViewState = it.getParcelable(RECYCLER_VIEW_POSITION)
        }

        Album.initialize(AlbumConfig.newBuilder(this)
                .setAlbumLoader(MediaLoader())
                .setLocale(Locale.getDefault())
                .build())

        setupUi()
    }

    override fun showHeaderImage(image: File) {
        Picasso.get()
                .load(image)
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .noPlaceholder()
                .into(image_toolbar_main)
    }

    private fun setupUi() {
        setSupportActionBar(toolbar_main)

        val toggle = ActionBarDrawerToggle(this, drawer_layout, toolbar_main, R.string.open, R.string.close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        mPresenter.attachView(this)
        mPresenter.onViewCreate()

        fab.setOnClickListener { mPresenter.onButtonAddNoteClick() }

        fab_toolbar.setOnClickListener { mPresenter.onButtonSetMainImageClick() }

        list_main.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(HeaderItemDecoration(this, mAdapter))
            adapter = mAdapter
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState?.putParcelable(RECYCLER_VIEW_POSITION, list_main.layoutManager?.onSaveInstanceState())
        super.onSaveInstanceState(outState)
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        return false
    }

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        mode?.title = "Test"
        mode?.menuInflater?.inflate(R.menu.dialog_tags_list_item_menu, menu)
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {

    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        return false
    }

    override fun showViewNewNote() {
        val intent = Intent(this, NoteActivity::class.java)
        intent.putExtra(EXTRA_MODE, Mode.ADD)
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
                .statusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
                .toolBarColor(ContextCompat.getColor(this, R.color.colorPrimary))
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
                .onResult { mPresenter.onHeaderImagePicked(it.firstOrNull()?.path) }
                .start()
    }

    override fun showAdded() {
        Toast.makeText(this, "Added", Toast.LENGTH_SHORT).show()
    }

    override fun showDeleted() {
        Toast.makeText(this, "Deleted", Toast.LENGTH_SHORT).show()
    }

    override fun showNotes(notes: List<MainListItem>?) {
        mAdapter.submitList(notes)
        mRecyclerViewState?.let {
            list_main.layoutManager?.onRestoreInstanceState(mRecyclerViewState)
            mRecyclerViewState = null
        }
    }

    override fun onMainListItemClick(note: MyNote) {
        mPresenter.onMainListItemClick(note)
    }

    override fun onMainListItemLongClick(note: MyNote) {
        mPresenter.onMainListItemLongClick(note)
    }

    override fun showContextualActionBar() {
        startSupportActionMode(this)
    }

    override fun openNotePager(position: Int) {
        val intent = Intent(this, NoteActivity::class.java)
        intent.putExtra(EXTRA_CLICKED_NOTE_POSITION, position)
        intent.putExtra(EXTRA_MODE, Mode.READ)
        startActivity(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }
}
