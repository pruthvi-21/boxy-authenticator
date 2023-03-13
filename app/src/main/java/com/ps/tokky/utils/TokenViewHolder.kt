package com.ps.tokky.utils

import android.content.Context
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ps.tokky.databinding.RvAuthCardBinding
import com.ps.tokky.models.Thumbnails
import com.ps.tokky.models.TokenEntry

class TokenViewHolder(
    val context: Context, val binding: RvAuthCardBinding
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

    fun bind(entry: TokenEntry) {
        this.entry = entry
        binding.issuerLabel.text = entry.issuer
        if (entry.label.isNotEmpty()) {
            binding.accountLabel.visibility = View.VISIBLE
            binding.accountLabel.text = entry.label
        } else {
            binding.accountLabel.visibility = View.GONE
        }

        generateIcon()

        if (editModeEnabled) {
            binding.arrow.visibility = View.GONE
            binding.cardHiddenLayout.visibility = View.GONE
            isExpanded = false

            binding.edit.setOnClickListener {
                listener?.onEdit(entry, adapterPosition)
            }

            binding.delete.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    listener?.onDelete(entry, adapterPosition)
                }
            }
            return
        }

        binding.arrow.visibility = View.VISIBLE
        binding.otpHolder.typeface = Typeface.MONOSPACE
        updateOTP()

        binding.progressBar.setMax(entry.period)
        binding.cardView.setOnClickListener {
            if (editModeEnabled) return@setOnClickListener
            listener?.onExpand(this, adapterPosition, !isExpanded)
        }

        binding.otpHolder.setOnLongClickListener {
            Utils.copyToClipboard(context, entry.otpFormattedString)
            true
        }
    }

    private fun generateIcon() {
        if (entry == null) return

        val thumb = Thumbnails.values().find { it.name.lowercase() == entry!!.issuer.replace(" ", "").lowercase() }
        if (thumb != null) {
            binding.thumbnail.setImageResource(thumb.icon)
            return
        }

        val drawable =
            LetterBitmap(context).getLetterTile(entry?.issuer + entry?.label) //appending label for different color if same issuer name
        binding.thumbnail.setImageBitmap(drawable)
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

            binding.arrow.animate().rotation(if (value) 180f else 0f).setDuration(200).start()
        }

    var editModeEnabled = false
        set(value) {
            field = value

            if (value) {
                isExpanded = false
            }

            binding.arrow.visibility = if (value) View.GONE else View.VISIBLE
            binding.edit.visibility = if (value) View.VISIBLE else View.GONE
            binding.delete.visibility = binding.edit.visibility
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
        fun onDelete(entry: TokenEntry, position: Int)
    }
}
