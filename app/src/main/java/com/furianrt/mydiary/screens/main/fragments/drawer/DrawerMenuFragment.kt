package com.furianrt.mydiary.screens.main.fragments.drawer

import android.animation.Animator
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.furianrt.mydiary.BuildConfig
import com.furianrt.mydiary.R
import com.furianrt.mydiary.base.BaseActivity.Companion.ITEM_TEST_SKU
import com.furianrt.mydiary.data.model.*
import com.furianrt.mydiary.data.model.pojo.SearchEntries
import com.furianrt.mydiary.screens.main.fragments.authentication.AuthFragment
import com.furianrt.mydiary.screens.main.fragments.drawer.adapter.SearchGroup
import com.furianrt.mydiary.screens.main.fragments.drawer.adapter.SearchItem
import com.furianrt.mydiary.screens.main.fragments.drawer.adapter.SearchListAdapter
import com.furianrt.mydiary.screens.main.fragments.premium.PremiumFragment
import com.furianrt.mydiary.screens.main.fragments.profile.ProfileFragment
import com.furianrt.mydiary.services.sync.SyncService
import com.furianrt.mydiary.utils.inTransaction
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.bottom_sheet_main.*
import kotlinx.android.synthetic.main.fragment_drawer_menu.*
import kotlinx.android.synthetic.main.fragment_drawer_menu.view.*
import javax.inject.Inject

