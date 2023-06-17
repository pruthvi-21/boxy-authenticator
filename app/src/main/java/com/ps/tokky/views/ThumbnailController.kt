package com.ps.tokky.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnClickListener
import android.widget.LinearLayout
import com.ps.tokky.databinding.ContainerThumbnailBinding
import com.ps.tokky.utils.getInitials
import kotlin.random.Random

class ThumbnailController @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : LinearLayout(context, attrs), OnClickListener {

    private val binding = ContainerThumbnailBinding.inflate(
        LayoutInflater.from(context), this, true
    )

    private val colors = arrayOf(
        Color.parseColor("#A0522D"),
        Color.parseColor("#376B97"),
        Color.parseColor("#556B2F"),
        Color.parseColor("#B18F96"),
        Color.parseColor("#C8AA4B"),
    )

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
        binding.thumbnailContainer.setCardBackgroundColor(colorInt)
    }

    private fun selectTile(tile: View) {
        lastSelectedTile?.isSelected = false
        lastSelectedTile = tile
        lastSelectedTile?.isSelected = true
    }

    fun setInitials(str: String?) {
        binding.initialsView.text = str?.getInitials()
    }
}