package com.furianrt.mydiary.view.screens.note.fragments.mainnote

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.location.Geocoder
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.speech.RecognizerIntent
import android.util.Log
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat.getColor
import androidx.viewpager2.widget.ViewPager2
import com.furianrt.mydiary.R
import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.view.base.BaseFragment
import com.furianrt.mydiary.data.model.*
import com.furianrt.mydiary.data.model.pojo.TagsAndAppearance
import com.furianrt.mydiary.view.dialogs.categories.CategoriesDialog
import com.furianrt.mydiary.view.dialogs.delete.note.DeleteNoteDialog
import com.furianrt.mydiary.view.dialogs.moods.MoodsDialog
import com.furianrt.mydiary.view.dialogs.tags.TagsDialog
import com.furianrt.mydiary.view.general.AppBarLayoutBehavior
import com.furianrt.mydiary.view.general.GlideApp
import com.furianrt.mydiary.view.screens.gallery.GalleryActivity
import com.furianrt.mydiary.view.screens.note.fragments.mainnote.content.NoteContentFragment
import com.furianrt.mydiary.view.screens.note.fragments.mainnote.edit.NoteEditFragment
import com.furianrt.mydiary.view.screens.settings.note.NoteSettingsActivity
import com.furianrt.mydiary.utils.*
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
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
import java.util.Locale
import java.util.Calendar
import javax.inject.Inject
import kotlin.collections.ArrayList

class NoteFragment : BaseFragment(), NoteFragmentContract.MvpView, DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener, NoteImagePagerAdapter.OnNoteImagePagerInteractionListener {

