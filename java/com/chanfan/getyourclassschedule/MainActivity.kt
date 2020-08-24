package com.chanfan.getyourclassschedule

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private val welcomeFragment = WelcomeFragment()
    private val textModeFragment = TextModeFragment()
    private val netModeFragment = NetModeFragment()
    private val aboutFragment = AboutFragment()
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