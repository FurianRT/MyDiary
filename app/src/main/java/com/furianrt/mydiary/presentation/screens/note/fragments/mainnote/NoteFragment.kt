/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.note.fragments.mainnote

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.text.ParcelableSpan
import android.text.Spannable
import android.text.style.*
import android.util.Log
import android.view.*
import android.widget.*
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.FileProvider
import androidx.viewpager2.widget.ViewPager2
import com.furianrt.mydiary.R
import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.presentation.base.BaseFragment
import com.furianrt.mydiary.model.entity.*
import com.furianrt.mydiary.model.entity.pojo.TagsAndAppearance
import com.furianrt.mydiary.presentation.dialogs.categories.CategoriesDialog
import com.furianrt.mydiary.presentation.dialogs.delete.note.DeleteNoteDialog
import com.furianrt.mydiary.presentation.dialogs.moods.MoodsDialog
import com.furianrt.mydiary.presentation.dialogs.tags.TagsDialog
import com.furianrt.mydiary.presentation.general.AppBarLayoutBehavior
import com.furianrt.mydiary.presentation.general.GlideApp
import com.furianrt.mydiary.presentation.screens.gallery.GalleryActivity
import com.furianrt.mydiary.presentation.screens.note.fragments.mainnote.content.NoteContentFragment
import com.furianrt.mydiary.presentation.screens.note.fragments.mainnote.edit.NoteEditFragment
import com.furianrt.mydiary.presentation.screens.settings.note.NoteSettingsActivity
import com.furianrt.mydiary.utils.*
import com.gjiazhe.panoramaimageview.GyroscopeObserver
import com.google.android.material.card.MaterialCardView
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import kotlinx.android.synthetic.main.fragment_note.*
import kotlinx.android.synthetic.main.fragment_note_toolbar.*
import kotlinx.android.synthetic.main.rich_text_menu.*
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class NoteFragment : BaseFragment(R.layout.fragment_note), NoteFragmentContract.View, DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener, NoteImagePagerAdapter.OnNoteImagePagerInteractionListener,
        EasyPermissions.PermissionCallbacks {

    companion object {
        const val TAG = "NoteFragment"
        private const val ARG_NOTE_ID = "note_id"
        private const val ARG_IS_NEW_NOTE = "is_new_note"
        private const val ARG_NOTE_WITH_IMAGE = "note_with_image"
        private const val ARG_NOTE_APPEARANCE = "note_appearance"
        private const val BUNDLE_PHOTO_PATH = "photo_path"
        private const val LOCATION_PERMISSIONS_REQUEST_CODE = 1
        private const val STORAGE_PERMISSIONS_REQUEST_CODE = 2
        private const val PLAY_SERVICES_REQUEST_CODE = 3
        private const val SPEECH_TO_TEXT_REQUEST_CODE = 4
        private const val IMAGE_PICKER_REQUEST_CODE = 6
        private const val CAMERA_PERMISSIONS_REQUEST_CODE = 7
        private const val CAMERA_REQUEST_CODE = 8
        private const val TIME_PICKER_TAG = "timePicker"
        private const val DATE_PICKER_TAG = "datePicker"
        private const val MAX_IMAGE_COUNT_TO_SHARE = 15

        @JvmStatic
        fun newInstance(noteId: String, isNewNote: Boolean, withImage: Boolean, appearance: MyNoteAppearance?) =
                NoteFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_NOTE_ID, noteId)
                        putBoolean(ARG_IS_NEW_NOTE, isNewNote)
                        putBoolean(ARG_NOTE_WITH_IMAGE, withImage)
                        appearance?.let { putParcelable(ARG_NOTE_APPEARANCE, it) }
                    }
                }
    }

    @Inject
    lateinit var presenter: NoteFragmentContract.Presenter

    private val mGyroscopeObserver = GyroscopeObserver()
    private var mListener: OnNoteFragmentInteractionListener? = null
    private val mImagePagerAdapter = NoteImagePagerAdapter(listener = this, gyroscope = mGyroscopeObserver)
    private var mNoteId: String? = null
    private var mIsNewNote = true
    private var mImagePagerPosition = 0
    private var mPhotoPath: String? = null
    private val mOnPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            mImagePagerPosition = pager_note_image.currentItem
            text_note_image_counter.text = getString(
                    R.string.counter_format,
                    mImagePagerPosition + 1,
                    mImagePagerAdapter.itemCount
            )
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        with(requireArguments()) {
            mNoteId = getString(ARG_NOTE_ID)
            mIsNewNote = getBoolean(ARG_IS_NEW_NOTE)
            presenter.init(mNoteId!!, mIsNewNote, getParcelable(ARG_NOTE_APPEARANCE))
        }

        mPhotoPath = savedInstanceState?.getString(BUNDLE_PHOTO_PATH)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mPhotoPath?.let { outState.putString(BUNDLE_PHOTO_PATH, it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        retainInstance = true
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pager_note_image.adapter = mImagePagerAdapter
        pager_note_image.isSaveEnabled = false

        if (requireArguments().getBoolean(ARG_NOTE_WITH_IMAGE, false)
                && childFragmentManager.findFragmentByTag(NoteEditFragment.TAG) == null) {
            showLoading()
            enableActionBarExpanding(expanded = true, animate = false)
        }

        layout_mood.setOnClickListener {
            removeEditFragment()
            presenter.onMoodFieldClick()
        }
        layout_category.setOnClickListener {
            removeEditFragment()
            presenter.onCategoryFieldClick()
        }
        layout_tags.setOnClickListener {
            removeEditFragment()
            presenter.onTagsFieldClick()
        }
        text_date.setOnClickListener {
            removeEditFragment()
            presenter.onDateFieldClick()
        }
        text_time.setOnClickListener {
            removeEditFragment()
            presenter.onTimeFieldClick()
        }
        button_text_bold.setOnClickListener {
            childFragmentManager.findFragmentByTag(NoteEditFragment.TAG)?.let {
                (it as NoteEditFragment).onButtonTextBoldClick()
            }
        }
        button_text_italic.setOnClickListener {
            childFragmentManager.findFragmentByTag(NoteEditFragment.TAG)?.let {
                (it as NoteEditFragment).onButtonTextItalicClick()
            }
        }
        button_text_strikethrough.setOnClickListener {
            childFragmentManager.findFragmentByTag(NoteEditFragment.TAG)?.let {
                (it as NoteEditFragment).onButtonTextStrikethroughClick()
            }
        }
        button_text_large.setOnClickListener {
            childFragmentManager.findFragmentByTag(NoteEditFragment.TAG)?.let {
                (it as NoteEditFragment).onButtonTextLargeClick()
            }
        }
        button_text_color.setOnClickListener {
            childFragmentManager.findFragmentByTag(NoteEditFragment.TAG)?.let {
                (it as NoteEditFragment).onButtonTextColorClick(null)
            }
        }
        button_text_fill_color.setOnClickListener {
            childFragmentManager.findFragmentByTag(NoteEditFragment.TAG)?.let {
                (it as NoteEditFragment).onButtonTextFillColorClick(null)
            }
        }
        button_redo.setOnClickListener {
            analytics.sendEvent(MyAnalytics.EVENT_NOTE_REDO)
            presenter.onButtonRedoClick()
        }
        button_undo.setOnClickListener {
            analytics.sendEvent(MyAnalytics.EVENT_NOTE_UNDO)
            presenter.onButtonUndoClick()
        }
        layout_loading.setOnTouchListener { _, _ -> true }

        button_undo.enableCustom(false)
        button_redo.enableCustom(false)

        mNoteId?.let { noteId ->
            if (childFragmentManager.findFragmentByTag(NoteContentFragment.TAG) == null) {
                childFragmentManager.inTransaction {
                    add(R.id.container_note_edit, NoteContentFragment.newInstance(noteId, mIsNewNote), NoteContentFragment.TAG)
                }
            }
        }

        spinner_text_color.adapter = ColorSpinnerAdapter(requireContext(), R.array.spinner_colors)
        spinner_text_color.dropDownVerticalOffset = -dpToPx(8f)
        spinner_text_color.onItemSelectListener = { spinner, position ->
            childFragmentManager.findFragmentByTag(NoteEditFragment.TAG)?.let {
                val color = spinner.getItemAtPosition(position)
                (it as NoteEditFragment).onButtonTextColorClick(color as String)
            }
        }

        spinner_text_fill_color.adapter = ColorSpinnerAdapter(requireContext(), R.array.spinner_colors)
        spinner_text_fill_color.dropDownVerticalOffset = -dpToPx(8f)
        spinner_text_fill_color.onItemSelectListener = { spinner, position ->
            childFragmentManager.findFragmentByTag(NoteEditFragment.TAG)?.let {
                val color = spinner.getItemAtPosition(position)
                (it as NoteEditFragment).onButtonTextFillColorClick(color as String)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mGyroscopeObserver.register(requireContext())
    }

    override fun onPause() {
        super.onPause()
        mGyroscopeObserver.unregister()
    }

    override fun onStart() {
        super.onStart()
        pager_note_image.registerOnPageChangeCallback(mOnPageChangeCallback)
        presenter.attachView(this)
    }

    override fun onStop() {
        super.onStop()
        pager_note_image.unregisterOnPageChangeCallback(mOnPageChangeCallback)
        presenter.detachView()
    }

    override fun showNoteText(title: String, content: String, textSpans: List<MyTextSpan>) {
        (childFragmentManager.findFragmentByTag(NoteContentFragment.TAG) as? NoteContentFragment?)
                ?.showNoteText(title, content.applyTextSpans(textSpans))
        (childFragmentManager.findFragmentByTag(NoteEditFragment.TAG) as? NoteEditFragment?)
                ?.showNoteText(title, content.applyTextSpans(textSpans))
    }

    @SuppressLint("SetTextI18n")
    override fun showDateAndTime(time: Long, is24TimeFormat: Boolean) {
        text_date.text = formatTime(time) + " "
        text_time.text = getTime(time, is24TimeFormat) + " "
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
            when (item.itemId) {
                R.id.menu_gallery -> {
                    removeEditFragment()
                    presenter.onButtonSelectPhotoClick()
                    true
                }
                R.id.menu_take_photo -> {
                    removeEditFragment()
                    presenter.onButtonTakePhotoClick()
                    true
                }
                R.id.menu_delete -> {
                    presenter.onButtonDeleteClick()
                    true
                }
                R.id.menu_appearance -> {
                    removeEditFragment()
                    analytics.sendEvent(MyAnalytics.EVENT_NOTE_SETTINGS)
                    presenter.onButtonAppearanceClick()
                    true
                }
                R.id.menu_date -> {
                    removeEditFragment()
                    presenter.onDateFieldClick()
                    true
                }
                R.id.menu_time -> {
                    removeEditFragment()
                    presenter.onTimeFieldClick()
                    true
                }
                R.id.menu_edit -> {
                    presenter.onButtonEditClick()
                    true
                }
                R.id.menu_mic -> {
                    analytics.sendEvent(MyAnalytics.EVENT_SPEECH_TO_TEXT)
                    presenter.onButtonMicClick()
                    true
                }
                R.id.menu_share -> {
                    removeEditFragment()
                    analytics.sendEvent(MyAnalytics.EVENT_SHARE_NOTE)
                    presenter.onButtonShareClick()
                    true
                }
                android.R.id.home -> {
                    removeEditFragment()
                    super.onOptionsItemSelected(item)
                }
                else -> super.onOptionsItemSelected(item)
            }

    override fun recordSpeech() {
        if (requireActivity().isGoogleServicesAvailable(PLAY_SERVICES_REQUEST_CODE)) {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault())
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.speech_to_text_title))
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivityForResult(intent, SPEECH_TO_TEXT_REQUEST_CODE)
            } else {
                Toast.makeText(requireContext(), getString(R.string.fragment_note_google_services_error), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            SPEECH_TO_TEXT_REQUEST_CODE -> if (resultCode == Activity.RESULT_OK && data != null) {
                val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.first()
                result?.let { appendToCurrentText(it) }
            }
            PLAY_SERVICES_REQUEST_CODE -> if (resultCode == Activity.RESULT_OK) {
                recordSpeech()
            }
            IMAGE_PICKER_REQUEST_CODE -> if (resultCode == Activity.RESULT_OK) {
                val clipData = data?.clipData
                val uriPaths = mutableListOf<String>()
                if (clipData != null && clipData.itemCount > 0) {
                    for (i in 0 until clipData.itemCount) {
                        clipData.getItemAt(i)?.uri?.let { uriPaths.add(it.toString()) }
                    }
                } else {
                    data?.data?.let { uriPaths.add(it.toString()) }
                }
                showLoading()
                presenter.onNoteImagesPicked(uriPaths)
            }
            CAMERA_REQUEST_CODE -> if (resultCode == Activity.RESULT_OK) {
                showLoading()
                presenter.onNewPhotoTaken(mPhotoPath)
            }
        }
    }

    private fun appendToCurrentText(text: String) {
        val editFragment = childFragmentManager.findFragmentByTag(NoteEditFragment.TAG) as? NoteEditFragment?
        if (editFragment != null) {
            presenter.onSpeechRecorded(
                    editFragment.getNoteTitleText(),
                    editFragment.getNoteContentText().toString(),
                    editFragment.getNoteContentText().getTextSpans(),
                    text
            )
        } else {
            childFragmentManager.findFragmentByTag(NoteContentFragment.TAG)?.let {
                presenter.onSpeechRecorded(
                        (it as NoteContentFragment).getNoteTitleText(),
                        it.getNoteContentText().toString(),
                        it.getNoteContentText().getTextSpans(),
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
        presenter.onToolbarImageClick(image)
    }

    override fun showGalleryView(noteId: String, image: MyImage) {
        startActivity(GalleryActivity.newIntent(requireContext(), noteId, pager_note_image.currentItem))
    }

    override fun updateNoteAppearance(appearance: MyNoteAppearance) {
        appearance.background?.let { layout_root_note.setBackgroundColor(it) }
        appearance.textBackground?.let { card_note_edit.setCardBackgroundColor(it) }
        appearance.textColor?.let { color ->
            text_mood.setTextColor(color)
            text_category.setTextColor(color)
        }
        appearance.surfaceTextColor?.let { color ->
            text_location.setTextColor(color)
            text_date.setTextColor(color)
            text_time.setTextColor(color)
            text_temp.setTextColor(color)
            text_location.setTextColor(color)
            image_location.setColorFilter(color, PorterDuff.Mode.SRC_IN)
        }

        childFragmentManager.findFragmentByTag(NoteContentFragment.TAG)?.let {
            (it as NoteContentFragment).setAppearance(appearance)
        }
    }

    override fun showNoTagsMessage(tagsAndAppearance: TagsAndAppearance) {
        layout_tags.removeViews(1, layout_tags.flexItemCount - 1)

        val image = layout_tags.getChildAt(0) as ImageView
        image.setColorFilter(
                tagsAndAppearance.appearance?.surfaceTextColor
                        ?: requireContext().getColorCompat(R.color.black),
                PorterDuff.Mode.SRC_IN
        )
        image.alpha = 0.4f

        val textNoTags = TextView(requireContext())
        textNoTags.setTextColor(tagsAndAppearance.appearance?.textColor ?: Color.BLACK)
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
                tagsAndAppearance.appearance?.surfaceTextColor ?: Color.BLACK,
                PorterDuff.Mode.SRC_IN
        )
        image.alpha = 1.0f
        for (tag in tagsAndAppearance.tags) {
            tagsAndAppearance.appearance?.let { appearance ->
                layout_tags.addView(wrapTextIntoCardView(tag.name, appearance))
            }
        }
    }

    private fun wrapTextIntoCardView(text: String, appearance: MyNoteAppearance) =
            MaterialCardView(requireContext()).apply {
                elevation = 5f
                radius = 25f
                setCardBackgroundColor(
                        appearance.textBackground ?: requireContext().getColorCompat(R.color.white)
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
                            appearance.textColor ?: requireContext().getColorCompat(R.color.black)
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

    override fun showMoodsView(noteId: String) {
        MoodsDialog.newInstance(noteId).show(requireActivity().supportFragmentManager, MoodsDialog.TAG)
    }

    override fun showLocation(location: MyLocation) {
        layout_location.visibility = View.VISIBLE
        text_location.text = location.name
    }

    @AfterPermissionGranted(LOCATION_PERMISSIONS_REQUEST_CODE)
    override fun requestLocationPermissions() {
        val fineLocation = Manifest.permission.ACCESS_FINE_LOCATION
        val coarseLocation = Manifest.permission.ACCESS_COARSE_LOCATION
        if (EasyPermissions.hasPermissions(requireContext(), fineLocation, coarseLocation)) {
            presenter.onLocationPermissionsGranted()
        } else {
            EasyPermissions.requestPermissions(this, getString(R.string.location_permission_request),
                    LOCATION_PERMISSIONS_REQUEST_CODE, fineLocation, coarseLocation)
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) = Unit
    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        if (requestCode == LOCATION_PERMISSIONS_REQUEST_CODE) {
            presenter.onLocationPermissionDenied()
        }
    }

    fun disableActionBarExpanding(animate: Boolean) {
        Log.e(TAG, "disableActionBarExpanding")
        app_bar_layout.setExpanded(false, animate)

        val coordParams = app_bar_layout.layoutParams as CoordinatorLayout.LayoutParams
        (coordParams.behavior as AppBarLayoutBehavior).shouldScroll = false
    }

    fun enableActionBarExpanding(expanded: Boolean, animate: Boolean) {
        Log.e(TAG, "enableActionBarExpanding")
        app_bar_layout.setExpanded(expanded, animate)

        val params = app_bar_layout.layoutParams as CoordinatorLayout.LayoutParams
        (params.behavior as AppBarLayoutBehavior).shouldScroll = true
    }

    override fun showCategoriesView(noteId: String) {
        CategoriesDialog.newInstance(listOf(noteId))
                .show(requireActivity().supportFragmentManager, CategoriesDialog.TAG)
    }

    override fun showTagsView(noteId: String) {
        TagsDialog.newInstance(noteId).show(requireActivity().supportFragmentManager, TagsDialog.TAG)
    }

    override fun showForecast(temp: String, iconUri: String) {
        GlideApp.with(this)
                .load(iconUri)
                .into(image_weather)

        text_temp.text = temp
    }

    override fun showErrorForecast() {
        analytics.sendEvent(MyAnalytics.EVENT_FORECAST_LOAD_ERROR)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun requestStoragePermissions() {
        val readExtStorage = Manifest.permission.READ_EXTERNAL_STORAGE
        if (EasyPermissions.hasPermissions(requireContext(), readExtStorage)) {
            presenter.onStoragePermissionsGranted()
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.storage_permission_request),
                    STORAGE_PERMISSIONS_REQUEST_CODE,
                    readExtStorage
            )
        }
    }

    @AfterPermissionGranted(STORAGE_PERMISSIONS_REQUEST_CODE)
    override fun showImageExplorer() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        galleryIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        if (galleryIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(galleryIntent, IMAGE_PICKER_REQUEST_CODE)
        } else {
            with(Intent()) {
                type = "image/*"
                putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
                action = Intent.ACTION_GET_CONTENT
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                if (resolveActivity(requireActivity().packageManager) != null) {
                    startActivityForResult(Intent.createChooser(this, ""), IMAGE_PICKER_REQUEST_CODE)
                } else {
                    analytics.sendEvent(MyAnalytics.EVENT_GALLERY_NOT_FOUND_ERROR)
                    Toast.makeText(requireContext(), getString(R.string.phone_related_error), Toast.LENGTH_SHORT).show()
                }
            }
        }
        mListener?.onNoteFragmentImagePickerOpen()
    }

    override fun requestCameraPermissions() {
        val cameraPermission = Manifest.permission.CAMERA
        if (EasyPermissions.hasPermissions(requireContext(), cameraPermission)) {
            presenter.onCameraPermissionsGranted()
        } else {
            EasyPermissions.requestPermissions(
                    this,
                    getString(R.string.camera_permission_request),
                    CAMERA_PERMISSIONS_REQUEST_CODE,
                    cameraPermission
            )
        }
    }

    @AfterPermissionGranted(CAMERA_PERMISSIONS_REQUEST_CODE)
    override fun showCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).let { takePictureIntent ->
            takePictureIntent.resolveActivity(requireContext().packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (e: IOException) {
                    e.printStackTrace()
                    null
                }
                photoFile?.let {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            requireContext(),
                            "com.furianrt.mydiary.fileprovider",
                            it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE)
                    mListener?.onNoteFragmentImagePickerOpen()
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(Date())
        return File.createTempFile(timeStamp, ".jpg").apply {
            mPhotoPath = absolutePath
        }
    }

    override fun showLoading() {
        layout_loading?.alpha = 1f
        layout_loading?.visibility = View.VISIBLE
    }

    override fun hideLoading() {
        if (layout_loading?.alpha == 1f && layout_loading?.visibility != View.GONE) {
            layout_loading?.animateAlpha(from = 1f, to = 0f) {
                layout_loading?.visibility = View.GONE
            }
        }
    }

    override fun onImageLoadFailed() {
        hideLoading()
    }

    override fun onImageLoaded() {
        hideLoading()
    }

    override fun showImages(images: List<MyImage>, panorama: Boolean) {
        Log.e(TAG, "showImages")
        mImagePagerAdapter.submitImages(images.toMutableList(), panorama)
        pager_note_image.setCurrentItem(mImagePagerPosition, false)
        text_note_image_counter.text = getString(
                R.string.counter_format,
                mImagePagerPosition + 1,
                images.count()
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

    override fun showErrorSaveImage() {
        analytics.sendEvent(MyAnalytics.EVENT_IMAGE_SAVE_ERROR)
        Toast.makeText(requireContext(), R.string.fragment_gallery_list_image_save_error, Toast.LENGTH_SHORT).show()
    }

    fun onNoteTextChange(title: String, content: Spannable) {
        presenter.onNoteTextChange(title, content.toString(), content.getTextSpans())
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
        presenter.onDateSelected(year, monthOfYear, dayOfMonth)
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
        presenter.onTimeSelected(hourOfDay, minute)
    }

    override fun enableRedoButton(enable: Boolean) {
        button_redo.enableCustom(enable)
    }

    override fun enableUndoButton(enable: Boolean) {
        button_undo.enableCustom(enable)
    }

    override fun sendUndoErrorEvent() {
        analytics.sendEvent(MyAnalytics.EVENT_UNDO_ERROR)
    }

    override fun sendRedoErrorEvent() {
        analytics.sendEvent(MyAnalytics.EVENT_REDO_ERROR)
    }

    fun onNoteFragmentEditModeEnabled() {
        mListener?.onNoteFragmentEditModeEnabled()
        presenter.onEditModeEnabled()
    }

    fun onNoteFragmentEditModeDisabled(noteTitle: String, noteContent: Spannable) {
        enableActionBarExpanding(expanded = false, animate = false)
        val noteContentFragment =
                childFragmentManager.findFragmentByTag(NoteContentFragment.TAG) as? NoteContentFragment
        noteContentFragment?.showNoteText(noteTitle, noteContent)
        mListener?.onNoteFragmentEditModeDisabled()
        presenter.onEditModeDisabled()
    }

    override fun showRichTextOptions() {
        layout_rich_text?.visibility = View.VISIBLE
    }

    override fun hideRichTextOptions() {
        layout_rich_text?.visibility = View.GONE
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnNoteFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException("$context must implement OnNoteFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun shareNote(note: MyNoteWithProp) {
        val size = minOf(MAX_IMAGE_COUNT_TO_SHARE, note.images.size)
        val uris = ArrayList<Uri>()
        for (i in 0 until size) {
            Uri.parse(note.images[i].path).path?.let { path ->
                uris.add(FileProvider.getUriForFile(
                        requireContext(),
                        "com.furianrt.mydiary.fileprovider",
                        File(path)
                ))
            }
        }
        val intent: Intent
        when {
            uris.size > 1 -> {
                intent = Intent(Intent.ACTION_SEND_MULTIPLE)
                intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris)
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
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION and Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(Intent.createChooser(intent, getString(R.string.share)))
    }

    fun onSpansChange(spans: Array<ParcelableSpan>) {
        if (spans.find { it is StyleSpan && it.style == Typeface.BOLD } != null) {
            button_text_bold.setBackgroundResource(R.drawable.background_corner_grey_stroke)
            button_text_bold.setColorFilter(requireContext().getThemeAccentColor())
        } else {
            button_text_bold.background = null
            button_text_bold.clearColorFilter()
        }

        if (spans.find { it is StyleSpan && it.style == Typeface.ITALIC } != null) {
            button_text_italic.setBackgroundResource(R.drawable.background_corner_grey_stroke)
            button_text_italic.setColorFilter(requireContext().getThemeAccentColor())
        } else {
            button_text_italic.background = null
            button_text_italic.clearColorFilter()
        }

        if (spans.find { it is StrikethroughSpan } != null) {
            button_text_strikethrough.setBackgroundResource(R.drawable.background_corner_grey_stroke)
            button_text_strikethrough.setColorFilter(requireContext().getThemeAccentColor())
        } else {
            button_text_strikethrough.background = null
            button_text_strikethrough.clearColorFilter()
        }

        if (spans.find { it is RelativeSizeSpan } != null) {
            button_text_large.setBackgroundResource(R.drawable.background_corner_grey_stroke)
            button_text_large.setColorFilter(requireContext().getThemeAccentColor())
        } else {
            button_text_large.background = null
            button_text_large.clearColorFilter()
        }

        val textColorSpan = spans.find { it is ForegroundColorSpan } as ForegroundColorSpan?
        if (textColorSpan != null) {
            button_text_color.setBackgroundResource(R.drawable.background_corner_grey_stroke)
            button_text_color.setColorFilter(textColorSpan.foregroundColor)
            spinner_text_color.visibility = View.GONE
        } else {
            button_text_color.background = null
            button_text_color.clearColorFilter()
            spinner_text_color.visibility = View.VISIBLE
        }

        val textFillColorSpan = spans.find { it is BackgroundColorSpan } as BackgroundColorSpan?
        if (textFillColorSpan != null) {
            button_text_fill_color.setBackgroundResource(R.drawable.background_corner_grey_stroke)
            button_text_fill_color.setColorFilter(textFillColorSpan.backgroundColor)
            spinner_text_fill_color.visibility = View.GONE
        } else {
            button_text_fill_color.background = null
            button_text_fill_color.clearColorFilter()
            spinner_text_fill_color.visibility = View.VISIBLE
        }
    }

    interface OnNoteFragmentInteractionListener {
        fun onNoteFragmentEditModeEnabled()
        fun onNoteFragmentEditModeDisabled()
        fun onNoteFragmentImagePickerOpen()
    }
}
