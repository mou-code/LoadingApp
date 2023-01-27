package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        custom_button.setOnClickListener {
            createChannel(getString(R.string.download_notification_channel_id), getString(R.string.download_notification_channel_id))
            if (radio_group.checkedRadioButtonId == -1) {
                Toast.makeText(
                    applicationContext,
                    "Please select the file to download",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                when (radio_group.checkedRadioButtonId) {
                    radiobutton_glide.id -> download(GlideURL,radiobutton_glide.text)
                    radiobutton_loadapp.id -> download(LoadAppURL,radiobutton_loadapp.text)
                    radiobutton_retrofit.id -> download(RetrofitURL,radiobutton_retrofit.text)
                }
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)

            if (downloadID == id) {
                val query = DownloadManager.Query() //getting the query
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val dmanager_cursor: Cursor = downloadManager.query(query) //pointing the cursor to the query
                if (dmanager_cursor.moveToFirst()) {
                    val success = dmanager_cursor.getInt(dmanager_cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) //download status
                    val status = success == DownloadManager.STATUS_SUCCESSFUL //if success is true
                    val description = dmanager_cursor.getString(dmanager_cursor.getColumnIndex(DownloadManager.COLUMN_TITLE)) //download title
                    sendNotificaiton(status, description)
                }
                custom_button.EndDownload()
                custom_button.isEnabled=true
            }
        }
    }

    private fun download(URL: String, title: CharSequence) {
        custom_button.StartDownload()
        custom_button.isEnabled=false
        val request =
            DownloadManager.Request(Uri.parse(URL))
                .setTitle(title)
                .setDescription(getString(R.string.app_description))
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_HIGH
            )
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.lightColor = Color.RED

            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun sendNotificaiton(isSuccess: Boolean, downlaodTitle: String) {
        val notificationManager = ContextCompat.getSystemService(
            this,
            NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancelNotifications()
        notificationManager.sendNotification(this,
            downlaodTitle, isSuccess)
    }


    companion object {
        private const val LoadAppURL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GlideURL =
            "https://github.com/bumptech/glide"
        private const val RetrofitURL =
            "https://github.com/square/retrofit"
        private const val CHANNEL_ID = "channelId"
    }

}