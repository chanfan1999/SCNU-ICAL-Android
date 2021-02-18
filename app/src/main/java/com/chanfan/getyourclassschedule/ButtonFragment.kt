package com.chanfan.getyourclassschedule


import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.button_fragment.*

class ButtonFragment(
    val text: String,
    val color: String,
    val originID: Int,
    val avatarID: Int = R.drawable.cat0
) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.button_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonName.text = text
        layout.setBackgroundColor(Color.parseColor(color))
        avatar.setImageResource(avatarID)
    }

}