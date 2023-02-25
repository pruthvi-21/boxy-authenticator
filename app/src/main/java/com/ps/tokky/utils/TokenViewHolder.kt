package com.ps.tokky.utils

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import com.ps.tokky.databinding.RvAuthCardBinding
import com.ps.tokky.models.TokenEntry

class TokenViewHolder(
    val context: Context,
    val binding: RvAuthCardBinding
) : RecyclerView.ViewHolder(binding.root) {

    private var onExpandListener: EntryExpandListener? = null
    private var entry: TokenEntry? = null

    private val handler = Handler(Looper.getMainLooper())
    private val handlerTask: Runnable = object : Runnable {
        override fun run() {
            binding.progressBar.setProgress((System.currentTimeMillis() / 1000 % entry!!.period).toInt())
            handler.postDelayed(this, 1000)
        }
    }

    fun bind(entry: TokenEntry) {
        this.entry = entry
        binding.issuerLabel.text = entry.issuer
        if (entry.label.isNotEmpty()) {
            binding.accountLabel.visibility = View.VISIBLE
            binding.accountLabel.text = entry.label
        }
        binding.otpHolder.typeface = Typeface.MONOSPACE
        updateOTP()

        binding.progressBar.setMax(entry.period)

        val drawable = LetterBitmap(context)
            .getLetterTile(entry.issuer.ifEmpty { entry.label })
        binding.thumbnail.setImageBitmap(drawable)

        binding.cardView.setOnClickListener {
            onExpandListener?.onEntryExpand(this, adapterPosition, !isExpanded)
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

    fun setOnExpandListener(listener: EntryExpandListener) {
        this.onExpandListener = listener
    }

    fun updateOTP() {
        entry ?: return
        binding.otpHolder.text = entry!!.otpFormatted
    }

    fun setBackground(@DrawableRes res: Int) {
        binding.cardView.setBackgroundResource(res)
        binding.cardView.backgroundTintList = ColorStateList.valueOf(ColorUtils.primaryTintedBackground(context))
    }

    interface EntryExpandListener {
        fun onEntryExpand(vh: TokenViewHolder, adapterPosition: Int, expanded: Boolean)
    }
}
