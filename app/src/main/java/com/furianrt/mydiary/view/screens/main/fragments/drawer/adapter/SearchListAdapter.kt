package com.furianrt.mydiary.view.screens.main.fragments.drawer.adapter

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Checkable
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyCategory
import com.furianrt.mydiary.data.model.MyLocation
import com.furianrt.mydiary.data.model.MyMood
import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.view.screens.main.fragments.drawer.adapter.SearchListAdapter.SearchChildViewHolder
import com.furianrt.mydiary.view.screens.main.fragments.drawer.adapter.SearchListAdapter.SearchGroupViewHolder
import com.furianrt.mydiary.utils.*
import com.furianrt.mydiary.view.views.CalendarDayView
import com.kizitonwose.calendarview.CalendarView
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import com.thoughtbot.expandablecheckrecyclerview.listeners.OnChildCheckChangedListener
import com.thoughtbot.expandablecheckrecyclerview.viewholders.CheckableChildViewHolder
import com.thoughtbot.expandablerecyclerview.MultiTypeExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import kotlinx.android.synthetic.main.calendar_day.view.*
import kotlinx.android.synthetic.main.nav_search_item_date.view.*
import kotlinx.android.synthetic.main.nav_search_group.view.*
import kotlinx.android.synthetic.main.nav_search_item_category.view.*
import kotlinx.android.synthetic.main.nav_search_item_location.view.*
import kotlinx.android.synthetic.main.nav_search_item_mood.view.*
import kotlinx.android.synthetic.main.nav_search_item_no_category.view.*
import kotlinx.android.synthetic.main.nav_search_item_no_location.view.*
import kotlinx.android.synthetic.main.nav_search_item_no_mood.view.*
import kotlinx.android.synthetic.main.nav_search_item_no_tags.view.*
import kotlinx.android.synthetic.main.nav_search_item_tag.view.*
import org.threeten.bp.LocalDate
import org.threeten.bp.Month
import org.threeten.bp.Year
import org.threeten.bp.YearMonth
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.TextStyle
import java.util.Locale
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

