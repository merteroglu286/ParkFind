package com.merteroglu286.parkfind.presentation.fragment.intro

import android.view.LayoutInflater
import android.view.ViewGroup
import com.merteroglu286.parkfind.R
import com.merteroglu286.parkfind.databinding.FragmentIntroBinding
import com.merteroglu286.parkfind.presentation.adapter.IntroAdapter
import com.merteroglu286.parkfind.presentation.base.BaseFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class IntroFragment : BaseFragment<FragmentIntroBinding, IntroVM>() {

    private var list : ArrayList<Int> = arrayListOf(R.drawable.intro_1, R.drawable.intro_2)

    override fun getViewBinding(
        inflater: LayoutInflater,
        container: ViewGroup?,
        attachToParent: Boolean
    ): FragmentIntroBinding {
        return FragmentIntroBinding.inflate(inflater, container, attachToParent)
    }

    override fun runOnce() {
        super.runOnce()

        if (getIntroShow()){
            viewModel.goSplashScreen()
        }
    }
    override fun initUI() {
        super.initUI()
        createSlider()
    }

    private fun createSlider() {
        with(binding) {
            sliderViewPager.apply {
                adapter = IntroAdapter(requireContext(), list) { position ->
                    startButton.visibility = if (position == list.size - 1) {
                        android.view.View.VISIBLE
                    } else {
                        android.view.View.GONE
                    }
                }

                addOnPageChangeListener(object : androidx.viewpager.widget.ViewPager.OnPageChangeListener {
                    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

                    override fun onPageSelected(position: Int) {
                        startButton.visibility = if (position == list.size - 1) {
                            android.view.View.VISIBLE
                        } else {
                            android.view.View.GONE
                        }
                    }

                    override fun onPageScrollStateChanged(state: Int) {}
                })
            }

            indicator.setViewPager(sliderViewPager)
        }
    }

    override fun setListeners() {
        super.setListeners()

        binding.startButton.setOnClickListener {
            setIntroShow(true)
            viewModel.goSplashScreen()
        }
    }
}