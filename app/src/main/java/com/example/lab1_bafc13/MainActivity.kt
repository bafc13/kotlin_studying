package com.example.lab1_bafc13

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val initialList = fillList()
        val initialString = makeString(initialList)
        val textView = findViewById<EditText>(R.id.initial_list)
        textView.setText(initialString)

        val button = findViewById<Button>(R.id.normalize_button)
        button.setOnClickListener {
            val normalizedList = normalizeByString(textView.text.toString())
            val normalizedString = makeString(normalizedList)
            val textView = findViewById<TextView>(R.id.normalized_list)
            textView.text = normalizedString
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}