package com.ps.tokky.utils

import android.content.Context
import androidx.core.view.isVisible
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

    private val otpRunnable = object : Runnable {
        override fun run() {
            entry?.let {
                if (it.timeRemaining.toInt() == it.period) {
                    updateOTP()
                    binding.progressBar.startAnim(it.period, 0, 200L) {
                        binding.progressBar.startAnim(0, it.period, it.period * 1000L - 200L)
                    }
                }
            }
            binding.otpHolder.postDelayed(this, 1000)
        }
    }

    fun bind(entry: TokenEntry) {
        this.entry = entry

        binding.issuerLabel.text = entry.issuer
        binding.accountLabel.isVisible = entry.label.isNotEmpty()
        binding.accountLabel.text = entry.label

        setThumbnail()

        binding.otpHolder.apply {
            removeCallbacks(otpRunnable)
            if (AppSettings.getUseMonospaceFont(context)) applyMonospaceFont()

            setOnLongClickListener {
                Utils.copyToClipboard(context, "${entry.otp}".padStart(entry.digits, '0'))
                true
            }
        }
        updateOTP()

        binding.progressBar.setMax(entry.period)

        binding.edit.isVisible = false
        binding.edit.setOnClickListener {
            listener?.onEdit(entry, adapterPosition)
        }

        binding.cardView.setOnClickListener {
            listener?.onExpand(this, entry.id)
        }
    }

    private fun setThumbnail() {
        entry ?: return
        if (entry!!.thumbnailIcon.isEmpty()) {
            binding.thumbnailFrame.isVisible = true
            binding.initialsView.isVisible = true
            binding.thumbnail.setBackgroundColor(entry!!.thumbnailColor)
            binding.thumbnail.setImageBitmap(null)
            binding.initialsView.text = entry!!.issuer.getInitials()
        } else {
            val fileName = entry!!.thumbnailIcon
            val logoBitmap = Utils.getThumbnailFromAssets(context.assets, fileName)
            Glide.with(context).load(logoBitmap).into(binding.thumbnail)
            binding.initialsView.isVisible = false
        }
    }

    var isExpanded = false
        set(value) {
            field = value

            if (value) {
                updateOTP()
                binding.otpHolder.post(otpRunnable)
                entry?.apply {
                    binding.progressBar.startAnim(
                        period - timeRemaining.toInt(),
                        period,
                        timeRemaining * 1000L
                    )
                }
            } else {
                binding.otpHolder.removeCallbacks(otpRunnable)
            }

            binding.cardHiddenLayout.isVisible = value
            binding.arrow.rotation = if (value) 180f else 0f

            binding.edit.isVisible = isExpanded
        }

    fun setCallback(listener: Callback?) {
        this.listener = listener
    }

    fun updateOTP() {
        entry ?: return
        binding.otpHolder.text = entry!!.otpFormatted
            .setMonospaceFontToOTP(AppSettings.getUseMonospaceFont(context))
    }

    interface Callback {
        fun onExpand(viewHolder: TokenViewHolder, id: String)
        fun onEdit(entry: TokenEntry, position: Int)
    }
}
