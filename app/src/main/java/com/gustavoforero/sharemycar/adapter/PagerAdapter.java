package com.gustavoforero.sharemycar.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gustavo on 17/11/2017.
 */

public class PagerAdapter extends FragmentPagerAdapter {
	private String[] tabTitles;
	private final List<Fragment> mFragmentList = new ArrayList<>();

	public PagerAdapter(FragmentManager fm, String[] titles) {
		super (fm);
		tabTitles =titles;
	}

	@Override
	public Fragment getItem(int position) {
		return mFragmentList.get (position);
	}

	@Override
	public int getCount() {
		return mFragmentList.size ();
	}

	public void addFragment(Fragment fragment) {
		mFragmentList.add (fragment);
	}

	@Override
	public CharSequence getPageTitle(int position) {
		return tabTitles[position];
	}
}
