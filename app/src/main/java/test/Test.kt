package test

import kotlinx.coroutines.*

suspend fun doSomething():String{
    println("hello")
    delay(1500)
    return "tttt"
}

fun main(){

    val job = Job()
    CoroutineScope(job)
    runBlocking {
        val data = withContext(Dispatchers.Default) {
            doSomething()
        }
        println(data)
    }
}