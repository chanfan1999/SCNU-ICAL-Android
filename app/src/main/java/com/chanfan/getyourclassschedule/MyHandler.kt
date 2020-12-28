package com.chanfan.getyourclassschedule

import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.Toast
import java.lang.ref.WeakReference

class MyHandler(mainActivity: MainActivity): Handler(Looper.myLooper()!!){
    private val weakActivity = WeakReference<MainActivity>(mainActivity).get()
    override fun handleMessage(msg: Message) {
        super.handleMessage(msg)
        weakActivity?.let {
            when (msg.what) {
                ProcessResultValues.FINISHED -> {
                    weakActivity.loadingDialog.dismiss()
                    weakActivity.shareDialog.show()
                }
                ProcessResultValues.ERROR -> {
                    weakActivity.loadingDialog.dismiss()
                    Toast.makeText(weakActivity, msg.obj.toString(), Toast.LENGTH_SHORT).show()
                }
                ProcessResultValues.EXISTED -> {
                    weakActivity.loadingDialog.dismiss()
                    Toast.makeText(weakActivity, "文件已经存在了~", Toast.LENGTH_SHORT).show()
                    weakActivity.shareDialog.show()
                }
                ProcessResultValues.RANDCODEERROR -> {
                    weakActivity
                }
                ProcessResultValues.PROCESSING -> {
                    weakActivity.loadingDialog.show()
                    Toast.makeText(weakActivity, "正在处理", Toast.LENGTH_SHORT).show()
                }
                else ->{}
            }
        }
    }
}
