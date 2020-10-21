package test

import com.alibaba.fastjson.JSONValidator
import kotlinx.coroutines.*

suspend fun doSomething():String{
    println("hello")
    delay(1500)
    return "tttt"
}

fun main(){

    val job = Job()
    val scope = CoroutineScope(job)
    runBlocking {
        val data = async {
            doSomething()
        }.await()
        println(data)
    }
}