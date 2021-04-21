package com.example.googleapi

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,         // 대략적 위치권한
        Manifest.permission.ACCESS_FINE_LOCATION)           // 상세 위치권한

    val REQUEST_PERMISSION_CODE = 1 // 권한을 요청하기위한 명령어

    val DEFAULT_ZOOM_LEVEL = 19f    // 지도 배율 설정

    val HANSEI = LatLng(37.551503, 126.951945)  // 권한 거절시 초기위치 설정

    var googleMap: GoogleMap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mapView.onCreate(savedInstanceState)

        if (checkPermissions()) {
            initMap()
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_CODE)
        }

        myLocationButton.setOnClickListener { onMyLocationButtonClick() }
    }   // 이전 권한요청이 허용되어 있을 시 맵을 초기화, 권한요청이 허용되어 있지 않을 시 퍼미션 되물음n

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        initMap()
    }

    private fun checkPermissions(): Boolean {

        for (permission in PERMISSIONS) {
            if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true
    }

    @SuppressLint("MissingPermission")
    fun initMap() {
        mapView.getMapAsync {

            googleMap = it
            it.uiSettings.isMyLocationButtonEnabled = false // 현재위치로 이동하는 버튼을 먼저 비활성화 후

            when {
                checkPermissions() -> {
                    it.isMyLocationEnabled = true
                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(getMyLocation(), DEFAULT_ZOOM_LEVEL))
                }   // 퍼미션을 확인하여 권한이 있으면 버튼을 활성화
                else -> {
                    it.moveCamera(CameraUpdateFactory.newLatLngZoom(HANSEI, DEFAULT_ZOOM_LEVEL))
                }   // 권한이 없으면 설정해뒀던 초기좌표로 이동
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun getMyLocation(): LatLng {

        val locationProvider: String = LocationManager.GPS_PROVIDER // 위치를 측정하는 프로바이더를 GPS로 설정

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager // 위치 서버스 객체를 불러와서

        val lastKnownLocation: Location = locationManager.getLastKnownLocation(locationProvider)  // 마지막으로 업데이트된 위치를 가져옴

        return LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)  // 그것을 위도 경도로 반환
    }

    private fun onMyLocationButtonClick() {
        when {
            checkPermissions() -> googleMap?.moveCamera(
                CameraUpdateFactory.newLatLngZoom(getMyLocation(), DEFAULT_ZOOM_LEVEL)
            )   //  현재위치로 이동하는 명령어
            else -> Toast.makeText(applicationContext, "위치사용권한 설정에 동의해주세요", Toast.LENGTH_LONG).show()
        }   //  퍼미션 확인 후 권한 거절이 확인되면 권한 재요청
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

}