class DrawerMenuFragment : Fragment(), DrawerMenuContract.View,
        SearchListAdapter.OnSearchListInteractionListener {

    companion object {
        const val TAG = "DrawerMenuFragment"
        private const val ANIMATION_PROGRESS_FADE_OUT_OFFSET = 2000L
        private const val ANIMATION_PROGRESS_DURATION = 500L
        private const val BOTTOM_SHEET_EXPAND_DELAY = 300L
    }

    @Inject
    lateinit var mPresenter: DrawerMenuContract.Presenter

    //Костыль для восстановления состояния списка.
    private var mSavedInstanse: Bundle? = Bundle()

    private var mListener: OnDrawerMenuInteractionListener? = null
    private var mSearchListAdapter = SearchListAdapter()
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
            SyncService.getProgressMessage(intent)?.let {
                view_sync.alpha = 0.35f
                if (it.hasError) {
                    button_sync.text = it.message
                    view_sync.layoutParams.width = button_sync.width
                    view_sync.requestLayout()
                    animateProgressAlpha()
                } else {
                    showSyncProgress(it)
                    if (it.taskIndex == SyncProgressMessage.SYNC_FINISHED) {
                        animateProgressAlpha()
                    }
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        getPresenterComponent(requireContext()).inject(this)
        super.onCreate(savedInstanceState)
        savedInstanceState?.let { mSavedInstanse = it }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_drawer_menu, container, false)

        view.image_drawer_header.setOnClickListener { mPresenter.onButtonProfileClick() }

        view.button_sync.setOnClickListener {
            mListener?.let {
                if (it.getIsBillingInitialized()) {
                    if (it.getIsItemPurshased(BuildConfig.ITEM_SYNC_SKU) || it.getIsItemPurshased(ITEM_TEST_SKU)) {
                        mPresenter.onButtonSyncClick()
                    } else {
                        mPresenter.onButtonPremiumClick()
                    }
                }
            }
        }

        view.list_search.layoutManager = LinearLayoutManager(requireContext())
        view.list_search.adapter = mSearchListAdapter

        val animator = view.list_search.itemAnimator
        if (animator is DefaultItemAnimator) {
            animator.supportsChangeAnimations = false
        }

        return view
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mSearchListAdapter.onSaveInstanceState(outState)
    }

    fun onBillingInitialized() {
        mListener?.let {
            if (it.getIsItemPurshased(BuildConfig.ITEM_SYNC_SKU) || it.getIsItemPurshased(ITEM_TEST_SKU)) {
                button_sync?.text = getString(R.string.nav_header_main_button_sync)
            } else {
                button_sync?.text = getString(R.string.nav_header_main_button_sync_pro)
            }
        }
    }

    fun onProductPurchased(productId: String) {
        if (productId == BuildConfig.ITEM_SYNC_SKU || productId == ITEM_TEST_SKU) {
            button_sync?.text = getString(R.string.nav_header_main_button_sync)
        }
    }

    override fun showSyncProgress(message: SyncProgressMessage) {
        button_sync.isEnabled = false
        view_sync.alpha = 0.35f
        view_sync.visibility = View.VISIBLE
        view_sync.layoutParams.width =
                (button_sync.width.toFloat() * message.progress.toFloat() / 100f).toInt()
        button_sync.text = getString(R.string.sync_progress_format, message.progress, message.message)
        view_sync.requestLayout()
    }

    override fun clearSyncProgress() {
        view_sync.visibility = View.INVISIBLE
        view_sync.layoutParams.width = 0
        button_sync.isEnabled = true
        mListener?.let {
            if (it.getIsBillingInitialized()
                    && (it.getIsItemPurshased(BuildConfig.ITEM_SYNC_SKU) || it.getIsItemPurshased(ITEM_TEST_SKU))) {
                button_sync.text = getString(R.string.nav_header_main_button_sync)
            } else {
                button_sync.text = getString(R.string.nav_header_main_button_sync_pro)
            }
        }

        view_sync.requestLayout()
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
        text_profile_description.text = getString(R.string.nav_header_main_sing_in)
    }

    override fun showProfile(profile: MyProfile) {
        text_email.text = profile.email
        text_profile_description.text = getString(R.string.nav_header_main_premium)
    }

    override fun showSearchEntries(entries: SearchEntries) {
        val tagGroup = SearchGroup(
                SearchGroup.TYPE_TAG,
                getString(R.string.tags),
                entries.tags.map { SearchItem(type = SearchItem.TYPE_TAG, tag = it) }
        )
        val categoryGroup = SearchGroup(
                SearchGroup.TYPE_CATEGORY,
                getString(R.string.categories),
                entries.categories.map { SearchItem(type = SearchItem.TYPE_CATEGORY, category = it) }
        )
        val moodGroup = SearchGroup(
                SearchGroup.TYPE_MOOD,
                getString(R.string.moods),
                entries.moods.map { SearchItem(type = SearchItem.TYPE_MOOD, mood = it) }
        )
        val locationGroup = SearchGroup(
                SearchGroup.TYPE_LOCATION,
                getString(R.string.locations),
                entries.location.map { SearchItem(type = SearchItem.TYPE_LOCATION, location = it) }
        )
        val groupList = mutableListOf(tagGroup, categoryGroup, moodGroup, locationGroup)

        mSearchListAdapter = SearchListAdapter(groupList, this)
        list_search.adapter = mSearchListAdapter
        mSearchListAdapter.onRestoreInstanceState(mSavedInstanse)

    }

    override fun onTagChackStateChange(tag: MyTag, checked: Boolean) {
        Log.e(TAG, "onTagChackStateChange")
    }

    override fun onCategoryChackStateChange(category: MyCategory, checked: Boolean) {
        Log.e(TAG, "onCategoryChackStateChange")
    }

    override fun onLocationChackStateChange(location: MyLocation, checked: Boolean) {
        Log.e(TAG, "onLocationChackStateChange")
    }

    override fun onMoodChackStateChange(mood: MyMood, checked: Boolean) {
        Log.e(TAG, "onMoodChackStateChange")
    }

    override fun showProfileSettings() {
        if (fragmentManager?.findFragmentByTag(ProfileFragment.TAG) == null) {
            fragmentManager?.inTransaction {
                replace(R.id.main_sheet_container, ProfileFragment(), ProfileFragment.TAG)
            }
        }
        mHandler.postDelayed(mBottomSheetOpenRunnable, BOTTOM_SHEET_EXPAND_DELAY)
    }

    override fun showLoginView() {
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
        mSearchListAdapter.onSaveInstanceState(mSavedInstanse)
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(mBroadcastReceiver)
        mPresenter.detachView()
    }

    interface OnDrawerMenuInteractionListener {
        fun getIsBillingInitialized(): Boolean
        fun getIsItemPurshased(productId: String): Boolean
    }
}
