package com.chanfan.getyourclassschedule

import android.content.ContentValues
import android.content.Context
import android.provider.CalendarContract
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.chanfan.getyourclassschedule.GlobalApp.Companion.context
import java.io.BufferedWriter
import java.io.OutputStreamWriter
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


class ClassTableICAL {

    companion object {
        const val SHIPAI: Int = 1
        const val NANHAI: Int = 2
        private val calender = SugarICAL.Calender()
        private const val pattern = "yyyyMMdd'T'HHmmSS"


        private fun calculateDate(beginWeek: Int, weekday: Int): String {
            val baseDate =
                Calendar.getInstance().apply { set(2022, Calendar.FEBRUARY, 21) }  //学期第一天
            val baseTime = baseDate.run {
                add(Calendar.DATE, (beginWeek - 1) * 7)
                add(Calendar.DATE, weekday - 1)
                time
            }
            return SimpleDateFormat("yyyyMMdd", Locale.CHINA).format(baseTime.time)
        }


        private val beginTimeShiPai = arrayOf(
            "0", "083000", "092000", "102000", "111000",
            "143000", "152000", "161000", "170000",
            "190000", "195000", "204000"
        )
        private val endTimeShiPai = arrayOf(
            "0", "091000", "100000", "110000", "115000",
            "151000", "160000", "165000", "174000",
            "194000", "203000", "212000"
        )

        private val beginTimeElse = arrayOf(
            "0", "083000", "092000", "102000", "111000",
            "140000", "145000", "154000", "163000",
            "190000", "195000", "204000"
        )


        private val endTimeElse = arrayOf(
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
                val period = get("zcd") as String //持续周，如“1-17周”, 边界case，会出现1-19周（单）的情况，做特殊处理
                val weekday = get("xqj") as String //星期几，如“2”表示星期二
                val className = get("kcmc") as String//课程名，如“软件工程”
                val lastTime = get("jcs") as String //课程持续时间，如“1-3”节
                val location = get("cdmc") as String //教室位置，如“南201”
                val teacherName = get("xm") as String //教师名称，如“曹阳MM”

                //将如“1-17周”中的1和17提取出来
                val regEx = "\\d*"
                val m = Pattern.compile(regEx).matcher(period)
                var isBegin = true
                //循环是为了处理类似“1-9周12-16周”的情况
                while (m.find()) {
                    if (isBegin) {
                        val temp = m.group(0)
                        if (temp != null && temp.isNotEmpty()) {
                            beginWeek = temp.toInt()
                            isBegin = false
                        }
                    } else {
                        val temp = m.group(0)
                        if (temp != null && temp.isNotEmpty()) {
                            endWeek = temp.toInt()
                            var gap = false
                            if (period.endsWith("(单)") || period.endsWith("(双)")) {
                                weekNumber = (endWeek - beginWeek) / 2 + 1
                                gap = true
                            }
                            weekNumber = endWeek - beginWeek + 1
                            //将“1-3”节课提取出来用于计算时间
                            val lastArray = lastTime.split("-".toRegex())
                            val begin = lastArray[0].toInt()
                            val end = lastArray[1].toInt()
                            val createDate: String = calculateDate(beginWeek, weekday.toInt())
                            lateinit var startTime: String
                            lateinit var duration: String
                            lateinit var endTime: String

                            when (zone) {
                                SHIPAI -> {
                                    startTime = createDate + "T" + beginTimeShiPai[begin]
                                    endTime = createDate + "T" + endTimeShiPai[end]
                                    duration = getDuration(startTime, endTime)
                                }
                                else -> {
                                    startTime = createDate + "T" + beginTimeElse[begin]
                                    endTime = createDate + "T" + endTimeElse[end]
                                    duration = getDuration(startTime, endTime)
                                }
                            }

                            val event = SugarICAL.Event(
                                summary = className,
                                location = location,
                                description = teacherName,
                                DTStart = startTime,
                                duration = duration,
                                count = weekNumber.toString(),
                                weekGap = gap
                            )
                            calender.addEvent(event)
                            isBegin = true
                        }
                    }


                }
            }
        }

