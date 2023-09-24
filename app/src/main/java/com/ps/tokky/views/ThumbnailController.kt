package com.ps.tokky.views

import android.animation.Animator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ps.tokky.R
import com.ps.tokky.databinding.BottomSheetThumbnailSelectorBinding
import com.ps.tokky.databinding.ContainerThumbnailBinding
import com.ps.tokky.databinding.RvItemThumbnailIconBinding
import com.ps.tokky.utils.Utils
import com.ps.tokky.utils.getInitials
import kotlin.random.Random

class ThumbnailController @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs), OnClickListener {

    private val layoutInflater = LayoutInflater.from(context)
    private val assets = context.assets
    private val binding = ContainerThumbnailBinding.inflate(layoutInflater, this, true)

    var thumbnailIcon: String? = null
        set(value) {
            field = value
            if (value == null) {
                binding.thumbnailRemove.visibility = View.GONE
                binding.thumbnailImage.visibility = View.GONE
                binding.tilesContainer.visibility = View.VISIBLE
                binding.tilesContainer.animate()
                    .scaleY(1f)
                    .setDuration(100)
                    .setListener(object : AnimatorListenerImpl() {
                        override fun onAnimationEnd(animation: Animator) {
                            binding.tilesContainer.visibility = View.VISIBLE
                        }
                    })
                    .start()
                return
            }
            binding.thumbnailRemove.visibility = View.VISIBLE
            Glide
                .with(context)
                .load(Utils.getThumbnailFromAssets(assets, thumbnailIcon!!))
                .into(binding.thumbnailImage)

            binding.thumbnailImage.visibility = View.VISIBLE
            binding.tilesContainer.animate()
                .scaleY(0f)
                .setDuration(100)
                .setListener(object : AnimatorListenerImpl() {
                    override fun onAnimationEnd(animation: Animator) {
                        binding.tilesContainer.visibility = View.GONE
                    }
                })
                .start()
        }

    private val icons = arrayOf(
        Pair("Amazon", "amazon.png"),
        Pair("BitBucket", "bitbucket.png"),
        Pair("BitWarden", "bitwarden.png"),
        Pair("Cardinal Health", "cardinal_health.png"),
        Pair("CoinDCX", "coindcx.png"),
        Pair("Dashlane", "dashlane.png"),
        Pair("Evernote", "evernote.png"),
        Pair("Expo Go", "expo_go.png"),
        Pair("Facebook", "facebook.png"),
        Pair("GitHub", "github.png"),
        Pair("Google", "google.png"),
        Pair("Instagram", "instagram.png"),
        Pair("LinkedIn", "linkedin.png"),
        Pair("Microsoft", "microsoft.png"),
        Pair("npm", "npm.png"),
        Pair("Nvidia", "nvidia.png"),
        Pair("Olymp Trade", "olymp_trade.png"),
        Pair("Open AI", "openai.png"),
        Pair("Outlook", "outlook.png"),
        Pair("PayPal", "paypal.png"),
        Pair("Proton", "proton.png"),
        Pair("Rockstar Games", "rockstar_games.png"),
        Pair("Snapchat", "snapchat.png"),
        Pair("Twitter", "twitter.png"),
        Pair("Upwork", "upwork.png"),
    )

    private val colors = IntArray(5) { index ->
        context.resources.obtainTypedArray(R.array.tile_colors).getColor(index, Color.GRAY)
    }

    private val tiles = arrayOf(
        binding.colorTile1,
        binding.colorTile2,
        binding.colorTile3,
        binding.colorTile4,
        binding.colorTile5,
    )

    private val tilesContainer = arrayOf(
        binding.colorTileContainer1,
        binding.colorTileContainer2,
        binding.colorTileContainer3,
        binding.colorTileContainer4,
        binding.colorTileContainer5,
    )

    private var lastSelectedTile: View? = null
    var selectedColor: Int = 0
        private set(value) {
            field = value

            colors.forEachIndexed { index, i ->
                if (i == value) {
                    selectTile(tilesContainer[index])
                    return
                }
            }
        }

    init {
        tiles.forEachIndexed { idx, view ->
            view.backgroundTintList = ColorStateList.valueOf(colors[idx])
            tilesContainer[idx].setOnClickListener(this)
        }

        val randomValInRange = Random.nextInt(colors.size)
        setThumbnailColor(colors[randomValInRange])
        selectTile(tiles[randomValInRange])

        thumbnailIcon = null
        binding.thumbnailRemove.setOnClickListener {
            thumbnailIcon = null
        }
        binding.thumbnailSelector.setOnClickListener {
            //Show bottom sheet
            showIconSelectorBottomSheet()
        }
    }

    override fun onClick(v: View?) {
        tilesContainer.forEachIndexed { idx, view ->
            if (view.id == v?.id) {
                setThumbnailColor(colors[idx])
                selectTile(tiles[idx])
            }
        }
    }

    fun setThumbnailColor(colorInt: Int) {
        selectedColor = colorInt
        binding.thumbnailContainer.setBackgroundColor(colorInt)
    }

    private fun selectTile(tile: View) {
        lastSelectedTile?.isSelected = false
        lastSelectedTile = tile
        lastSelectedTile?.isSelected = true
    }

    fun setInitials(str: String?) {
        binding.initialsView.text = str?.getInitials()
    }

    private fun showIconSelectorBottomSheet() {
        val dialogBinding = BottomSheetThumbnailSelectorBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(context)
        dialog.setContentView(dialogBinding.root)

        dialogBinding.rv.adapter = object : RecyclerView.Adapter<IconItemViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IconItemViewHolder {

                val binding = RvItemThumbnailIconBinding.inflate(layoutInflater, parent, false)
                return IconItemViewHolder(binding)
            }

            override fun getItemCount(): Int {
                return icons.size
            }

            override fun onBindViewHolder(holder: IconItemViewHolder, position: Int) {
                val logoBitmap = Utils.getThumbnailFromAssets(assets, icons[position].second)

                Glide.with(context).load(logoBitmap).into(holder.binding.icon)
                holder.binding.title.text = icons[position].first
                holder.binding.root.setOnClickListener {
                    thumbnailIcon = icons[position].second
                    dialog.dismiss()
                }
            }
        }

        dialog.show()
    }

    class IconItemViewHolder(val binding: RvItemThumbnailIconBinding) :
        RecyclerView.ViewHolder(binding.root)

    abstract class AnimatorListenerImpl : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator) {}
        override fun onAnimationEnd(animation: Animator) {}
        override fun onAnimationCancel(animation: Animator) {}
        override fun onAnimationRepeat(animation: Animator) {}
    }
}