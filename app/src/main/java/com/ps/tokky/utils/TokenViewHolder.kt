package com.ps.tokky.utils

import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.RotateAnimation
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.ps.tokky.databinding.RvAuthCardBinding
import com.ps.tokky.models.TokenEntry

class TokenViewHolder(
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
        binding.textViewLabel.text = entry.issuer
        binding.accountLabel.text = entry.label
        updateOTP()

        binding.progressBar.setMax(entry.period)

        binding.cardVisibleLayout.setOnClickListener {
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

            val transitionSet = AutoTransition()
            TransitionManager.beginDelayedTransition(binding.cardHiddenLayout, transitionSet)
            binding.cardHiddenLayout.visibility = if (field) View.VISIBLE else View.GONE

            val rotateAnimation = RotateAnimation(
                if (field) 0f else 180f,
                if (field) 180f else 0f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f
            ).apply {
                interpolator = DecelerateInterpolator()
                repeatCount = 0
                duration = 200
                fillAfter = true
            }

            binding.arrow.startAnimation(rotateAnimation)
        }

    fun setOnExpandListener(listener: EntryExpandListener) {
        this.onExpandListener = listener
    }

    fun updateOTP() {
        entry ?: return
        binding.valueText.text = entry!!.otpFormatted
    }

    interface EntryExpandListener {
        fun onEntryExpand(vh: TokenViewHolder, adapterPosition: Int, expanded: Boolean)
    }
}