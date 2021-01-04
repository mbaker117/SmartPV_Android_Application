package com.psut.smartpv.activity

import android.os.Bundle
import androidx.annotation.Nullable
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.psut.smartpv.R
import com.psut.smartpv.databinding.ActivityHistoryBinding
import com.psut.smartpv.databinding.HistoryNumbersBinding
import com.psut.smartpv.fragments.HistoryChartFragment
import com.psut.smartpv.fragments.HistoryNumbersFragment

class History : AppCompatActivity() {
    lateinit var binding: ActivityHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager(binding.tabViewpager)

        TabLayoutMediator(binding.tabTablayout, binding.tabViewpager) { tab, position ->
            when(position){
                0 -> {tab.icon =  ContextCompat.getDrawable(this,R.drawable.ic_baseline_history_24)}
                1 -> {tab.icon = ContextCompat.getDrawable(this,R.drawable.ic_stats)}
            }
        }.attach()
    }
    private fun setupViewPager(viewpager: ViewPager2) {
        var adapter: ViewPagerAdapter = ViewPagerAdapter(this)


        viewpager.setAdapter(adapter)
    }
    class ViewPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {




        override fun getItemCount(): Int {
         return  2
        }

        override fun createFragment(position: Int): Fragment {
            return when(position){
                0-> HistoryNumbersFragment()
                else -> HistoryChartFragment()
            }
        }

        // this function adds the fragment and title in 2 separate  arraylist.

    }

}