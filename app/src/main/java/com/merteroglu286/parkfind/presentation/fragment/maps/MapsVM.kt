package com.merteroglu286.parkfind.presentation.fragment.maps

import android.content.Intent
import android.net.Uri
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.viewModelScope
import com.merteroglu286.parkfind.presentation.base.BaseViewModel
import com.merteroglu286.parkfind.utility.manager.MediaManager
import com.merteroglu286.parkfind.utility.manager.PermissionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapsVM @Inject constructor(
    private val permissionManager: PermissionManager,
    private val mediaManager: MediaManager
) : BaseViewModel() {

    private val _currentPhotoUri = MutableSharedFlow<Uri?>()
    val currentPhotoUri: SharedFlow<Uri?> = _currentPhotoUri.asSharedFlow()

    fun checkAndRequestCameraPermission(
        permissionLauncher: ActivityResultLauncher<String>,
        onPermissionGranted: () -> Unit
    ) {
        if (permissionManager.hasCameraPermission()) {
            onPermissionGranted()
        } else {
            permissionManager.requestCameraPermission(permissionLauncher)
        }
    }

    fun startCamera(cameraLauncher: ActivityResultLauncher<Intent>) {
        viewModelScope.launch {
            mediaManager.startCameraIntent(cameraLauncher)
        }
        _currentPhotoUri.tryEmit(mediaManager.getCurrentPhotoUri())
    }

    fun getCurrentPhotoUri(): Uri? {
        return mediaManager.getCurrentPhotoUri()
    }
}