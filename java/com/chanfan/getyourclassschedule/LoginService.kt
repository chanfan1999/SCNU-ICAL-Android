package com.chanfan.getyourclassschedule

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface LoginService {
    @GET
    fun get(@Url url: String): Call<ResponseBody>

    @FormUrlEncoded
    @POST
    fun post(@FieldMap params: Map<String, String>, @Url url: String): Call<ResponseBody>

}