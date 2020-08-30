package com.chanfan.getyourclassschedule

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.net_mode_fragment.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class NetModeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.net_mode_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getCodePic.setOnClickListener {
            val loginService = ServiceCreator.create(LoginService::class.java)
            loginService.get("https://sso.scnu.edu.cn/AccountService/user/rancode.jpg")
                .enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        val inputStream = response.body()?.byteStream()
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        codeImg.setImageBitmap(bitmap)
                        inputStream?.close()
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(context, "网络好像出了问题呢~", Toast.LENGTH_SHORT).show()
                    }
                })

            login.setOnClickListener {
                val ac = account.text.toString()
                val pw = password.text.toString()
                val rc = verifyCode.text.toString()
                val loginForm = mapOf(
                    "account" to ac, "password" to pw, "rancode" to rc,
                    "client_id" to "", "response_type" to "", "redirect_url" to "", "jump" to ""
                )
                loginService.post(
                    loginForm,
                    "https://sso.scnu.edu.cn/AccountService/user/login.html"
                ).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(
                        call: Call<ResponseBody>,
                        response: Response<ResponseBody>
                    ) {
                        Toast.makeText(context, "正在处理", Toast.LENGTH_SHORT).show()
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(context, "网络好像出了问题呢~", Toast.LENGTH_SHORT).show()
                    }
                })

                loginService.get("https://sso.scnu.edu.cn/AccountService/openapi/onekeyapp.html?id=96")
                    .enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            Toast.makeText(context, "正在获取", Toast.LENGTH_SHORT).show()
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Toast.makeText(context, "网络好像出了问题呢~", Toast.LENGTH_SHORT).show()
                        }
                    })
                val formData = mapOf("xnm" to "2020", "xqm" to "3")
                loginService.post(
                    formData,
                    "https://jwxt.scnu.edu.cn/kbcx/xskbcx_cxXsKb.html?gnmkdm=N253508"
                )
                    .enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(
                            call: Call<ResponseBody>,
                            response: Response<ResponseBody>
                        ) {
                            val data = response.body()?.string()
                            if (data != null) {
                                if (SHIPAI.isChecked)
                                    ClassTableICAL.handleTextData(data, ClassTableICAL.SHIPAI)
                                else
                                    ClassTableICAL.handleTextData(data, ClassTableICAL.NANHAI)
                            }

                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Toast.makeText(context, "网络好像出了问题呢~", Toast.LENGTH_SHORT).show()
                        }
                    })

            }

            //TODO 使用协程完善流程
            //TODO 子线程中创建任务，主线程UI显示等待

        }
    }
}