        private fun writeToCalender() {
            val projection = arrayOf(
                CalendarContract.Calendars._ID,
                CalendarContract.Calendars.CALENDAR_DISPLAY_NAME
            )
            var calID: Long = 0
            var calName: String
            context.contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                projection, null, null, null
            )?.apply {
                val nameCol: Int = getColumnIndex(projection[1])
                val idCol: Int = getColumnIndex(projection[0])
                if (moveToFirst()) {
                    calName = getString(nameCol)
                    calID = getLong(idCol)
                }
                close()
            }


            calender.events.forEach {
                val startMillis =
                    SimpleDateFormat(pattern, Locale.CHINA).parse(it.DTStart)?.time
                // 如果是重复事件，那么事件的结束时间就是null，重复事件要靠duration属性来声明
                val values = ContentValues().apply {
                    put(CalendarContract.Events.CALENDAR_ID, calID)
                    put(CalendarContract.Events.DTSTART, startMillis)
                    put(CalendarContract.Events.DURATION, it.duration)
                    put(CalendarContract.Events.TITLE, it.summary)
                    put(CalendarContract.Events.EVENT_LOCATION, it.location)
                    put(
                        CalendarContract.Events.RRULE,
                        "FREQ=${it.RRule};COUNT=${it.count};${if (it.weekGap) "INTERVAL=2;" else ""}"
                    )
                    put(CalendarContract.Events.DESCRIPTION, it.description)
                    put(CalendarContract.Events.EVENT_TIMEZONE, "Asia/Shanghai")
                }
                val uri =
                    context.contentResolver.insert(CalendarContract.Events.CONTENT_URI, values)
                //模拟器没有同步账号日历，下面代码会报错。正式版本再启用
                val eventID: Long = uri?.lastPathSegment!!.toLong()
                val alarmValues = ContentValues().apply {
                    put(CalendarContract.Reminders.MINUTES, 20)
                    put(CalendarContract.Reminders.EVENT_ID, eventID)
                    put(
                        CalendarContract.Reminders.METHOD,
                        CalendarContract.Reminders.METHOD_ALARM
                    )
                }
                context.contentResolver.insert(CalendarContract.Reminders.CONTENT_URI, alarmValues)
            }
        }

        //Android上实现文件读写
        private fun makeFile() {
            val output = context.openFileOutput("new.ics", Context.MODE_PRIVATE)
            val writer = BufferedWriter(OutputStreamWriter(output))
            writer.apply {
                write("BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:Lucky 2 meet U~\n")
                for (e in calender.events) {
                    write("BEGIN:VEVENT\n")
                    write("UID:${e.UID}\n")
                    write("SUMMARY:${e.summary}\n")
                    write("LOCATION:${e.location}\n")
                    write("DESCRIPTION:${e.description}\n")
                    write("DTSTART:${e.DTStart}\n")
                    write("DURATION:${e.duration}\n")
                    write("RRULE:FREQ=${e.RRule};${if (e.weekGap) "INTERVAL=2;" else ""}")
                    write("COUNT=${e.count}\n")
                    write(
                        "BEGIN:VALARM\nACTION:DISPLAY\nTRIGGER;RELATED=START:-PT20M \nDESCRIPTION:${
                            e.alertDescription
                        }\nEND:VALARM\nEND:VEVENT\n"
                    )
                }
                write("END:VCALENDAR")
            }
            writer.close()
            output.close()
            calender.events.clear()
        }

        fun getDuration(DTStart: String, DTEnd: String): String {
            val startMillis =
                SimpleDateFormat(pattern, Locale.CHINA).parse(DTStart)?.time
            val endMillis =
                SimpleDateFormat(pattern, Locale.CHINA).parse(DTEnd)?.time
            val minutes = (endMillis!! - startMillis!!) / 60000
            return "PT${minutes.toInt()}M"
        }

        fun handleTextData(data: String, zone: Int) {
            val js = JSON.parseObject(data)["kbList"] as JSONArray
            js.forEach {
                process(it, zone)
            }
            writeToCalender()
            makeFile()
        }
    }
}




