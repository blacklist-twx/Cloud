package com.example.forlove.Forlove.View
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import com.example.forlove.Forlove.ViewModel.MainViewModel
import com.example.forlove.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.security.Permission
class MainActivity : AppCompatActivity() {
    lateinit var viewmodel:MainViewModel
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissionAndCamera()
        var account:String?=this.intent?.extras?.getString("account")
        viewmodel=ViewModelProvider(this,MainViewModel.ViewModeFactory(account,application))[MainViewModel::class.java]
        //var viewmodel:MainViewModel = MainViewModel(account)
        setContentView(R.layout.activity_main)
        val mToolbarTb: Toolbar = findViewById(R.id.toolbar)
        mToolbarTb.setTitle("我的网盘")
        val bottom_navigation_view:BottomNavigationView=findViewById(R.id.bottom_nav)
        bottom_navigation_view.setOnNavigationItemSelectedListener(object :BottomNavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                val navController = findNavController(R.id.main_nav_host_fragment)
                when(item.title){
                    "我的网盘" -> mToolbarTb.setTitle("我的网盘")
                    "联系人" -> mToolbarTb.setTitle("联系人")
                    "交际圈" -> mToolbarTb.setTitle("交际圈")
                    "我" -> mToolbarTb.setTitle("关于我")
                }
                return item.onNavDestinationSelected(navController)
            }
        })

    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            val home = Intent(Intent.ACTION_MAIN)
            home.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            home.addCategory(Intent.CATEGORY_HOME)
            startActivity(home)
            return true
        }
        return super.onKeyDown(keyCode, event)
    }
    @RequiresApi(Build.VERSION_CODES.M)
    fun checkPermissionAndCamera() {
        val checkSelfPermission2 =
            ContextCompat.checkSelfPermission(applicationContext,Manifest.permission.READ_EXTERNAL_STORAGE)
        val checkSelfPermission =
            ContextCompat.checkSelfPermission(applicationContext,Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (checkSelfPermission == PackageManager.PERMISSION_GRANTED
            &&checkSelfPermission2 == PackageManager.PERMISSION_GRANTED){
            println("permission ok")
        }else{
            val list=arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE)
            requestPermissions( list,1 )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

}