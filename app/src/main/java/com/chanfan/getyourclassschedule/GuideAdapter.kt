package com.chanfan.getyourclassschedule

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class GuideAdapter(val guideList: List<Any>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object{
        const val TEXT = 0
        const val IMAGE = 1
    }


    class TextInfoHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val content: TextView = view.findViewById(R.id.content)
    }

    class ImageInfoHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imageInfo)
    }

    override fun getItemViewType(position: Int): Int {
        return when (guideList[position]){
            is GuideText -> TEXT
            is ImageInfo -> IMAGE
            else -> 0
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType){
            TEXT -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.guide, parent, false)
                return TextInfoHolder(view)
            }
            IMAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.image_info_view, parent, false)
                return ImageInfoHolder(view)
            }
            else -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.guide, parent, false)
                return TextInfoHolder(view)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        when (holder){
            is TextInfoHolder -> {
                val msg = guideList[position] as GuideText
                holder.title.text = msg.title
                holder.content.text = msg.content
            }
            is ImageInfoHolder -> {
                val msg = guideList[position] as ImageInfo
                holder.image.apply {
                    val options = BitmapFactory.Options()
                    options.inJustDecodeBounds = true
                    BitmapFactory.decodeResource(resources, msg.resID, options)
                    options.inSampleSize = 2
                    options.inPreferredConfig = Bitmap.Config.RGB_565
                    options.inJustDecodeBounds = false
                    val bitmap = BitmapFactory.decodeResource(resources, msg.resID, options)
                    setImageBitmap(bitmap)
                    setOnClickListener {
                        ImageActivity.actionStart(context, msg.resID)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int = guideList.size

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val width = options.outWidth
        val height = options.outHeight
        var inSampleSize = 1
        val halfWidth = width / 2
        val halfHeight = height / 2
        while (halfWidth / inSampleSize >= reqWidth && halfHeight / inSampleSize >= reqHeight) {
            inSampleSize *= 2
        }
        return inSampleSize
    }
}
