package com.example.lab1_bafc13

import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.lab1_bafc13.databinding.ActivityMapBinding
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.mapview.MapView

class ActivityMap : AppCompatActivity() {

    private var _binding : ActivityMapBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException ("ActivityMapBinding is null")
    private lateinit var gestureDetector : GestureDetector
    private lateinit var mapView: MapView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)

        _binding = ActivityMapBinding.inflate(layoutInflater)

        enableEdgeToEdge()

        setContentView(binding.root)
        MapKitFactory.initialize(this)
        mapView = binding.mapview
        initializeGestureDetector()
        setButtonListeners()
    }

    fun setButtonListeners(){
        with(binding) {
            imageButton2.setOnClickListener {
                val intent = Intent(this@ActivityMap, ActivityFavorites::class.java)
                startActivity(intent)
            }
            imageButton3.setOnClickListener {
                val intent = Intent(this@ActivityMap, ActivityProfile::class.java)
                startActivity(intent)
            }
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
                        onSwipeLeft()
                    }
                }
                return true
            }
        })
    }

    override fun onStart() {
        super.onStart()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    override fun onStop() {
        mapView.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event != null) {
            gestureDetector.onTouchEvent(event)
        }
        return super.onTouchEvent(event)
    }

    private fun onSwipeLeft () {
        startActivity(Intent(this, ActivityFavorites::class.java))
        overridePendingTransition(R.anim.slide_in_right,R.anim.slide_out_left)
    }
}