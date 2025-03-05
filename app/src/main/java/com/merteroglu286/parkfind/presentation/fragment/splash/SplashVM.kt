package com.merteroglu286.parkfind.presentation.fragment.splash

import com.merteroglu286.parkfind.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import javax.inject.Inject

@ExperimentalCoroutinesApi
@HiltViewModel
class SplashVM @Inject constructor() : BaseViewModel() {

    fun goIntroScreen(){
        navigate(SplashFragmentDirections.actionSplashFragmentToIntroFragment())
    }

    fun goMapScreen(){
        navigate(SplashFragmentDirections.actionSplashFragmentToMapsFragment())
    }

}