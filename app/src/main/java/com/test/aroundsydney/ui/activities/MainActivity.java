package com.test.aroundsydney.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.test.aroundsydney.R;
import com.test.aroundsydney.common.AppLocationListener;
import com.test.aroundsydney.common.AroundSydneyApplication;
import com.test.aroundsydney.common.Constant;
import com.test.aroundsydney.ui.fragments.LocationListFragment;
import com.test.aroundsydney.ui.fragments.MapFragment;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MainActivity extends MvpAppCompatActivity {


    @Inject
    AppLocationListener locationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AroundSydneyApplication.getAppComponent().inject(this);
        initAppLocationListener();

        ViewPager viewPager = findViewById(R.id.viewpager);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new MapFragment());
        adapter.addFrag(new LocationListFragment());
        viewPager.setAdapter(adapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        TabLayout.Tab mapTab = tabLayout.getTabAt(0);
        if (mapTab != null)
            mapTab.setIcon(getResources().getDrawable(R.drawable.ic_map));

        TabLayout.Tab listTab = tabLayout.getTabAt(1);
        if (listTab != null)
            listTab.setIcon(getResources().getDrawable(R.drawable.ic_list));

    }

    private boolean checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    Constant.MY_PERMISSIONS_REQUEST_LOCATION);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constant.MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    initAppLocationListener();
                }
            }
        }
    }

    private void initAppLocationListener() {
        //init location listener
        if (checkLocationPermission()) {
            LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (mLocationManager != null) {
                mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, Constant.LOCATION_REFRESH_TIME,
                        Constant.LOCATION_REFRESH_DISTANCE, locationListener);

                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, Constant.LOCATION_REFRESH_TIME,
                        Constant.LOCATION_REFRESH_DISTANCE, locationListener);
                locationListener.myLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constant.LOCATION_PERMISSION_GRANTED_EVENT));
            }
        }
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFrag(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return null;
        }
    }
}
