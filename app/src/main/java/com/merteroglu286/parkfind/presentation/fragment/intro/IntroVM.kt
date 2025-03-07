package com.merteroglu286.parkfind.presentation.fragment.intro

import com.merteroglu286.parkfind.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class IntroVM @Inject constructor() : BaseViewModel() {

    fun goSplashScreen() {
        navigate(IntroFragmentDirections.actionIntroFragmentToSplashFragment())
    }

}