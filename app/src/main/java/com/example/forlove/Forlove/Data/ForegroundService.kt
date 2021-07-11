package com.example.forlove.Forlove.Data

import android.annotation.SuppressLint
import android.app.*
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.forlove.Forlove.UploadResponse
import com.example.forlove.Forlove.View.MainActivity
import com.example.forlove.R
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class ForegroundService: Service() {
    val model:Model = Model.getInstance()
    val threadpool: ExecutorService =Executors.newFixedThreadPool(5)
    @RequiresApi(Build.VERSION_CODES.N)
    fun createForegroundNotification(): Notification? {
        val ID = "com.example.forlove" //这里的id里面输入自己的项目的包的路径
        val NAME = "Channel One"
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val notification: NotificationCompat.Builder //创建服务对象

        val manager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager?
        notification = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(ID, NAME, NotificationManager.IMPORTANCE_HIGH)
            channel.enableLights(true)
            channel.setShowBadge(true)
            channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC)
            manager!!.createNotificationChannel(channel)

            NotificationCompat.Builder(this).setChannelId(ID)
        } else {
            NotificationCompat.Builder(this)
        }
        val bmp = BitmapFactory.decodeResource(resources, R.drawable.ic_stat_name)
        val no = notification.setChannelId(ID)
            .setContentTitle("后台服务")
            .setContentText("正在上传，请耐心等候...")
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_baseline_arrow_upward_24) //.setContentIntent(pendingIntent)
            //在build()方法之前还可以添加其他方法
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setLargeIcon(bmp)
            .build()
        manager?.notify(110,no)
        return no
    }


    override fun onCreate() {
        super.onCreate()
        Log.e(TAG, "onCreateService")
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "onStartService")
        threadpool.execute(object:Runnable{
            override fun run() {
                model.upload(intent?.getStringExtra("account"),intent?.getStringExtra("path"), object : UploadResponse {
                    override fun update(done: Long, total: Long) {
//                    val progress:Int =((done*100.0/total)).toInt()
//                    uploadProgress.postValue(progress)
                    }
                    @RequiresApi(Build.VERSION_CODES.N)
                    @SuppressLint("WrongConstant")
                    override fun succeed() {
                        Log.d("UploadResponse","succeed")
                        model.uploadResponse.postValue(1)
                        //stopForeground(110)
                        stopSelf()
                        onDestroy()
                    }
                    override fun failed() {
                        Log.d("UploadResponse","fail")
                        model.uploadResponse.postValue(-1)
                        stopSelf()
                        onDestroy()
                        //notification.postValue(-1)
                    }
                })
            }
        })

        startForeground(110, createForegroundNotification())
        return super.onStartCommand(intent, flags, startId)
    }
    override fun onBind(intent: Intent?): IBinder? {
        Log.e(TAG, "onBind")
        return null
    }

}