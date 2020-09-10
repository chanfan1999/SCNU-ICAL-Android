package com.chanfan.getyourclassschedule

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_image.*

class ImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image)
        val imgID = intent.getIntExtra("ImgResID", 0)
        image.setImageResource(imgID)
    }

    companion object {
        fun actionStart(context: Context, imageID: Int) {
            val intent = Intent(context, ImageActivity::class.java)
            intent.putExtra("ImgResID", imageID)
            context.startActivity(intent)
        }
    }


}