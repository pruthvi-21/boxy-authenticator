package com.ps.tokky.utils

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextUtils
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.shape.ShapeAppearanceModel
import com.ps.tokky.R
import com.ps.tokky.activities.EnterKeyDetailsActivity
import com.ps.tokky.activities.MainActivity
import com.ps.tokky.database.DBHelper
import com.ps.tokky.databinding.DialogTitleDeleteWarningBinding
import com.ps.tokky.databinding.RvAuthCardBinding
import com.ps.tokky.databinding.RvAuthCardHeaderBinding
import com.ps.tokky.models.TokenEntry
import java.util.*

class TokenAdapter(
    private val context: AppCompatActivity,
    private val recyclerView: RecyclerView,
    private val editActivityLauncher: ActivityResultLauncher<Intent>
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), TokenViewHolder.Callback {

    private var currentExpanded = -1

    private val handler = Handler(Looper.getMainLooper())
    private val db = DBHelper.getInstance(context)

    private val tokensList = ArrayList<GroupedItem>()

    private val handlerTask = object : Runnable {
        override fun run() {
            checkAndUpdateOTP()
            handler.postDelayed(this, Constants.OTP_GENERATION_REFRESH_INTERVAL)
        }
    }

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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == HEADER_VIEW) {
            (holder as TokenHeaderViewHolder).binding.title.text =
                "${tokensList[position].alphabet}"
            return
        }

        (holder as TokenViewHolder).bind(tokensList[position].item!!, editModeEnabled)
        holder.setCallback(this)

        val prevItemViewType = if (position > 0) getItemViewType(position - 1) else -1
        val nextItemViewType =
            if (position < tokensList.size - 1) getItemViewType(position + 1) else -1
        val isLastItem = position == tokensList.size - 1

        val itemPosition =
            when {
                prevItemViewType == HEADER_VIEW && (nextItemViewType == HEADER_VIEW || position == tokensList.size - 1) -> Position.SINGLE
                prevItemViewType != HEADER_VIEW && nextItemViewType == HEADER_VIEW -> Position.BOTTOM
                prevItemViewType == HEADER_VIEW -> Position.TOP
                isLastItem -> Position.BOTTOM
                else -> Position.MIDDLE
            }

        setShape(holder.binding.cardView, itemPosition)
        setMargin(holder.binding.cardView)
    }

    override fun getItemViewType(position: Int): Int {
        return if (tokensList[position].isHeader) HEADER_VIEW else CONTENT_VIEW
    }

    private fun setShape(card: MaterialCardView, position: Position) {
        val shapeBuilder = ShapeAppearanceModel.Builder()
        val radius = context.resources.getDimension(R.dimen.item_radius)

        if (!editModeEnabled) {
            if (position != Position.SINGLE) {
                if (position == Position.TOP) shapeBuilder.setTopLeftCornerSize(radius)
                    .setTopRightCornerSize(radius)
                if (position == Position.BOTTOM) shapeBuilder.setBottomLeftCornerSize(radius)
                    .setBottomRightCornerSize(radius)
            } else shapeBuilder.setAllCornerSizes(radius)
        }
        card.shapeAppearanceModel = shapeBuilder.build()
    }

    private fun setMargin(card: CardView) {
        (card.layoutParams as RecyclerView.LayoutParams).apply {
            val marginVertical =
                if (editModeEnabled) (context.resources.getDimension(R.dimen.rv_item_margin) / 2).toInt() else 0
            topMargin = marginVertical
            bottomMargin = marginVertical
        }
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

    fun checkAndUpdateOTP() {
        if (editModeEnabled) return
        for (i in tokensList.indices) {
            val item = tokensList[i]
            if (item.isHeader) continue
            val viewHolder = getViewHolderAt(i)
            if (item.item!!.updateOTP() && viewHolder is TokenViewHolder?) viewHolder?.updateOTP()
        }
    }

    private fun getViewHolderAt(pos: Int): RecyclerView.ViewHolder? {
        if (pos < 0 || pos > tokensList.size) return null
        return recyclerView.findViewHolderForAdapterPosition(pos)
    }

    var editModeEnabled: Boolean = false
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
            currentExpanded = -1
        }

    override fun onExpand(vh: TokenViewHolder, adapterPosition: Int, expanded: Boolean) {
        when (currentExpanded) {
            adapterPosition -> {
                vh.isExpanded = false
                currentExpanded = -1
            }
            else -> {
                val viewHolder = getViewHolderAt(currentExpanded)
                if (currentExpanded != -1 && viewHolder is TokenViewHolder?) viewHolder?.isExpanded =
                    false

                currentExpanded = adapterPosition
                vh.isExpanded = true
            }
        }
    }

    override fun onEdit(entry: TokenEntry, position: Int) {
        editActivityLauncher.launch(Intent(context, EnterKeyDetailsActivity::class.java).apply {
            putExtra("id", entry.id)
        })
    }

    override fun onDelete(entry: TokenEntry, position: Int) {
        val titleViewBinding = DialogTitleDeleteWarningBinding.inflate(LayoutInflater.from(context))

        val ssb = SpannableStringBuilder(entry.issuer)
        ssb.setSpan(
            StyleSpan(Typeface.BOLD),
            0,
            entry.issuer.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )

        titleViewBinding.title.text =
            SpannableStringBuilder(context.getString(R.string.dialog_title_delete_token))
                .append(" ")
                .append(ssb)
                .append("?")

        MaterialAlertDialogBuilder(context)
            .setCustomTitle(titleViewBinding.root)
            .setMessage(R.string.dialog_message_delete_token)
            .setPositiveButton(R.string.dialog_remove) { _, _ ->
                entry.id
                db.remove(entry.id)

                //verify and remove header
                var removeHeader = false
                if (tokensList[position - 1].isHeader) {
                    if (position != tokensList.size - 1) {
                        if (tokensList[position + 1].isHeader) {
                            removeHeader = true
                        }
                    } else {
                        removeHeader = true
                    }
                }

                tokensList.removeAt(position)
                notifyItemRemoved(position)

                if (removeHeader) {
                    tokensList.removeAt(position - 1)
                    notifyItemRemoved(position - 1)
                }

                if (tokensList.size == 0 && context is MainActivity) {
                    context.openEditMode(false)
                }
            }
            .setNegativeButton(R.string.dialog_cancel, null)
            .create()
            .show()
    }

    fun onResume() {
        if (!editModeEnabled) handler.post(handlerTask)
    }

    fun onPause() {
        if (!editModeEnabled) handler.removeCallbacks(handlerTask)
    }

    enum class Position {
        TOP, MIDDLE, BOTTOM, SINGLE
    }

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