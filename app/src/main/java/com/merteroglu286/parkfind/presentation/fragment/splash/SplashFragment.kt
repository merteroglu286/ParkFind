package com.merteroglu286.parkfind.presentation.fragment.splash

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.merteroglu286.parkfind.databinding.FragmentSplashBinding
import com.merteroglu286.parkfind.presentation.base.BaseFragment
import com.merteroglu286.parkfind.presentation.viewmodel.SharedViewModel
import com.merteroglu286.parkfind.utility.constant.AppConstants.SPLASH_DELAY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@ExperimentalCoroutinesApi
@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding, SplashVM>() {

    private val sharedViewModel: SharedViewModel by activityViewModels()

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentSplashBinding {
        return FragmentSplashBinding.inflate(inflater, container, false)
    }

    override fun initUI() {
        super.initUI()
    }

    override fun runOnce() {
        super.runOnce()
        checkIntro()
    }

    private fun checkIntro(){
        lifecycleScope.launch {
            delay(SPLASH_DELAY)
            if (getIntroShow()){
                viewModel.goMapScreen()
            }else{
                viewModel.goIntroScreen()
            }
        }

    }
}