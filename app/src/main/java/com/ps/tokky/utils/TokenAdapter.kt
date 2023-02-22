package com.ps.tokky.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ps.tokky.databinding.RvAuthCardBinding
import com.ps.tokky.models.TokenEntry

class TokenAdapter(
    private val context: Context,
    private val recyclerView: RecyclerView
) : RecyclerView.Adapter<TokenViewHolder>() {

    private val list = ArrayList<TokenEntry>()

    private var currentExpanded = -1

    private val handler = Handler(Looper.getMainLooper())
    private val handlerTask = object : Runnable {
        override fun run() {
            checkAndUpdateOTP()
            handler.postDelayed(this, Constants.OTP_GENERATION_REFRESH_INTERVAL)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TokenViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = RvAuthCardBinding.inflate(layoutInflater, parent, false)

        return TokenViewHolder(context, binding)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: TokenViewHolder, position: Int) {
        holder.bind(list[position])

        holder.setOnExpandListener(object : TokenViewHolder.EntryExpandListener {
            override fun onEntryExpand(vh: TokenViewHolder, adapterPosition: Int, expanded: Boolean) {
                when (currentExpanded) {
                    adapterPosition -> {
                        holder.isExpanded = false
                        currentExpanded = -1
                    }
                    else -> {
                        if (currentExpanded != -1)
                            getViewHolderAt(currentExpanded)?.isExpanded = false

                        currentExpanded = adapterPosition
                        holder.isExpanded = true
                    }
                }
            }
        })
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateEntries(list: List<TokenEntry>) {
        this.list.clear()
        this.list.addAll(list)
        notifyDataSetChanged()
    }

    fun checkAndUpdateOTP() {
        for (i in list.indices) {
            val item = list[i]
            if (item.updateOTP())
                getViewHolderAt(i)?.updateOTP()
        }
    }

    private fun getViewHolderAt(pos: Int): TokenViewHolder? {
        if (pos < 0 || pos > list.size) return null
        return recyclerView.findViewHolderForAdapterPosition(pos) as TokenViewHolder?
    }

    fun onResume() {
        handler.post(handlerTask)
    }

    fun onPause() {
        handler.removeCallbacks(handlerTask)
    }
}