package com.dragon.xchat;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Locale;

import com.dragon.xchat.network.ConnectorHelper;

public class MainActivity extends BaseActivity {

	private static final String TAG = "MainActivity";

	public static final int DISCOVER_FRAGMENT_POS = 1;
	public static final int CHAT_FRAGMENT_POS = 0;

	private PagerAdapter mSectionsPagerAdapter;
	private String mUserName = null;

	/**
	 * The {@link ViewPager} that will host the section contents.
	 */
	ViewPager mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mUserName = this.getIntent().getStringExtra("user_name");
		this.setTitle(mUserName);

		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayHomeAsUpEnabled(true);

		ColorDrawable stackedDrawable = new ColorDrawable();
		stackedDrawable.setColor(0xff990000);
		actionBar.setStackedBackgroundDrawable(stackedDrawable);

		actionBar.setIcon(R.drawable.actionbar_icon);
		ColorDrawable drawable = new ColorDrawable();
		drawable.setColor(0xff550000);
		actionBar.setBackgroundDrawable(drawable);

		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = new ViewPagerAdapter(
				this.getSupportFragmentManager());

		// Set up the ViewPager with the sections adapte
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding
		// tab. We can also use ActionBar.Tab#select() to do this if we have
		// a reference to the Tab.
		mViewPager
				.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						actionBar.setSelectedNavigationItem(position);
					}
				});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			final int item = i;
			actionBar.addTab(actionBar.newTab()
					.setText(mSectionsPagerAdapter.getPageTitle(i))
					.setTabListener(new TabListener() {

						@Override
						public void onTabReselected(Tab arg0,
								FragmentTransaction arg1) {
							// TODO Auto-generated method stub

						}

						@Override
						public void onTabSelected(Tab arg0,
								FragmentTransaction arg1) {
							// TODO Auto-generated method stub
							mViewPager.setCurrentItem(item);
						}

						@Override
						public void onTabUnselected(Tab arg0,
								FragmentTransaction arg1) {
							// TODO Auto-generated method stub

						}

					}));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		Log.d("tag", "onOptionsItemSelected.." + item);
		if (id == R.id.action_settings) {
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class ViewPagerAdapter extends FragmentPagerAdapter {

		public ViewPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Fragment getItem(int pos) {
			// TODO Auto-generated method stub
			Fragment fm = null;
			switch (pos) {
			case DISCOVER_FRAGMENT_POS:
				fm = new DiscoverFragment();
				break;
			case CHAT_FRAGMENT_POS:
				fm = new ChatFragment(mUserName);
				break;
			default:
				Log.e(TAG, "ViewPagaerAdapter,error occur");
				break;
			}

			return fm;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			// TODO Auto-generated method stub
			CharSequence title = "";
			switch (position) {
			case DISCOVER_FRAGMENT_POS:
				title = getString(R.string.discover);
				break;
			case CHAT_FRAGMENT_POS:
				title = getString(R.string.chat);
				break;
			}
			return title;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 2;
		}

	}
	
	protected void onDestroy(){
		super.onDestroy(mUserName);
	}
}