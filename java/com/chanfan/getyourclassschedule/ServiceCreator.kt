package com.chanfan.getyourclassschedule

import okhttp3.JavaNetCookieJar
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.net.CookieHandler
import java.net.CookieManager
import java.net.CookiePolicy
import java.util.concurrent.TimeUnit

object ServiceCreator {
    private const val BASE_URL = "https://sso.scnu.edu.cn/"

    private var cookieHandler: CookieHandler = CookieManager().apply {
        setCookiePolicy(CookiePolicy.ACCEPT_ALL)
    }

    private val client = OkHttpClient.Builder().run {
        cookieJar(JavaNetCookieJar(cookieHandler))
        connectTimeout(10, TimeUnit.SECONDS)
        readTimeout(500, TimeUnit.MILLISECONDS)
        writeTimeout(1000, TimeUnit.MILLISECONDS)
        retryOnConnectionFailure(true)
        build()
    }

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .build()

    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)
}