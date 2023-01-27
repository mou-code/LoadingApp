package com.udacity

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        //Getting the extras
        val string_title = intent.getStringExtra("description")
        val status = intent.getBooleanExtra("status",false)

        //updating the views with the extras
        text_title.text= string_title
        if(status){
            text_status.text="Success"
            text_status.setTextColor(Color.parseColor("#2AD061"))
        }
        else{
            text_status.text="Failure"
            text_status.setTextColor(Color.parseColor("#D02A2A"))
        }
        //going back to home screen
        imageView.setOnClickListener{
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or  Intent.FLAG_ACTIVITY_NEW_TASK
                startActivity(intent)
        }
    }

}
