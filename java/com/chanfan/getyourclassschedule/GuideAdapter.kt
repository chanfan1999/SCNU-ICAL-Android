package com.chanfan.getyourclassschedule

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class GuideAdapter(val guideList: List<GuideText>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.title)
        val content: TextView = view.findViewById(R.id.content)
        val image: ImageView = view.findViewById(R.id.guideImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.guide, parent, false)
        return MyHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = guideList[position]
        holder as MyHolder
        holder.title.text = msg.title
        holder.content.text = msg.content
        if (msg.imageResID != null) {
            holder.image.apply {
                val options = BitmapFactory.Options()
                options.inJustDecodeBounds = true
                BitmapFactory.decodeResource(resources, msg.imageResID, options)
                options.inSampleSize = 2
                options.inPreferredConfig = Bitmap.Config.RGB_565
                options.inJustDecodeBounds = false
                val bitmap = BitmapFactory.decodeResource(resources, msg.imageResID, options)
                setImageBitmap(bitmap)
                setOnClickListener {
                    ImageActivity.actionStart(context, msg.imageResID)
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
