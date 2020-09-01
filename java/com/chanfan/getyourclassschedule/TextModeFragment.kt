package com.chanfan.getyourclassschedule

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.text_mode_fragment.*
import kotlinx.android.synthetic.main.text_mode_fragment.view.*
import java.io.File


class TextModeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.text_mode_fragment, container, false)
        view.fabButton.setOnClickListener {
            val f = File(context?.filesDir!!.path, "new.ics")
            if (!f.exists()) {
                if (ContextCompat.checkSelfPermission(
                        GlobalApp.context,
                        Manifest.permission.WRITE_CALENDAR
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    requestPermissions(
                        arrayOf(
                            Manifest.permission.WRITE_CALENDAR,
                            Manifest.permission.READ_CALENDAR
                        ), 1
                    )
                } else {
                    writeCalendar()
                }
            } else {
                if (activity != null) {
                    val mainActivity = activity as MainActivity
                    mainActivity.shareDialog.show()
                }
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
                var allGrant = false
                for (i in 0 until 2) {
                    if (grantResults.isNotEmpty() && grantResults[i] == PackageManager.PERMISSION_GRANTED)
                        allGrant = true
                    else {
                        allGrant = false
                        Toast.makeText(context, "权限被拒绝了呢~", Toast.LENGTH_SHORT).show()
                    }
                }
                if (allGrant) {
                    writeCalendar()
                } else {
                    Toast.makeText(context, "权限没给够哦", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun writeCalendar() {
        val data = textData.text.toString()
        if (data != "")
            if (SHIPAI.isChecked) {
                ClassTableICAL.handleTextData(data, ClassTableICAL.SHIPAI)
            } else {
                ClassTableICAL.handleTextData(data, ClassTableICAL.NANHAI)
            }
        else {
            Toast.makeText(context, "请输入文本信息", Toast.LENGTH_SHORT).show()
        }
    }

}