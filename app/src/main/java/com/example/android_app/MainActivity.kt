package com.example.android_app

import android.content.res.Resources
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.android_app.adapters.CarouselAdapter
import com.example.android_app.adapters.ListAdapter
import com.example.android_app.databinding.ActivityMainBinding
import com.example.android_app.models.CarouselItem
import com.example.android_app.models.ListItem

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initCarousel()
        initList()
    }

    private fun initList() {
        val originalList = listOf(
            ListItem(R.mipmap.test_image, "Label 1"),
            ListItem(R.mipmap.test_image, "Label 2"),
            ListItem(R.mipmap.test_image, "Label 3")
        )

        val mutableList = originalList.toMutableList()

        val adapter = ListAdapter(mutableList)
        binding.listRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.listRecyclerView.adapter = adapter

        binding.searchBar.addTextChangedListener { text ->
            val searchText = text?.toString()?.trim() ?: ""

            val filteredList = if (searchText.isNotEmpty()) {
                originalList.filter { it.title.contains(searchText, ignoreCase = true) }
            } else {
                originalList
            }

            mutableList.clear()
            mutableList.addAll(filteredList)
            adapter.notifyDataSetChanged()
        }
    }

    private fun initCarousel() {
        val carouselItems = listOf(
            CarouselItem(R.mipmap.test_image),
            CarouselItem(R.mipmap.test_image),
            CarouselItem(R.mipmap.test_image)
        )

        binding.carouselRecyclerView.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.carouselRecyclerView.adapter = CarouselAdapter(carouselItems)

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.carouselRecyclerView)

        setupPageIndicator(carouselItems.size)

        binding.carouselRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    val centerView = snapHelper.findSnapView(recyclerView.layoutManager)
                    val pos = recyclerView.getChildAdapterPosition(centerView!!)
                    updatePageIndicator(pos)
                }
            }
        })
    }

    private fun setupPageIndicator(count: Int) {
        for (i in 0 until count) {
            val dot = ImageView(this).apply {
                setImageResource(R.drawable.indicator_dot_inactive)
                layoutParams = LinearLayout.LayoutParams(16.dpToPx(), 16.dpToPx()).apply {
                    setMargins(8.dpToPx(), 0, 8.dpToPx(), 0)
                }
            }
            binding.pageIndicator.addView(dot)
            val firstDot = binding.pageIndicator.getChildAt(0) as ImageView
            firstDot.setImageResource(R.drawable.indicator_dot_active)
        }
    }

    private fun updatePageIndicator(selectedPosition: Int) {
        for (i in 0 until binding.pageIndicator.childCount) {
            val dot = binding.pageIndicator.getChildAt(i) as ImageView
            dot.setImageResource(if (i == selectedPosition) R.drawable.indicator_dot_active else R.drawable.indicator_dot_inactive)
        }
    }

    private fun Int.dpToPx(): Int = (this * Resources.getSystem().displayMetrics.density).toInt()
}