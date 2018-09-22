package com.furianrt.mydiary.note.fragments.notefragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.support.design.widget.CoordinatorLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat
import android.support.v4.content.ContextCompat.getColor
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import com.furianrt.mydiary.LOG_TAG
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.api.Forecast
import com.furianrt.mydiary.data.model.MyImage
import com.furianrt.mydiary.data.model.MyLocation
import com.furianrt.mydiary.data.model.MyNoteWithProp
import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.gallery.EXTRA_NOTE_ID
import com.furianrt.mydiary.gallery.EXTRA_POSITION
import com.furianrt.mydiary.gallery.GalleryActivity
import com.furianrt.mydiary.general.AppBarLayoutBehavior
import com.furianrt.mydiary.general.GlideApp
import com.furianrt.mydiary.note.Mode
import com.furianrt.mydiary.note.dialogs.TagsDialog
import com.furianrt.mydiary.note.fragments.notefragment.content.NoteContentFragment
import com.furianrt.mydiary.note.fragments.notefragment.edit.ClickedView
import com.furianrt.mydiary.note.fragments.notefragment.edit.NoteEditFragment
import com.furianrt.mydiary.utils.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.api.widget.Widget
import kotlinx.android.synthetic.main.activity_main_toolbar.*
import kotlinx.android.synthetic.main.fragment_note.view.*
import kotlinx.android.synthetic.main.fragment_note_toolbar.view.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.IOException
import java.util.*
import javax.inject.Inject

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
    val fragmentTransaction = beginTransaction()
    fragmentTransaction.func()
    fragmentTransaction.commit()
}

const val ARG_NOTE = "note"
const val ARG_MODE = "mode"

private const val LOCATION_INTERVAL = 400L
private const val LOCATION_PERMISSIONS_REQUEST_CODE = 1
private const val STORAGE_PERMISSIONS_REQUEST_CODE = 2
private const val ZOOM = 15f

