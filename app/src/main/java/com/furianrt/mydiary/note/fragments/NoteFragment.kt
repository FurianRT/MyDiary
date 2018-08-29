package com.furianrt.mydiary.note.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
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
import com.furianrt.mydiary.note.Mode
import com.furianrt.mydiary.note.fragments.content.NoteContentFragment
import com.furianrt.mydiary.note.fragments.edit.ClickedView
import com.furianrt.mydiary.note.fragments.edit.NoteEditFragment
import com.furianrt.mydiary.utils.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_note.*
import kotlinx.android.synthetic.main.fragment_note.view.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import javax.inject.Inject

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
    val fragmentTransaction = beginTransaction()
    fragmentTransaction.func()
    fragmentTransaction.commit()
}

const val ARG_NOTE = "note"
const val ARG_MODE = "mode"

private const val MIN_TIME = 400L
private const val ZOOM = 15f
private const val MIN_DISTANCE = 1f
private const val REQUEST_CODE = 123

class NoteFragment : Fragment(), NoteFragmentContract.View, OnMapReadyCallback, LocationListener {

    private lateinit var mNote: MyNote
    private lateinit var mMode: Mode
    private var mGoogleMap: GoogleMap? = null

    @Inject
    lateinit var mLocationManager: LocationManager

    @Inject
    lateinit var mPresenter: NoteFragmentContract.Presenter

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
        }

        showFragments(savedInstanceState)

        return view
    }

    private fun showFragments(savedInstanceState: Bundle?) {
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
                        val mapFragment = SupportMapFragment()
                        add(R.id.container_map_note, mapFragment, mapTag)
                        mapFragment.getMapAsync(this@NoteFragment)
                        Log.e(LOG_TAG, "LoadingMap")
                    }
                }
                mPresenter.getForecast()
        } else if (mMode == Mode.READ) {

        }
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
        view?.container_map_note?.visibility = View.GONE
        mGoogleMap = map
        mGoogleMap?.uiSettings?.isScrollGesturesEnabled = false
        val permission = Manifest.permission.ACCESS_FINE_LOCATION
        if (EasyPermissions.hasPermissions(context!!, permission)) {
            requestLocation()
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.permission_request),
                    REQUEST_CODE, permission)
        }
    }

    @SuppressLint("MissingPermission")
    @AfterPermissionGranted(REQUEST_CODE)
    private fun requestLocation() {
        val criteria = Criteria()
        val provider = mLocationManager.getBestProvider(criteria, false)
        mLocationManager.requestLocationUpdates(provider, MIN_TIME, MIN_DISTANCE, this)
    }

    override fun onLocationChanged(location: Location?) {
        location?.let {
            Log.e(LOG_TAG, "ApplyingCoordsTOMap")
            val coords = LatLng(it.latitude, it.longitude)
            val camera = CameraUpdateFactory.newLatLngZoom(coords, ZOOM)
            mGoogleMap?.moveCamera(camera)
            mLocationManager.removeUpdates(this)
            view?.container_map_note?.visibility = View.VISIBLE
        }
    }

    override fun onProviderDisabled(provider: String?) {
        Log.e(LOG_TAG, "onProviderDisabled")
    }

    override fun onProviderEnabled(provider: String?) {
        Log.e(LOG_TAG, "onProviderEnabled")
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        Log.e(LOG_TAG, "onStatusChanged")
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
