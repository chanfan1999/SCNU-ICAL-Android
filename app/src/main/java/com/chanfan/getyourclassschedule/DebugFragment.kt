package com.chanfan.getyourclassschedule

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.CalendarContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.debug_fragment.*
import java.io.File

class DebugFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.debug_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val idList = ArrayList<String>()

        showAll.setOnClickListener {
            context?.contentResolver?.query(
                Uri.parse("content://com.android.calendar/events"), null,
                null, null, null
            )?.run {
                while (moveToNext()) {
                    val id = getString(getColumnIndex(CalendarContract.Events._ID))
                    val title = getString(getColumnIndex("title"))
                    val sl = getString(getColumnIndex("dtstart"))
                    val se = getString(getColumnIndex(CalendarContract.Events.DURATION))
                    Log.i("日历", "事件名称：$title")
                    Log.i("日历", "事件id：$id")
                    Log.i("日历", "开始时间：$sl")
                    Log.i("日历", "持续时间：$se")
                    idList.add(id)
                }
                close()
            }
        }
        deleteAllEvent.setOnClickListener {
            context?.contentResolver?.query(
                Uri.parse("content://com.android.calendar/events"), null,
                null, null, null
            )?.run {
                while (moveToNext()) {
                    val id = getString(getColumnIndex(CalendarContract.Events._ID))
                    val title = getString(getColumnIndex("title"))
                    val sl = getString(getColumnIndex("dtstart"))
                    val se = getString(getColumnIndex("dtend"))
                    Log.i("日历", "事件名称：$title")
                    Log.i("日历", "事件id：$id")
                    Log.i("日历", "开始时间：$sl")
                    Log.i("日历", "结束时间：$se")
                    idList.add(id)
                }
                close()
            }
            for (i in idList) {
                context?.contentResolver?.delete(
                    Uri.parse("content://com.android.calendar/events"),
                    CalendarContract.Events._ID + "=?",
                    arrayOf(i)
                )
                Log.i("已删除", i)
            }

            val f = File(context?.filesDir, "new.ics")
            if (f.exists()) {
                f.delete()
                Log.i("已删除", "清理成功")
            }
        }

        open.setOnClickListener {
            startActivity(Intent(context, MainActivity2::class.java))
        }
    }

}