package test

import kotlinx.coroutines.delay
import java.util.regex.Pattern

suspend fun doSomething():String{
    println("hello")
    delay(1500)
    return "tttt"
}

fun main() {
    val period = """1-9周12-19周"""
    var beginWeek = 0
    var endWeek = 0
    //将如“1-17周”中的1和17提取出来
    val regEx = "\\d*"
    val m = Pattern.compile(regEx).matcher(period)
    var isBegin = true
    //循环是为了处理类似“1-9周12-16周”的情况
    while (m.find()) {
        val t = m.group(0)
        println(t)

//        if (isBegin) {
//            beginWeek = m.group(0).toInt()
//            isBegin = false
//        } else {
//            endWeek = m.group(0).toInt()
//            isBegin = true
//        }
//        println("beginWeek:$beginWeek")
//        println("endWeek:$endWeek")
    }
}