/*******************************************************************************
 *  @author FurianRT
 *  Copyright 2019
 *
 *  All rights reserved.
 *  Distribution of the software in any form is only allowed with
 *  explicit, prior permission from the owner.
 *
 ******************************************************************************/

package com.furianrt.mydiary.view.screens.main.fragments.drawer

import android.animation.Animator
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.OvershootInterpolator
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.furianrt.mydiary.BuildConfig
import com.furianrt.mydiary.R
import com.furianrt.mydiary.analytics.MyAnalytics
import com.furianrt.mydiary.view.base.BaseFragment
import com.furianrt.mydiary.data.model.*
import com.furianrt.mydiary.data.model.pojo.SearchEntries
import com.furianrt.mydiary.view.screens.main.fragments.authentication.AuthFragment
import com.furianrt.mydiary.view.screens.main.fragments.drawer.adapter.SearchGroup
import com.furianrt.mydiary.view.screens.main.fragments.drawer.adapter.SearchItem
import com.furianrt.mydiary.view.screens.main.fragments.drawer.adapter.SearchListAdapter
import com.furianrt.mydiary.view.screens.main.fragments.premium.PremiumFragment
import com.furianrt.mydiary.view.screens.main.fragments.profile.ProfileFragment
import com.furianrt.mydiary.view.services.sync.SyncService
import com.furianrt.mydiary.utils.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_sheet_main.*
import kotlinx.android.synthetic.main.fragment_drawer_menu.*
import kotlinx.android.synthetic.main.fragment_drawer_menu.view.*
import org.threeten.bp.LocalDate
import java.util.TreeMap
import javax.inject.Inject

class DrawerMenuFragment : BaseFragment(), DrawerMenuContract.MvpView,
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
    lateinit var mPresenter: DrawerMenuContract.Presenter

    private var mListener: OnDrawerMenuInteractionListener? = null
    private var mSearchListAdapter = SearchListAdapter(listener = this)
    private val mHandler = Handler()
    private val mBottomSheetOpenRunnable: Runnable = Runnable {
        BottomSheetBehavior.from(requireActivity().main_sheet_container).state =
                BottomSheetBehavior.STATE_EXPANDED
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
                value.add(requireContext().getThemePrimaryColor())
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

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_drawer_menu, container, false)

        view.image_drawer_header.setOnClickListener {
            if (requireActivity().isGoogleServicesAvailable(PLAY_SERVICES_REQUEST_CODE)) {
                mPresenter.onButtonProfileClick()
            }
        }

        view.button_sync.setOnClickListener {
            mListener?.let {
                if (it.getIsBillingInitialized()) {
                    if (it.getIsItemPurchased(BuildConfig.ITEM_PREMIUM_SKU)/* || it.getIsItemPurchased(ITEM_TEST_SKU)*/) {
                        mPresenter.onButtonSyncClick()
                    } else {
                        mPresenter.onButtonPremiumClick()
                    }
                }
            }
        }

        savedInstanceState?.let {
            view.button_clear_filters.translationY = it.getFloat(BUNDLE_CLEAR_CHOICES_TRANSLATION_Y, 0f)
        }
        view.button_clear_filters.setOnClickListener { mPresenter.onButtonClearFiltersClick() }

        view.list_search.layoutManager = LinearLayoutManager(requireContext())
        view.list_search.adapter = mSearchListAdapter
        view.list_search.setHasFixedSize(true)
        view.list_search.setItemViewCacheSize(100)

        val animator = view.list_search.itemAnimator
        if (animator is DefaultItemAnimator) {
            animator.supportsChangeAnimations = false
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLAY_SERVICES_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                mPresenter.onButtonProfileClick()
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putFloat(BUNDLE_CLEAR_CHOICES_TRANSLATION_Y, button_clear_filters.translationY)
        mSearchListAdapter.onSaveInstanceState(outState)
    }

    override fun showSyncProgress(message: SyncProgressMessage) {
        button_sync.isEnabled = false
        view_sync.alpha = 0.35f
        view_sync.visibility = View.VISIBLE
        view_sync.layoutParams.width = (button_sync.width.toFloat() * message.progress.toFloat() / 100f).toInt()
        button_sync.text = getString(R.string.sync_progress_format, message.progress, message.message)
        view_sync.requestLayout()
        if (message.taskIndex == SyncProgressMessage.SYNC_FINISHED) {
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
        bundle.putInt(MyAnalytics.BUNDLE_TASK_INDEX, progressMessage.taskIndex)
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
                entries.tags.map { SearchItem(type = SearchItem.TYPE_TAG, tag = it) }
                        .toMutableList()
                        .apply { add(SearchItem(type = SearchItem.TYPE_NO_TAGS)) }
        ))

        groups.add(SearchGroup(
                SearchGroup.TYPE_CATEGORY,
                getString(R.string.categories),
                entries.categories.map { SearchItem(type = SearchItem.TYPE_CATEGORY, category = it) }
                        .toMutableList()
                        .apply { add(SearchItem(type = SearchItem.TYPE_NO_CATEGORY)) }
        ))

        entries.moods?.let { moods ->
            groups.add(SearchGroup(
                    SearchGroup.TYPE_MOOD,
                    getString(R.string.moods),
                    moods.map { SearchItem(type = SearchItem.TYPE_MOOD, mood = it) }
                            .toMutableList()
                            .apply { add(SearchItem(type = SearchItem.TYPE_NO_MOOD)) }
            ))
        }

        entries.locations?.let { locations ->
            groups.add(SearchGroup(
                    SearchGroup.TYPE_LOCATION,
                    getString(R.string.locations),
                    locations.map { SearchItem(type = SearchItem.TYPE_LOCATION, location = it) }
                            .toMutableList()
                            .apply { add(SearchItem(type = SearchItem.TYPE_NO_LOCATION)) }
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
        requireContext().startService(Intent(requireContext(), SyncService::class.java))
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
        mPresenter.attachView(this)
        LocalBroadcastManager.getInstance(requireContext())
                .registerReceiver(mBroadcastReceiver, IntentFilter(Intent.ACTION_SYNC))
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(mBroadcastReceiver)
        mPresenter.detachView()
    }

    interface OnDrawerMenuInteractionListener {
        fun getIsBillingInitialized(): Boolean
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