package com.ps.tokky.utils

import android.annotation.SuppressLint
import android.content.Intent
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.shape.ShapeAppearanceModel
import com.ps.tokky.R
import com.ps.tokky.activities.EnterKeyDetailsActivity
import com.ps.tokky.databinding.RvAuthCardBinding
import com.ps.tokky.databinding.RvAuthCardHeaderBinding
import com.ps.tokky.models.TokenEntry
import java.util.*

class TokenAdapter(
    private val context: AppCompatActivity,
    private val editActivityLauncher: ActivityResultLauncher<Intent>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), TokenViewHolder.Callback {

    private var activeId: String? = null
    private var activeViewHolder: TokenViewHolder? = null

    private val tokensList = ArrayList<GroupedItem>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        if (viewType == HEADER_VIEW) {
            val binding = RvAuthCardHeaderBinding.inflate(layoutInflater, parent, false)
            return TokenHeaderViewHolder(binding)
        }
        val binding = RvAuthCardBinding.inflate(layoutInflater, parent, false)
        return TokenViewHolder(context, binding)
    }

    override fun getItemCount() = tokensList.size

    override fun onBindViewHolder(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == HEADER_VIEW) {
            (viewHolder as TokenHeaderViewHolder).binding.title.text =
                "${tokensList[position].alphabet}"
            return
        }

        val holder = (viewHolder as TokenViewHolder)
        val entry = tokensList[position].item!!
        val isExpanded = entry.id == activeId
        if (isExpanded) activeViewHolder = holder

        holder.bind(entry)
        holder.setCallback(this)
        holder.isExpanded = isExpanded

        setShape(holder.binding.cardView, position)
    }

    override fun getItemViewType(position: Int): Int {
        return if (tokensList[position].isHeader) HEADER_VIEW else CONTENT_VIEW
    }

    private fun setShape(card: MaterialCardView, position: Int) {
        val shapeBuilder = ShapeAppearanceModel.Builder()
        val radius = context.resources.getDimension(R.dimen.item_radius)

        val prevItemViewType = if (position > 0) getItemViewType(position - 1) else -1
        val nextItemViewType =
            if (position < tokensList.size - 1) getItemViewType(position + 1) else -1

        val isSingle = prevItemViewType == HEADER_VIEW &&
                (nextItemViewType == HEADER_VIEW || position == tokensList.size - 1)
        val isTop = prevItemViewType == HEADER_VIEW
        val isBottom = prevItemViewType != HEADER_VIEW && nextItemViewType == HEADER_VIEW
        val isLastItem = position == tokensList.size - 1

        when {
            isSingle -> shapeBuilder.setAllCornerSizes(radius)
            isTop -> shapeBuilder.setTopLeftCornerSize(radius)
                .setTopRightCornerSize(radius)

            isLastItem || isBottom -> shapeBuilder.setBottomLeftCornerSize(radius)
                .setBottomRightCornerSize(radius)
        }
        card.shapeAppearanceModel = shapeBuilder.build()
    }

    private fun getGroupedList(list: List<TokenEntry>): ArrayList<GroupedItem> {
        val groupedList = ArrayList<GroupedItem>()
        var lastHeader: String? = ""
        for (element in list) {
            val user: TokenEntry = element
            val header = user.issuer[0].uppercase(Locale.getDefault())
            if (!TextUtils.equals(lastHeader, header)) {
                lastHeader = header
                groupedList.add(GroupedItem(null, lastHeader, true))
            }
            groupedList.add(GroupedItem(user))
        }
        return groupedList
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateEntries(list: List<TokenEntry>) {
        tokensList.clear()
        if (GROUP_LIST) {
            tokensList.addAll(getGroupedList(list.sortedBy { it.name.uppercase() }))
        } else {
            val sortedList = list.sortedBy { it.name }.map { GroupedItem(it) }
            tokensList.addAll(sortedList)
        }

        notifyDataSetChanged()
    }

    override fun onExpand(viewHolder: TokenViewHolder, id: String) {
        when (activeId) {
            id -> {
                viewHolder.isExpanded = false
                activeId = null
                activeViewHolder = null
            }

            else -> {
                activeViewHolder?.isExpanded = false
                activeId = id
                activeViewHolder = viewHolder
                viewHolder.isExpanded = true
            }
        }
    }

    override fun onEdit(entry: TokenEntry, position: Int) {
        val intent = Intent(context, EnterKeyDetailsActivity::class.java).apply {
            putExtra("id", entry.id)
        }
        editActivityLauncher.launch(intent)
    }

    fun onResume() {}

    fun onPause() {}

    data class GroupedItem(
        val item: TokenEntry? = null,
        val alphabet: String? = null,
        val isHeader: Boolean = false
    )

    companion object {
        private const val HEADER_VIEW = 0
        private const val CONTENT_VIEW = 1

        private const val GROUP_LIST = true
    }
}