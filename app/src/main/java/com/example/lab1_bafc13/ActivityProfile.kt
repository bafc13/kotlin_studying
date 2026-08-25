package com.example.lab1_bafc13

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.lab1_bafc13.databinding.ActivityProfileBinding
import com.example.lab1_bafc13.viewmodels.UserViewModel

class ActivityProfile : AppCompatActivity() {

    private var _binding : ActivityProfileBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException("ActivityProfileBinding is null")
    private var _userViewModel : UserViewModel? = null
    private val userViewModel
        get() = _userViewModel ?: throw java.lang.IllegalStateException("Map VM in ActivityMap is null")
    private lateinit var gestureDetector : GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityProfileBinding.inflate(layoutInflater)
        _userViewModel = ViewModelProvider(this).get(UserViewModel::class.java)
        userViewModel.initContext(this)

        setContentView(binding.root)
        setSpinner()
        initializeGestureDetector()
        setButtonListeners()
        setUserData()
    }

    fun setUserData(){
        binding.userName.text = userViewModel.getUserName()
        binding.uuid.text = "Пользовательский идентификатор: " + userViewModel.getUuid()
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
            val adapter  = ArrayAdapter.createFromResource(this@ActivityProfile, R.array.regions, R.layout.item_spinner)
            adapter.setDropDownViewResource(R.layout.item_spinner)
            statesSpinner.adapter = adapter

            statesSpinner.setSelection(adapter.getPosition(userViewModel.getState()))

            statesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                    val selectedText = parent?.getItemAtPosition(position).toString()
                    userViewModel.setState(selectedText)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
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
    }

    private fun onSwipeRight() {
        val intent = Intent(this, ActivityFavorites::class.java)
        startActivity(intent)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(
                Activity.OVERRIDE_TRANSITION_OPEN,
                R.anim.slide_out_left,
                android.R.anim.slide_out_right
            )
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(R.anim.slide_out_left,android.R.anim.slide_out_right)
        }
    }
}