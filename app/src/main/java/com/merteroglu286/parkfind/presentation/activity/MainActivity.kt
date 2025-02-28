package com.merteroglu286.parkfind.presentation.activity

import android.Manifest
import android.app.Activity
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import com.merteroglu286.parkfind.databinding.ActivityMainBinding
import com.merteroglu286.parkfind.domain.preferences.Preferences
import com.merteroglu286.parkfind.presentation.base.BaseActivity
import com.merteroglu286.parkfind.utility.GPSUtility
import com.merteroglu286.parkfind.utility.manager.PermissionManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainVM>() {

    @Inject
    lateinit var prefs: Preferences

    @Inject
    lateinit var permissionManager: PermissionManager

    override fun getViewBinding() = ActivityMainBinding.inflate(layoutInflater)

/*    private var _gpsListener: (isOk: Boolean) -> Unit = { _ -> }

    // GPS ayarlarını açmak için launcher
    private val locationSettingsLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            _gpsListener.invoke(result.resultCode == Activity.RESULT_OK)
        }

    // Konum izinlerini istemek için launcher
    private val locationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (fineLocationGranted || coarseLocationGranted) {
                // İzin verildiyse GPS kontrolünü yap
                checkGPS({
                    // GPS açıldıysa yapılacak işlemler
                }, {
                    // GPS açma işlemi başarısız olursa
                })
            } else {
                // İzin reddedildiyse kullanıcıya bilgi ver
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // Uygulama başladığında izinleri ve GPS'i kontrol et
        checkLocationPermissionsAndGPS()
    }

    // Konum izinlerini kontrol et, izin yoksa iste ve GPS'i aç
    private fun checkLocationPermissionsAndGPS() {
        if (permissionManager.checkLocationPermission()) {
            // İzin varsa GPS durumunu kontrol et
            checkGPS({
                // GPS açıldıysa yapılacak işlemler
            }, {
                // GPS açma işlemi başarısız olursa
            })
        } else {
            // İzin yoksa, izin iste
            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    // GPS kontrolü
    fun checkGPS(
        successCallback: () -> Unit,
        errorCallback: () -> Unit,
        notFoundLocationService: (() -> Unit?) = {}
    ) {
        GPSUtility.enableLocationSettings(
            this,
            locationSettingsLauncher,
            successCallback,
            errorCallback,
            notFoundLocationService
        )
    }

    fun gpsListener(f: (isOk: Boolean) -> Unit) {
        _gpsListener = f
    }*/
}
