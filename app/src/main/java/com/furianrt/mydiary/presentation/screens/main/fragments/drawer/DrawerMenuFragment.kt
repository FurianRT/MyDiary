/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.presentation.screens.main.fragments.drawer

import android.animation.Animator
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.furianrt.mydiary.BuildConfig
import com.furianrt.mydiary.R
import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.presentation.base.BaseFragment
import com.furianrt.mydiary.model.entity.*
import com.furianrt.mydiary.model.entity.pojo.SearchEntries
import com.furianrt.mydiary.presentation.screens.main.fragments.authentication.AuthFragment
import com.furianrt.mydiary.presentation.screens.main.fragments.drawer.adapter.SearchGroup
import com.furianrt.mydiary.presentation.screens.main.fragments.drawer.adapter.SearchItem
import com.furianrt.mydiary.presentation.screens.main.fragments.drawer.adapter.SearchListAdapter
import com.furianrt.mydiary.presentation.screens.main.fragments.premium.PremiumFragment
import com.furianrt.mydiary.presentation.screens.main.fragments.profile.ProfileFragment
import com.furianrt.mydiary.services.SyncService
import com.furianrt.mydiary.utils.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.bottom_sheet_main.*
import kotlinx.android.synthetic.main.fragment_drawer_menu.*
import org.threeten.bp.LocalDate
import java.util.TreeMap
import javax.inject.Inject

