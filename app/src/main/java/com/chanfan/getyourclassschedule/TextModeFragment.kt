package com.chanfan.getyourclassschedule

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.chanfan.getyourclassschedule.ProcessResultValues.ERROR
import com.chanfan.getyourclassschedule.ProcessResultValues.EXISTED
import com.chanfan.getyourclassschedule.ProcessResultValues.FINISHED
import kotlinx.android.synthetic.main.text_mode_fragment.*
import kotlinx.android.synthetic.main.text_mode_fragment.view.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.File


class TextModeFragment : Fragment() {
    lateinit var handler: MyHandler
    lateinit var mainActivity: MainActivity2

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.text_mode_fragment, container, false)
        mainActivity = activity as MainActivity2
        handler = MyHandler(mainActivity)
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
            if (data.isNotBlank()) {
                val job = Job()
                val scope = CoroutineScope(job)
                scope.launch {
                    try {
                        if (SHIPAI.isChecked) {
                            ClassTableICAL.handleTextData(data, ClassTableICAL.SHIPAI)
                        } else {
                            ClassTableICAL.handleTextData(data, ClassTableICAL.NANHAI)
                        }
                        handler.sendMessage(Message.obtain().apply {
                            what = FINISHED
                        })
                    } catch (e: Exception) {
                        handler.sendMessage(Message.obtain().apply {
                            what = ERROR
                            obj = "处理过程出问题了"
                        })
                    }
                }
            } else {
                Toast.makeText(context, "请输入文本信息", Toast.LENGTH_SHORT).show()
            }
        } else {
            handler.sendMessage(Message.obtain().apply {
                what = EXISTED
            })
        }

    }

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

}