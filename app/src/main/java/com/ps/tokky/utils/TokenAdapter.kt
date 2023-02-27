package com.ps.tokky.utils

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.shape.ShapeAppearanceModel
import com.ps.tokky.R
import com.ps.tokky.activities.EnterKeyDetailsActivity
import com.ps.tokky.databinding.RvAuthCardBinding
import com.ps.tokky.models.TokenEntry

class TokenAdapter(
    private val context: AppCompatActivity,
    private val list: ArrayList<TokenEntry>,
    private val recyclerView: RecyclerView,
    private val editActivityLauncher: ActivityResultLauncher<Intent>
) : RecyclerView.Adapter<TokenViewHolder>(), TokenViewHolder.Callback {

    private var currentExpanded = -1

    private val handler = Handler(Looper.getMainLooper())
    private val handlerTask = object : Runnable {
        override fun run() {
            checkAndUpdateOTP()
            handler.postDelayed(this, Constants.OTP_GENERATION_REFRESH_INTERVAL)
        }
    }

    private val dbHelper = DBHelper(context)

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
        set(value) {
            field = value
            notifyDataSetChanged()
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

    override fun onEdit(entry: TokenEntry) {
        editActivityLauncher.launch(Intent(context, EnterKeyDetailsActivity::class.java).apply {
            putExtra("obj", entry)
        })
    }

    override fun onDelete(entry: TokenEntry, position: Int) {
        dbHelper.removeEntry(entry)
        list.removeAt(position)
        notifyItemRemoved(position)
    }

    fun onResume() {
        if (!editModeEnabled) handler.post(handlerTask)
    }

    fun onPause() {
        if (!editModeEnabled) handler.removeCallbacks(handlerTask)
    }
}