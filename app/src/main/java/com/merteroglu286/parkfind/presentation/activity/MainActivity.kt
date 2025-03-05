package com.merteroglu286.parkfind.presentation.activity

import com.merteroglu286.parkfind.databinding.ActivityMainBinding
import com.merteroglu286.parkfind.presentation.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : BaseActivity<ActivityMainBinding, MainVM>() {

    override fun getViewBinding() = ActivityMainBinding.inflate(layoutInflater)
}
