package com.example.lab1_bafc13

import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import com.example.lab1_bafc13.databinding.ActivityProfileBinding

class ActivityProfile : AppCompatActivity() {

    private var _binding : ActivityProfileBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("ActivityProfileBinding is null")
    private lateinit var gestureDetector : GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSpinner()
        initializeGestureDetector()
        setButtonListeners()
    }

    fun setButtonListeners(){
        with(binding) {
            imageButton.setOnClickListener {
                val intent = Intent(this@ActivityProfile, ActivityMap::class.java)
                startActivity(intent)
            }
            imageButton2.setOnClickListener {
                val intent = Intent(this@ActivityProfile, ActivityFavorites::class.java)
                startActivity(intent)
            }
        }
    }

    fun setSpinner() {
        with(binding) {
//val spinner = findViewById<Spinner>(R.id.spinner)
//val selected = spinner.selectedItem
//val textView = findViewById<TextView>(R.id.text)
//textView.text = "Выбрано: $selected"
            val adapter  = ArrayAdapter.createFromResource(this@ActivityProfile, R.array.regions, R.layout.item_spinner)
            adapter.setDropDownViewResource(R.layout.item_spinner)
            statesSpinner.adapter = adapter

        }
    }


    fun initializeGestureDetector () {
        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onFling(
                e1: MotionEvent?,
                e2: MotionEvent,
                velocityX: Float,
                velocityY: Float
            ): Boolean {
                if (Math.abs(e1!!.x - e2.x) > 150) {
                    if (e2.x > e1.x) {
                        onSwipeRight()
                    } else {
                        onSwipeLeft()
                    }
                }
                return true
            }
        })
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            gestureDetector.onTouchEvent(event)
        }
        return super.onTouchEvent(event)
    }

    private fun onSwipeLeft() {
//        startActivity(Intent(this, ActivityProfile::class.java))
//        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun onSwipeRight() {
        val intent = Intent(this, ActivityFavorites::class.java)
//        val x_coord : Double = 2.2
//        val y_coord : Double = 3.3
//        intent.putExtra("x_coord", x_coord)
//        intent.putExtra("y_coord", y_coord)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_out_left, android.R.anim.slide_out_right)
    }
}