package com.furianrt.mydiary.note.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
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
import com.google.android.gms.maps.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_note.*
import kotlinx.android.synthetic.main.fragment_note.view.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject
import android.location.Geocoder
import android.os.Looper
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import java.util.*

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
                val lastLocation = result.lastLocation
                val latLng = LatLng(lastLocation.latitude, lastLocation.longitude)
                mPresenter.getForecast(latLng)
                showLocation(latLng)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(context!!).inject(this)
        super.onCreate(savedInstanceState)
        arguments?.let {
            mNote = it.getSerializable(ARG_NOTE) as MyNote
            mMode = it.getSerializable(ARG_MODE) as Mode
        }
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

        if (mNote.id != 0L) {
            mPresenter.loadNoteProperties(mNote)
        }

        addFragments(savedInstanceState)

        mMapFragment.getMapAsync(this@NoteFragment)

        return view
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
        if (mMode == Mode.ADD && isNetworkAvailable(context!!)) {
            val mapTag = SupportMapFragment::class.toString()
            if (childFragmentManager.findFragmentByTag(mapTag) == null) {
                childFragmentManager.inTransaction {
                    add(R.id.container_map_note, mMapFragment, mapTag)
                }
            }
        } else if (mMode == Mode.READ) {

        }
    }

    override fun onClick(v: View) {
        val editTag = NoteEditFragment::class.toString()
        if (mMode == Mode.ADD && childFragmentManager.findFragmentByTag(editTag) != null) {
            childFragmentManager.popBackStack()
        }
        when(v.id) {
            R.id.text_tags -> mPresenter.onTagsFieldClick(mNote)
        }
    }

    override fun showTagsDialog(tags: ArrayList<MyTag>) {
        val dialog = TagsDialog.newInstance(tags)
        dialog.setOnTagChangedListener(this)
        dialog.show(activity?.supportFragmentManager, TagsDialog::class.toString())
    }

    override fun showForecast(forecast: Forecast?) {
        Picasso.get()
                .load("http://openweathermap.org/img/w/"
                        + forecast?.weather?.get(0)?.icon
                        + ".png")
                .into(image_weather)
        val temp = forecast?.main?.temp?.toInt()?.toString() + " °C"
        val wind = getString(R.string.wind) + " " + forecast?.wind?.speed?.toInt()?.toString() +
                " " + getString(R.string.ms)
        text_temp.text = temp
        text_wind_speed.text = wind
    }

    override fun showTagNames(tagNames: List<String>) {
        if (tagNames.isEmpty()) {
            view?.text_tags?.text = getString(R.string.choose_tags)
            view?.text_tags?.setTextColor(resources.getColor(R.color.grey_dark))
        } else {
            view?.text_tags?.text = tagNames.joinToString(", ")
            view?.text_tags?.setTextColor(Color.BLACK)
        }
    }

    override fun onTagsDialogPositiveButtonClick(tags: List<MyTag>) {
        mPresenter.changeNoteTags(mNote, tags)
    }

    override fun onTagsDialogTagDeleted() {

    }

    override fun onTagsDialogTagEdited() {

    }

    override fun onDestroyView() {
        super.onDestroyView()
        mPresenter.detachView()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onMapReady(map: GoogleMap?) {
        Log.e(LOG_TAG, "MapLoaded")
        mGoogleMap = map
        mGoogleMap?.let {
            it.uiSettings.isScrollGesturesEnabled = false
            requestLocationPermissions(context!!)
        }
    }

    private fun requestLocationPermissions(context: Context) {
        val permission1 = Manifest.permission.ACCESS_FINE_LOCATION
        val permission2 = Manifest.permission.ACCESS_COARSE_LOCATION
        if (EasyPermissions.hasPermissions(context, permission1, permission2)) {
            requestLocation()
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.permission_request),
                    REQUEST_CODE, permission1, permission2)
        }
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(REQUEST_CODE)
    private fun requestLocation() {
        val locationRequest = LocationRequest.create()
        locationRequest.apply {
            interval = LOCATION_INTERVAL
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        mFusedLocationClient
                .requestLocationUpdates(locationRequest,mLocationCallback, Looper.myLooper())
    }

    private fun showLocation(latLng: LatLng) {
        val geocoder = Geocoder(context, Locale.getDefault())
        val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
        val address = addresses[0].getAddressLine(0)
        view?.text_location?.text = address
        view?.text_location?.setTextColor(resources.getColor(R.color.black))
        val camera = CameraUpdateFactory.newLatLngZoom(latLng, ZOOM)
        mGoogleMap?.moveCamera(camera)
        view?.container_map_note?.visibility = View.VISIBLE
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
