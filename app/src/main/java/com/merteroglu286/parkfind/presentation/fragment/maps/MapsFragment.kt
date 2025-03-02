package com.merteroglu286.parkfind.presentation.fragment.maps

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.Uri
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.merteroglu286.parkfind.R
import com.merteroglu286.parkfind.databinding.FragmentMapsBinding
import com.merteroglu286.parkfind.presentation.base.BaseFragment
import com.merteroglu286.parkfind.utility.GPSUtility
import com.merteroglu286.parkfind.utility.manager.PermissionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MapsFragment : BaseFragment<FragmentMapsBinding, MapsVM>(), OnMapReadyCallback {

    @Inject
    lateinit var permissionManager: PermissionManager

    private var _gpsListener: (isOk: Boolean) -> Unit = { _ -> }

    private lateinit var gMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val turkeyLatLng = LatLng(39.9208, 32.8541)

    private var navigatedToSettings = false

    private lateinit var userLatLng: LatLng

    private val gpsLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.e("gpsLog", "GPS açıldı")
                _gpsListener(true)
                startLocationUpdates() // GPS açıldıktan sonra konumu al
            }
        }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentMapsBinding {
        return FragmentMapsBinding.inflate(inflater, container, attachToParent)
    }

    // initUI fonksiyonunda çağırın
    override fun initUI() {
        super.initUI()
        createMap()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        setupGpsReceiver() // BroadcastReceiver'ı kur
        checkLocationPermissionsAndGPS()
    }

    private lateinit var locationManager: LocationManager
    private var gpsReceiver: BroadcastReceiver? = null

    // GPS durumunu kontrol eden yardımcı fonksiyon
    private fun isGpsEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    // BroadcastReceiver kurulumu
    private fun setupGpsReceiver() {
        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        gpsReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
                    val isGpsOn = isGpsEnabled()
                    Log.d("GPSReceiver", "GPS durumu değişti: ${if (isGpsOn) "Açık" else "Kapalı"}")

                    if (isGpsOn) {
                        // GPS açıldığında yapılacak işlemler
                        startLocationUpdates()
                        _gpsListener(true)
                    } else {
                        // GPS kapandığında yapılacak işlemler
                        fusedLocationClient.removeLocationUpdates(locationCallback)
                        _gpsListener(false)

                        // Kullanıcıya GPS'i açması için bir mesaj gösterebilirsiniz
                        Snackbar.make(
                            requireView(),
                            "Konumunuzu görmek için GPS'i açın",
                            Snackbar.LENGTH_LONG
                        ).setAction("Aç") {
                            checkGPS({}, {})
                        }.show()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // Alıcıyı kaydet
        if (gpsReceiver != null) {
            requireContext().registerReceiver(
                gpsReceiver,
                IntentFilter(LocationManager.PROVIDERS_CHANGED_ACTION)
            )
        }

        // İlk durumu kontrol et
        if (::locationManager.isInitialized && isGpsEnabled()) {
            startLocationUpdates()
            _gpsListener(true)
        } else if (::locationManager.isInitialized) {
            _gpsListener(false)
        }

        if (navigatedToSettings) {
            checkLocationPermissionsAndGPS()
            navigatedToSettings = false
        }
    }

    override fun onPause() {
        super.onPause()

        // Alıcıyı kaldır
        if (gpsReceiver != null) {
            requireContext().unregisterReceiver(gpsReceiver)
        }

        // Konum güncellemelerini durdur
        if (::fusedLocationClient.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(turkeyLatLng, 5f))
    }

    private fun createMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (fineLocationGranted || coarseLocationGranted) {
                Log.e("izinLog", "İzin verilmiş, GPS kontrol ediliyor...")
                checkGPS({
                    _gpsListener(true)
                    Log.e("gpsLog", "GPS açık")
                    startLocationUpdates() // GPS açıksa konumu al
                }, {
                    _gpsListener(false)
                    Log.e("gpsLog", "GPS kapalı, kullanıcıya uyarı göster")
                    // GPS açılması gerektiğini belirten bir mesaj gösterebilirsin
                })
            } else {
                Log.e("izinLog", "izin verilmedi")
                showPermissionDeniedSnackbar()
            }
        }

    private fun checkLocationPermissionsAndGPS() {
        if (permissionManager.checkLocationPermission()) {
            Log.e("izinLog", "İzin verilmiş, GPS kontrol ediliyor...")
            checkGPS({
                _gpsListener(true)
                Log.e("gpsLog", "GPS açık")
                startLocationUpdates() // GPS açıksa konumu al
            }, {
                _gpsListener(false)
                Log.e("gpsLog", "GPS kapalı, kullanıcıya uyarı göster")
                // GPS açılması gerektiğini belirten bir mesaj gösterebilirsin
            })
        } else {
            Log.e("izinLog", "izin verilmedi, tekrar istendi")
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun showPermissionDeniedSnackbar() {
        val snackbar = Snackbar.make(
            requireView(),
            "Konum izni gerekiyor. Lütfen izin verin.",
            Snackbar.LENGTH_INDEFINITE
        )
        snackbar.setAction("Ayarlar") {
            openAppSettings()
        }
        snackbar.show()
    }

    private fun openAppSettings() {
        navigatedToSettings = true
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", requireContext().packageName, null)
        }
        startActivity(intent)
    }

    private fun checkGPS(
        successCallback: () -> Unit,
        errorCallback: () -> Unit,
        notFoundLocationService: (() -> Unit?) = {}
    ) {
        GPSUtility.enableLocationSettings(
            requireActivity(),
            gpsLauncher,
            {
                // GPS zaten açıksa anlık konum güncellemelerini başlat
                startLocationUpdates()
                successCallback()
            },
            errorCallback,
            notFoundLocationService
        )
    }

    // MapsFragment sınıfına eklenecek kod
    private val locationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        interval = 10000
        fastestInterval = 2000
    }

    private val locationCallback = object : LocationCallback() {

        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation?.let { location ->
                Log.i("gpsLog", "Yeni konum alındı: lat: ${location.latitude}, lon: ${location.longitude}")
                userLatLng = LatLng(location.latitude, location.longitude)
                gMap.animateCamera(
                    CameraUpdateFactory.newLatLngZoom(userLatLng, 15f),
                    1000, // Animasyon süresi (ms cinsinden, örneğin 1000ms = 1 saniye)
                    null
                )
                if (permissionManager.checkLocationPermission()){
                    gMap.isMyLocationEnabled = true
                }
                // Konum alındıktan sonra güncellemeleri durdur
                fusedLocationClient.removeLocationUpdates(this)
            }
        }
    }

    private fun startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }
    }

    fun gpsListener(f: (isOk: Boolean) -> Unit) {
        _gpsListener = f
    }

    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.startCamera(cameraLauncher)
        } else {
            Toast.makeText(requireContext(), "Kamera izni gerekli", Toast.LENGTH_SHORT).show()
        }
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            viewModel.getCurrentPhotoUri()?.let { uri ->

            }
        } else {
            Toast.makeText(requireContext(), "Fotoğraf çekimi iptal edildi", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun setListeners() {
        super.setListeners()

        with(binding){
            btnCamera.setOnClickListener{
                showConfirmPopup("Kamera ile kaydetmek istiyor musunuz?",{
                    viewModel.checkAndRequestCameraPermission(requestCameraPermissionLauncher) {
                        viewModel.startCamera(cameraLauncher)
                    }
                },{
                    gMap.addMarker(MarkerOptions().position(userLatLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)))
                })
            }
        }
    }
}