    companion object {
        const val TAG = "NoteFragment"
        private const val ARG_NOTE_ID = "note_id"
        private const val ARG_IS_NEW_NOTE = "is_new_note"
        private const val LOCATION_INTERVAL = 400L
        private const val LOCATION_PERMISSIONS_REQUEST_CODE = 1
        private const val STORAGE_PERMISSIONS_REQUEST_CODE = 2
        private const val PLAY_SERVICES_REQUEST_CODE = 3
        private const val SPEECH_TO_TEXT_REQUEST_CODE = 4
        private const val BUNDLE_IMAGE_PAGER_POSITION = "imagePagerPosition"
        private const val BUNDLE_NOTE_TEXT_BUFFER = "noteTextBuffer"
        private const val TIME_PICKER_TAG = "timePicker"
        private const val DATE_PICKER_TAG = "datePicker"

        @JvmStatic
        fun newInstance(noteId: String, isNewNote: Boolean) =
                NoteFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_NOTE_ID, noteId)
                        putBoolean(ARG_IS_NEW_NOTE, isNewNote)
                    }
                }
    }

    @Inject
    lateinit var mFusedLocationClient: FusedLocationProviderClient

    @Inject
    lateinit var mPresenter: NoteFragmentContract.Presenter

    private val mImagePagerAdapter = NoteImagePagerAdapter(listener = this)
    private var mIsNewNote = true
    private var mImagePagerPosition = 0
    private val mOnPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            mImagePagerPosition = pager_note_image.currentItem
            text_note_image_counter.text = getString(
                    R.string.counter_format,
                    position + 1,
                    mImagePagerAdapter.itemCount
            )
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
        setHasOptionsMenu(true)

        val noteId = arguments?.getString(ARG_NOTE_ID)!!
        mIsNewNote = arguments?.getBoolean(ARG_IS_NEW_NOTE)!!

        mPresenter.init(noteId, mIsNewNote)

        savedInstanceState?.let {
            mImagePagerPosition = it.getInt(BUNDLE_IMAGE_PAGER_POSITION, 0)
            mPresenter.setNoteTextBuffer(it.getParcelableArrayList(BUNDLE_NOTE_TEXT_BUFFER)
                    ?: ArrayList())
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_note, container, false)

        view.pager_note_image.adapter = mImagePagerAdapter
        view.pager_note_image.isSaveEnabled = false

        view.layout_mood.setOnClickListener {
            removeEditFragment()
            mPresenter.onMoodFieldClick()
        }
        view.layout_category.setOnClickListener {
            removeEditFragment()
            mPresenter.onCategoryFieldClick()
        }
        view.layout_tags.setOnClickListener {
            removeEditFragment()
            mPresenter.onTagsFieldClick()
        }
        view.text_date.setOnClickListener {
            removeEditFragment()
            mPresenter.onDateFieldClick()
        }
        view.text_time.setOnClickListener {
            removeEditFragment()
            mPresenter.onTimeFieldClick()
        }
        view.fab_add_image.setOnClickListener {
            removeEditFragment()
            analytics.sendEvent(MyAnalytics.EVENT_NOTE_IMAGE_PAGER_OPENED)
            mPresenter.onButtonAddImageClick()
        }
        view.layout_loading.setOnTouchListener { _, _ -> true }

        if (childFragmentManager.findFragmentByTag(NoteContentFragment.TAG) == null) {
            childFragmentManager.inTransaction {
                add(R.id.container_note_edit, NoteContentFragment.newInstance(mIsNewNote), NoteContentFragment.TAG)
            }
        }

        return view
    }

    override fun onStart() {
        super.onStart()
        requireActivity().window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)
        pager_note_image.registerOnPageChangeCallback(mOnPageChangeCallback)
        mPresenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        pager_note_image.unregisterOnPageChangeCallback(mOnPageChangeCallback)
        mPresenter.detachView()
    }

    override fun isLocationAvailable() =
            requireContext().isNetworkAvailable() && requireContext().isLocationEnabled()

    override fun showNoteText(title: String, content: String) {
        (childFragmentManager.findFragmentByTag(NoteContentFragment.TAG) as? NoteContentFragment?)
                ?.showNoteText(title, content)
        (childFragmentManager.findFragmentByTag(NoteEditFragment.TAG) as? NoteEditFragment?)
                ?.showNoteText(title, content)
    }

    @SuppressLint("SetTextI18n")
    override fun showDateAndTime(time: Long, is24TimeFormat: Boolean) {
        text_date.text = formatTime(time) + " "
        text_time.text = getTime(time, is24TimeFormat) + " "
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
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
                analytics.sendEvent(MyAnalytics.EVENT_NOTE_SETTINGS)
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
                analytics.sendEvent(MyAnalytics.EVENT_NOTE_UNDO)
                mPresenter.onButtonUndoClick()
                true
            }
            R.id.menu_redo -> {
                analytics.sendEvent(MyAnalytics.EVENT_NORE_REDO)
                mPresenter.onButtonRedoClick()
                true
            }
            R.id.menu_mic -> {
                analytics.sendEvent(MyAnalytics.EVENT_SPEECH_TO_TEXT)
                mPresenter.onButtonMicClick()
                true
            }
            R.id.menu_share -> {
                analytics.sendEvent(MyAnalytics.EVENT_SHARE_NOTE)
                mPresenter.onButtonShareClick()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun recordSpeech() {
        if (requireActivity().isGoogleServicesAvailable(PLAY_SERVICES_REQUEST_CODE)) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_to_text_title))
            startActivityForResult(intent, SPEECH_TO_TEXT_REQUEST_CODE)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SPEECH_TO_TEXT_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.first()
                result?.let { appendToCurrentText(it) }
            }
        } else if (requestCode == PLAY_SERVICES_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                recordSpeech()
            }
        }
    }

    private fun appendToCurrentText(text: String) {
        val editFragment = childFragmentManager.findFragmentByTag(NoteEditFragment.TAG) as? NoteEditFragment?
        if (editFragment != null) {
            mPresenter.onSpeechRecorded(
                    editFragment.getNoteTitleText(),
                    editFragment.getNoteContentText(),
                    text
            )
        } else {
            childFragmentManager.findFragmentByTag(NoteContentFragment.TAG)?.let {
                mPresenter.onSpeechRecorded(
                        (it as NoteContentFragment).getNoteTitleText(),
                        it.getNoteContentText(),
                        text
                )
            }
        }
    }

    override fun showDeleteConfirmationDialog(noteId: String) {
        DeleteNoteDialog.newInstance(listOf(noteId))
                .show(requireActivity().supportFragmentManager, DeleteNoteDialog.TAG)
    }

    override fun shoNoteEditView() {
        childFragmentManager.findFragmentByTag(NoteContentFragment.TAG)?.let {
            (it as NoteContentFragment).showNoteEditViewForTitleEnd()
        }
    }

    override fun showNoteSettingsView(noteId: String) {
        val intent = Intent(requireContext(), NoteSettingsActivity::class.java)
        intent.putExtra(NoteSettingsActivity.EXTRA_NOTE_ID, noteId)
        startActivity(intent)
    }

    private fun removeEditFragment() {
        childFragmentManager.findFragmentByTag(NoteContentFragment.TAG)?.let {
            (it as NoteContentFragment).removeEditFragment()
        }
    }

    override fun onImageClick(image: MyImage) {
        analytics.sendEvent(MyAnalytics.EVENT_NOTE_IMAGE_PAGER_OPENED)
        mPresenter.onToolbarImageClick(image)
    }

    fun onNoteEditFinished(noteTitle: String, noteContent: String) {
        val noteContentFragment =
                childFragmentManager.findFragmentByTag(NoteContentFragment.TAG) as? NoteContentFragment
        noteContentFragment?.showNoteText(noteTitle, noteContent)
        mPresenter.updateNoteText(noteTitle, noteContent)
    }

    override fun showGalleryView(noteId: String, image: MyImage) {
        startActivity(GalleryActivity.newIntent(requireContext(), noteId, pager_note_image.currentItem))
    }

    override fun updateNoteAppearance(appearance: MyNoteAppearance) {
        appearance.background?.let { layout_root_note.setBackgroundColor(it) }
        appearance.textBackground?.let { card_note_edit.setCardBackgroundColor(it) }
        appearance.textColor?.let {
            text_mood.setTextColor(it)
            text_category.setTextColor(it)
        }
        appearance.surfaceTextColor?.let {
            text_location.setTextColor(it)
            text_date.setTextColor(it)
            text_time.setTextColor(it)
            text_temp.setTextColor(it)
            text_location.setTextColor(it)
        }
        image_location.setColorFilter(
                appearance.surfaceTextColor ?: Color.BLACK,
                PorterDuff.Mode.SRC_IN
        )
        childFragmentManager.findFragmentByTag(NoteContentFragment.TAG)?.let {
            (it as NoteContentFragment).setAppearance(appearance)
        }
    }

    override fun showNoTagsMessage(tagsAndAppearance: TagsAndAppearance) {
        layout_tags.removeViews(1, layout_tags.flexItemCount - 1)

        val image = layout_tags.getChildAt(0) as ImageView
        image.setColorFilter(
                tagsAndAppearance.appearance.surfaceTextColor ?: getColor(requireContext(), R.color.black),
                PorterDuff.Mode.SRC_IN
        )
        image.alpha = 0.4f

        val textNoTags = TextView(requireContext())
        textNoTags.setTextColor(tagsAndAppearance.appearance.textColor ?: Color.BLACK)
        textNoTags.setText(R.string.choose_tags)
        textNoTags.alpha = 0.4f

        val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        )
        params.marginStart = 16
        textNoTags.layoutParams = params

        layout_tags.addView(textNoTags)
    }

    override fun showTags(tagsAndAppearance: TagsAndAppearance) {
        layout_tags.removeViews(1, layout_tags.flexItemCount - 1)
        val image = layout_tags.getChildAt(0) as ImageView
        image.setColorFilter(
                tagsAndAppearance.appearance.surfaceTextColor ?: Color.BLACK,
                PorterDuff.Mode.SRC_IN
        )
        image.alpha = 1.0f
        for (tag in tagsAndAppearance.tags) {
            layout_tags.addView(wrapTextIntoCardView(tag.name, tagsAndAppearance.appearance))
        }
    }

    private fun wrapTextIntoCardView(text: String, appearance: MyNoteAppearance) =
            MaterialCardView(requireContext()).apply {
                elevation = 5f
                radius = 25f
                setCardBackgroundColor(
                        appearance.textBackground ?: getColor(requireContext(), R.color.white)
                )
                val params = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(10, 10, 10, 10)
                layoutParams = params

                addView(TextView(context).apply {
                    this.text = text
                    setTextColor(
                            appearance.textColor ?: getColor(requireContext(), R.color.black)
                    )
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
        layout_mood.visibility = View.VISIBLE
        text_mood.alpha = 1f
        text_mood.text = mood.name
        val smile = resources.getIdentifier(mood.iconName, "drawable", requireContext().packageName)
        image_mood.clearColorFilter()
        image_mood.setImageResource(smile)
        image_mood.alpha = 1f
    }

    override fun showNoMoodMessage() {
        layout_mood.visibility = View.VISIBLE
        text_mood.text = getString(R.string.choose_mood)
        text_mood.alpha = 0.5f
        image_mood.alpha = 0.5f
        image_mood.setImageResource(R.drawable.ic_smile)
        image_mood.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN)
    }

    override fun showMoodsDialog(noteId: String) {
        MoodsDialog.newInstance(noteId).show(requireActivity().supportFragmentManager, MoodsDialog.TAG)
    }

    override fun showLocation(location: MyLocation) {
        layout_location.visibility = View.VISIBLE
        text_location.text = location.name
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

    fun disableActionBarExpanding(animate: Boolean) {
        Log.e(TAG, "disableActionBarExpanding")
        app_bar_layout.setExpanded(false, animate)

        val coordParams = app_bar_layout.layoutParams as CoordinatorLayout.LayoutParams
        (coordParams.behavior as AppBarLayoutBehavior).shouldScroll = false
    }

    fun enableActionBarExpanding(expanded: Boolean, animate: Boolean) {
        Log.e(TAG, "enableActionBarExpanding")
        if (mImagePagerAdapter.itemCount == 0) {
            return
        }
        app_bar_layout.setExpanded(expanded, animate)

        val params = app_bar_layout.layoutParams as CoordinatorLayout.LayoutParams
        (params.behavior as AppBarLayoutBehavior).shouldScroll = true
    }

    override fun showCategoriesDialog(noteId: String) {
        CategoriesDialog.newInstance(listOf(noteId))
                .show(requireActivity().supportFragmentManager, CategoriesDialog.TAG)
    }

    override fun showTagsDialog(noteId: String) {
        TagsDialog.newInstance(noteId).show(requireActivity().supportFragmentManager, TagsDialog.TAG)
    }

    override fun showForecast(temp: String, iconUri: String) {
        GlideApp.with(this)
                .load(iconUri)
                .into(image_weather)

        text_temp.text = temp
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

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
            val geoCoder = Geocoder(requireContext(), Locale.getDefault())
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
                .statusBarColor(requireContext().getThemePrimaryDarkColor())
                .toolBarColor(requireContext().getThemePrimaryColor())
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
        mImagePagerAdapter.submitImages(images.toMutableList())
        pager_note_image.setCurrentItem(mImagePagerPosition, false)
        text_note_image_counter.text = getString(
                R.string.counter_format,
                mImagePagerPosition + 1,
                mImagePagerAdapter.itemCount
        )
        if (childFragmentManager.findFragmentByTag(NoteEditFragment.TAG) == null) {
            enableActionBarExpanding(expanded = true, animate = true)
        }
    }

    override fun showNoImages() {
        Log.e(TAG, "showNoImages")
        text_note_image_counter.text = getString(R.string.counter_format, 0, 0)
        mImagePagerAdapter.submitImages(emptyList())
        disableActionBarExpanding(false)
    }

    fun onNoteTextChange(title: String, content: String) {
        mPresenter.onNoteTextChange(title, content)
        childFragmentManager.findFragmentByTag(NoteContentFragment.TAG)?.let {
            (it as NoteContentFragment).updateNoteText(title, content)
        }
    }

    override fun showDatePicker(year: Int, monthOfYear: Int, dayOfMonth: Int) {
        DatePickerDialog.newInstance(this, year, monthOfYear, dayOfMonth).apply {
            val themePrimaryColor = this@NoteFragment.requireContext().getThemePrimaryColor()
            accentColor = themePrimaryColor
            setOkColor(themePrimaryColor)
            setCancelColor(themePrimaryColor)
            minDate = Calendar.getInstance().apply { set(1980, 0, 1) }
        }.show(requireActivity().supportFragmentManager, DATE_PICKER_TAG)
    }

    override fun onDateSet(view: DatePickerDialog?, year: Int, monthOfYear: Int, dayOfMonth: Int) {
        analytics.sendEvent(MyAnalytics.EVENT_NOTE_DATE_CHANGED)
        mPresenter.onDateSelected(year, monthOfYear, dayOfMonth)
    }

    override fun showTimePicker(hourOfDay: Int, minute: Int, is24HourMode: Boolean) {
        TimePickerDialog.newInstance(this, hourOfDay, minute, is24HourMode).apply {
            val themePrimaryColor = this@NoteFragment.requireContext().getThemePrimaryColor()
            accentColor = themePrimaryColor
            setOkColor(themePrimaryColor)
            setCancelColor(themePrimaryColor)
            setOkText(this@NoteFragment.getString(R.string.ok).toUpperCase(Locale.getDefault()))
            setCancelText(this@NoteFragment.getString(R.string.cancel).toUpperCase(Locale.getDefault()))
            setLocale(Locale.getDefault())
        }.show(requireActivity().supportFragmentManager, TIME_PICKER_TAG)
    }

    override fun onTimeSet(view: TimePickerDialog?, hourOfDay: Int, minute: Int, second: Int) {
        analytics.sendEvent(MyAnalytics.EVENT_NOTE_TIME_CHANGED)
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

    override fun sendUndoErrorEvent() {
        analytics.sendEvent(MyAnalytics.EVENT_UNDO_ERROR)
    }

    override fun sendRedoErrorEvent() {
        analytics.sendEvent(MyAnalytics.EVENT_REDO_ERROR)
    }

    fun onNoteFragmentEditModeEnabled() {
        mPresenter.onEditModeEnabled()
    }

    override fun shareNote(note: MyNoteWithProp) {
        val uris = note.images.map { Uri.parse(it.uri) }
        val intent: Intent
        when {
            uris.size > 1 -> {
                intent = Intent(Intent.ACTION_SEND_MULTIPLE)
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, ArrayList(uris))
                intent.type = "image/*"
            }
            uris.size == 1 -> {
                intent = Intent(Intent.ACTION_SEND)
                intent.putExtra(Intent.EXTRA_STREAM, uris.first())
                intent.type = "image/*"
            }
            else -> {
                intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
            }
        }
        intent.putExtra(Intent.EXTRA_SUBJECT, note.note.title)
        intent.putExtra(Intent.EXTRA_TEXT, note.note.content)
        startActivity(Intent.createChooser(intent, getString(R.string.share)))
    }
}
