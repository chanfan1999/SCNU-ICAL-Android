package com.chanfan.getyourclassschedule

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.text_mode_fragment.*
import kotlinx.android.synthetic.main.text_mode_fragment.view.*
import java.io.File
import kotlin.concurrent.thread


class TextModeFragment : Fragment() {
    lateinit var handler: Handler
    lateinit var mainActivity: MainActivity

    companion object {
        val FINISHED = 1
        val ERROR = 0
        val EXISTED = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.text_mode_fragment, container, false)
        mainActivity = activity as MainActivity
        handler = object : Handler(Looper.myLooper()!!) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    FINISHED -> {
                        mainActivity.loadingDialog.dismiss()
                        mainActivity.shareDialog.show()
                    }
                    ERROR -> {
                        mainActivity.loadingDialog.dismiss()
                        Toast.makeText(context, "出问题了~", Toast.LENGTH_SHORT).show()
                    }
                    EXISTED -> {
                        mainActivity.loadingDialog.dismiss()
                        Toast.makeText(context, "~", Toast.LENGTH_SHORT).show()

                    }
                }
            }
        }


        view.fabButton.setOnClickListener {
            if (hasPermissions(
                    GlobalApp.context,
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.WRITE_CALENDAR
                )
            ) {
                writeCalendar()
            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_CALENDAR,
                        Manifest.permission.WRITE_CALENDAR
                    ), 1
                )
            }
        }
        return view
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    writeCalendar()
                } else {
                    Toast.makeText(context, "权限被拒绝了", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun writeCalendar() {
        val f = File(context?.filesDir!!.path, "new.ics")
        if (!f.exists()) {
            val data = textData.text.toString()
            if (data != "") {
                thread {
                    try {
                        if (SHIPAI.isChecked) {
                            ClassTableICAL.handleTextData(data, ClassTableICAL.SHIPAI)
                        } else {
                            ClassTableICAL.handleTextData(data, ClassTableICAL.NANHAI)
                        }
                        handler.sendMessage(Message().apply {
                            what = FINISHED
                        })
                    } catch (e: Exception) {
                        handler.sendMessage(Message().apply {
                            what = ERROR
                        })
                    }
                }
            } else {
                Toast.makeText(context, "请输入文本信息", Toast.LENGTH_SHORT).show()
            }
        } else {
            handler.sendMessage(Message().apply {
                what = EXISTED
            })
        }

    }

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

}