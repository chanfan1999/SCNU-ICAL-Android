package com.chanfan.getyourclassschedule

import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStreamWriter
import java.util.*

class SugarICAL {
    companion object {
        const val WEEKLY = "WEEKLY"
        const val MONTHLY = "MONTHLY"
    }

    data class Event(
        var UID: String = UUID.randomUUID().toString(),
        var summary: String = "",
        var location: String = "",
        var description: String = "write something",
        var DTStart: String = "20200629T090000",
        var duration: String = "PT120M",
        var RRule: String = WEEKLY,
        var count: String = "0",
        var alertDescription: String = "是时候出发去${location}上${summary}啦",
        var weekGap: Boolean = false
    )

    class Calender {
        val events = ArrayList<Event>()

        fun addEvent(e: Event) {
            events.add(e)
        }

        fun deleteEvent(event: Event) {
            events.remove(event)
        }

        fun getEventNum() = events.size

        fun makeICALFile(path: String = "newICAL.ics") {
            val f = File(path)
            val fos = FileOutputStream(f)
            val out = OutputStreamWriter(fos)
            val bw = BufferedWriter(out)
            bw.apply {
                write("BEGIN:VCALENDAR\nVERSION:2.0\nPRODID:Lucky 2 meet U~\n")
                for (e in events) {
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
            bw.close()
            out.close()
            fos.close()
            events.clear()
        }
    }
}