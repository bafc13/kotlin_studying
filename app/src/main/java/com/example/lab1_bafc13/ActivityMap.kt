package com.example.lab1_bafc13

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.lab1_bafc13.databinding.ActivityMapBinding
import com.example.lab1_bafc13.models.User
import com.example.lab1_bafc13.viewmodels.MapViewModel
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.mapview.MapView
import kotlinx.coroutines.launch
import java.util.UUID

class ActivityMap : AppCompatActivity() {

    private var _binding : ActivityMapBinding? = null
    private val binding
        get() = _binding ?: throw IllegalStateException ("ActivityMapBinding is null")
    private lateinit var gestureDetector : GestureDetector
    private lateinit var mapView: MapView
    private var _mapViewModel : MapViewModel? = null
    private val mapViewModel
        get() = _mapViewModel ?: throw java.lang.IllegalStateException("Map VM in ActivityMap is null")

    private lateinit var loginLayout: LinearLayout
    private lateinit var nameInput: EditText
    private lateinit var submitButton: Button


    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            accessSharedFd()
        } else {
            Toast.makeText(this, "Разрешения отклонены", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)

        _binding = ActivityMapBinding.inflate(layoutInflater)
        _mapViewModel = ViewModelProvider(this).get(MapViewModel::class.java)
        mapViewModel.initContext(this)

        enableEdgeToEdge()
        initViews()

        setContentView(binding.root)

        initializeGestureDetector()
        setButtonListeners()

        MapKitFactory.initialize(this)
        mapView = binding.mapview
        binding.mapview.visibility = View.GONE

        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VIDEO,
                Manifest.permission.READ_MEDIA_AUDIO,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        } else {
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }

        val permissionsToRequest = permissions.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }.toTypedArray()

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissionLauncher.launch(permissionsToRequest)
        } else {
            accessSharedFd()
        }
    }

    private fun accessSharedFd() {
        checkUserOrRequestLogin()
        Toast.makeText(this, "Доступ к sharedFd разрешен", Toast.LENGTH_SHORT).show()
    }

    private fun initMap() {
        binding.mapview.visibility = View.VISIBLE
    }
    private fun initViews() {
        loginLayout = binding.loginLayout
        nameInput = binding.nameInput
        submitButton = binding.submitButton

        submitButton.setOnClickListener {
            val userName = nameInput.text.toString().trim()
            if (userName.isNotEmpty()) {

                createNewUser(userName)
                loginLayout.visibility = View.GONE
                initMap()
            } else {
                Toast.makeText(this, "Введите имя", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkUserOrRequestLogin() {
        val savedUuid = mapViewModel.getUuid()
        if (savedUuid != null) {
            checkUserOnServer(savedUuid)
        } else {
            showLoginForm()
        }
    }

    private fun checkUserOnServer(uuid: String) {
        lifecycleScope.launch {
            try {
                val user = mapViewModel.getUserByUuid(uuid)
                if (user.isNotEmpty()) {
                    loginLayout.visibility = View.GONE
                } else {
                    showLoginForm()
                }
            } catch (e: Exception) {
                val savedName = mapViewModel.getUserName()
                if (savedName != null) {
                    loginLayout.visibility = View.GONE
                    initMap()
                } else {
                    showLoginForm()
                    Toast.makeText(
                        this@ActivityMap,
                        "Ошибка подключения",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                e.printStackTrace()
            }
        }
    }

    private fun createNewUser(userName: String) {
        lifecycleScope.launch {
            try {
                val newUuid = UUID.randomUUID().toString()
                val newUser = User(newUuid, userName)

                mapViewModel.createNewUser(newUuid, newUser)

            } catch (e: Exception) {
                Toast.makeText(
                    this@ActivityMap,
                    "Ошибка создания пользователя",
                    Toast.LENGTH_SHORT
                ).show()
                e.printStackTrace()
            }
        }
    }

    private fun showLoginForm() {
        loginLayout.visibility = View.VISIBLE
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
            overrideActivityTransition(
                Activity.OVERRIDE_TRANSITION_OPEN,
                R.anim.slide_in_right,
                R.anim.slide_out_left
            )
        } else {
            @Suppress("DEPRECATION")
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }
    }
}