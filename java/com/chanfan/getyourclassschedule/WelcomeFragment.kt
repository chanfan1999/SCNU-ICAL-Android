package com.chanfan.getyourclassschedule

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.welcome_fragment.*

class WelcomeFragment : Fragment() {

    var guideList: ArrayList<GuideText> = ArrayList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.welcome_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        guideList.add(GuideText("隐私说明:", getString(R.string.briefText), null))
        guideList.add(GuideText("教程时间:", getString(R.string.guide), null))
        guideList.add(GuideText("离线获取文件详情:", "点击查看大图", R.drawable.guide))
        val layoutManager = LinearLayoutManager(context)
        recyclerView.layoutManager = layoutManager
        val adapter = GuideAdapter(guideList)
        recyclerView.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        guideList.clear()
    }
}