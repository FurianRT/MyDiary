package com.furianrt.mydiary.screens.main.adapters.searchlist

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Checkable
import com.furianrt.mydiary.R
import com.furianrt.mydiary.data.model.MyCategory
import com.furianrt.mydiary.data.model.MyLocation
import com.furianrt.mydiary.data.model.MyMood
import com.furianrt.mydiary.data.model.MyTag
import com.furianrt.mydiary.screens.main.adapters.searchlist.SearchListAdapter.SearchChildViewHolder
import com.furianrt.mydiary.screens.main.adapters.searchlist.SearchListAdapter.SearchGroupViewHolder
import com.thoughtbot.expandablecheckrecyclerview.viewholders.CheckableChildViewHolder
import com.thoughtbot.expandablerecyclerview.MultiTypeExpandableRecyclerViewAdapter
import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup
import com.thoughtbot.expandablerecyclerview.viewholders.GroupViewHolder
import kotlinx.android.synthetic.main.nav_header_search_group.view.*
import kotlinx.android.synthetic.main.nav_header_search_item.view.*

class SearchListAdapter(
        groupList: List<SearchGroup>,
        var listener: OnSearchListInteractionListener? = null
) : MultiTypeExpandableRecyclerViewAdapter<SearchGroupViewHolder, SearchChildViewHolder>(groupList) {

    override fun onCreateGroupViewHolder(parent: ViewGroup?, viewType: Int): SearchGroupViewHolder {
        val view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.nav_header_search_group, parent, false)
        return SearchGroupViewHolder(view)
    }

    override fun onCreateChildViewHolder(parent: ViewGroup?, viewType: Int): SearchChildViewHolder {
        val view = LayoutInflater.from(parent?.context)
                .inflate(R.layout.nav_header_search_item, parent, false)
        return when (viewType) {
            SearchItem.TYPE_TAG -> SearchTagsViewHolder(view)
            SearchItem.TYPE_CATEGORY -> SearchCategoriesViewHolder(view)
            SearchItem.TYPE_LOCATION -> SearchLocationsViewHolder(view)
            SearchItem.TYPE_MOOD -> SearchMoodsViewHolder(view)
            else -> throw IllegalStateException("Wrong view tyoe")
        }
    }

    override fun onBindGroupViewHolder(holder: SearchGroupViewHolder?, flatPosition: Int, group: ExpandableGroup<*>?) {
        holder?.bind(group as SearchGroup)
    }

    override fun onBindChildViewHolder(holder: SearchChildViewHolder?, flatPosition: Int, group: ExpandableGroup<*>?, childIndex: Int) {
        val item = (group as SearchGroup).groupItems[childIndex]
        when (item.type) {
            SearchItem.TYPE_TAG -> (holder as SearchTagsViewHolder).bind(item)
            SearchItem.TYPE_CATEGORY -> (holder as SearchCategoriesViewHolder).bind(item)
            SearchItem.TYPE_LOCATION -> (holder as SearchLocationsViewHolder).bind(item)
            SearchItem.TYPE_MOOD -> (holder as SearchMoodsViewHolder).bind(item)
        }
    }

    override fun getChildViewType(position: Int, group: ExpandableGroup<*>?, childIndex: Int): Int =
            (group as SearchGroup).groupItems[childIndex].type

    override fun isChild(viewType: Int): Boolean =
            viewType == SearchItem.TYPE_TAG
                    || viewType == SearchItem.TYPE_CATEGORY
                    || viewType == SearchItem.TYPE_LOCATION
                    || viewType == SearchItem.TYPE_MOOD


    class SearchGroupViewHolder(view: View) : GroupViewHolder(view) {
        fun bind(group: SearchGroup) {
            itemView.text_search_group_name.text = group.groupTitle
        }
    }

    abstract class SearchChildViewHolder(view: View) : CheckableChildViewHolder(view) {
        abstract fun bind(item: SearchItem)
        override fun getCheckable(): Checkable = itemView.check_search_item
    }

    inner class SearchTagsViewHolder(view: View) : SearchChildViewHolder(view) {
        override fun bind(item: SearchItem) {
            val tag = item.tag!!
            itemView.check_search_item.setOnClickListener {
                listener?.onTagChackStateChange(tag, (it as Checkable).isChecked)
            }
            itemView.check_search_item.text = tag.name
        }
    }

    inner class SearchCategoriesViewHolder(view: View) : SearchChildViewHolder(view) {
        override fun bind(item: SearchItem) {
            val category = item.category!!
            itemView.check_search_item.setOnClickListener {
                listener?.onCategoryChackStateChange(category, (it as Checkable).isChecked)
            }
            itemView.check_search_item.text = category.name
        }
    }

    inner class SearchLocationsViewHolder(view: View) : SearchChildViewHolder(view) {
        override fun bind(item: SearchItem) {
            val location = item.location!!
            itemView.check_search_item.setOnClickListener {
                listener?.onLocationChackStateChange(location, (it as Checkable).isChecked)
            }
            itemView.check_search_item.text = location.name
        }
    }

    inner class SearchMoodsViewHolder(view: View) : SearchChildViewHolder(view) {
        override fun bind(item: SearchItem) {
            val mood = item.mood!!
            itemView.check_search_item.setOnClickListener {
                listener?.onMoodChackStateChange(mood, (it as Checkable).isChecked)
            }
            itemView.check_search_item.text = mood.name
        }
    }

    interface OnSearchListInteractionListener {
        fun onTagChackStateChange(tag: MyTag, checked: Boolean)
        fun onCategoryChackStateChange(category: MyCategory, checked: Boolean)
        fun onLocationChackStateChange(location: MyLocation, checked: Boolean)
        fun onMoodChackStateChange(mood: MyMood, checked: Boolean)
    }
}