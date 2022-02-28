package com.chanfan.getyourclassschedule

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main2.*
import java.io.File
import kotlin.math.abs


class MainActivity2 : AppCompatActivity(), MotionLayout.TransitionListener {

    companion object {
        private val welcomeFragment = WelcomeFragment()
        private val textModeFragment = TextModeFragment()
        private val netModeFragment = NetModeFragment()
        private val debugFragment = DebugFragment()
        private val informationButton = ButtonFragment("教程&介绍", "#B4D1E1", R.id.information)
        private val netModeButton = ButtonFragment("登录获取", "#CCE1F3", R.id.netMode, R.drawable.dog3)
        private val textModeButton =
            ButtonFragment("本地获取", "#F7D7C8", R.id.textMode, R.drawable.dog2)
        private val debugModeButton =
            ButtonFragment("Debug!", "#FCD9DD", R.id.debugMode, R.drawable.cat2)
        private var originFragment: Fragment? = null
        private var toEnd = true

        private val shareIntent = Intent(Intent.ACTION_SEND)
    }


    private val detailID2Button = HashMap<Int, ButtonFragment>().apply {
        put(R.id.informationDetail, informationButton)
        put(R.id.netModeDetail, netModeButton)
        put(R.id.textModeDetail, textModeButton)
        put(R.id.debugModeDetail, debugModeButton)
    }
    private val detailID2Fragment = HashMap<Int, Fragment>().apply {
        put(R.id.informationDetail, welcomeFragment)
        put(R.id.netModeDetail, netModeFragment)
        put(R.id.textModeDetail, textModeFragment)
        put(R.id.debugModeDetail, debugFragment)
    }

    lateinit var shareDialog: AlertDialog
    lateinit var loadingDialog: AlertDialog

    override fun onBackPressed() {
        if (motionLayout.startState == motionLayout.currentState) {
            super.onBackPressed()
        } else {
            motionLayout.transitionToStart()
            toolBar.title = resources.getString(R.string.versionHint)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main2)
        setSupportActionBar(toolBar)
        toolBar.title = resources.getString(R.string.versionHint)

        shareDialog = AlertDialog.Builder(this).run {
            setTitle("要导出ics日历文件吗？")
            setMessage(
                "课表已经完成导入，现在退出就可以在系统日历中查看了。" +
                        "可以通过微信或者QQ等应用发送到自己电脑上，让电脑也有一份课表。"
            )
            setCancelable(true)
            setPositiveButton("好") { _, _ ->
                run {
                    shareIntent.type = "*/*"
                    shareIntent.putExtra(
                        Intent.EXTRA_STREAM,
                        FileProvider.getUriForFile(
                            context, "com.chanfan.getyourclassschedule.fileprovider",
                            File(context.filesDir, "new.ics")
                        )
                    )
                    context.startActivity(shareIntent)
                }
            }
            setNegativeButton("不必了") { _, _ ->
                Toast.makeText(context, "日历写入成功~", Toast.LENGTH_SHORT).show()
            }
            create()
        }

        loadingDialog = AlertDialog.Builder(this).setView(R.layout.my_loading).create().apply {
            setCanceledOnTouchOutside(false)
            setCancelable(false)
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction().apply {
                replace(R.id.information, informationButton)
                replace(R.id.netMode, netModeButton)
                replace(R.id.textMode, textModeButton)
                replace(R.id.debugMode, debugModeButton)
                commitNow()
            }
        }
        motionLayout.setTransitionListener(this)
    }

    override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {
    }

    override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {
    }

    override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
        if (toEnd) {
            val atEnd = abs(p3 - 1f) < 0.1f
            if (atEnd) {
                val transaction = supportFragmentManager.beginTransaction()
                toolBar.title = detailID2Button[p2]?.text
                toolBar.setBackgroundColor(Color.parseColor(detailID2Button[p2]?.color))
                detailID2Fragment[p2]?.also {
                    detailID2Button[p2]?.originID?.let { it1 ->
                        transaction.setCustomAnimations(R.animator.show, 0)
                            .replace(it1, it)
                            .commitNow()
                    }
                }
            }
        } else {
            val atEnd = abs(p3 - 1f) < 0.4f
            if (!atEnd) {
                val transaction = supportFragmentManager.beginTransaction()
                originFragment?.let {
                    detailID2Button[p2]?.originID?.let { it1 ->
                        transaction.setCustomAnimations(R.animator.show, 0)
                            .replace(it1, it)
                            .commitNow()
                    }
                }
                toolBar.title = resources.getString(R.string.versionHint)
            }
        }
    }

    override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {
        if (p1 == p0?.startState) {
            // 恢复初始状态，待转换fragment变成详情
            toEnd = true
        } else {
            toEnd = false
            // 详情状态，待转换fragment为初始
            originFragment = detailID2Button[p1]
//            when (p1) {
//                R.id.informationDetail -> {
//                    originFragment = informationButton
//                }
//                R.id.netModeDetail -> {
//                    originFragment = netModeButton
//                }
//                R.id.textModeDetail -> {
//                    originFragment = textModeButton
//                }
//                R.id.debugModeDetail -> {
//                    originFragment = debugModeButton
//                }
//            }
        }
    }
}