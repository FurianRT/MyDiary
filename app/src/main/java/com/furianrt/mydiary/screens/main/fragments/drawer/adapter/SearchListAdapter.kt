package com.furianrt.mydiary.screens.main.fragments.drawer.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyCategory
import com.furianrt.mydiary.data.model.MyLocation
import com.furianrt.mydiary.data.model.MyMood
import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.screens.main.fragments.drawer.adapter.SearchListAdapter.SearchChildViewHolder
import com.furianrt.mydiary.screens.main.fragments.drawer.adapter.SearchListAdapter.SearchGroupViewHolder
import com.thoughtbot.expandablecheckrecyclerview.listeners.OnChildCheckChangedListener
import com.thoughtbot.expandablecheckrecyclerview.viewholders.CheckableChildViewHolder
import com.thoughtbot.expandablerecyclerview.MultiTypeExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import kotlinx.android.synthetic.main.nav_search_group.view.*
import kotlinx.android.synthetic.main.nav_search_item_category.view.*
import kotlinx.android.synthetic.main.nav_search_item_location.view.*
import kotlinx.android.synthetic.main.nav_search_item_mood.view.*
import kotlinx.android.synthetic.main.nav_search_item_no_category.view.*
import kotlinx.android.synthetic.main.nav_search_item_no_location.view.*
import kotlinx.android.synthetic.main.nav_search_item_no_mood.view.*
import kotlinx.android.synthetic.main.nav_search_item_no_tags.view.*
import kotlinx.android.synthetic.main.nav_search_item_tag.view.*

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
        private const val BUNDLE_SELECTED_LOCATION_IDS = "selected_location_ids"
        private const val BUNDLE_SELECTED_ITEM_NON_TYPES = "selected_item_non_types"
    }

    private val mExpandedGroupTypes = HashSet<Int>()
    private val mSelectedTagIds = HashSet<String>()
    private val mSelectedCategoryIds = HashSet<String>()
    private val mSelectedMoodIds = HashSet<Int>()
    private val mSelectedLocationIds = HashSet<String>()
    private val mSelectedNoItemTypes = HashSet<Int>()

    override fun onSaveInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            it.putIntegerArrayList(BUNDLE_EXPANDED_GROUP_TYPES, ArrayList(mExpandedGroupTypes))
            it.putStringArrayList(BUNDLE_SELECTED_TAG_IDS, ArrayList(mSelectedTagIds))
            it.putStringArrayList(BUNDLE_SELECTED_CATEGORY_IDS, ArrayList(mSelectedCategoryIds))
            it.putIntegerArrayList(BUNDLE_SELECTED_MOOD_IDS, ArrayList(mSelectedMoodIds))
            it.putStringArrayList(BUNDLE_SELECTED_LOCATION_IDS, ArrayList(mSelectedLocationIds))
            it.putIntegerArrayList(BUNDLE_SELECTED_ITEM_NON_TYPES, ArrayList(mSelectedNoItemTypes))
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            mExpandedGroupTypes.clear()
            mExpandedGroupTypes.addAll(it.getIntegerArrayList(BUNDLE_EXPANDED_GROUP_TYPES)
                    ?: emptyList())

            mSelectedTagIds.clear()
            mSelectedTagIds.addAll(it.getStringArrayList(BUNDLE_SELECTED_TAG_IDS) ?: emptyList())

            mSelectedCategoryIds.clear()
            mSelectedCategoryIds.addAll(it.getStringArrayList(BUNDLE_SELECTED_CATEGORY_IDS)
                    ?: emptyList())

            mSelectedMoodIds.clear()
            mSelectedMoodIds.addAll(it.getIntegerArrayList(BUNDLE_SELECTED_MOOD_IDS) ?: emptyList())

            mSelectedLocationIds.clear()
            mSelectedLocationIds.addAll(it.getStringArrayList(BUNDLE_SELECTED_LOCATION_IDS)
                    ?: emptyList())

            mSelectedNoItemTypes.clear()
            mSelectedNoItemTypes.addAll(it.getIntegerArrayList(BUNDLE_SELECTED_ITEM_NON_TYPES)
                    ?: emptyList())
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
        mSelectedLocationIds.clear()
        mSelectedNoItemTypes.clear()

        //only update the child views that are visible (i.e. their group is expanded)
        for (i in 0 until groups.size) {
            val group = groups[i]
            if (isGroupExpanded(group)) {
                notifyItemRangeChanged(expandableList.getFlattenedFirstChildIndex(i), group.itemCount)
            }
        }
        listener?.onCheckCleared()
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
            SearchItem.TYPE_TAG ->
                holder?.onBindViewHolder(flatPosition, mSelectedTagIds.contains(item.tag!!.id))
            SearchItem.TYPE_CATEGORY ->
                holder?.onBindViewHolder(flatPosition, mSelectedCategoryIds.contains(item.category!!.id))
            SearchItem.TYPE_LOCATION ->
                holder?.onBindViewHolder(flatPosition, mSelectedLocationIds.contains(item.location!!.noteId))
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

    override fun isGroup(viewType: Int): Boolean =
            viewType == SearchGroup.TYPE_TAG
                    || viewType == SearchGroup.TYPE_CATEGORY
                    || viewType == SearchGroup.TYPE_LOCATION
                    || viewType == SearchGroup.TYPE_MOOD

    override fun isChild(viewType: Int): Boolean =
            viewType == SearchItem.TYPE_TAG
                    || viewType == SearchItem.TYPE_CATEGORY
                    || viewType == SearchItem.TYPE_LOCATION
                    || viewType == SearchItem.TYPE_MOOD
                    || viewType == SearchItem.TYPE_NO_TAGS
                    || viewType == SearchItem.TYPE_NO_CATEGORY
                    || viewType == SearchItem.TYPE_NO_MOOD
                    || viewType == SearchItem.TYPE_NO_LOCATION

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
                    mSelectedLocationIds.add(item.location!!.noteId)
                    isFirstCheck()
                } else {
                    mSelectedLocationIds.remove(item.location!!.noteId)
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
                && mSelectedLocationIds.isEmpty()
                && mSelectedNoItemTypes.isEmpty()) {
            listener?.onCheckCleared()
        }
    }

    private fun isFirstCheck() {
        val checkCount = mSelectedTagIds.size + mSelectedCategoryIds.size + mSelectedMoodIds.size +
                mSelectedLocationIds.size + mSelectedNoItemTypes.size
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
        fun onCheckCleared()
        fun onFirstCheck()
    }
}