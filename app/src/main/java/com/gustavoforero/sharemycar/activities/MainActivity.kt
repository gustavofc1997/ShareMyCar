package com.gustavoforero.sharemycar.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.gustavoforero.sharemycar.R
import com.gustavoforero.sharemycar.adapter.PagerAdapter
import com.gustavoforero.sharemycar.fragments.MyTripsFragment
import com.gustavoforero.sharemycar.fragments.SearchFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
    }

    fun initViews() {
        val adapter = PagerAdapter(supportFragmentManager, resources.getStringArray(R.array.tab_main))
        adapter.addFragment(SearchFragment.newInstance())
        adapter.addFragment(MyTripsFragment.newInstance())
        pager.adapter = adapter
        tab.setupWithViewPager(pager)
    }


}
