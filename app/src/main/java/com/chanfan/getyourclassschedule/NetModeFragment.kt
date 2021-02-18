package com.chanfan.getyourclassschedule

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.alibaba.fastjson.JSONValidator
import com.chanfan.getyourclassschedule.ProcessResultValues.ERROR
import com.chanfan.getyourclassschedule.ProcessResultValues.EXISTED
import com.chanfan.getyourclassschedule.ProcessResultValues.FINISHED
import com.chanfan.getyourclassschedule.ProcessResultValues.PROCESSING
import com.chanfan.getyourclassschedule.ProcessResultValues.RANDCODEERROR
import kotlinx.android.synthetic.main.net_mode_fragment.*
import kotlinx.coroutines.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class NetModeFragment : Fragment() {
    private val loginService = ServiceCreator.create(LoginService::class.java)
    lateinit var mainActivity: MainActivity2
    private lateinit var handler: Handler
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.net_mode_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mainActivity = activity as MainActivity2
        handler = MyHandler(mainActivity)
        getCodePic.setOnClickListener {
            loginService.get(getString(R.string.verifyCodeLink))
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
        }
        login.setOnClickListener {
            if (hasPermissions(
                    GlobalApp.context,
                    Manifest.permission.READ_CALENDAR,
                    Manifest.permission.WRITE_CALENDAR
                )
            ) {
                writeCalendar()
            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_CALENDAR,
                        Manifest.permission.WRITE_CALENDAR
                    ), 1
                )
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            1 -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    writeCalendar()
                } else {
                    Toast.makeText(context, "权限被拒绝了", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun doLogin(): String? {
        val ac = account.text.toString()
        val pw = password.text.toString()
        val rc = verifyCode.text.toString()
        if (ac == "" || pw == "" || rc == "") {
            Toast.makeText(context, "请输入账号信息", Toast.LENGTH_SHORT).show()
        } else {
            val randCode = mapOf("random" to rc)
            val info: String? = loginService.post(
                randCode,
                "https://sso.scnu.edu.cn/AccountService/user/checkrandom.html"
            ).execute().body()?.string()
            if (info.equals("false")) {
                handler.sendMessage(Message.obtain().apply {
                    what = RANDCODEERROR
                })
            } else {
                handler.sendMessage(Message.obtain().apply {
                    what = PROCESSING
                })

                val loginForm = mapOf(
                    "account" to ac,
                    "password" to pw,
                    "rancode" to rc,
                    "client_id" to "",
                    "response_type" to "",
                    "redirect_url" to "",
                    "jump" to ""
                )
                //异步会发生顺序错误导致无法登陆
                loginService.post(
                    loginForm, getString(R.string.loginLink)
                ).execute()
                loginService.get(getString(R.string.confirmLoginLink))
                    .execute()
                loginService.get(getString(R.string.jwxtLink))
                    .execute()
                val formData =
                    mapOf(
                        "xnm" to getString(R.string.xnm),
                        "xqm" to getString(R.string.xqm)
                    )
                return if (ac.length != 11) {
                    // 教师账号
                    loginService.post(
                        formData, getString(R.string.teacherLink)
                    ).execute().body()?.string()
                } else {
                    loginService.post(
                        formData, getString(R.string.studentLink)
                    ).execute().body()?.string()
                }
            }
        }
        return ""
    }


    private fun writeCalendar() {

        val f = File(context?.filesDir!!.path, "new.ics")
        if (!f.exists()) {
            val job = Job()
            val scope = CoroutineScope(job)
            scope.launch {
                val classData = withContext(Dispatchers.Default) { doLogin() }
                if (!classData.isNullOrBlank()) {
                    try {
                        if (!JSONValidator.from(classData).validate())
                            throw java.lang.Exception("密码或者账号出错了")
                        if (SHIPAI.isChecked)
                            ClassTableICAL.handleTextData(classData, ClassTableICAL.SHIPAI)
                        else
                            ClassTableICAL.handleTextData(classData, ClassTableICAL.NANHAI)
                        handler.sendMessage(Message.obtain().apply {
                            what = FINISHED
                        })
                    } catch (e: Exception) {
                        handler.sendMessage(Message.obtain().apply {
                            what = ERROR
                            obj = e.message
                        })
                    }
                }
            }
        } else {
            handler.sendMessage(Message.obtain().apply {
                what = EXISTED
            })
        }
    }

    private fun hasPermissions(context: Context, vararg permissions: String): Boolean =
        permissions.all {
            ActivityCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }


}
