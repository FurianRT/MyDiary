package com.furianrt.mydiary.note.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v4.content.ContextCompat.getColor
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.furianrt.mydiary.LOG_TAG
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.api.Forecast
import com.furianrt.mydiary.data.model.MyNote
import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.note.Mode
import com.furianrt.mydiary.note.dialogs.TagsDialog
import com.furianrt.mydiary.note.fragments.content.NoteContentFragment
import com.furianrt.mydiary.note.fragments.edit.ClickedView
import com.furianrt.mydiary.note.fragments.edit.NoteEditFragment
import com.furianrt.mydiary.utils.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_note.view.*
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
private const val REQUEST_CODE = 123
private const val ZOOM = 15f

class NoteFragment : Fragment(), NoteFragmentContract.View, OnMapReadyCallback,
        TagsDialog.OnTagsDialogInteractionListener, View.OnClickListener {

    private lateinit var mNote: MyNote
    private lateinit var mMode: Mode
    private var mGoogleMap: GoogleMap? = null
    private val mMapFragment = SupportMapFragment()

    @Inject
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    @Inject
    lateinit var mPresenter: NoteFragmentContract.Presenter

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)
            if (result != null) {
                mFusedLocationClient.removeLocationUpdates(this)
                mPresenter.onLocationReceived(mNote, result)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(context!!).inject(this)
        super.onCreate(savedInstanceState)
        mNote = if (savedInstanceState != null) {
            savedInstanceState.getSerializable(ARG_NOTE) as MyNote
        } else {
            arguments?.getSerializable(ARG_NOTE) as MyNote
        }
        mMode = arguments?.getSerializable(ARG_MODE) as Mode
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_note, container, false)

        mPresenter.attachView(this)

        view.apply {
            val time = mNote.time
            text_day.text = getDay(time)
            text_day_of_week.text = getFullDayOfWeek(time)
            text_month.text = getMonth(time)
            text_year.text = getYear(time)
            text_time.text = getTime(time)
            text_tags.setOnClickListener(this@NoteFragment)
        }

        addFragments(savedInstanceState)

        view.map_touch_event_interceptor.setOnTouchListener { _, _ -> true }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mPresenter.loadNoteProperties(mNote)
        mPresenter.findLocation(mNote, mMode,
                isLocationEnabled(context!!), isNetworkAvailable(context!!))
    }

    override fun requestLocationPermissions() {
        val permission1 = Manifest.permission.ACCESS_FINE_LOCATION
        val permission2 = Manifest.permission.ACCESS_COARSE_LOCATION
        if (EasyPermissions.hasPermissions(context!!, permission1, permission2)) {
            mPresenter.onPermissionsGranted()
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.permission_request),
                    REQUEST_CODE, permission1, permission2)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putSerializable(ARG_NOTE, mNote)
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

    private fun addFragments(savedInstanceState: Bundle?) {
        val contentTag = NoteContentFragment::class.toString()
        if (childFragmentManager.findFragmentByTag(contentTag) == null) {
            childFragmentManager.inTransaction {
                add(R.id.container_note_edit, NoteContentFragment.newInstance(mNote), contentTag)
            }
        }

        val editTag = NoteEditFragment::class.toString()
        if (mMode == Mode.ADD
                && childFragmentManager.findFragmentByTag(editTag) == null
                && savedInstanceState == null) {
            fragmentManager?.inTransaction {
                setPrimaryNavigationFragment(this@NoteFragment)
            }
            childFragmentManager.inTransaction {
                replace(R.id.container_note_edit,
                        NoteEditFragment.newInstance(mNote, ClickedView.TITLE, 0), editTag)
                        .addToBackStack(null)
            }
        }

        val mapTag = SupportMapFragment::class.toString()
        if (childFragmentManager.findFragmentByTag(mapTag) == null) {
            childFragmentManager.inTransaction {
                add(R.id.container_map_note, mMapFragment, mapTag)
            }
        }
    }

    override fun onClick(v: View) {
        val editTag = NoteEditFragment::class.toString()
        if (childFragmentManager.findFragmentByTag(editTag) != null) {
            childFragmentManager.popBackStack()
        }
        when (v.id) {
            R.id.text_tags -> mPresenter.onTagsFieldClick(mNote)
        }
    }

    override fun showTagsDialog(tags: ArrayList<MyTag>) {
        val dialog = TagsDialog.newInstance(mNote, tags)
        dialog.setOnTagChangedListener(this)
        dialog.show(activity?.supportFragmentManager, TagsDialog::class.toString())
    }

    override fun showForecast(forecast: Forecast) {
        Picasso.get()
                .load("http://openweathermap.org/img/w/"
                        + forecast.weather[0].icon
                        + ".png")
                .into(view?.image_weather)
        val temp = forecast.main.temp.toInt().toString() + " °C"
        val wind = getString(R.string.wind) + " " + forecast.wind.speed.toInt().toString() +
                " " + getString(R.string.ms)
        view?.text_temp?.text = temp
        view?.text_wind_speed?.text = wind
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
        mPresenter.changeNoteTags(mNote, tags)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter.detachView()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(REQUEST_CODE)
    override fun requestLocation() {
        val locationRequest = LocationRequest.create()
        locationRequest.apply {
            interval = LOCATION_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        mFusedLocationClient
                .requestLocationUpdates(locationRequest, mLocationCallback, Looper.myLooper())
    }

    override fun showAddress(address: String) {
        view?.text_location?.text = address
        view?.text_location?.setTextColor(getColor(context!!, R.color.black))
    }

    override fun showMap() {
        view?.container_map_note?.visibility = View.VISIBLE
        mMapFragment.getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap?) {
        Log.e(LOG_TAG, "MapLoaded")
        mGoogleMap = map
        mGoogleMap?.let {
            it.uiSettings.isScrollGesturesEnabled = false
            mPresenter.onMapReady(mNote.latitude, mNote.longitude)
        }
    }

    override fun zoomMap(latitude: Double, longitude: Double) {
        val camera = CameraUpdateFactory
                .newLatLngZoom(LatLng(latitude, longitude), ZOOM)
        mGoogleMap?.moveCamera(camera)
    }

    override fun showAddressNotFound() {
        mNote.address = getString(R.string.unknown_address)
        view?.text_location?.text = mNote.address
    }

    override fun findAddress(latitude: Double, longitude: Double) {
        try {
            val geoCoder = Geocoder(context, Locale.getDefault())
            val addresses = geoCoder.getFromLocation(latitude, longitude, 1)
            mPresenter.onAddressFound(mNote, addresses)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(note: MyNote, mode: Mode) =
                NoteFragment().apply {
                    arguments = Bundle().apply {
                        putSerializable(ARG_NOTE, note)
                        putSerializable(ARG_MODE, mode)
                    }
                }
    }
}