class DrawerMenuFragment : BaseFragment(R.layout.fragment_drawer_menu), DrawerMenuContract.View,
        SearchListAdapter.OnSearchListInteractionListener {

    companion object {
        const val TAG = "DrawerMenuFragment"
        private const val PLAY_SERVICES_REQUEST_CODE = 1
        private const val ANIMATION_PROGRESS_FADE_OUT_OFFSET = 2000L
        private const val ANIMATION_PROGRESS_DURATION = 500L
        private const val BOTTOM_SHEET_EXPAND_DELAY = 300L
        private const val ANIMATION_BUTTON_DURATION = 500L
        private const val ANIMATION_BUTTON_START_DELAY = 400L
        private const val ANIMATION_BUTTON_TRANSLATION_VALUE_DP = 100f
        private const val BUNDLE_CLEAR_CHOICES_TRANSLATION_Y = "clear_choices_translation_y"
    }

    @Inject
    lateinit var presenter: DrawerMenuContract.Presenter

    private var mListener: OnDrawerMenuInteractionListener? = null
    private var mSearchListAdapter = SearchListAdapter(listener = this)
    private val mHandler = Handler()
    private val mBottomSheetOpenRunnable: Runnable = Runnable {
        activity?.let {
            BottomSheetBehavior.from(it.main_sheet_container).state =
                    BottomSheetBehavior.STATE_EXPANDED
        }
    }

    private val mProgressAnimationListener = object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {}
        override fun onAnimationCancel(animation: Animator?) {}
        override fun onAnimationStart(animation: Animator?) {}
        override fun onAnimationEnd(animation: Animator?) {
            clearSyncProgress()
        }
    }

    private val mBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            SyncService.getProgressMessage(intent)?.let { message ->
                view_sync.alpha = 0.35f
                if (message.hasError) {
                    showErrorSync(message)
                } else {
                    showSyncProgress(message)
                }
            }
        }
    }

    private fun List<MyNoteWithProp>.toDateColors(): Map<LocalDate, List<Int>> {
        val result = TreeMap<LocalDate, ArrayList<Int>>()
        for (note in this) {
            val localDate = note.note.time.toLocalDate()
            var value = result[localDate]
            if (value == null) {
                value = ArrayList()
                result[localDate] = value
            }
            val category = note.category
            if (category == null) {
                value.add(requireContext().getColorCompat(R.color.grey_dark))
            } else {
                value.add(category.color)
            }
        }
        return result
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
        mSearchListAdapter.onRestoreInstanceState(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        image_drawer_header.setOnClickListener {
            if (requireActivity().isGoogleServicesAvailable(PLAY_SERVICES_REQUEST_CODE)) {
                presenter.onButtonProfileClick()
            }
        }

        button_sync.setOnClickListener {
            mListener?.let { listener ->
                if (listener.getIsBillingInitialized()) {
                    if (listener.getIsItemPurchased(BuildConfig.ITEM_PREMIUM_SKU)) {
                        presenter.onButtonSyncClick()
                    } else {
                        addDisposable(Single.fromCallable { listener.loadOwnedPurchases() }
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe { loaded ->
                                    if (!loaded) {
                                        Toast.makeText(requireContext(), R.string.network_error, Toast.LENGTH_SHORT).show()
                                    } else if (listener.getIsItemPurchased(BuildConfig.ITEM_PREMIUM_SKU)) {
                                        presenter.onButtonSyncClick()
                                    } else {
                                        presenter.onButtonPremiumClick()
                                    }
                                })
                    }
                }
            }
        }

        savedInstanceState?.let {
            button_clear_filters.translationY = it.getFloat(BUNDLE_CLEAR_CHOICES_TRANSLATION_Y, 0f)
        }
        button_clear_filters.setOnClickListener { presenter.onButtonClearFiltersClick() }

        list_search.layoutManager = LinearLayoutManager(requireContext())
        list_search.adapter = mSearchListAdapter
        list_search.setHasFixedSize(true)
        list_search.setItemViewCacheSize(100)

        val animator = list_search.itemAnimator
        if (animator is DefaultItemAnimator) {
            animator.supportsChangeAnimations = false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLAY_SERVICES_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                presenter.onButtonProfileClick()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putFloat(BUNDLE_CLEAR_CHOICES_TRANSLATION_Y, button_clear_filters.translationY)
        mSearchListAdapter.onSaveInstanceState(outState)
    }

    fun onApplyWindowInsets(insets: WindowInsets) {
        image_drawer_header?.let { header ->
            image_profile?.let { profile ->
                val profileMarginParams = profile.layoutParams as ViewGroup.MarginLayoutParams
                val headerParams = header.layoutParams as ViewGroup.LayoutParams
                headerParams.height = dpToPx(130f) + insets.systemWindowInsetTop
                profileMarginParams.topMargin = dpToPx(16f) + insets.systemWindowInsetTop
            }
        }
    }

    override fun showSyncProgress(message: SyncProgressMessage) {
        button_sync.isEnabled = false
        view_sync.alpha = 0.35f
        view_sync.visibility = View.VISIBLE
        view_sync.layoutParams.width = (button_sync.width.toFloat() * message.progress.toFloat() / 100f).toInt()
        button_sync.text = getString(R.string.sync_progress_format, message.progress, message.message)
        view_sync.requestLayout()
        if (message.task == SyncProgressMessage.SYNC_FINISHED) {
            analytics.sendEvent(MyAnalytics.EVENT_SYNC_COMPLETED)
            animateProgressAlpha()
        }
    }

    //Не убирать проверку на null
    override fun clearSyncProgress() {
        view_sync?.visibility = View.INVISIBLE
        view_sync?.layoutParams?.width = 0
        button_sync?.isEnabled = true
        button_sync?.text = getString(R.string.nav_header_main_button_sync)
        view_sync?.requestLayout()
    }

    private fun showErrorSync(progressMessage: SyncProgressMessage) {
        val bundle = Bundle()
        bundle.putInt(MyAnalytics.BUNDLE_TASK_INDEX, progressMessage.task)
        analytics.sendEvent(MyAnalytics.EVENT_SYNC_FAILED, bundle)
        button_sync.text = progressMessage.message
        view_sync.layoutParams.width = button_sync.width
        view_sync.requestLayout()
        animateProgressAlpha()
    }

    private fun animateProgressAlpha() {
        view_sync.animate()
                .alpha(0f)
                .setDuration(ANIMATION_PROGRESS_DURATION)
                .setStartDelay(ANIMATION_PROGRESS_FADE_OUT_OFFSET)
                .setListener(mProgressAnimationListener)
    }

    override fun showNotesTotal(count: Int) {
        text_notes_total.text = count.toString()
    }

    override fun showNotesCountToday(count: Int) {
        text_notes_today.text = count.toString()
    }

    override fun showImageCount(count: Int) {
        text_image_total.text = count.toString()
    }

    override fun showAnonymousProfile() {
        text_email.text = getString(R.string.nav_header_main_anonymous)
        text_profile_description.text = getString(R.string.nav_header_main_sign_in)
    }

    override fun showProfile(profile: MyProfile) {
        text_email.text = profile.email
        text_profile_description.text = getString(R.string.nav_header_main_profile_description)
    }

    override fun showSearchEntries(entries: SearchEntries) {
        val groups = mutableListOf<SearchGroup>()

        groups.add(SearchGroup(
                SearchGroup.TYPE_DATE,
                getString(R.string.date),
                listOf(SearchItem(type = SearchItem.TYPE_DATE, dateColors = entries.notes.toDateColors()))
        ))

        groups.add(SearchGroup(
                SearchGroup.TYPE_TAG,
                getString(R.string.tags),
                entries.tags.map { tag ->
                    SearchItem(
                            type = SearchItem.TYPE_TAG,
                            tag = tag,
                            count = entries.notes.count { note -> note.tags.find { it.id == tag.id } != null }
                    )
                }
                        .sortedByDescending { it.count }
                        .toMutableList()
                        .apply {
                            add(SearchItem(
                                    type = SearchItem.TYPE_NO_TAGS,
                                    count = entries.notes.count { it.tags.isEmpty() }
                            ))
                        }
        ))

        groups.add(SearchGroup(
                SearchGroup.TYPE_CATEGORY,
                getString(R.string.categories),
                entries.categories.map { category ->
                    SearchItem(
                            type = SearchItem.TYPE_CATEGORY,
                            category = category,
                            count = entries.notes.count { it.note.categoryId == category.id }
                    )
                }
                        .sortedByDescending { it.count }
                        .toMutableList()
                        .apply {
                            add(SearchItem(
                                    type = SearchItem.TYPE_NO_CATEGORY,
                                    count = entries.notes.count { it.category == null }))
                        }
        ))

        entries.moods?.let { moods ->
            groups.add(SearchGroup(
                    SearchGroup.TYPE_MOOD,
                    getString(R.string.moods),
                    moods.map { mood ->
                        SearchItem(
                                type = SearchItem.TYPE_MOOD,
                                mood = mood,
                                count = entries.notes.count { it.note.moodId == mood.id }
                        )
                    }
                            .toMutableList()
                            .apply {
                                add(SearchItem(
                                        type = SearchItem.TYPE_NO_MOOD,
                                        count = entries.notes.count { it.mood == null }))
                            }
            ))
        }

        entries.locations?.let { locations ->
            groups.add(SearchGroup(
                    SearchGroup.TYPE_LOCATION,
                    getString(R.string.locations),
                    locations.map { location ->
                        SearchItem(
                                type = SearchItem.TYPE_LOCATION,
                                location = location,
                                count = entries.notes.count { note -> note.locations.find { it.name == location.name } != null }
                        )
                    }
                            .filter { it.count > 0 }
                            .sortedByDescending { it.count }
                            .toMutableList()
                            .apply {
                                add(SearchItem(
                                        type = SearchItem.TYPE_NO_LOCATION,
                                        count = entries.notes.count { it.locations.isEmpty() }))
                            }
            ))
        }

        mSearchListAdapter.submitGroups(groups)
    }

    override fun onTagCheckStateChange(tag: MyTag, checked: Boolean) {
        analytics.sendEvent(MyAnalytics.EVENT_SEARCH_TAG_CHANGED)
        mListener?.onTagCheckStateChange(tag, checked)
    }

    override fun onCategoryCheckStateChange(category: MyCategory, checked: Boolean) {
        analytics.sendEvent(MyAnalytics.EVENT_SEARCH_CATEGORY_CHANGED)
        mListener?.onCategoryCheckStateChange(category, checked)
    }

    override fun onLocationCheckStateChange(location: MyLocation, checked: Boolean) {
        analytics.sendEvent(MyAnalytics.EVENT_SEARCH_LOCATION_CHANGED)
        mListener?.onLocationCheckStateChange(location, checked)
    }

    override fun onMoodCheckStateChange(mood: MyMood, checked: Boolean) {
        analytics.sendEvent(MyAnalytics.EVENT_SEARCH_MOOD_CHANGED)
        mListener?.onMoodCheckStateChange(mood, checked)
    }

    override fun onNoTagsCheckStateChange(checked: Boolean) {
        analytics.sendEvent(MyAnalytics.EVENT_SEARCH_NO_TAGS_CHANGED)
        mListener?.onNoTagsCheckStateChange(checked)
    }

    override fun onNoCategoryCheckStateChange(checked: Boolean) {
        analytics.sendEvent(MyAnalytics.EVENT_SEARCH_NO_CATEGORY_CHANGED)
        mListener?.onNoCategoryCheckStateChange(checked)
    }

    override fun onNoMoodCheckStateChange(checked: Boolean) {
        analytics.sendEvent(MyAnalytics.EVENT_SEARCH_NO_MOOD_CHANGED)
        mListener?.onNoMoodCheckStateChange(checked)
    }

    override fun onNoLocationCheckStateChange(checked: Boolean) {
        analytics.sendEvent(MyAnalytics.EVENT_SEARCH_NO_LOCATION_CHANGED)
        mListener?.onNoLocationCheckStateChange(checked)
    }

    override fun onSearchDatesSelected(startDate: Long?, endDate: Long?) {
        analytics.sendEvent(MyAnalytics.EVENT_SEARCH_DATE_CHANGED)
        mListener?.onSearchDatesSelected(startDate, endDate)
    }

    override fun showProfileSettings() {
        analytics.sendEvent(MyAnalytics.EVENT_PROFILE_SETTINGS)
        if (fragmentManager?.findFragmentByTag(ProfileFragment.TAG) == null) {
            fragmentManager?.inTransaction {
                replace(R.id.main_sheet_container, ProfileFragment(), ProfileFragment.TAG)
            }
        }
        mHandler.postDelayed(mBottomSheetOpenRunnable, BOTTOM_SHEET_EXPAND_DELAY)
    }

    override fun showLoginView() {
        analytics.sendEvent(MyAnalytics.EVENT_SIGN_IN)
        if (fragmentManager?.findFragmentByTag(AuthFragment.TAG) == null) {
            fragmentManager?.inTransaction {
                replace(R.id.main_sheet_container, AuthFragment(), AuthFragment.TAG)
            }
        }
        mHandler.postDelayed(mBottomSheetOpenRunnable, BOTTOM_SHEET_EXPAND_DELAY)
    }

    override fun showPremiumView() {
        if (fragmentManager?.findFragmentByTag(PremiumFragment.TAG) == null) {
            fragmentManager?.inTransaction {
                replace(R.id.main_sheet_container, PremiumFragment(), PremiumFragment.TAG)
            }
        }
        mHandler.postDelayed(mBottomSheetOpenRunnable, BOTTOM_SHEET_EXPAND_DELAY)
    }

    override fun startSyncService() {
        requireContext().startService(SyncService.getIntent(requireContext()))
    }

    override fun onFirstCheck() {
        showClearChoicesButton()
    }

    override fun onCheckCleared() {
        hideClearChoicesButton()
    }

    override fun clearFilters() {
        mSearchListAdapter.clearChoices()
        mListener?.onClearFilters()
    }

    private fun showClearChoicesButton() {
        button_clear_filters.animate()
                .translationY(0f)
                .setDuration(ANIMATION_BUTTON_DURATION)
                .setInterpolator(OvershootInterpolator())
                .startDelay = ANIMATION_BUTTON_START_DELAY
    }

    private fun hideClearChoicesButton() {
        button_clear_filters.animate()
                .translationY(dpToPx(ANIMATION_BUTTON_TRANSLATION_VALUE_DP).toFloat())
                .setDuration(ANIMATION_BUTTON_DURATION)
                .setInterpolator(AnticipateOvershootInterpolator())
                .startDelay = ANIMATION_BUTTON_START_DELAY
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnDrawerMenuInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException("$context must implement OnDrawerMenuInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }

    override fun onStart() {
        super.onStart()
        activity?.layout_main?.requestApplyInsets()
        presenter.attachView(this)
        LocalBroadcastManager.getInstance(requireContext())
                .registerReceiver(mBroadcastReceiver, IntentFilter(Intent.ACTION_SYNC))
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(mBroadcastReceiver)
        presenter.detachView()
    }

    interface OnDrawerMenuInteractionListener {
        fun getIsBillingInitialized(): Boolean
        fun loadOwnedPurchases(): Boolean
        fun getIsItemPurchased(productId: String): Boolean
        fun onTagCheckStateChange(tag: MyTag, checked: Boolean)
        fun onCategoryCheckStateChange(category: MyCategory, checked: Boolean)
        fun onLocationCheckStateChange(location: MyLocation, checked: Boolean)
        fun onMoodCheckStateChange(mood: MyMood, checked: Boolean)
        fun onClearFilters()
        fun onNoTagsCheckStateChange(checked: Boolean)
        fun onNoCategoryCheckStateChange(checked: Boolean)
        fun onNoMoodCheckStateChange(checked: Boolean)
        fun onNoLocationCheckStateChange(checked: Boolean)
        fun onSearchDatesSelected(startDate: Long?, endDate: Long?)
    }
}
