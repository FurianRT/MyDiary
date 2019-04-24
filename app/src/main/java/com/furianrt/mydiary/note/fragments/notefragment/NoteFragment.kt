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
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat.getColor
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.api.forecast.Forecast
import com.furianrt.mydiary.data.model.*
import com.furianrt.mydiary.data.prefs.PreferencesHelper
import com.furianrt.mydiary.dialogs.categories.CategoriesDialog
import com.furianrt.mydiary.dialogs.delete.note.DeleteNoteDialog
import com.furianrt.mydiary.dialogs.moods.MoodsDialog
import com.furianrt.mydiary.dialogs.tags.TagsDialog
import com.furianrt.mydiary.gallery.GalleryActivity
import com.furianrt.mydiary.general.AppBarLayoutBehavior
import com.furianrt.mydiary.general.GlideApp
import com.furianrt.mydiary.note.NoteActivity
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
import com.google.android.material.card.MaterialCardView
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import com.yanzhenjie.album.Album
import com.yanzhenjie.album.api.widget.Widget
import kotlinx.android.synthetic.main.fragment_note.*
import kotlinx.android.synthetic.main.fragment_note.view.*
import kotlinx.android.synthetic.main.fragment_note_toolbar.*
import kotlinx.android.synthetic.main.fragment_note_toolbar.view.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.IOException
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class NoteFragment : Fragment(), NoteFragmentContract.View, OnMapReadyCallback,
        TagsDialog.OnTagsDialogInteractionListener, View.OnClickListener,
        MoodsDialog.OnMoodsDialogInteractionListener,
        CategoriesDialog.OnCategoriesDialogInteractionListener,
        DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener,
        DeleteNoteDialog.OnDeleteNoteConfirmListener {

    companion object {
        const val TAG = "NoteFragment"
        private const val ARG_NOTE = "note"
        private const val ARG_MODE = "mode"
        private const val LOCATION_INTERVAL = 400L
        private const val LOCATION_PERMISSIONS_REQUEST_CODE = 1
        private const val STORAGE_PERMISSIONS_REQUEST_CODE = 2
        private const val ZOOM = 15f
        private const val BUNDLE_IMAGE_PAGER_POSITION = "imagePagerPosition"
        private const val BUNDLE_NOTE_TEXT_BUFFER = "noteTextBuffer"
        private const val BASE_WEATHER_IMAGE_URL = "http://openweathermap.org/img/w/"
        private const val TIME_PICKER_TAG = "timePicker"
        private const val DATE_PICKER_TAG = "datePicker"

        @JvmStatic
        fun newInstance(note: MyNote, mode: NoteActivity.Companion.Mode) =
                NoteFragment().apply {
                    arguments = Bundle().apply {
                        putParcelable(ARG_NOTE, note)
                        putSerializable(ARG_MODE, mode)
                    }
                }
    }

    @Inject
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    @Inject
    lateinit var mPresenter: NoteFragmentContract.Presenter

    private var mGoogleMap: GoogleMap? = null
    private lateinit var mPagerAdapter: NoteFragmentPagerAdapter
    private lateinit var mMode: NoteActivity.Companion.Mode
    private var mImagePagerPosition = 0

    private val mOnPageChangeListener = object : ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {}
        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
        override fun onPageSelected(position: Int) {
            text_image_position.text = (position + 1).toString()
        }
    }

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
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
        val note = arguments?.getParcelable<MyNote>(ARG_NOTE) ?: throw IllegalArgumentException()
        mMode = arguments?.getSerializable(ARG_MODE) as NoteActivity.Companion.Mode
        savedInstanceState?.let {
            mImagePagerPosition = it.getInt(BUNDLE_IMAGE_PAGER_POSITION, 0)
            mPresenter.setNoteTextBuffer(it.getParcelableArrayList(BUNDLE_NOTE_TEXT_BUFFER)
                    ?: ArrayList())
        }
        mPresenter.init(note, mMode)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_note, container, false)


        someTemporaryFunction(view)

        mPagerAdapter = NoteFragmentPagerAdapter(childFragmentManager)
        view.pager_note_image.adapter = mPagerAdapter

        view.layout_mood.setOnClickListener(this@NoteFragment)
        view.layout_category.setOnClickListener(this@NoteFragment)
        view.layout_tags.setOnClickListener(this@NoteFragment)
        view.text_date.setOnClickListener(this@NoteFragment)
        view.text_time.setOnClickListener(this@NoteFragment)
        view.fab_add_image.setOnClickListener(this@NoteFragment)
        view.layout_loading.setOnTouchListener { _, _ -> true }
        view.map_touch_event_interceptor.setOnTouchListener { _, _ -> true }

        if (childFragmentManager.findFragmentByTag(NoteContentFragment.TAG) == null) {
            childFragmentManager.inTransaction {
                add(R.id.container_note_edit, NoteContentFragment.newInstance(mMode), NoteContentFragment.TAG)
            }
        }

        return view
    }

    override fun onResume() {
        super.onResume()
        activity?.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        pager_note_image.addOnPageChangeListener(mOnPageChangeListener)
        mPresenter.attachView(this)
        mPresenter.onViewStart(requireContext().isLocationEnabled(), requireContext().isNetworkAvailable())
    }

    override fun onPause() {
        super.onPause()
        mImagePagerPosition = pager_note_image.currentItem
        pager_note_image.removeOnPageChangeListener(mOnPageChangeListener)
        mPresenter.detachView()
    }

    override fun showNoteText(title: String, content: String) {
        (childFragmentManager.findFragmentByTag(NoteContentFragment.TAG) as? NoteContentFragment?)
                ?.showNoteText(title, content)
        (childFragmentManager.findFragmentByTag(NoteEditFragment.TAG) as? NoteEditFragment?)
                ?.showNoteText(title, content)
    }

    override fun showDateAndTime(time: Long, is24TimeFormat: Boolean) {
        text_date.text = formatTime(time)
        text_time.text = getTime(time, is24TimeFormat)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.menu_image -> {
                removeEditFragment()
                mPresenter.onButtonAddImageClick()
                true
            }
            R.id.menu_delete -> {
                mPresenter.onButtonDeleteClick()
                true
            }
            R.id.menu_appearance -> {
                removeEditFragment()
                mPresenter.onButtonAppearanceClick()
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
            R.id.menu_edit -> {
                mPresenter.onButtonEditClick()
                true
            }
            R.id.menu_undo -> {
                mPresenter.onButtonUndoClick()
                true
            }
            R.id.menu_redo -> {
                mPresenter.onButtonRedoClick()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun showDeleteConfirmationDialog(note: MyNote) {
        DeleteNoteDialog.newInstance(listOf(note)).apply {
            setOnDeleteConfirmListener(this@NoteFragment)
        }.show(activity?.supportFragmentManager, DeleteNoteDialog.TAG)
    }

    override fun onDialogButtonDeleteClick(notes: List<MyNote>) {
        mPresenter.onButtonDeleteConfirmClick(notes.first())
    }

    override fun shoNoteEditView() {
        childFragmentManager.findFragmentByTag(NoteContentFragment.TAG)?.let {
            (it as NoteContentFragment).showNoteEditViewForTitleEnd()
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
        mPresenter.updateNoteText(noteTitle, noteContent)
    }

    override fun showGalleryView(noteId: String) {
        val intent = Intent(context, GalleryActivity::class.java)
        intent.putExtra(GalleryActivity.EXTRA_POSITION, view!!.pager_note_image.currentItem)
        intent.putExtra(GalleryActivity.EXTRA_NOTE_ID, noteId)
        startActivity(intent)
    }

    private fun someTemporaryFunction(view: View) {   //todo убрать костыль
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
        text_location.visibility = View.GONE
    }

    override fun hideMood() {
        text_mood.visibility = View.GONE
    }

    override fun showNoTagsMessage() {
        val itemCount = layout_tags.flexItemCount
        layout_tags.removeViews(1, itemCount - 1)

        val image = layout_tags.getChildAt(0) as? ImageView?
        image?.setColorFilter(getColor(requireContext(), R.color.grey_dark), PorterDuff.Mode.SRC_IN)

        val textNoTags = TextView(requireContext())
        textNoTags.setTextColor(getColor(requireContext(), R.color.grey_dark))
        textNoTags.setText(R.string.choose_tags)

        val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.marginStart = 16
        textNoTags.layoutParams = params

        layout_tags.addView(textNoTags)
    }

    override fun showTagNames(tagNames: List<String>) {
        val itemCount = layout_tags.flexItemCount
        layout_tags.removeViews(1, itemCount - 1)
        val image = layout_tags.getChildAt(0) as? ImageView?
        image?.setColorFilter(getColor(requireContext(), R.color.black), PorterDuff.Mode.SRC_IN)
        for (tagName in tagNames) {
            layout_tags.addView(wrapTextIntoCardView(tagName))
        }
    }

    private fun wrapTextIntoCardView(text: String) =
            MaterialCardView(requireContext()).apply {
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
        text_category.text = category.name
        text_category.alpha = 1f
        image_folder.alpha = 1f
        image_folder.setColorFilter(category.color, PorterDuff.Mode.SRC_IN)
        layout_category_color.setCardBackgroundColor(category.color)
        layout_category_color.visibility = View.VISIBLE
    }

    override fun showNoCategoryMessage() {
        text_category.text = getString(R.string.choose_category)
        text_category.alpha = 0.5f
        image_folder.alpha = 0.5f
        image_folder.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN)
        layout_category_color.visibility = View.GONE
    }

    override fun showMood(mood: MyMood) {
        text_mood.alpha = 1f
        text_mood.text = mood.name
        val smile = resources.getIdentifier(mood.iconName, "drawable", context?.packageName)
        image_mood.clearColorFilter()
        image_mood.setImageResource(smile)
        image_mood.alpha = 1f
    }

    override fun showNoMoodMessage() {
        text_mood.text = getString(R.string.choose_mood)
        text_mood.alpha = 0.5f
        image_mood.alpha = 0.5f
        image_mood.setImageResource(R.drawable.ic_smile)
        image_mood.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN)
    }

    override fun showMoodsDialog(moods: List<MyMood>) {
        MoodsDialog().apply {
            setOnMoodsDialogInteractionListener(this@NoteFragment)
        }.show(activity?.supportFragmentManager, MoodsDialog.TAG)
    }

    override fun showLocation(location: MyLocation) {
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
        if (EasyPermissions.hasPermissions(requireContext(), fineLocation, coarseLocation)) {
            mPresenter.onLocationPermissionsGranted()
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.location_permission_request),
                    LOCATION_PERMISSIONS_REQUEST_CODE, fineLocation, coarseLocation)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(BUNDLE_IMAGE_PAGER_POSITION, pager_note_image.currentItem)
        outState.putParcelableArrayList(BUNDLE_NOTE_TEXT_BUFFER, mPresenter.getNoteTextBuffer())
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            (activity?.supportFragmentManager?.findFragmentByTag(TagsDialog.TAG) as? TagsDialog?)
                    ?.setOnTagChangedListener(this)
            (activity?.supportFragmentManager?.findFragmentByTag(MoodsDialog.TAG) as? MoodsDialog?)
                    ?.setOnMoodsDialogInteractionListener(this)
            (activity?.supportFragmentManager?.findFragmentByTag(CategoriesDialog.TAG) as? CategoriesDialog?)
                    ?.setOnCategoriesDialogListener(this)
            (activity?.supportFragmentManager?.findFragmentByTag(DeleteNoteDialog.TAG) as? DeleteNoteDialog?)
                    ?.setOnDeleteConfirmListener(this)
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
                mPresenter.onButtonAddImageClick()
            }
        }
    }

    override fun showCategoriesDialog(noteId: String) {
        CategoriesDialog.newInstance(noteId).apply {
            setOnCategoriesDialogListener(this@NoteFragment)
        }.show(activity?.supportFragmentManager, CategoriesDialog.TAG)
    }

    override fun showTagsDialog(tags: ArrayList<MyTag>) {
        TagsDialog.newInstance(tags).apply {
            setOnTagChangedListener(this@NoteFragment)
        }.show(activity?.supportFragmentManager, TagsDialog.TAG)
    }

    override fun showForecast(forecast: Forecast) {
        val url = BASE_WEATHER_IMAGE_URL + forecast.weather[0].icon + ".png"
        GlideApp.with(this)
                .load(url)
                .into(image_weather)

        val temp = forecast.main.temp.toInt().toString() + " °C"
        text_temp.text = temp
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
        if (EasyPermissions.hasPermissions(requireContext(), readExtStorage, camera)) {
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
                .statusBarColor(getThemePrimaryDarkColor(requireContext()))
                .toolBarColor(getThemePrimaryColor(requireContext()))
                .navigationBarColor(getColor(requireContext(), R.color.black))
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
                    showLoading()
                    mPresenter.onNoteImagesPicked(albImage.map { it.path })
                }
                .start()
    }

    override fun showLoading() {
        layout_loading.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        layout_loading.visibility = View.GONE
    }

    override fun showImages(images: List<MyImage>) {
        Log.e(TAG, "showImages")
        text_image_count.text = images.size.toString()
        mPagerAdapter.images = images
        mPagerAdapter.notifyDataSetChanged()
        pager_note_image.setCurrentItem(mImagePagerPosition, false)
        if (childFragmentManager.findFragmentByTag(NoteEditFragment.TAG) == null) {
            enableActionBarExpanding(expanded = true, animate = true)
        }
    }

    override fun showNoImages() {
        Log.e(TAG, "showNoImages")
        text_image_count.text = "0"
        mPagerAdapter.images = emptyList()
        mPagerAdapter.notifyDataSetChanged()
        disableActionBarExpanding(false)
    }

    override fun closeView() {
        activity?.finish()
    }

    fun onNoteTextChange(title: String, content: String) {
        mPresenter.onNoteTextChange(title, content)
        childFragmentManager.findFragmentByTag(NoteContentFragment.TAG)?.let {
            (it as NoteContentFragment).updateNoteText(title, content)
        }
    }

    override fun showDatePicker(calendar: Calendar) {
        DatePickerDialog.newInstance(this, calendar).apply {
            accentColor = getThemePrimaryColor(this@NoteFragment.requireContext())
            val themeAccentColor = getThemeAccentColor(this@NoteFragment.requireContext())
            setOkColor(themeAccentColor)
            setCancelColor(themeAccentColor)
        }.show(fragmentManager, DATE_PICKER_TAG)
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        mPresenter.onDateSelected(year, monthOfYear, dayOfMonth)
    }

    override fun showTimePicker(hourOfDay: Int, minute: Int, is24HourMode: Boolean) {
        TimePickerDialog.newInstance(this, hourOfDay, minute, is24HourMode).apply {
            accentColor = getThemePrimaryColor(this@NoteFragment.requireContext())
            val themeAccentColor = getThemeAccentColor(this@NoteFragment.requireContext())
            setOkColor(themeAccentColor)
            setCancelColor(themeAccentColor)
            activity?.let {
                setOkText(it.getString(R.string.ok).toUpperCase())
                setCancelText(it.getString(R.string.cancel).toUpperCase())
            }
            setLocale(Locale.ENGLISH)
        }.show(fragmentManager, TIME_PICKER_TAG)
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        mPresenter.onTimeSelected(hourOfDay, minute)
    }

    override fun enableRedoButton(enable: Boolean) {
        (childFragmentManager.findFragmentByTag(NoteEditFragment.TAG) as? NoteEditFragment?)
                ?.enableRedoButton(enable)
    }

    override fun enableUndoButton(enable: Boolean) {
        (childFragmentManager.findFragmentByTag(NoteEditFragment.TAG) as? NoteEditFragment?)
                ?.enableUndoButton(enable)
    }

    fun onNoteFragmentEditModeEnabled() {
        mPresenter.onEditModeEnabled()
    }
}
