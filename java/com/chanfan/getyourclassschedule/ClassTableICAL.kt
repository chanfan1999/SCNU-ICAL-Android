package com.chanfan.getyourclassschedule

import SugarICAL
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.widget.Toast
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.chanfan.getyourclassschedule.GlobalApp.Companion.context
import kotlinx.android.synthetic.main.my_loading.*
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class ClassTableICAL {

    companion object {
        val SHIPAI: Int = 1
        val NANHAI: Int = 2
        private val dialog = AlertDialog.Builder(context).setView(R.layout.my_loading).create()
        private val calender = SugarICAL.Calender()

        private const val pattern = "yyyyMMdd'T'HHmmSS"
        private val shareIntent = Intent(Intent.ACTION_SEND)
        val shareDialog = AlertDialog.Builder(context).apply {
            setTitle("要导出ics日历文件吗？")
            setMessage("可以通过微信等应用发送到自己电脑上")
            setCancelable(true)
            setPositiveButton("好") { _, _ ->
                run {
                    shareIntent.type = "application/pdf"
                    val fileWithinMyDir = context.filesDir
                    shareIntent.putExtra(
                        Intent.EXTRA_STREAM,
                        Uri.parse("content://${fileWithinMyDir}/new.ics")
                    )
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Sharing File...")
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Sharing File...")
                    context.startActivity(Intent.createChooser(shareIntent, "Share File"))
                }
            }
            setNegativeButton("不必了") { _, _ ->
            }
            create()
        }

        private fun calculateDate(beginWeek: Int, weekday: Int): String {
            val baseDate =
                Calendar.getInstance().apply { set(2020, Calendar.SEPTEMBER, 7) }  //学期第一天
            val baseTime = baseDate.run {
                add(Calendar.DATE, (beginWeek - 1) * 7)
                add(Calendar.DATE, weekday - 1)
                time
            }
            return SimpleDateFormat("yyyyMMdd", Locale.CHINA).format(baseTime.time)
        }


        private var beginTimeShiPai = arrayOf(
            "0", "083000", "092000", "102000", "111000",
            "143000", "152000", "161000", "170000",
            "190000", "195000", "204000"
        )
        private var endTimeShiPai = arrayOf(
            "0", "091000", "100000", "110000", "115000",
            "151000", "160000", "165000", "174000",
            "194000", "203000", "212000"
        )

        private var beginTimeElse = arrayOf(
            "0", "083000", "092000", "102000", "111000",
            "140000", "145000", "154000", "163000",
            "190000", "195000", "204000"
        )


        private var endTimeElse = arrayOf(
            "0", "091000", "100000", "110000", "115000",
            "144000", "153000", "162000", "171000",
            "194000", "203000", "212000"
        )

        private fun process(ob: Any, zone: Int) {
            val jsonObject = ob as JSONObject
            var beginWeek = 0
            var endWeek: Int
            var weekNumber: Int
            jsonObject.apply {
                val period = get("zcd") as String //持续周，如“1-17周”
                val weekday = get("xqj") as String //星期几，如“2”表示星期二
                val className = get("kcmc") as String//课程名，如“软件工程”
                val lastTime = get("jcs") as String //课程持续时间，如“1-3”节
                val location = get("cdmc") as String //教室位置，如“南201”
                val teacherName = get("xm") as String //教师名称，如“曹阳MM”

                //将如“1-17周”中的1和17提取出来
                val regEx = "\\d+"
                val m = Pattern.compile(regEx).matcher(period)
                var isBegin = true
                //循环是为了处理类似“1-9周12-16周”的情况
                while (m.find()) {
                    if (isBegin) {
                        beginWeek = m.group(0).toInt()
                        isBegin = false
                    } else {
                        endWeek = m.group(0).toInt()
                        weekNumber = endWeek - beginWeek + 1
                        //将“1-3”节课提取出来用于计算时间
                        val last = lastTime.split("-".toRegex()).toTypedArray()
                        val begin = last[0].toInt()
                        val end = last[1].toInt()
                        val createDate: String = calculateDate(beginWeek, weekday.toInt())
                        lateinit var startTime: String
                        lateinit var endTime: String
                        when (zone) {
                            SHIPAI -> {
                                startTime = createDate + "T" + beginTimeShiPai[begin]
                                endTime = createDate + "T" + endTimeShiPai[end]
                            }
                            else -> {
                                startTime = createDate + "T" + beginTimeElse[begin]
                                endTime = createDate + "T" + endTimeElse[end]
                            }
                        }

                        val event = SugarICAL.Event(
                            summary = className,
                            location = location,
                            description = teacherName,
                            DTStart = startTime,
                            DTEnd = endTime,
                            count = weekNumber.toString()
                        )
                        calender.addEvent(event)
                        isBegin = true
                    }
                }
            }
        }

        @SuppressLint("SetTextI18n")
        private fun writeToCalender() {
            val sum = calender.getEventNum() + 1
            var i = 1

            calender.events.forEach {
                dialog.textView.text = "正在处理：$i / $sum"
                val startMillis =
                    SimpleDateFormat(pattern, Locale.CHINA).parse(it.DTStart)?.time?.toLong()
                val endMillis =
                    SimpleDateFormat(pattern, Locale.CHINA).parse(it.DTEnd)?.time?.toLong()
                val values = ContentValues().apply {
                    put(CalendarContract.Events.DTSTART, startMillis)
                    put(CalendarContract.Events.DTEND, endMillis)
                    put(CalendarContract.Events.TITLE, it.summary)
                    put(CalendarContract.Events.DESCRIPTION, it.description)
                    put(CalendarContract.Events.CALENDAR_ID, 0)
                    put(CalendarContract.Events.HAS_ALARM, 1)
                    put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Shanghai")
                }
                val uri =
                    context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
                //模拟器没有同步账号日历，下面代码会报错。正式版本再启用
//                val eventID: Long = uri?.lastPathSegment!!.toLong()
//                val alarmValues = ContentValues().apply {
//                    put(CalendarContract.Reminders.MINUTES, 20)
//                    put(CalendarContract.Reminders.EVENT_ID, eventID)
//                    put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_DEFAULT)
//                }
//                context.contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, alarmValues)
                i += 1
            }

        }

        //Android上实现文件读写
        private fun makeFile() {
            val output = context.openFileOutput("new.ics", Context.MODE_PRIVATE)
            val writer = BufferedWriter(OutputStreamWriter(output))
            writer.use {
                it.apply {
                    write("BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:Lucky 2 meet U~\n")
                    for (e in calender.events) {
                        write("BEGIN:VEVENT\n")
                        write("UID:${e.UID}\n")
                        write("SUMMARY:${e.summary}\n")
                        write("LOCATION:${e.location}\n")
                        write("DESCRIPTION:${e.description}\n")
                        write("DTSTART:${e.DTStart}\n")
                        write("DTEND:${e.DTEnd}\n")
                        write("RRULE:FREQ=${e.RRule};")
                        write("COUNT=${e.count}\n")
                        write(
                            "BEGIN:VALARM\nACTION:DISPLAY\nTRIGGER;RELATED=START:-PT20M \nDESCRIPTION:${
                                e.alertDescription
                            }\nEND:VALARM\nEND:VEVENT\n"
                        )
                    }
                    write("END:VCALENDAR")
                }
            }

        }

        fun handleTextData(data: String, zone: Int) {
            val js = JSON.parseObject(data)["kbList"] as JSONArray
            js.forEach {
                process(it, zone)
            }
            dialog.show()
            dialog.setCanceledOnTouchOutside(false)
            dialog.setCancelable(false)
            writeToCalender()
            dialog.dismiss()
            Toast.makeText(context, "写入完成", Toast.LENGTH_SHORT).show()
            makeFile()
            shareDialog.show()
        }
    }
}




