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
import com.thoughtbot.expandablecheckrecyclerview.ChildCheckController
import com.thoughtbot.expandablecheckrecyclerview.listeners.OnChildCheckChangedListener
import com.thoughtbot.expandablecheckrecyclerview.listeners.OnChildrenCheckStateChangedListener
import com.thoughtbot.expandablecheckrecyclerview.viewholders.CheckableChildViewHolder
import com.thoughtbot.expandablerecyclerview.MultiTypeExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import kotlinx.android.synthetic.main.nav_search_group.view.*
import kotlinx.android.synthetic.main.nav_search_item_category.view.*
import kotlinx.android.synthetic.main.nav_search_item_location.view.*
import kotlinx.android.synthetic.main.nav_search_item_mood.view.*
import kotlinx.android.synthetic.main.nav_search_item_tag.view.*

class SearchListAdapter(
        groupList: List<SearchGroup> = emptyList(),
        private var listener: OnSearchListInteractionListener? = null
) : MultiTypeExpandableRecyclerViewAdapter<SearchGroupViewHolder, SearchChildViewHolder>(groupList),
        OnChildCheckChangedListener, OnChildrenCheckStateChangedListener {

    companion object {
        private const val ANIM_ARROW_RATATION_DURATION = 350L
        private const val CHECKED_STATE_MAP = "checked_state_map"

    }

    private val mChildCheckController = ChildCheckController(expandableList, this)

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
                        .apply { setOnChildCheckedListener(this@SearchListAdapter) }
            SearchItem.TYPE_CATEGORY ->
                SearchCategoriesViewHolder(inflater.inflate(R.layout.nav_search_item_category, parent, false))
                        .apply { setOnChildCheckedListener(this@SearchListAdapter) }
            SearchItem.TYPE_LOCATION ->
                SearchLocationsViewHolder(inflater.inflate(R.layout.nav_search_item_location, parent, false))
                        .apply { setOnChildCheckedListener(this@SearchListAdapter) }
            SearchItem.TYPE_MOOD ->
                SearchMoodsViewHolder(inflater.inflate(R.layout.nav_search_item_mood, parent, false))
                        .apply { setOnChildCheckedListener(this@SearchListAdapter) }
            else -> throw IllegalStateException("Wrong view type")
        }
    }

    override fun onBindGroupViewHolder(holder: SearchGroupViewHolder?, flatPosition: Int, group: ExpandableGroup<*>?) {
        holder?.bind(group as SearchGroup)
    }

    override fun onBindChildViewHolder(holder: SearchChildViewHolder?, flatPosition: Int, group: ExpandableGroup<*>?, childIndex: Int) {
        val item = (group as SearchGroup).groupItems[childIndex]
        when (item.type) {
            SearchItem.TYPE_TAG ->
                with(holder as SearchTagsViewHolder) {
                    val listPosition = expandableList.getUnflattenedPosition(flatPosition)
                    onBindViewHolder(flatPosition, mChildCheckController.isChildChecked(listPosition))
                    bind(item)
                }
            SearchItem.TYPE_CATEGORY ->
                with(holder as SearchCategoriesViewHolder) {
                    val listPosition = expandableList.getUnflattenedPosition(flatPosition)
                    onBindViewHolder(flatPosition, mChildCheckController.isChildChecked(listPosition))
                    bind(item)
                }
            SearchItem.TYPE_LOCATION ->
                with(holder as SearchLocationsViewHolder) {
                    val listPosition = expandableList.getUnflattenedPosition(flatPosition)
                    onBindViewHolder(flatPosition, mChildCheckController.isChildChecked(listPosition))
                    bind(item)
                }
            SearchItem.TYPE_MOOD ->
                with(holder as SearchMoodsViewHolder) {
                    val listPosition = expandableList.getUnflattenedPosition(flatPosition)
                    onBindViewHolder(flatPosition, mChildCheckController.isChildChecked(listPosition))
                    bind(item)
                }
        }
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

    override fun onChildCheckChanged(view: View?, checked: Boolean, flatPos: Int) {
        val listPos = expandableList.getUnflattenedPosition(flatPos)
        mChildCheckController.onChildCheckChanged(checked, listPos)
        listener?.let {
            val item = (expandableList.getExpandableGroup(listPos) as SearchGroup).groupItems[listPos.childPos]
            when (item.type) {
                SearchItem.TYPE_TAG -> it.onTagChackStateChange(item.tag!!, checked)
                SearchItem.TYPE_CATEGORY -> it.onCategoryChackStateChange(item.category!!, checked)
                SearchItem.TYPE_LOCATION -> it.onLocationChackStateChange(item.location!!, checked)
                SearchItem.TYPE_MOOD -> it.onMoodChackStateChange(item.mood!!, checked)
            }
        }
    }

    override fun updateChildrenCheckState(firstChildFlattenedIndex: Int, numChildren: Int) {
        notifyItemRangeChanged(firstChildFlattenedIndex, numChildren)
    }

    override fun onSaveInstanceState(savedInstanceState: Bundle?) {
        savedInstanceState?.putParcelableArrayList(CHECKED_STATE_MAP, ArrayList(expandableList.groups))
        super.onSaveInstanceState(savedInstanceState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        if (savedInstanceState == null || !savedInstanceState.containsKey(CHECKED_STATE_MAP)) {
            return
        }
        expandableList.groups = savedInstanceState.getParcelableArrayList<ExpandableGroup<MyCategory>>(CHECKED_STATE_MAP)
        super.onRestoreInstanceState(savedInstanceState)
    }

    inner class SearchGroupViewHolder(view: View) : GroupViewHolder(view) {
        fun bind(group: SearchGroup) {
            itemView.text_search_group_name.text = group.groupTitle

            val position = expandableList.getUnflattenedPosition(layoutPosition).groupPos
            if (expandableList.expandedGroupIndexes[position]) {
                itemView.image_group_arrow.rotation = 180f
            } else {
                itemView.image_group_arrow.rotation = 0f
            }

            when (group.type) {
                SearchGroup.TYPE_TAG -> itemView.image_group.setImageResource(R.drawable.ic_tag_big)
                SearchGroup.TYPE_CATEGORY -> itemView.image_group.setImageResource(R.drawable.ic_folder_big)
                SearchGroup.TYPE_LOCATION -> itemView.image_group.setImageResource(R.drawable.ic_place_big)
                SearchGroup.TYPE_MOOD -> itemView.image_group.setImageResource(R.drawable.ic_smile)
            }
        }

        override fun expand() {
            itemView.image_group_arrow.animate()
                    .rotation(180f)
                    .setDuration(ANIM_ARROW_RATATION_DURATION)
                    .start()
        }

        override fun collapse() {
            itemView.image_group_arrow.animate()
                    .rotation(0f)
                    .setDuration(ANIM_ARROW_RATATION_DURATION)
                    .start()
        }
    }

    abstract class SearchChildViewHolder(view: View) : CheckableChildViewHolder(view) {
        abstract fun bind(item: SearchItem)
    }

    class SearchTagsViewHolder(view: View) : SearchChildViewHolder(view) {
        override fun bind(item: SearchItem) {
            val tag = item.tag!!
            itemView.check_search_item_tag.text = tag.name
        }

        override fun getCheckable(): Checkable = itemView.check_search_item_tag
    }

    class SearchCategoriesViewHolder(view: View) : SearchChildViewHolder(view) {
        override fun bind(item: SearchItem) {
            val category = item.category!!
            itemView.check_search_item_category.text = category.name
            itemView.view_category_color.setBackgroundColor(category.color)
        }

        override fun getCheckable(): Checkable = itemView.check_search_item_category
    }

    class SearchLocationsViewHolder(view: View) : SearchChildViewHolder(view) {
        override fun bind(item: SearchItem) {
            val location = item.location!!
            itemView.check_search_item_location.text = location.name
        }

        override fun getCheckable(): Checkable = itemView.check_search_item_location
    }

    class SearchMoodsViewHolder(view: View) : SearchChildViewHolder(view) {
        override fun bind(item: SearchItem) {
            val mood = item.mood!!
            itemView.check_search_item_mood.text = mood.name
            val smile = itemView.context.resources
                    .getIdentifier(mood.iconName, "drawable", itemView.context.packageName)
            itemView.check_search_item_mood.setCompoundDrawablesWithIntrinsicBounds(smile, 0, 0, 0)
        }

        override fun getCheckable(): Checkable = itemView.check_search_item_mood
    }

    interface OnSearchListInteractionListener {
        fun onTagChackStateChange(tag: MyTag, checked: Boolean)
        fun onCategoryChackStateChange(category: MyCategory, checked: Boolean)
        fun onLocationChackStateChange(location: MyLocation, checked: Boolean)
        fun onMoodChackStateChange(mood: MyMood, checked: Boolean)
    }
}