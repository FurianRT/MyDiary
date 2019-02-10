package com.furianrt.mydiary.note.fragments.notefragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.location.Geocoder
import android.os.Bundle
import android.os.Looper
import android.preference.PreferenceManager
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.api.Forecast
import com.furianrt.mydiary.data.model.*
import com.furianrt.mydiary.data.prefs.PreferencesHelper
import com.furianrt.mydiary.gallery.GalleryActivity
import com.furianrt.mydiary.general.AppBarLayoutBehavior
import com.furianrt.mydiary.general.GlideApp
import com.furianrt.mydiary.note.NoteActivity
import com.furianrt.mydiary.note.dialogs.categories.CategoriesDialog
import com.furianrt.mydiary.note.dialogs.moods.MoodsDialog
import com.furianrt.mydiary.note.dialogs.tags.TagsDialog
import com.furianrt.mydiary.note.fragments.notefragment.content.NoteContentFragment
import com.furianrt.mydiary.note.fragments.notefragment.edit.NoteEditFragment
import com.furianrt.mydiary.settings.note.NoteSettingsActivity
import com.furianrt.mydiary.utils.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.api.widget.Widget
import kotlinx.android.synthetic.main.fragment_note.view.*
import kotlinx.android.synthetic.main.fragment_note_toolbar.*
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

