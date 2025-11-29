package com.example.clipsync

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import android.content.SharedPreferences

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val prefs = getSharedPreferences("clipsync_prefs", MODE_PRIVATE)
        val ipInput = findViewById<EditText>(R.id.ipInput)
        val saveBtn = findViewById<Button>(R.id.saveBtn)

        // Pre-fill old IP if exists
        ipInput.setText(prefs.getString("laptop_ip", ""))

        saveBtn.setOnClickListener {
            val ip = ipInput.text.toString().trim()
            if (ip.isNotEmpty()) {
                prefs.edit().putString("laptop_ip", ip).apply()

                val i = Intent(this, SyncService::class.java)
                startForegroundService(i)

                finish()
            }
        }
    }
}
