package com.chanfan.getyourclassschedule

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.chanfan.getyourclassschedule.ProcessResultValues.ERROR
import com.chanfan.getyourclassschedule.ProcessResultValues.EXISTED
import com.chanfan.getyourclassschedule.ProcessResultValues.FINISHED
import com.chanfan.getyourclassschedule.ProcessResultValues.PROCESSING
import com.chanfan.getyourclassschedule.ProcessResultValues.RANDCODEERROR
import kotlinx.android.synthetic.main.net_mode_fragment.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import kotlin.concurrent.thread

class NetModeFragment : Fragment() {
    private lateinit var loginService: LoginService
    lateinit var mainActivity: MainActivity
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
        mainActivity = activity as MainActivity
        handler = object : Handler(Looper.myLooper()!!) {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {

                    FINISHED -> {
                        mainActivity.loadingDialog.dismiss()
                        mainActivity.shareDialog.show()
                    }
                    ERROR -> {
                        mainActivity.loadingDialog.dismiss()
                        Toast.makeText(context, "出问题了~", Toast.LENGTH_SHORT).show()
                    }
                    EXISTED -> {
                        mainActivity.loadingDialog.dismiss()
                        Toast.makeText(context, "文件已经存在了", Toast.LENGTH_SHORT).show()
                        mainActivity.shareDialog.show()
                    }
                    PROCESSING -> {
                        mainActivity.loadingDialog.show()
                        Toast.makeText(context, "正在处理", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        getCodePic.setOnClickListener {
            loginService = ServiceCreator.create(LoginService::class.java)
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


    private fun writeCalendar() {

        val f = File(context?.filesDir!!.path, "new.ics")
        if (!f.exists()) {
            val ac = account.text.toString()
            val pw = password.text.toString()
            val rc = verifyCode.text.toString()
            if (ac == "" || pw == "" || rc == "") {
                Toast.makeText(context, "请输入账号信息", Toast.LENGTH_SHORT).show()
            } else {
                thread {
                    val randCode = mapOf("random" to rc)
                    val info: String? = loginService.post(
                        randCode,
                        "https://sso.scnu.edu.cn/AccountService/user/checkrandom.html"
                    ).execute().body()?.string()
                    if (info.equals("false")) {
                        Looper.prepare()
                        Toast.makeText(context, "验证码不正确", Toast.LENGTH_SHORT).show()
                        Looper.loop()
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
                        loginService.get(getString(R.string.jwxtLink))
                            .execute()
                        val formData =
                            mapOf(
                                "xnm" to getString(R.string.xnm),
                                "xqm" to getString(R.string.xqm)
                            )
                        val classData = loginService.post(
                            formData, getString(R.string.dataLink)
                        ).execute().body()?.string()
                        try {
                            if (classData != null)
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
                            })
                        }
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