class NoteFragment : Fragment(), NoteFragmentContract.View, OnMapReadyCallback,
        TagsDialog.OnTagsDialogInteractionListener, View.OnClickListener, MoodsDialog.OnMoodsDialogInteractionListener, CategoriesDialog.OnCategoriesDialogInteractionListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    @Inject
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    @Inject
    lateinit var mPresenter: NoteFragmentContract.Presenter

    private var mGoogleMap: GoogleMap? = null
    private lateinit var mPagerAdapter: NoteFragmentPagerAdapter
    private lateinit var mMode: NoteActivity.Companion.Mode
    private var mImagePagerPosition = 0
    private lateinit var mNoteId: String

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

        mNoteId = arguments?.getString(ARG_NOTE_ID) ?: throw IllegalArgumentException()
        savedInstanceState?.let {
            mImagePagerPosition = it.getInt(BUNDLE_IMAGE_PAGER_POSITION, 0)
        }

        mMode = arguments?.getSerializable(ARG_MODE) as NoteActivity.Companion.Mode
        setHasOptionsMenu(true)
    }

    override fun onResume() {
        super.onResume()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_note, container, false)

        someTemporaryFunction(view)

        mPagerAdapter = NoteFragmentPagerAdapter(childFragmentManager)

        view.apply {
            layout_mood.setOnClickListener(this@NoteFragment)
            layout_category.setOnClickListener(this@NoteFragment)
            layout_tags.setOnClickListener(this@NoteFragment)
            text_date.setOnClickListener(this@NoteFragment)
            text_time.setOnClickListener(this@NoteFragment)
            fab_add_image.setOnClickListener(this@NoteFragment)
            pager_note_image.adapter = mPagerAdapter
            map_touch_event_interceptor.setOnTouchListener { _, _ -> true }
        }

        addFragments()

        return view
    }

    override fun onStart() {
        super.onStart()
        mPresenter.attachView(this)
        mPresenter.loadNote(mNoteId, mMode, isLocationEnabled(context!!), isNetworkAvailable(context!!))
        mPresenter.loadNoteAppearance(mNoteId)
        mPresenter.loadTags(mNoteId)
        mPresenter.loadImages(mNoteId)
    }

    override fun showNoteText(title: String, content: String) {
        val noteContentFragment =
                childFragmentManager.findFragmentByTag(NoteContentFragment.TAG) as? NoteContentFragment
        noteContentFragment?.showNoteText(title, content)
    }

    override fun showDateAndTime(time: Long, is24TimeFormat: Boolean) {
        view?.apply {
            text_date.text = formatTime(time)
            text_time.text = getTime(time, is24TimeFormat)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_image -> {
                removeEditFragment()
                mPresenter.onAddImageButtonClick()
                true
            }
            R.id.menu_delete -> {
                mPresenter.onDeleteButtonClick()
                true
            }
            R.id.menu_appearance -> {
                removeEditFragment()
                mPresenter.onAppearanceButtonClick()
                true
            }
            R.id.menu_date -> {
                removeEditFragment()
                mPresenter.onDateFieldClick()
                true
            }
            R.id.menu_time -> {
                removeEditFragment()
                mPresenter.onTimeFieldClick()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun showNoteSettingsView(noteId: String) {
        val intent = Intent(context, NoteSettingsActivity::class.java)
        intent.putExtra(NoteSettingsActivity.EXTRA_NOTE_ID, noteId)
        startActivity(intent)
    }

    override fun updateNoteAppearance(appearance: MyNoteAppearance) {
        view?.apply {
            appearance.background?.let { layout_root_note.setBackgroundColor(it) }
            appearance.textBackground?.let { card_note_edit.setCardBackgroundColor(it) }
            appearance.textColor?.let { text_mood.setTextColor(it) }
            appearance.textColor?.let { text_location.setTextColor(it) }
            appearance.textColor?.let { text_date.setTextColor(it) }
            appearance.textColor?.let { text_time.setTextColor(it) }
            appearance.textColor?.let { text_temp.setTextColor(it) }
            appearance.textColor?.let { text_category.setTextColor(it) }
        }
        childFragmentManager.findFragmentByTag(NoteContentFragment.TAG)?.let {
            (it as NoteContentFragment).setAppearance(appearance)
        }
    }

    private fun removeEditFragment() {
        childFragmentManager.findFragmentByTag(NoteContentFragment.TAG)?.let {
            (it as NoteContentFragment).removeEditFragment()
        }
    }

    fun onToolbarImageClick() {
        mPresenter.onToolbarImageClick()
    }

    fun onNoteEditFinished(noteTitle: String, noteContent: String) {
        val noteContentFragment =
                childFragmentManager.findFragmentByTag(NoteContentFragment.TAG) as? NoteContentFragment
        noteContentFragment?.showNoteText(noteTitle, noteContent)
        mPresenter.updateNoteText(mNoteId, noteTitle, noteContent)
    }

    override fun showGalleryView(noteId: String) {
        val intent = Intent(context, GalleryActivity::class.java)
        intent.putExtra(GalleryActivity.EXTRA_POSITION, view!!.pager_note_image.currentItem)
        intent.putExtra(GalleryActivity.EXTRA_NOTE_ID, noteId)
        startActivity(intent)
    }

    private fun someTemporaryFunction(view: View) {
        val isMoodEnabled = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PreferencesHelper.MOOD_AVAILABILITY, true)
        if (!isMoodEnabled) {
            view.layout_mood.visibility = View.GONE
        }

        val isMapEnabled = PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PreferencesHelper.MAP_AVAILABILITY, true)
        if (!isMapEnabled) {
            view.map_view.visibility = View.GONE
            view.text_location.visibility = View.GONE
            view.map_touch_event_interceptor.visibility = View.GONE
        }
    }

    override fun hideLocation() {
        view?.text_location?.visibility = View.GONE
    }

    override fun hideMood() {
        view?.text_mood?.visibility = View.GONE
    }

    override fun showNoTagsMessage() {
        view?.apply {
            val itemCount = layout_tags.flexItemCount
            layout_tags.removeViews(1, itemCount - 1)

            val image = layout_tags.getChildAt(0) as? ImageView?
            image?.setColorFilter(getColor(context!!, R.color.grey_dark), PorterDuff.Mode.SRC_IN)

            val textNoTags = TextView(context!!)
            textNoTags.setTextColor(getColor(context!!, R.color.grey_dark))
            textNoTags.setText(R.string.choose_tags)

            val params = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.marginStart = 16
            textNoTags.layoutParams = params

            layout_tags.addView(textNoTags)
        }
    }

    override fun showTagNames(tagNames: List<String>) {
        view?.apply {
            val itemCount = layout_tags.flexItemCount
            layout_tags.removeViews(1, itemCount - 1)
            val image = layout_tags.getChildAt(0) as? ImageView?
            image?.setColorFilter(getColor(context!!, R.color.black), PorterDuff.Mode.SRC_IN)
            for (tagName in tagNames) {
                layout_tags.addView(wrapTextIntoCardView(tagName))
            }
        }
    }

    private fun wrapTextIntoCardView(text: String) =
            CardView(context!!).apply {
                elevation = 5f
                radius = 25f
                val params = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(10, 10, 10, 10)
                layoutParams = params

                addView(TextView(context).apply {
                    this.text = text
                    setTextColor(Color.BLACK)
                    setPadding(20, 10, 20, 10)
                })
            }

    override fun showCategory(category: MyCategory) {
        view?.apply {
            text_category.text = category.name
            text_category.alpha = 1f
            image_folder.alpha = 1f
            image_folder.setColorFilter(category.color, PorterDuff.Mode.SRC_IN)
            layout_category_color.setCardBackgroundColor(category.color)
            layout_category_color.visibility = View.VISIBLE
        }
    }

    override fun showNoCategoryMessage() {
        view?.apply {
            text_category.text = getString(R.string.choose_category)
            text_category.alpha = 0.5f
            image_folder.alpha = 0.5f
            image_folder.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN)
            layout_category_color.visibility = View.GONE
        }
    }

    override fun showMood(mood: MyMood) {
        view?.apply {
            text_mood.alpha = 1f
            text_mood.text = mood.name
            val smile = resources.getIdentifier(mood.iconName, "drawable", context?.packageName)
            image_mood.clearColorFilter()
            image_mood.setImageResource(smile)
            image_mood.alpha = 1f
        }
    }

    override fun showNoMoodMessage() {
        view?.apply {
            text_mood.text = getString(R.string.choose_mood)
            text_mood.alpha = 0.5f
            image_mood.alpha = 0.5f
            image_mood.setImageResource(R.drawable.ic_smile)
            image_mood.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN)
        }
    }

    override fun showMoodsDialog(moods: List<MyMood>) {
        val dialog = MoodsDialog()
        dialog.setOnMoodsDialogInteractionListener(this)
        dialog.show(activity?.supportFragmentManager, MoodsDialog.TAG)
    }

    override fun showLocation(location: MyLocation) {
        view?.apply {
            text_location.text = location.name
            text_location.alpha = 1f
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
        view?.let { outState.putInt(BUNDLE_IMAGE_PAGER_POSITION, it.pager_note_image.currentItem) }
        super.onSaveInstanceState(outState)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            val tagsDialog =
                    fragmentManager?.findFragmentByTag(TagsDialog.TAG) as TagsDialog?
            tagsDialog?.setOnTagChangedListener(this)

            val moodsDialog =
                    fragmentManager?.findFragmentByTag(MoodsDialog.TAG) as MoodsDialog?
            moodsDialog?.setOnMoodsDialogInteractionListener(this)

            val categoriesDialog =
                    fragmentManager?.findFragmentByTag(CategoriesDialog.TAG) as CategoriesDialog?
            categoriesDialog?.setOnCategoriesDialogListener(this)
        }
    }

    override fun onMoodPicked(mood: MyMood) {
        mPresenter.onMoodPicked(mood)
    }

    override fun onNoMoodPicked() {
        mPresenter.onNoMoodPicked()
    }

    fun disableActionBarExpanding(animate: Boolean) {
        Log.e(TAG, "disableActionBarExpanding")
        app_bar_layout.setExpanded(false, animate)

        val coordParams = app_bar_layout.layoutParams as CoordinatorLayout.LayoutParams
        (coordParams.behavior as AppBarLayoutBehavior).shouldScroll = false
    }

    fun enableActionBarExpanding(expanded: Boolean, animate: Boolean) {
        Log.e(TAG, "enableActionBarExpanding")
        if (mPagerAdapter.images.isEmpty()) {
            return
        }
        app_bar_layout.setExpanded(expanded, animate)

        val params = app_bar_layout.layoutParams as CoordinatorLayout.LayoutParams
        (params.behavior as AppBarLayoutBehavior).shouldScroll = true
    }

    private fun addFragments() {
        if (childFragmentManager.findFragmentByTag(NoteContentFragment.TAG) == null) {
            childFragmentManager.inTransaction {
                add(R.id.container_note_edit, NoteContentFragment.newInstance(mMode), NoteContentFragment.TAG)
            }
        }
    }

    override fun onClick(v: View) {
        removeEditFragment()
        when (v.id) {
            R.id.layout_tags -> mPresenter.onTagsFieldClick()
            R.id.layout_mood -> mPresenter.onMoodFieldClick()
            R.id.layout_category -> mPresenter.onCategoryFieldClick()
            R.id.text_date -> mPresenter.onDateFieldClick()
            R.id.text_time -> mPresenter.onTimeFieldClick()
            R.id.fab_add_image -> {
                removeEditFragment()
                mPresenter.onAddImageButtonClick()
            }
        }
    }

    override fun showCategoriesDialog(noteId: String) {
        val dialog = CategoriesDialog.newInstance(noteId)
        dialog.setOnCategoriesDialogListener(this)
        dialog.show(activity?.supportFragmentManager, CategoriesDialog.TAG)
    }

    override fun showTagsDialog(tags: ArrayList<MyTag>) {
        val dialog = TagsDialog.newInstance(tags)
        dialog.setOnTagChangedListener(this)
        dialog.show(activity?.supportFragmentManager, TagsDialog.TAG)
    }

    override fun showForecast(forecast: Forecast) {
        view?.apply {
            val url = "http://openweathermap.org/img/w/" + forecast.weather[0].icon + ".png"
            GlideApp.with(this)
                    .load(url)
                    .into(image_weather)

            val temp = forecast.main.temp.toInt().toString() + " °C"
            text_temp.text = temp
        }
    }

    override fun onCategoryPicked(category: MyCategory) {
        mPresenter.onCategoryPicked(category)
    }

    override fun onNoCategoryPicked() {
        mPresenter.onNoCategoryPicked()
    }

    override fun onTagsDialogPositiveButtonClick(tags: List<MyTag>) {
        mPresenter.onNoteTagsChanged(tags)
    }

    override fun onStop() {
        super.onStop()
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
                .statusBarColor(getThemePrimaryDarkColor(context!!))
                .toolBarColor(getThemePrimaryColor(context!!))
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
                    mImagePagerPosition = 0
                    mPresenter.onNoteImagesPicked(albImage.map { it.path })
                }
                .start()
    }

    override fun showImages(images: List<MyImage>) {
        Log.e(TAG, "showImages")
        mPagerAdapter.images = images
        mPagerAdapter.notifyDataSetChanged()
        view?.pager_note_image?.setCurrentItem(mImagePagerPosition, false)
        if (childFragmentManager.findFragmentByTag(NoteEditFragment.TAG) == null) {
            enableActionBarExpanding(expanded = true, animate = true)
        }
    }

    override fun showNoImages() {
        Log.e(TAG, "showNoImages")
        mPagerAdapter.images = emptyList()
        mPagerAdapter.notifyDataSetChanged()
        disableActionBarExpanding(false)
    }

    override fun closeView() {
        activity?.finish()
    }

    fun onNoteTextChange(title: String, content: String) {
        childFragmentManager.findFragmentByTag(NoteContentFragment.TAG)?.let {
            (it as NoteContentFragment).updateNoteText(title, content)
        }
    }

    override fun showDatePicker(calendar: Calendar) {
        DatePickerDialog.newInstance(this, calendar).apply {
            accentColor = fetchPrimaryColor()
            val accentColor = fetchAccentColor()
            setOkColor(accentColor)
            setCancelColor(accentColor)
        }.show(fragmentManager, "datePicker")
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        mPresenter.onDateSelected(year, monthOfYear, dayOfMonth)
    }

    override fun showTimePicker(hourOfDay: Int, minute: Int, is24HourMode: Boolean) {
        TimePickerDialog.newInstance(this, hourOfDay, minute, is24HourMode).apply {
            accentColor = fetchPrimaryColor()
            setOkColor(fetchAccentColor())
            setCancelColor(fetchAccentColor())
            activity?.let {
                setOkText(it.getString(R.string.time_picker_okay))
                setCancelText(it.getString(R.string.time_picker_cancel))
            }
            setLocale(Locale.ENGLISH)
        }.show(fragmentManager, "timePicker")
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        mPresenter.onTimeSelected(hourOfDay, minute)
    }

    private fun fetchPrimaryColor(): Int {
        val typedValue = TypedValue()
        val a =
                activity!!.obtainStyledAttributes(typedValue.data, intArrayOf(R.attr.colorPrimary))
        val color = a.getColor(0, 0)
        a.recycle()
        return color
    }

    private fun fetchAccentColor(): Int {
        val typedValue = TypedValue()
        val a =
                activity!!.obtainStyledAttributes(typedValue.data, intArrayOf(R.attr.colorAccent))
        val color = a.getColor(0, 0)
        a.recycle()
        return color
    }

    companion object {

        const val TAG = "NoteFragment"
        private const val ARG_NOTE_ID = "noteId"
        private const val ARG_MODE = "mode"
        private const val LOCATION_INTERVAL = 400L
        private const val LOCATION_PERMISSIONS_REQUEST_CODE = 1
        private const val STORAGE_PERMISSIONS_REQUEST_CODE = 2
        private const val ZOOM = 15f
        private const val BUNDLE_IMAGE_PAGER_POSITION = "imagePagerPosition"

        @JvmStatic
        fun newInstance(noteId: String, mode: NoteActivity.Companion.Mode) =
                NoteFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_NOTE_ID, noteId)
                        putSerializable(ARG_MODE, mode)
                    }
                }
    }
}