class NoteFragment : Fragment(), NoteFragmentContract.View, OnMapReadyCallback,
        TagsDialog.OnTagsDialogInteractionListener, View.OnClickListener {

    @Inject
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    @Inject
    lateinit var mPresenter: NoteFragmentContract.Presenter

    private var mGoogleMap: GoogleMap? = null
    private lateinit var mPagerAdapter: NoteFragmentPagerAdapter
    private lateinit var mMode: Mode

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            if (result != null) {
                mFusedLocationClient.removeLocationUpdates(this)
                mPresenter.onLocationReceived(result)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(context!!).inject(this)
        super.onCreate(savedInstanceState)
        val note = (if (savedInstanceState != null) {
            savedInstanceState.getParcelable<MyNoteWithProp>(ARG_NOTE)
        } else {
            arguments?.getParcelable(ARG_NOTE)
        }) ?: throw IllegalStateException()

        mPresenter.setNote(note)

        mMode = arguments?.getSerializable(ARG_MODE) as Mode
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_note, container, false)

        mPresenter.attachView(this)

        mPagerAdapter = NoteFragmentPagerAdapter(childFragmentManager)

        setupUi(view)

        addFragments(savedInstanceState)

        return view
    }

    private fun setupUi(view: View) {
        view.apply {
            val time = mPresenter.getNote().note.time
            text_day.text = getDay(time)
            text_day_of_week.text = getFullDayOfWeek(time)
            text_month.text = getMonth(time)
            text_year.text = getYear(time)
            text_time.text = getTime(time)
            text_tags.setOnClickListener(this@NoteFragment)
            fab_toolbar_note.setOnClickListener(this@NoteFragment)
            pager_note_image.adapter = mPagerAdapter
            map_touch_event_interceptor.setOnTouchListener { _, _ -> true }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPresenter.onViewCreated(mMode, isLocationEnabled(context!!), isNetworkAvailable(context!!))
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_image -> {
                mPresenter.onAddImageButtonClick()
                true
            }
            R.id.menu_edit -> {
                mPresenter.onEditButtonClick()
                true
            }
            R.id.menu_delete -> {
                mPresenter.onDeleteButtonClick()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }

    fun onToolbarImageClick() {
        mPresenter.onToolbarImageClick()
    }

    override fun showGalleryView(noteId: String) {
        val intent = Intent(context, GalleryActivity::class.java)
        intent.putExtra(EXTRA_POSITION, view!!.pager_note_image.currentItem)
        intent.putExtra(EXTRA_NOTE_ID, noteId)
        startActivity(intent)
    }

    override fun showNoteEditView() {
        val editTag = NoteEditFragment::class.toString()
        if (childFragmentManager.findFragmentByTag(editTag) == null) {
            fragmentManager?.inTransaction {
                setPrimaryNavigationFragment(this@NoteFragment)
            }
            childFragmentManager.inTransaction {
                val note = mPresenter.getNote().note
                replace(
                        R.id.container_note_edit,
                        NoteEditFragment.newInstance(note, ClickedView.TITLE, note.title.length),
                        editTag
                )
                        .addToBackStack(null)
            }
        }
    }

    override fun showNoTagsMessage() {
        view?.text_category?.text = getString(R.string.choose_tags)
        view?.text_category?.setTextColor(getColor(context!!, R.color.grey_dark))
    }

    override fun showCategoryName(name: String?) {
        view?.text_tags?.text = name
        view?.text_tags?.setTextColor(Color.BLACK)
    }

    override fun showNoCategoryMessage() {
        view?.text_category?.text = getString(R.string.choose_category)
        view?.text_category?.setTextColor(getColor(context!!, R.color.grey_dark))
    }

    override fun showMood(name: String?) {
        view?.text_tags?.text = name
        view?.text_tags?.setTextColor(Color.BLACK)
    }

    override fun showNoMoodMessage() {
        view?.text_mood?.text = getString(R.string.choose_mood)
        view?.text_mood?.setTextColor(getColor(context!!, R.color.grey_dark))
    }

    override fun showLocation(location: MyLocation) {
        view?.apply {
            text_location.text = location.name
            text_location.setTextColor(getColor(context!!, R.color.black))
            map_touch_event_interceptor.visibility = View.VISIBLE
            map_view.apply {
                visibility = View.VISIBLE
                onCreate(null)
                onResume()
                getMapAsync(this@NoteFragment)
            }
        }
    }

    override fun onMapReady(map: GoogleMap?) {
        Log.e(LOG_TAG, "MapLoaded")
        mGoogleMap = map
        mGoogleMap?.let {
            it.uiSettings.isScrollGesturesEnabled = false
            mPresenter.onMapReady()
        }
    }

    override fun zoomMap(latitude: Double, longitude: Double) {
        val camera = CameraUpdateFactory
                .newLatLngZoom(LatLng(latitude, longitude), ZOOM)
        mGoogleMap?.moveCamera(camera)
    }

    override fun requestLocationPermissions() {
        val fineLocation = Manifest.permission.ACCESS_FINE_LOCATION
        val coarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION
        if (EasyPermissions.hasPermissions(context!!, fineLocation, coarseLocation)) {
            mPresenter.onLocationPermissionsGranted()
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.location_permission_request),
                    LOCATION_PERMISSIONS_REQUEST_CODE, fineLocation, coarseLocation)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(ARG_NOTE, mPresenter.getNote())
        super.onSaveInstanceState(outState)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            val dialog =
                    fragmentManager?.findFragmentByTag(TagsDialog::class.toString()) as TagsDialog?
            dialog?.setOnTagChangedListener(this)
        }
    }

    fun disableActionBarExpanding(animate: Boolean) {
        app_bar_layout.setExpanded(false, animate)

        val coordParams = app_bar_layout.layoutParams as CoordinatorLayout.LayoutParams
        (coordParams.behavior as AppBarLayoutBehavior).shouldScroll = false
    }

    fun enableActionBarExpanding(expanded: Boolean, animate: Boolean) {
        app_bar_layout.setExpanded(expanded, animate)

        val coordParams = app_bar_layout.layoutParams as CoordinatorLayout.LayoutParams
        (coordParams.behavior as AppBarLayoutBehavior).shouldScroll = true
    }

    private fun addFragments(savedInstanceState: Bundle?) {
        val contentTag = NoteContentFragment::class.toString()
        if (childFragmentManager.findFragmentByTag(contentTag) == null) {
            childFragmentManager.inTransaction {
                replace(R.id.container_note_edit,
                        NoteContentFragment.newInstance(mPresenter.getNote().note), contentTag)
            }
        }

        if (mMode == Mode.ADD && savedInstanceState == null) {
            showNoteEditView()
        }
    }

    override fun onClick(v: View) {
        val editTag = NoteEditFragment::class.toString()
        if (childFragmentManager.findFragmentByTag(editTag) != null) {
            childFragmentManager.popBackStack()
        }

        when (v.id) {
            R.id.text_tags -> mPresenter.onTagsFieldClick()
            R.id.fab_toolbar_note -> mPresenter.onAddImageButtonClick()
        }
    }

    override fun showTagsDialog(tags: ArrayList<MyTag>) {
        val dialog = TagsDialog.newInstance(tags)
        dialog.setOnTagChangedListener(this)
        dialog.show(activity?.supportFragmentManager, TagsDialog::class.toString())
    }

    override fun showForecast(forecast: Forecast) {
        view?.apply {
            val url = "http://openweathermap.org/img/w/" + forecast.weather[0].icon + ".png"
            GlideApp.with(this)
                    .load(url)
                    .into(image_weather)

            val temp = forecast.main.temp.toInt().toString() + " °C"
            val wind = getString(R.string.wind) +
                    " " +
                    forecast.wind.speed.toInt().toString() +
                    " " +
                    getString(R.string.ms)

            text_temp.text = temp
            text_wind_speed.text = wind
        }
    }

    override fun showTagNames(tagNames: List<String>) {
        if (tagNames.isEmpty()) {
            view?.text_tags?.text = getString(R.string.choose_tags)
            view?.text_tags?.setTextColor(getColor(context!!, R.color.grey_dark))
        } else {
            view?.text_tags?.text = tagNames.joinToString(", ")
            view?.text_tags?.setTextColor(Color.BLACK)
        }
    }

    override fun onTagsDialogPositiveButtonClick(tags: List<MyTag>) {
        mPresenter.onNoteTagsChanged(tags)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter.detachView()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(LOCATION_PERMISSIONS_REQUEST_CODE)
    override fun requestLocation() {
        val locationRequest = LocationRequest.create()
        locationRequest.apply {
            interval = LOCATION_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        mFusedLocationClient
                .requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper())
    }

    override fun findAddress(latitude: Double, longitude: Double) {
        try {
            val geoCoder = Geocoder(context, Locale.getDefault())
            val addresses = geoCoder.getFromLocation(latitude, longitude, 1)
            mPresenter.onAddressFound(addresses, latitude, longitude)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun requestStoragePermissions() {
        val readExtStorage = Manifest.permission.READ_EXTERNAL_STORAGE
        val camera = Manifest.permission.CAMERA
        if (EasyPermissions.hasPermissions(context!!, readExtStorage, camera)) {
            mPresenter.onStoragePermissionsGranted()
        } else {
            EasyPermissions.requestPermissions(this,
                    getString(R.string.storage_permission_request),
                    STORAGE_PERMISSIONS_REQUEST_CODE, readExtStorage, camera)
        }
    }

    @AfterPermissionGranted(STORAGE_PERMISSIONS_REQUEST_CODE)
    override fun showImageExplorer() {
        val widget = Widget.newDarkBuilder(context)
                .statusBarColor(ContextCompat.getColor(context!!, R.color.colorPrimaryDark))
                .toolBarColor(ContextCompat.getColor(context!!, R.color.colorPrimary))
                .navigationBarColor(ContextCompat.getColor(context!!, R.color.black))
                .title(R.string.album)
                .build()

        Album.image(this)
                .multipleChoice()
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
                .onResult { albImage ->
                    mPresenter.onNoteImagesPicked(albImage.map { it.path })
                }
                .start()
    }

    override fun showImages(images: List<MyImage>) {
        mPagerAdapter.images = images
        mPagerAdapter.notifyDataSetChanged()
        Log.e(LOG_TAG, "showImages " + images.toString())
        if (childFragmentManager.findFragmentByTag(NoteEditFragment::class.toString()) == null) {
            enableActionBarExpanding(true, true)
        }
    }

    override fun showNoImages() {
        mPagerAdapter.images = emptyList()
        mPagerAdapter.notifyDataSetChanged()
        disableActionBarExpanding(false)
    }

    override fun closeView() {
        activity?.finish()
    }

    companion object {
        @JvmStatic
        fun newInstance(note: MyNoteWithProp, mode: Mode) =
                NoteFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_NOTE, note)
                        putSerializable(ARG_MODE, mode)
                    }
                }
    }
}
