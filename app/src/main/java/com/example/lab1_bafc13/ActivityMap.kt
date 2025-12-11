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
import com.example.lab1_bafc13.models.Radar
import com.example.lab1_bafc13.models.User
import com.example.lab1_bafc13.viewmodels.MapViewModel
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider
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
    private val placemarks = mutableListOf<PlacemarkMapObject>()

    var coord_y : Double = 0.0
    var coord_x : Double = 0.0



    companion object {
        private var isApiKeySet = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!isApiKeySet) {
            MapKitFactory.setApiKey(BuildConfig.MAPKIT_API_KEY)
            isApiKeySet = true
        }

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

        coord_y = intent.getDoubleExtra("EXTRA_COORD_Y", 0.0)
        coord_x = intent.getDoubleExtra("EXTRA_COORD_X", 0.0)

        checkAndRequestPermissions()
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            accessSharedFd()
        }
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
    }

    private fun initMap() {
        if(!openFavoritePoint()) {
            val state = mapViewModel.getState()
            if(state == "Калужская область" || state == null || state == "") {
                mapView.mapWindow.map.move( //Калужская область
                    CameraPosition(
                        Point(54.517, 36.261),
                        9.0f,
                        0.0f,
                        0.0f
                    )
                )
            } else {
                mapView.mapWindow.map.move( //Москва
                    CameraPosition(
                        Point(55.75, 37.62),
                        9.0f,
                        0.0f,
                        0.0f
                    )
                )
            }
        }

        mapViewModel.radars.observe(this) { radars ->
            addRadarMarkers(radars)
        }
        mapViewModel.loadRadars()

        binding.mapview.visibility = View.VISIBLE
    }

    fun openFavoritePoint() : Boolean {
        if (coord_y != 0.0 && coord_x != 0.0) {
            mapView.mapWindow.map.move(
                CameraPosition(
                    Point(coord_y, coord_x),
                    18.0f,
                    0.0f,
                    0.0f
                ),
                Animation(Animation.Type.SMOOTH, 1.5f),
                null
            )
            return true;
        }
        return false;
    }

    private fun addRadarMarkers(radars: List<Radar>) {
        val mapObjects = mapView.mapWindow.map.mapObjects
        val icon = ImageProvider.fromResource(this, R.drawable.ic_action_name)
        placemarks.forEach { placemark ->
            mapObjects.remove(placemark)  }
        placemarks.clear()

        radars.forEach { radar ->
            val point = Point(radar.gps_y, radar.gps_x)
            val placemark = mapObjects.addPlacemark().apply {
                geometry = point
                setIcon(icon)
                direction = 0.0f
                addTapListener { mapObject, point ->
                    mapViewModel.saveToFavorites(radar)
                    true
                }
            }
            placemarks.add(placemark)
        }
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