package com.ps.tokky.utils

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.ps.tokky.R
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

    private var initialLoad = true

    fun bind(entry: TokenEntry, inEditMode: Boolean) {
        this.entry = entry
        binding.issuerLabel.text = entry.issuer
        if (entry.label.isNotEmpty()) {
            binding.accountLabel.visibility = View.VISIBLE
            binding.accountLabel.text = entry.label
        } else {
            binding.accountLabel.visibility = View.GONE
        }
        val initialWidth = context.resources.getDimension(R.dimen.card_thumbnail_width).toInt()

        if (inEditMode) {
            binding.editTools.visibility = View.VISIBLE
            binding.arrow.visibility = View.GONE

            ObjectAnimator.ofFloat(binding.editTools, View.ALPHA, 0f, 1f).apply {
                duration = ANIM_DURATION
                if (binding.editTools.alpha == 0f) start()
            }

            ValueAnimator.ofInt(initialWidth, 0).apply {
                duration = ANIM_DURATION
                addUpdateListener { animator ->
                    val animatedValue = animator.animatedValue as Int
                    binding.thumbnailFrame.layoutParams =
                        binding.thumbnailFrame.layoutParams.apply {
                            width = animatedValue
                        }
                    binding.thumbnailFrame.alpha = (animatedValue.toFloat() / initialWidth)
                }
                start()
            }

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

        if (!initialLoad) {
            binding.editTools.visibility = View.GONE
            ObjectAnimator.ofFloat(binding.editTools, View.ALPHA, 1f, 0f).apply {
                duration = ANIM_DURATION
                addListener(object : AnimatorListenerImpl() {
                    override fun onAnimationEnd(animation: Animator) {
                        binding.editTools.visibility = View.GONE
                        binding.arrow.visibility = View.VISIBLE
                    }
                })
                if (binding.editTools.alpha == 1f) start()
            }

            ValueAnimator.ofInt(0, initialWidth).apply {
                duration = ANIM_DURATION
                addUpdateListener { animator ->
                    val animatedValue = animator.animatedValue as Int
                    val layoutParams = binding.thumbnailFrame.layoutParams
                    layoutParams.width = animatedValue
                    binding.thumbnailFrame.layoutParams = layoutParams
                    binding.thumbnailFrame.alpha = (animatedValue.toFloat() / initialWidth)
                }
                start()
            }
        } else {
            binding.editTools.visibility = View.GONE
        }

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
        initialLoad = false
    }

    private fun setThumbnail() {
        entry ?: return
        if (preferences.displayIcon) {
            if (entry!!.thumbnailIcon.isEmpty()) {
                binding.thumbnailFrame.visibility = View.VISIBLE
                binding.initialsView.visibility = View.VISIBLE
                binding.thumbnail.setBackgroundColor(entry!!.thumbnailColor)
                binding.thumbnail.setImageBitmap(null)
                binding.initialsView.text = entry!!.issuer.getInitials()
            } else {
                val fileName = entry!!.thumbnailIcon
                val logoBitmap = Utils.getThumbnailFromAssets(context.assets, fileName)
                binding.thumbnail.setImageBitmap(logoBitmap)
                binding.initialsView.visibility = View.GONE
            }
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

    companion object {
        private const val ANIM_DURATION = 150L
    }

    interface Callback {
        fun onExpand(vh: TokenViewHolder, adapterPosition: Int, expanded: Boolean)
        fun onEdit(entry: TokenEntry, position: Int)
        fun onDelete(entry: TokenEntry, position: Int)
    }

    abstract class AnimatorListenerImpl : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {}
        override fun onAnimationEnd(animation: Animator) {}
        override fun onAnimationCancel(animation: Animator) {}
        override fun onAnimationRepeat(animation: Animator) {}
    }
}
