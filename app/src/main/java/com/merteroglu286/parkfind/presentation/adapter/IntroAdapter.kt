package com.merteroglu286.parkfind.presentation.adapter

import android.app.Service
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.merteroglu286.parkfind.databinding.ItemIntroBinding

class IntroAdapter(
    private val context: Context,
    private val images: List<Int>,
    private val onClick: (position: Int) -> Unit = {}
) : PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val layoutInflater =
            context.getSystemService(Service.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val binding = ItemIntroBinding.inflate(layoutInflater)

        with(binding) {
            imageView.setImageResource(images[position])

            root.setOnClickListener {
                onClick(position)
            }
        }

        container.addView(binding.root)

        return binding.root
    }

    override fun getCount() = images.size

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
}