package com.example.lab1_bafc13

import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.lab1_bafc13.adapters.FavoriteRadarAdapter
import com.example.lab1_bafc13.databinding.ActivityFavoritesBinding
import com.example.lab1_bafc13.viewmodels.FavoriteRadarViewModel

class ActivityFavorites : AppCompatActivity(){

    private var _viewModel: FavoriteRadarViewModel? = null
    private var _adapter: FavoriteRadarAdapter? = null
    private var _binding : ActivityFavoritesBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException ("ActivityMapBinding is null")

    private val viewModel
        get() = _viewModel ?: throw IllegalStateException("ViewModel for FavoriteRadars must not be null")
    private val adapter
        get() = _adapter ?: throw IllegalStateException("Adapter for FavoriteRadars must not be null")
    private lateinit var gestureDetector : GestureDetector


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityFavoritesBinding.inflate(layoutInflater)

        enableEdgeToEdge()
        setContentView(binding.root)

        initializeGestureDetector()
        setButtonListeners()

        _viewModel = ViewModelProvider(this).get(FavoriteRadarViewModel::class.java)
        _adapter = FavoriteRadarAdapter {}
        binding.recyclerView.apply {
            adapter = this@ActivityFavorites.adapter
            layoutManager = GridLayoutManager(this@ActivityFavorites, 1)
        }

        viewModel.radars.observe(this) { radars ->
            adapter.updateRadars(radars)
        }
        viewModel.loadRadars()
        viewModel.error.observe(this) { errorMessage ->
            if (!errorMessage.isNullOrEmpty()) {
                println("Ошибка: $errorMessage")
            }
        }
    }

    fun setButtonListeners(){
        with(binding) {
            imageButton.setOnClickListener {
                val intent = Intent(this@ActivityFavorites, ActivityMap::class.java)
                startActivity(intent)
            }
            imageButton3.setOnClickListener {
                val intent = Intent(this@ActivityFavorites, ActivityProfile::class.java)
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
        startActivity(Intent(this, ActivityProfile::class.java))
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    private fun onSwipeRight() {
        val intent = Intent(this, ActivityMap::class.java)
//        val x_coord : Double = 2.2
//        val y_coord : Double = 3.3
//        intent.putExtra("x_coord", x_coord)
//        intent.putExtra("y_coord", y_coord)
        startActivity(intent)
        overridePendingTransition(R.anim.slide_out_left, android.R.anim.slide_out_right)
    }

}