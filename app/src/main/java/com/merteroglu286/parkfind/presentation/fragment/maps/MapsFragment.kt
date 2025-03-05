package com.merteroglu286.parkfind.presentation.fragment.maps

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.location.Geocoder
import android.location.LocationManager
import android.net.Uri
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.snackbar.Snackbar
import com.merteroglu286.parkfind.R
import com.merteroglu286.parkfind.databinding.FragmentMapsBinding
import com.merteroglu286.parkfind.domain.model.ParkModel
import com.merteroglu286.parkfind.presentation.base.BaseFragment
import com.merteroglu286.parkfind.utility.GPSUtility
import com.merteroglu286.parkfind.utility.MapUtils
import dagger.hilt.android.AndroidEntryPoint
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@AndroidEntryPoint
class MapsFragment : BaseFragment<FragmentMapsBinding, MapsVM>(), OnMapReadyCallback {

    private var _gpsListener: (isOk: Boolean) -> Unit = { _ -> }

    private var gMap: GoogleMap? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val turkeyLatLng = LatLng(39.9208, 32.8541)

    private var navigatedToSettings = false

    private var userLatLng: LatLng? = null
    private var address: String? = null

    private val gpsLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                Log.e("gpsLog", "GPS açıldı")
                _gpsListener(true)
                startLocationUpdates()
            }
        }

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentMapsBinding {
        return FragmentMapsBinding.inflate(inflater, container, attachToParent)
    }

    override fun initUI() {
        super.initUI()
        createMap()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        setupGpsReceiver()
        checkLocationPermissionsAndGPS()
    }

    private lateinit var locationManager: LocationManager
    private var gpsReceiver: BroadcastReceiver? = null

    private fun isGpsEnabled(): Boolean {
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }

    private fun setupGpsReceiver() {
        locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager

        gpsReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (intent.action == LocationManager.PROVIDERS_CHANGED_ACTION) {
                    val isGpsOn = isGpsEnabled()
                    Log.d("GPSReceiver", "GPS durumu değişti: ${if (isGpsOn) "Açık" else "Kapalı"}")

                    if (isGpsOn) {
                        startLocationUpdates()
                        _gpsListener(true)
                    } else {
                        fusedLocationClient.removeLocationUpdates(locationCallback)
                        _gpsListener(false)

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

        if (getLastLocation().first == 0.0 &&
            getLastLocation().second == 0.0 && gMap != null){
            gMap!!.clear()
        }
    }

    override fun onPause() {
        super.onPause()

        if (gpsReceiver != null) {
            requireContext().unregisterReceiver(gpsReceiver)
        }

        if (::fusedLocationClient.isInitialized) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap
        gMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(turkeyLatLng, 5f))

        val lastLatLng = LatLng(getLastLocation().first,getLastLocation().second)

        if (lastLatLng.latitude != 0.0 &&
            lastLatLng.longitude != 0.0){
            gMap?.addMarker(MarkerOptions().position(lastLatLng)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)))
        }

        setMapStyle(gMap)
    }

    private fun createMap() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
    }

    private fun setMapStyle(map: GoogleMap?) {
        try {
            val success = map?.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style)
            )
            if (success == false) {
                Log.e("GOOGLE_MAP", "Failed to apply map style.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e("GOOGLE_MAP", "Map style resource not found.", e)
        }
    }

    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            if (viewModel.checkLocationPermission()) {
                Log.e("izinLog", "İzin verilmiş, GPS kontrol ediliyor...")
                checkGPS({
                    _gpsListener(true)
                    Log.e("gpsLog", "GPS açık")
                    startLocationUpdates()
                }, {
                    _gpsListener(false)
                    Log.e("gpsLog", "GPS kapalı, kullanıcıya uyarı göster")
                })
            } else {
                Log.e("izinLog", "izin verilmedi")
                showPermissionDeniedSnackbar()
            }
        }

    private fun checkLocationPermissionsAndGPS() {
        viewModel.checkAndRequestLocationPermission(
            locationPermissionLauncher
        ) {
            Log.e("izinLog", "İzin verilmiş, GPS kontrol ediliyor...")
            checkGPS({
                _gpsListener(true)
                Log.e("gpsLog", "GPS açık")
                startLocationUpdates()
            }, {
                _gpsListener(false)
                Log.e("gpsLog", "GPS kapalı, kullanıcıya uyarı göster")
            })
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
                startLocationUpdates()
                successCallback()
            },
            errorCallback,
            notFoundLocationService
        )
    }

    private val locationRequest = LocationRequest.create().apply {
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        interval = 10000
        fastestInterval = 2000
    }

    private fun getAddressFromLocation(latLng: LatLng, context: Context): String {
        val geocoder = Geocoder(context, Locale.getDefault())
        var addressText = "Adres bulunamadı"

        try {
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses != null) {
                if (addresses.isNotEmpty()) {
                    val address = addresses[0]
                    val sb = StringBuilder()
                    for (i in 0..address.maxAddressLineIndex) {
                        sb.append(address.getAddressLine(i)).append("\n")
                    }
                    addressText = sb.toString()
                }
            }
        } catch (e: IOException) {
            Log.e("Geocoder", "Adres alınırken hata oluştu: ${e.message}")
        }

        return addressText
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation.let { location ->
                Log.i("gpsLog", "Yeni konum alındı: lat: ${location.latitude}, lon: ${location.longitude}")
                userLatLng = LatLng(location.latitude, location.longitude)

                if (viewModel.checkLocationPermission()) {
                    gMap?.isMyLocationEnabled = true
                }

                if (userLatLng != null){
                    gMap?.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(userLatLng!!, 15f),
                        1000,
                        null
                    )
                    address = getAddressFromLocation(userLatLng!!, requireContext())

                }

                fusedLocationClient.removeLocationUpdates(this)
            }
        }
    }

    private fun startLocationUpdates() {
        if (viewModel.checkLocationPermission()) {
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
                if (userLatLng != null && address != null){
                    viewModel.insertPark(ParkModel(
                        lat = userLatLng!!.latitude,
                        lon = userLatLng!!.longitude,
                        address = address!!,
                        time = getCurrentTime(),
                        imagePath = uri.toString()))

                    gMap?.clear()
                    gMap?.addMarker(MarkerOptions().position(userLatLng!!)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)))

                    setLastLocation(userLatLng!!.latitude, userLatLng!!.longitude)
                }
            }
        } else {
            Toast.makeText(requireContext(), "Fotoğraf çekimi iptal edildi", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun setListeners() {
        super.setListeners()

        with(binding){
            cameraBtn.setOnClickListener {
                if (userLatLng != null && address != null) {
                    showConfirmPopup("Kamera ile kaydetmek istiyor musunuz?", {
                        viewModel.checkAndRequestCameraPermission(requestCameraPermissionLauncher) {
                            viewModel.startCamera(cameraLauncher)
                        }
                    }, {
                        viewModel.insertPark(ParkModel(
                            lat = userLatLng!!.latitude,
                            lon = userLatLng!!.longitude,
                            address = address!!,
                            time = getCurrentTime(),
                            imagePath = null))
                        gMap?.clear()
                        gMap?.addMarker(MarkerOptions().position(userLatLng!!)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)))

                        setLastLocation(userLatLng!!.latitude, userLatLng!!.longitude)
                    })
                } else {
                    Toast.makeText(requireContext(), "Konum bilgisi alınamadı", Toast.LENGTH_SHORT).show()
                }
            }

            parkingBtn.setOnClickListener{
                viewModel.goHistoryScreen()
            }

            mapBtn.setOnClickListener{
                if (getLastLocation() != Pair(0.0,0.0)){
                    MapUtils.openMap(requireContext(), getLastLocation().first,getLastLocation().second)
                }
            }
        }
    }

    private fun getCurrentTime(): String {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return dateFormat.format(Date())
    }
}