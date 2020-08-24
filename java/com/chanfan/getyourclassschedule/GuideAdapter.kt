package com.chanfan.getyourclassschedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class GuideAdapter(val guideList: List<GuildText>) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class MyHolder(view: View) : RecyclerView.ViewHolder(view) {
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
                setImageResource(msg.imageResID)
                setOnClickListener {
                    ImageActivity.actionStart(context, msg.imageResID)
                }
            }
        }
    }

    override fun getItemCount(): Int = guideList.size
}
