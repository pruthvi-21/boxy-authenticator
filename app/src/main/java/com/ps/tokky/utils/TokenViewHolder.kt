package com.ps.tokky.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ps.tokky.databinding.RvAuthCardBinding
import com.ps.tokky.models.TokenEntry

class TokenViewHolder(
    val context: Context,
    val binding: RvAuthCardBinding
) : RecyclerView.ViewHolder(binding.root) {

    private val preferences = AppPreferences.getInstance(context)

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
            binding.thumbnailFrame.visibility = View.GONE
            binding.arrow.visibility = View.GONE
            binding.edit.visibility = View.VISIBLE
            binding.delete.visibility = View.VISIBLE

            isExpanded = false

            binding.cardView.setOnClickListener(null)
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

        binding.thumbnailFrame.visibility = View.VISIBLE
        binding.arrow.visibility = View.VISIBLE
        binding.edit.visibility = View.GONE
        binding.delete.visibility = View.GONE

        setThumbnail(entry.issuer, entry.label)

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

    private fun setThumbnail(issuer: String, label: String) {
        if (preferences.displayIcon) {
            binding.thumbnailFrame.visibility = View.VISIBLE
            binding.thumbnail.setBackgroundColor(entry?.thumbnailColor ?: Color.BLACK)
            binding.initialsView.text = issuer.getInitials()
//            val fileName = "logo_${issuer.lowercase().replace(" ", "_")}.png"
//            val logoBitmap =
//                Utils.getThumbnailFromAssets(context.assets, fileName) ?: LetterBitmap(context)
//                    .getLetterTile("$issuer$label") //appending label for different color if same issuer name
//            binding.thumbnail.setImageBitmap(logoBitmap)
        } else {
            binding.thumbnailFrame.visibility = View.GONE
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
        fun onDelete(entry: TokenEntry, position: Int)
    }
}
