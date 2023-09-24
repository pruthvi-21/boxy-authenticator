package com.ps.tokky.utils

import android.content.Context
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ps.tokky.databinding.RvAuthCardBinding
import com.ps.tokky.models.TokenEntry

class TokenViewHolder(
    val context: Context,
    val binding: RvAuthCardBinding
) : RecyclerView.ViewHolder(binding.root) {

    private var listener: Callback? = null
    private var entry: TokenEntry? = null

    private val handler = Handler(Looper.getMainLooper())
    private val handlerTask: Runnable = object : Runnable {
        override fun run() {
            entry?.progressPercent?.toInt()?.let { binding.progressBar.setProgress(it) }
            handler.postDelayed(this, 1000)
        }
    }

    fun bind(entry: TokenEntry, inEditMode: Boolean) {
        this.entry = entry
        binding.issuerLabel.text = entry.issuer
        if (entry.label.isNotEmpty()) {
            binding.accountLabel.visibility = View.VISIBLE
            binding.accountLabel.text = entry.label
        } else {
            binding.accountLabel.visibility = View.GONE
        }

        if (inEditMode) {
            binding.edit.visibility = View.VISIBLE
            binding.arrow.visibility = View.GONE

            isExpanded = false

            binding.cardView.setOnClickListener(null)
            binding.edit.setOnClickListener {
                listener?.onEdit(entry, adapterPosition)
            }

            return
        }

        binding.edit.visibility = View.GONE
        binding.arrow.visibility = View.VISIBLE

        setThumbnail()

        binding.otpHolder.typeface = Typeface.MONOSPACE
        updateOTP()

        binding.progressBar.setMax(entry.period)
        binding.cardView.setOnClickListener {
            listener?.onExpand(this, adapterPosition, !isExpanded)
        }

        binding.otpHolder.setOnLongClickListener {
            Utils.copyToClipboard(context, entry.otpFormattedString)
            true
        }
    }

    private fun setThumbnail() {
        entry ?: return
        if (entry!!.thumbnailIcon.isEmpty()) {
            binding.thumbnailFrame.visibility = View.VISIBLE
            binding.initialsView.visibility = View.VISIBLE
            binding.thumbnail.setBackgroundColor(entry!!.thumbnailColor)
            binding.thumbnail.setImageBitmap(null)
            binding.initialsView.text = entry!!.issuer.getInitials()
        } else {
            val fileName = entry!!.thumbnailIcon
            val logoBitmap = Utils.getThumbnailFromAssets(context.assets, fileName)
            Glide.with(context).load(logoBitmap).into(binding.thumbnail)
            binding.initialsView.visibility = View.GONE
        }
    }

    var isExpanded = false
        set(value) {
            field = value

            if (field) {
                updateOTP()
                handler.post(handlerTask)
            } else {
                handler.removeCallbacks(handlerTask)
            }

            binding.cardHiddenLayout.visibility = if (field) View.VISIBLE else View.GONE

            binding.arrow.animate()
                .rotation(if (value) 180f else 0f)
                .setDuration(200)
                .start()
        }

    fun setCallback(listener: Callback?) {
        this.listener = listener
    }

    fun updateOTP() {
        entry ?: return
        binding.otpHolder.text = entry!!.otpFormattedSpan
    }

    interface Callback {
        fun onExpand(vh: TokenViewHolder, adapterPosition: Int, expanded: Boolean)
        fun onEdit(entry: TokenEntry, position: Int)
    }

}
