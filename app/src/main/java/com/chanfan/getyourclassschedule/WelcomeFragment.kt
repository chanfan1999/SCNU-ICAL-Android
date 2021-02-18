package com.chanfan.getyourclassschedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.welcome_fragment.*

class WelcomeFragment : Fragment() {

    private var guideList: ArrayList<Any> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.welcome_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        guideList.add(TextInfo("隐私说明", getString(R.string.briefText), null))
        guideList.add(TextInfo("教程时间", getString(R.string.guide), null))
        guideList.add(ImageInfo(R.drawable.guide, "点击查看大图"))
        guideList.add(TextInfo("关于本应用", getString(R.string.about), null))
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = GuideAdapter(guideList)
    }

    override fun onDestroy() {
        super.onDestroy()
        guideList.clear()
    }
}