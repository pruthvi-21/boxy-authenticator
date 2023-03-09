package com.ps.tokky.utils

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.shape.ShapeAppearanceModel
import com.ps.tokky.R
import com.ps.tokky.activities.EnterKeyDetailsActivity
import com.ps.tokky.activities.MainActivity
import com.ps.tokky.databinding.DialogTitleDeleteWarningBinding
import com.ps.tokky.databinding.RvAuthCardBinding
import com.ps.tokky.models.TokenEntry
import com.ps.tokky.utils.Constants.KEY_LIST_ORDER
import java.util.*

class TokenAdapter(
    private val context: AppCompatActivity,
    private val list: ArrayList<TokenEntry>,
    private val recyclerView: RecyclerView,
    private val editActivityLauncher: ActivityResultLauncher<Intent>
) : RecyclerView.Adapter<TokenViewHolder>(), TokenViewHolder.Callback {

    private var currentExpanded = -1

    private val handler = Handler(Looper.getMainLooper())
    private val dbHelper = DBHelper.getInstance(context)
    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    private val handlerTask = object : Runnable {
        override fun run() {
            checkAndUpdateOTP()
            handler.postDelayed(this, Constants.OTP_GENERATION_REFRESH_INTERVAL)
        }
    }
    private val itemTouchHelper by lazy {
        val callback = object : ItemTouchHelper.Callback() {
            override fun getMovementFlags(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ): Int {
                return makeMovementFlags(
                    ItemTouchHelper.UP or ItemTouchHelper.DOWN,
                    0
                )
            }

            override fun onMove(
                recyclerView: RecyclerView,
                source: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val fromPosition = source.adapterPosition
                val toPosition = target.adapterPosition

                Collections.swap(list, fromPosition, toPosition)
                notifyItemMoved(fromPosition, toPosition)
                saveListOrder()

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            }

        }
        ItemTouchHelper(callback)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TokenViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = RvAuthCardBinding.inflate(layoutInflater, parent, false)

        return TokenViewHolder(context, binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: TokenViewHolder, position: Int) {
        holder.editModeEnabled = editModeEnabled
        holder.bind(list[position])

        holder.setCallback(this)
        setShape(holder.binding.cardView, position)
        setMargin(holder.binding.cardView)
    }

    private fun setShape(card: MaterialCardView, position: Int) {
        val shapeBuilder = ShapeAppearanceModel.Builder()
        val radius = context.resources.getDimension(R.dimen.radius_large)

        if (!editModeEnabled) {
            if (list.size != 1) {
                if (position == 0) shapeBuilder.setTopLeftCornerSize(radius).setTopRightCornerSize(radius)
                if (position == list.size - 1) shapeBuilder.setBottomLeftCornerSize(radius).setBottomRightCornerSize(radius)
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

    @SuppressLint("NotifyDataSetChanged")
    fun updateEntries(list: List<TokenEntry>) {
        this.list.clear()
        this.list.addAll(list)

        setListOrder()

        notifyDataSetChanged()
    }

    fun checkAndUpdateOTP() {
        if (editModeEnabled) return
        for (i in list.indices) {
            val item = list[i]
            if (item.updateOTP()) getViewHolderAt(i)?.updateOTP()
        }
    }

    private fun getViewHolderAt(pos: Int): TokenViewHolder? {
        if (pos < 0 || pos > list.size) return null
        return recyclerView.findViewHolderForAdapterPosition(pos) as TokenViewHolder?
    }

    var editModeEnabled: Boolean = false
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
            itemTouchHelper.attachToRecyclerView(if (value) this.recyclerView else null)
        }

    override fun onExpand(vh: TokenViewHolder, adapterPosition: Int, expanded: Boolean) {
        when (currentExpanded) {
            adapterPosition -> {
                vh.isExpanded = false
                currentExpanded = -1
            }
            else -> {
                if (currentExpanded != -1) getViewHolderAt(currentExpanded)?.isExpanded = false

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
        ssb.setSpan(StyleSpan(Typeface.BOLD), 0, entry.issuer.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        titleViewBinding.title.text = SpannableStringBuilder(context.getString(R.string.dialog_title_delete_token))
            .append(" ")
            .append(ssb)
            .append("?")

        MaterialAlertDialogBuilder(context)
            .setCustomTitle(titleViewBinding.root)
            .setMessage(R.string.dialog_message_delete_token)
            .setPositiveButton(R.string.dialog_remove) { _, _ ->
                dbHelper.removeEntry(entry.id)
                list.removeAt(position)

                saveListOrder()

                notifyItemRemoved(position)

                if (list.size == 0 && context is MainActivity) {
                    context.openEditMode(false, updateUI = true)
                }
            }
            .setNegativeButton(R.string.dialog_cancel, null)
            .create()
            .show()
    }

    private fun setListOrder() {
        val listOrderStr = sharedPreferences.getString(KEY_LIST_ORDER, "")
        val listOrder = listOrderStr?.split(",")?.toList()

        if (listOrder?.isNotEmpty() == true) {
            val sortedThings = list.sortedWith { a, b ->
                listOrder.indexOf(a.id).compareTo(listOrder.indexOf(b.id))
            }

            this.list.clear()
            this.list.addAll(sortedThings)
        }
    }

    private fun saveListOrder() {
        val ids = ArrayList<String>()
        list.forEach { ids.add(it.id!!) }
        sharedPreferences.edit().putString(KEY_LIST_ORDER, ids.joinToString(",")).apply()
    }

    fun onResume() {
        if (!editModeEnabled) handler.post(handlerTask)
    }

    fun onPause() {
        if (!editModeEnabled) handler.removeCallbacks(handlerTask)
    }

    fun addToken(token: TokenEntry?) {
        token ?: return
        list.add(token)
        notifyItemInserted(list.indexOf(token))
        saveListOrder()
    }
}