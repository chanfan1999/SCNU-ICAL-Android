package com.chanfan.getyourclassschedule

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {

    private val welcomeFragment = WelcomeFragment()
    private val textModeFragment = TextModeFragment()
    private val netModeFragment = NetModeFragment()
    private val aboutFragment = AboutFragment()
    lateinit var shareDialog: AlertDialog
    lateinit var loadingDialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        GlobalApp.context = this
        title = "欢迎/说明"
        setSupportActionBar(toolBar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.ic_menu)
        }
        loadingDialog = AlertDialog.Builder(this).setView(R.layout.my_loading).create().apply {
            setCanceledOnTouchOutside(false)
            setCancelable(false)
        }
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareDialog = AlertDialog.Builder(this).run {
            setTitle("要导出ics日历文件吗？")
            setMessage(
                "课表已经完成导入，现在退出就可以在系统日历中查看了。" +
                        "可以通过微信或者QQ等应用发送到自己电脑上，让电脑也有一份课表。"
            )
            setCancelable(true)
            setPositiveButton("好") { _, _ ->
                run {
                    shareIntent.type = "*/*"
                    shareIntent.putExtra(
                        Intent.EXTRA_STREAM,
                        FileProvider.getUriForFile(
                            context, "com.chanfan.getyourclassschedule.fileprovider",
                            File(context.filesDir, "new.ics")
                        )
                    )
                    context.startActivity(shareIntent)
                }
            }
            setNegativeButton("不必了") { _, _ ->
                Toast.makeText(context, "日历写入成功~", Toast.LENGTH_SHORT).show()
            }
            create()
        }
        navView.setCheckedItem(R.id.welcome)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.welcome -> {
                    title = "欢迎/说明"
                    replaceFragment(welcomeFragment)
                }
                R.id.getViaNet -> {
                    title = "登录获取"
                    replaceFragment(netModeFragment)
                }
                R.id.getViaText -> {
                    title = "本地获取"
                    replaceFragment(textModeFragment)
                }
                R.id.about -> {
                    replaceFragment(aboutFragment)
                }
            }
            drawerLayout.closeDrawers()
            true
        }
        replaceFragment(welcomeFragment)
    }

    private fun replaceFragment(fragment: Fragment) {
        val fragmentManager = supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.replace(R.id.rightLayout, fragment)
        transaction.commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }
        return true
    }
}