//todo тут жесть, надо порефакторить
class SearchListAdapter(
        var listener: OnSearchListInteractionListener? = null
) : MultiTypeExpandableRecyclerViewAdapter<SearchGroupViewHolder, SearchChildViewHolder>(mutableListOf()),
        OnChildCheckChangedListener {

    companion object {
        private const val ANIM_ARROW_RATATION_DURATION = 350L
        private const val BUNDLE_EXPANDED_GROUP_TYPES = "expanded_group_types"
        private const val BUNDLE_SELECTED_TAG_IDS = "selected_tag_ids"
        private const val BUNDLE_SELECTED_CATEGORY_IDS = "selected_category_ids"
        private const val BUNDLE_SELECTED_MOOD_IDS = "selected_mood_ids"
        private const val BUNDLE_SELECTED_LOCATION_NAMES = "selected_location_names"
        private const val BUNDLE_SELECTED_ITEM_NON_TYPES = "selected_item_non_types"
        private const val BUNDLE_START_DATE = "start_date"
        private const val BUNDLE_END_DATE = "end_date"
        private const val BUNDLE_CALENDAR_SCROLL_DATE = "calendar_scroll_date"
        private const val BUNDLE_IS_MONTH_SELECTION_ACTIVE = "is_month_selection_active"
        private const val BUNDLE_IS_YEAR_SELECTION_ACTIVE = "is_year_selection_active"
        private const val BUNDLE_IS_TODAY_SELECTION_ACTIVE = "is_today_selection_active"
    }

    private val mExpandedGroupTypes = HashSet<Int>()
    private val mSelectedTagIds = HashSet<String>()
    private val mSelectedCategoryIds = HashSet<String>()
    private val mSelectedMoodIds = HashSet<Int>()
    private val mSelectedLocationNames = HashSet<String>()
    private val mSelectedNoItemTypes = HashSet<Int>()
    private var mStartDate: LocalDate? = null
    private var mEndDate: LocalDate? = null
    private var mCalendarScrollDate: YearMonth? = null
    private var mCalendar: CalendarView? = null
    private var mIsMonthSelectionActive = false
    private var mIsYearSelectionActive = false
    private var mIsTodaySelectionActive = false
    private val mRecyclerPool = RecyclerView.RecycledViewPool()

    override fun onSaveInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState?.let { state ->
            state.putIntegerArrayList(BUNDLE_EXPANDED_GROUP_TYPES, ArrayList(mExpandedGroupTypes))
            state.putStringArrayList(BUNDLE_SELECTED_TAG_IDS, ArrayList(mSelectedTagIds))
            state.putStringArrayList(BUNDLE_SELECTED_CATEGORY_IDS, ArrayList(mSelectedCategoryIds))
            state.putIntegerArrayList(BUNDLE_SELECTED_MOOD_IDS, ArrayList(mSelectedMoodIds))
            state.putStringArrayList(BUNDLE_SELECTED_LOCATION_NAMES, ArrayList(mSelectedLocationNames))
            state.putIntegerArrayList(BUNDLE_SELECTED_ITEM_NON_TYPES, ArrayList(mSelectedNoItemTypes))
            state.putBoolean(BUNDLE_IS_MONTH_SELECTION_ACTIVE, mIsMonthSelectionActive)
            state.putBoolean(BUNDLE_IS_YEAR_SELECTION_ACTIVE, mIsYearSelectionActive)
            state.putBoolean(BUNDLE_IS_TODAY_SELECTION_ACTIVE, mIsTodaySelectionActive)
            mStartDate?.let { state.putSerializable(BUNDLE_START_DATE, it) }
            mEndDate?.let { state.putSerializable(BUNDLE_END_DATE, it) }
            mCalendarScrollDate?.let { state.putSerializable(BUNDLE_CALENDAR_SCROLL_DATE, it) }

        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState?.let { state ->
            mExpandedGroupTypes.clear()
            mExpandedGroupTypes.addAll(state.getIntegerArrayList(BUNDLE_EXPANDED_GROUP_TYPES)
                    ?: emptyList())

            mSelectedTagIds.clear()
            mSelectedTagIds.addAll(state.getStringArrayList(BUNDLE_SELECTED_TAG_IDS)
                    ?: emptyList())

            mSelectedCategoryIds.clear()
            mSelectedCategoryIds.addAll(state.getStringArrayList(BUNDLE_SELECTED_CATEGORY_IDS)
                    ?: emptyList())

            mSelectedMoodIds.clear()
            mSelectedMoodIds.addAll(state.getIntegerArrayList(BUNDLE_SELECTED_MOOD_IDS)
                    ?: emptyList())

            mSelectedLocationNames.clear()
            mSelectedLocationNames.addAll(state.getStringArrayList(BUNDLE_SELECTED_LOCATION_NAMES)
                    ?: emptyList())

            mSelectedNoItemTypes.clear()
            mSelectedNoItemTypes.addAll(state.getIntegerArrayList(BUNDLE_SELECTED_ITEM_NON_TYPES)
                    ?: emptyList())

            mStartDate = state.getSerializable(BUNDLE_START_DATE) as LocalDate?
            mEndDate = state.getSerializable(BUNDLE_END_DATE) as LocalDate?

            mCalendarScrollDate = state.getSerializable(BUNDLE_CALENDAR_SCROLL_DATE) as YearMonth?

            mIsMonthSelectionActive = state.getBoolean(BUNDLE_IS_MONTH_SELECTION_ACTIVE, false)
            mIsYearSelectionActive = state.getBoolean(BUNDLE_IS_YEAR_SELECTION_ACTIVE, false)
            mIsTodaySelectionActive = state.getBoolean(BUNDLE_IS_TODAY_SELECTION_ACTIVE, false)
        }
    }

    private fun notifyGroupDataChanged() {
        expandableList.expandedGroupIndexes = BooleanArray(groups.size)
        for (i in 0 until groups.size) {
            expandableList.expandedGroupIndexes[i] = false
        }
    }

    fun clearChoices() {
        mSelectedTagIds.clear()
        mSelectedCategoryIds.clear()
        mSelectedMoodIds.clear()
        mSelectedLocationNames.clear()
        mSelectedNoItemTypes.clear()
        mIsTodaySelectionActive = false
        mStartDate = null
        mEndDate = null
        notifyItemsStateChanged()
        listener?.onCheckCleared()
    }

    private fun notifyItemsStateChanged() {
        //only update the child views that are visible (i.e. their group is expanded)
        for (i in 0 until groups.size) {
            val group = groups[i] as SearchGroup
            if (isGroupExpanded(group)) {
                if (group.type == SearchGroup.TYPE_DATE) {
                    mCalendar?.notifyCalendarChanged()
                } else {
                    notifyItemRangeChanged(expandableList.getFlattenedFirstChildIndex(i), group.itemCount)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun submitGroups(groups: List<SearchGroup>) {
        with(getGroups() as MutableList<SearchGroup>) {
            clear()
            addAll(groups)
            notifyGroupDataChanged()
            notifyDataSetChanged()
            mExpandedGroupTypes
                    .sortedByDescending { it }
                    .forEach { groupType ->
                        find { it.type == groupType }?.let {
                            expandableList.expandedGroupIndexes[indexOf(it)] = true
                        }
                    }
        }
    }

    override fun onCreateGroupViewHolder(parent: ViewGroup?, viewType: Int): SearchGroupViewHolder {
        val view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.nav_search_group, parent, false)
        return SearchGroupViewHolder(view)
    }

    override fun onCreateChildViewHolder(parent: ViewGroup?, viewType: Int): SearchChildViewHolder {
        val inflater = LayoutInflater.from(parent?.context)
        return when (viewType) {
            SearchItem.TYPE_DATE -> {
                val view = inflater.inflate(R.layout.nav_search_item_date, parent, false)
                view.calendar_search.setRecycledViewPool(mRecyclerPool)
                view.calendar_search.setHasFixedSize(true)
                (view.calendar_search.layoutManager as LinearLayoutManager?)?.initialPrefetchItemCount = 1
                SearchDateViewHolder(view)
            }
            SearchItem.TYPE_TAG ->
                SearchTagsViewHolder(inflater.inflate(R.layout.nav_search_item_tag, parent, false))
            SearchItem.TYPE_CATEGORY ->
                SearchCategoriesViewHolder(inflater.inflate(R.layout.nav_search_item_category, parent, false))
            SearchItem.TYPE_LOCATION ->
                SearchLocationsViewHolder(inflater.inflate(R.layout.nav_search_item_location, parent, false))
            SearchItem.TYPE_MOOD ->
                SearchMoodsViewHolder(inflater.inflate(R.layout.nav_search_item_mood, parent, false))
            SearchItem.TYPE_NO_TAGS ->
                SearchNoTagsViewHolder(inflater.inflate(R.layout.nav_search_item_no_tags, parent, false))
            SearchItem.TYPE_NO_CATEGORY ->
                SearchNoCategoryViewHolder(inflater.inflate(R.layout.nav_search_item_no_category, parent, false))
            SearchItem.TYPE_NO_MOOD ->
                SearchNoMoodViewHolder(inflater.inflate(R.layout.nav_search_item_no_mood, parent, false))
            SearchItem.TYPE_NO_LOCATION ->
                SearchNoLocationViewHolder(inflater.inflate(R.layout.nav_search_item_no_location, parent, false))
            else -> throw IllegalStateException("Unsupported holder type")
        }
    }

    override fun onBindGroupViewHolder(holder: SearchGroupViewHolder?, flatPosition: Int, group: ExpandableGroup<*>?) {
        holder?.bind(group as SearchGroup)
    }

    override fun onBindChildViewHolder(holder: SearchChildViewHolder?, flatPosition: Int, group: ExpandableGroup<*>?, childIndex: Int) {
        val item = (group as SearchGroup).groupItems[childIndex]
        when (item.type) {
            SearchItem.TYPE_DATE ->
                holder?.onBindViewHolder(flatPosition, false)
            SearchItem.TYPE_TAG ->
                holder?.onBindViewHolder(flatPosition, mSelectedTagIds.contains(item.tag!!.id))
            SearchItem.TYPE_CATEGORY ->
                holder?.onBindViewHolder(flatPosition, mSelectedCategoryIds.contains(item.category!!.id))
            SearchItem.TYPE_LOCATION ->
                holder?.onBindViewHolder(flatPosition, mSelectedLocationNames.contains(item.location!!.name))
            SearchItem.TYPE_MOOD ->
                holder?.onBindViewHolder(flatPosition, mSelectedMoodIds.contains(item.mood!!.id))
            else ->
                holder?.onBindViewHolder(flatPosition, mSelectedNoItemTypes.contains(item.type))
        }
        holder?.bind(item)
    }

    override fun getGroupViewType(position: Int, group: ExpandableGroup<*>?): Int =
            (group as SearchGroup).type

    override fun getChildViewType(position: Int, group: ExpandableGroup<*>?, childIndex: Int): Int =
            (group as SearchGroup).groupItems[childIndex].type

    override fun isGroup(viewType: Int): Boolean = viewType == SearchGroup.TYPE_TAG
            || viewType == SearchGroup.TYPE_CATEGORY
            || viewType == SearchGroup.TYPE_LOCATION
            || viewType == SearchGroup.TYPE_MOOD
            || viewType == SearchGroup.TYPE_DATE

    override fun isChild(viewType: Int): Boolean = viewType == SearchItem.TYPE_TAG
            || viewType == SearchItem.TYPE_CATEGORY
            || viewType == SearchItem.TYPE_LOCATION
            || viewType == SearchItem.TYPE_MOOD
            || viewType == SearchItem.TYPE_NO_TAGS
            || viewType == SearchItem.TYPE_NO_CATEGORY
            || viewType == SearchItem.TYPE_NO_MOOD
            || viewType == SearchItem.TYPE_NO_LOCATION
            || viewType == SearchItem.TYPE_DATE

    override fun onChildCheckChanged(view: View?, checked: Boolean, flatPos: Int) {
        val listPos = expandableList.getUnflattenedPosition(flatPos)
        val item = (expandableList.getExpandableGroup(listPos) as SearchGroup).groupItems[listPos.childPos]
        when (item.type) {
            SearchItem.TYPE_TAG -> {
                if (checked) {
                    mSelectedTagIds.add(item.tag!!.id)
                    isFirstCheck()
                } else {
                    mSelectedTagIds.remove(item.tag!!.id)
                    isLastCheck()
                }
                listener?.onTagCheckStateChange(item.tag, checked)
            }
            SearchItem.TYPE_CATEGORY -> {
                if (checked) {
                    mSelectedCategoryIds.add(item.category!!.id)
                    isFirstCheck()
                } else {
                    mSelectedCategoryIds.remove(item.category!!.id)
                    isLastCheck()
                }
                listener?.onCategoryCheckStateChange(item.category, checked)
            }
            SearchItem.TYPE_LOCATION -> {
                if (checked) {
                    mSelectedLocationNames.add(item.location!!.name)
                    isFirstCheck()
                } else {
                    mSelectedLocationNames.remove(item.location!!.name)
                    isLastCheck()
                }
                listener?.onLocationCheckStateChange(item.location, checked)
            }
            SearchItem.TYPE_MOOD -> {
                if (checked) {
                    mSelectedMoodIds.add(item.mood!!.id)
                    isFirstCheck()
                } else {
                    mSelectedMoodIds.remove(item.mood!!.id)
                    isLastCheck()
                }
                listener?.onMoodCheckStateChange(item.mood, checked)
            }
            SearchItem.TYPE_NO_TAGS -> {
                if (checked) {
                    mSelectedNoItemTypes.add(item.type)
                    isFirstCheck()
                } else {
                    mSelectedNoItemTypes.remove(item.type)
                    isLastCheck()
                }
                listener?.onNoTagsCheckStateChange(checked)
            }
            SearchItem.TYPE_NO_CATEGORY -> {
                if (checked) {
                    mSelectedNoItemTypes.add(item.type)
                    isFirstCheck()
                } else {
                    mSelectedNoItemTypes.remove(item.type)
                    isLastCheck()
                }
                listener?.onNoCategoryCheckStateChange(checked)
            }
            SearchItem.TYPE_NO_MOOD -> {
                if (checked) {
                    mSelectedNoItemTypes.add(item.type)
                    isFirstCheck()
                } else {
                    mSelectedNoItemTypes.remove(item.type)
                    isLastCheck()
                }
                listener?.onNoMoodCheckStateChange(checked)
            }
            SearchItem.TYPE_NO_LOCATION -> {
                if (checked) {
                    mSelectedNoItemTypes.add(item.type)
                    isFirstCheck()
                } else {
                    mSelectedNoItemTypes.remove(item.type)
                    isLastCheck()
                }
                listener?.onNoLocationChackStateChange(checked)
            }
        }
    }

    private fun isLastCheck() {
        if (mSelectedTagIds.isEmpty()
                && mSelectedCategoryIds.isEmpty()
                && mSelectedMoodIds.isEmpty()
                && mSelectedLocationNames.isEmpty()
                && mSelectedNoItemTypes.isEmpty()
                && mStartDate == null
                && mEndDate == null) {
            listener?.onCheckCleared()
        }
    }

    private fun isFirstCheck() {
        val dates = if (mStartDate != null && mEndDate != null) {
            2
        } else if (mStartDate != null && mEndDate == null) {
            1
        } else {
            0
        }
        val checkCount = mSelectedTagIds.size + mSelectedCategoryIds.size + mSelectedMoodIds.size +
                mSelectedLocationNames.size + mSelectedNoItemTypes.size + dates
        if (checkCount == 1) {
            listener?.onFirstCheck()
        }
    }

    inner class SearchGroupViewHolder(view: View) : GroupViewHolder(view) {

        private lateinit var mGroup: SearchGroup

        fun bind(group: SearchGroup) {
            mGroup = group

            if (mExpandedGroupTypes.contains(group.type)) {
                itemView.image_group_arrow.rotation = 180f
            } else {
                itemView.image_group_arrow.rotation = 0f
            }

            itemView.text_search_group_name.text = group.groupTitle

            when (group.type) {
                SearchGroup.TYPE_DATE -> itemView.image_group.setImageResource(R.drawable.ic_date_range)
                SearchGroup.TYPE_TAG -> itemView.image_group.setImageResource(R.drawable.ic_tag_big)
                SearchGroup.TYPE_CATEGORY -> itemView.image_group.setImageResource(R.drawable.ic_folder_big)
                SearchGroup.TYPE_MOOD -> itemView.image_group.setImageResource(R.drawable.ic_smile_bold)
                SearchGroup.TYPE_LOCATION -> itemView.image_group.setImageResource(R.drawable.ic_place_big)
            }
        }

        override fun expand() {
            mExpandedGroupTypes.add(mGroup.type)
            itemView.image_group_arrow.animate()
                    .rotation(180f)
                    .setDuration(ANIM_ARROW_RATATION_DURATION)
                    .start()
        }

        override fun collapse() {
            mExpandedGroupTypes.remove(mGroup.type)
            itemView.image_group_arrow.animate()
                    .rotation(0f)
                    .setDuration(ANIM_ARROW_RATATION_DURATION)
                    .start()
        }
    }

    abstract class SearchChildViewHolder(view: View) : CheckableChildViewHolder(view) {
        abstract fun bind(item: SearchItem)
    }

    inner class SearchDateViewHolder(view: View) : SearchChildViewHolder(view) {

        private val mToday = LocalDate.now()

        @SuppressLint("DefaultLocale")
        override fun bind(item: SearchItem) {
            // Set the First day of week depending on Locale
            val daysOfWeek = daysOfWeekFromLocale()
            for (i in 0 until itemView.layout_legend.childCount) {
                val textView = itemView.layout_legend.getChildAt(i) as TextView
                textView.text = daysOfWeek[i].getDisplayName(TextStyle.SHORT, Locale.getDefault())
            }

            val currentDate = YearMonth.now()
            val minDate = YearMonth.of(1980, Month.JANUARY)
            val maxDate = YearMonth.of(2100, Month.DECEMBER)

            mCalendar = itemView.calendar_search

            itemView.calendar_search.setup(minDate, maxDate, daysOfWeek.first())

            itemView.calendar_search.monthScrollListener = { calendarMonth ->
                mCalendarScrollDate = calendarMonth.yearMonth
                updateCalendarHeader(calendarMonth.yearMonth)
            }

            val savedScrollDate = mCalendarScrollDate
            if (savedScrollDate != null) {
                scrollToMonth(savedScrollDate)
            } else {
                scrollToMonth(currentDate)
            }

            itemView.calendar_search.dayBinder = object : DayBinder<DayViewContainer> {
                override fun create(view: View) = DayViewContainer(view)
                override fun bind(container: DayViewContainer, day: CalendarDay) {
                    with(container) {
                        this.day = day
                        calendarDayView.text = day.date.dayOfMonth.toString()
                        calendarDayView.background = null
                        calendarDayView.showCircle = false

                        item.dateColors!![day.date]?.let { colors ->
                            calendarDayView.showCircle = true
                            calendarDayView.setColors(colors)
                        }

                        val select = mStartDate == day.date && mEndDate == null
                                || (mStartDate != null && mEndDate != null && day.date >= mStartDate && day.date <= mEndDate)

                        calendarDayView.isCurrentDay = day.date == mToday
                        calendarDayView.isCurrentMonth = day.owner == DayOwner.THIS_MONTH
                        calendarDayView.isSelected = select

                        if (calendarDayView.isCurrentDay) {
                            selectToday(select && mEndDate == null)
                        }
                    }
                }
            }

            itemView.layout_list_calendar.setOnTouchListener { _, _ -> true }

            selectToday(mIsTodaySelectionActive)

            if (mIsMonthSelectionActive) {
                showMonthSelection()
            } else if (mIsYearSelectionActive) {
                showYearSelection(item)
            }

            itemView.text_today.setOnClickListener {
                if (mEndDate == null && mStartDate == mToday) {
                    mStartDate = null
                    selectToday(false)
                    itemView.calendar_search.notifyCalendarChanged()
                    isLastCheck()
                } else {
                    mStartDate = mToday
                    mEndDate = null
                    selectToday(true)
                    itemView.calendar_search.notifyCalendarChanged()
                    scrollToMonth(YearMonth.now())
                    isFirstCheck()
                }
                listener?.onSearchDatesSelected(mStartDate?.toMills(), mEndDate?.toMills())
            }

            itemView.text_month.setOnClickListener {
                if (mIsMonthSelectionActive) {
                    hideMonthSelection()
                } else {
                    hideYearSelection()
                    showMonthSelection()
                }
            }

            if (!item.dateColors.isNullOrEmpty()) {
                itemView.text_year.setOnClickListener {
                    if (mIsYearSelectionActive) {
                        hideYearSelection()
                    } else {
                        hideMonthSelection()
                        showYearSelection(item)
                    }
                }
            }
        }

        private fun selectToday(select: Boolean) {
            mIsTodaySelectionActive = select
            if (select) {
                itemView.text_today.setTextColorResource(R.color.white)
                itemView.text_today.setBackgroundResource(R.drawable.background_corner_solid)
            } else {
                itemView.text_today.setTextColor(itemView.context.getThemePrimaryColor())
                itemView.text_today.setBackgroundResource(R.drawable.background_corner_stroke)
            }
        }

        private fun updateCalendarHeader(yearMonth: YearMonth) {
            itemView.text_month.text = DateTimeFormatter.ofPattern("MMMM")
                    .format(yearMonth)
                    .capitalize()
            itemView.text_year.text = yearMonth.year.toString()
        }

        private fun scrollToMonth(yearMonth: YearMonth) {
            mCalendarScrollDate = yearMonth
            itemView.calendar_search.scrollToMonth(yearMonth)
            updateCalendarHeader(yearMonth)
        }

        private fun showMonthSelection() {
            mIsMonthSelectionActive = true
            itemView.text_month.setTextColorResource(R.color.white)
            itemView.text_month.setBackgroundResource(R.drawable.background_corner_solid)
            val adapter = CalendarMonthAdapter(object : CalendarMonthAdapter.OnMonthListInteractionListener {
                override fun onMonthClick(month: Month) {
                    mIsMonthSelectionActive = false
                    hideMonthSelection()
                    mCalendarScrollDate?.let { scrollToMonth(YearMonth.of(it.year, month.value)) }
                }
            })
            itemView.layout_list_calendar.visibility = View.VISIBLE
            itemView.list_calendar.layoutManager = GridLayoutManager(itemView.context, 4, GridLayoutManager.HORIZONTAL, false)
            itemView.list_calendar.adapter = adapter
            adapter.showList(Month.values().toList())
        }

        private fun hideMonthSelection() {
            mIsMonthSelectionActive = false
            itemView.layout_list_calendar.visibility = View.GONE
            itemView.text_month.setTextColor(itemView.context.getThemePrimaryColor())
            itemView.text_month.setBackgroundResource(R.drawable.background_corner_stroke)
        }

        private fun showYearSelection(item: SearchItem) {
            mIsYearSelectionActive = true
            itemView.text_year.setTextColorResource(R.color.white)
            itemView.text_year.setBackgroundResource(R.drawable.background_corner_solid)
            val adapter = CalendarYearAdapter(object : CalendarYearAdapter.OnYearListInteractionListener {
                override fun onYearClick(year: Year) {
                    hideYearSelection()
                    mCalendarScrollDate?.let { scrollToMonth(YearMonth.of(year.value, it.monthValue)) }
                }
            })
            itemView.layout_list_calendar.visibility = View.VISIBLE
            itemView.list_calendar.layoutManager = GridLayoutManager(itemView.context, 4, GridLayoutManager.HORIZONTAL, false)
            itemView.list_calendar.adapter = adapter
            adapter.showList(item.dateColors!!.keys.map { Year.of(it.year) }.distinct())
        }

        private fun hideYearSelection() {
            mIsYearSelectionActive = false
            itemView.layout_list_calendar.visibility = View.GONE
            itemView.text_year.setTextColor(itemView.context.getThemePrimaryColor())
            itemView.text_year.setBackgroundResource(R.drawable.background_corner_stroke)
        }

        inner class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay // Will be set when this container is bound.
            val calendarDayView: CalendarDayView = view.text_calendar_day

            init {
                view.setOnClickListener {
                    val date = day.date
                    if (mStartDate != null) {
                        if (date < mStartDate || mEndDate != null) {
                            mStartDate = date
                            mEndDate = null
                        } else if (date != mStartDate) {
                            mEndDate = date
                        } else if (date == mStartDate) {
                            mStartDate = null
                            isLastCheck()
                        }
                    } else {
                        mStartDate = date
                        isFirstCheck()
                    }

                    itemView.calendar_search.notifyCalendarChanged()
                    listener?.onSearchDatesSelected(mStartDate?.toMills(), mEndDate?.toMills())
                }
            }
        }

        //Для календаря Checkable не нужен, но передать что-то нужно
        override fun getCheckable(): Checkable = CheckBox(itemView.context)
    }

    inner class SearchTagsViewHolder(view: View) : SearchChildViewHolder(view) {
        override fun bind(item: SearchItem) {
            val tag = item.tag!!
            setOnChildCheckedListener(this@SearchListAdapter)
            itemView.check_search_item_tag.text = tag.name
        }

        override fun getCheckable(): Checkable = itemView.check_search_item_tag
    }

    inner class SearchCategoriesViewHolder(view: View) : SearchChildViewHolder(view) {
        override fun bind(item: SearchItem) {
            val category = item.category!!
            setOnChildCheckedListener(this@SearchListAdapter)
            itemView.check_search_item_category.text = category.name
            itemView.view_category_color.setBackgroundColor(category.color)
        }

        override fun getCheckable(): Checkable = itemView.check_search_item_category
    }

    inner class SearchLocationsViewHolder(view: View) : SearchChildViewHolder(view) {
        override fun bind(item: SearchItem) {
            val location = item.location!!
            setOnChildCheckedListener(this@SearchListAdapter)
            itemView.check_search_item_location.text = location.name
        }

        override fun getCheckable(): Checkable = itemView.check_search_item_location
    }

    inner class SearchMoodsViewHolder(view: View) : SearchChildViewHolder(view) {
        override fun bind(item: SearchItem) {
            val mood = item.mood!!
            setOnChildCheckedListener(this@SearchListAdapter)
            itemView.check_search_item_mood.text = mood.name
            val smile = itemView.context.resources
                    .getIdentifier(mood.iconName, "drawable", itemView.context.packageName)
            itemView.check_search_item_mood.setCompoundDrawablesWithIntrinsicBounds(smile, 0, 0, 0)
        }

        override fun getCheckable(): Checkable = itemView.check_search_item_mood
    }

    inner class SearchNoTagsViewHolder(view: View) : SearchChildViewHolder(view) {
        override fun bind(item: SearchItem) {
            setOnChildCheckedListener(this@SearchListAdapter)
            itemView.check_search_no_tags.text =
                    itemView.context.getString(R.string.fragment_drawer_menu_no_tags)
        }

        override fun getCheckable(): Checkable = itemView.check_search_no_tags
    }

    inner class SearchNoCategoryViewHolder(view: View) : SearchChildViewHolder(view) {
        override fun bind(item: SearchItem) {
            setOnChildCheckedListener(this@SearchListAdapter)
            itemView.check_search_no_category.text =
                    itemView.context.getString(R.string.fragment_drawer_menu_no_category)
        }

        override fun getCheckable(): Checkable = itemView.check_search_no_category
    }

    inner class SearchNoMoodViewHolder(view: View) : SearchChildViewHolder(view) {
        override fun bind(item: SearchItem) {
            setOnChildCheckedListener(this@SearchListAdapter)
            itemView.check_search_no_mood.text =
                    itemView.context.getString(R.string.fragment_drawer_menu_no_mood)
        }

        override fun getCheckable(): Checkable = itemView.check_search_no_mood
    }

    inner class SearchNoLocationViewHolder(view: View) : SearchChildViewHolder(view) {
        override fun bind(item: SearchItem) {
            setOnChildCheckedListener(this@SearchListAdapter)
            itemView.check_search_no_location.text =
                    itemView.context.getString(R.string.fragment_drawer_menu_no_location)
        }

        override fun getCheckable(): Checkable = itemView.check_search_no_location
    }

    interface OnSearchListInteractionListener {
        fun onTagCheckStateChange(tag: MyTag, checked: Boolean)
        fun onNoTagsCheckStateChange(checked: Boolean)
        fun onCategoryCheckStateChange(category: MyCategory, checked: Boolean)
        fun onNoCategoryCheckStateChange(checked: Boolean)
        fun onLocationCheckStateChange(location: MyLocation, checked: Boolean)
        fun onNoLocationChackStateChange(checked: Boolean)
        fun onMoodCheckStateChange(mood: MyMood, checked: Boolean)
        fun onNoMoodCheckStateChange(checked: Boolean)
        fun onSearchDatesSelected(startDate: Long?, endDate: Long?)
        fun onCheckCleared()
        fun onFirstCheck()
    }
}