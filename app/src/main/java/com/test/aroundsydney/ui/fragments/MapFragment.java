package com.test.aroundsydney.ui.fragments;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.test.aroundsydney.R;
import com.test.aroundsydney.common.Constant;
import com.test.aroundsydney.common.Utils;
import com.test.aroundsydney.models.entitys.Location;
import com.test.aroundsydney.presenters.MapPresenter;
import com.test.aroundsydney.ui.activities.LocationDetailsActivity;
import com.test.aroundsydney.views.MapView;

import java.util.ArrayList;
import java.util.List;

public class MapFragment extends MvpAppCompatFragment implements MapView, OnMapReadyCallback {


    @InjectPresenter
    MapPresenter mapPresenter;

    private GoogleMap mMap;
    private List<Location> locationsOnMap = new ArrayList<>();

    IntentFilter intentFilter = new IntentFilter(Constant.LOCATION_PERMISSION_GRANTED_EVENT);
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals(Constant.LOCATION_PERMISSION_GRANTED_EVENT)) {
                    enableMyLocation();
                } else if (intent.getAction().equals(Constant.LOCATIONS_DATA_UPDATE_EVENT)) {
                    mapPresenter.requestLocations();
                }
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        intentFilter.addAction(Constant.LOCATION_PERMISSION_GRANTED_EVENT);
        intentFilter.addAction(Constant.LOCATIONS_DATA_UPDATE_EVENT);
        if (getContext() != null)
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, intentFilter);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return rootView;
    }

    @Override
    public void showPins(List<Location> locations) {
        if (mMap == null)
            return;
        mMap.clear();
        locationsOnMap.clear();
        for (Location item : locations) {
            locationsOnMap.add(item);
            mMap.addMarker(prepareMarkerOptions(item));
        }
    }

    @Override
    public void addPin(Location location) {
        locationsOnMap.add(location);
        mMap.addMarker(prepareMarkerOptions(location));
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapPresenter.requestLocations();

        moveCameraToSydney();
        enableMyLocation();

        //run details screen activity
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if (locationsOnMap != null && !locationsOnMap.isEmpty())
                    for (Location item : locationsOnMap) {
                        if (item.name.equals(marker.getTitle())) {
                            Intent intent = new Intent(getActivity(), LocationDetailsActivity.class);
                            intent.putExtra(Constant.LOCATION_DETAILS_EXTRA_KEY, item);
                            startActivity(intent);
                            return;
                        }
                    }
            }
        });
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                showNewCustomLocationDialog(latLng);
            }
        });
    }

    private MarkerOptions prepareMarkerOptions(Location location) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.draggable(false);
        markerOptions.title(location.name);
        markerOptions.position(new LatLng(location.latitude, location.longitude));
        markerOptions.icon(Utils.bitmapDescriptorFromVector(getContext(), R.drawable.ic_pin));
        return markerOptions;
    }

    private void showNewCustomLocationDialog(final LatLng latLng) {
        if (getContext() != null) {
            new MaterialDialog.Builder(getContext())
                    .title(R.string.add_new_location)
                    .inputRangeRes(1, -1, android.R.color.holo_red_light)
                    .input(getString(R.string.location_name), null, false, new MaterialDialog.InputCallback() {
                        @Override
                        public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                            //empty callback for showing edit text
                        }
                    })
                    .positiveText(R.string.done)
                    .widgetColor(getResources().getColor(R.color.colorPrimary))
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            EditText editText = dialog.getInputEditText();
                            if (editText != null) {
                                mapPresenter.addCustomLocation(latLng, editText.getText().toString());
                                LocalBroadcastManager.getInstance(getContext()).sendBroadcast(new Intent(Constant.LOCATIONS_DATA_UPDATE_EVENT));
                            }
                        }
                    })
                    .show();
        }
    }

    private void moveCameraToSydney() {
        LatLng sydney = new LatLng(-33.860178, 151.212706);
        int zoom = 11;
        int tilt = 70;
        int bearing = 0;
        CameraPosition cameraPosition = new CameraPosition(sydney, zoom, tilt, bearing);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getContext() != null)
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
    }

    private void enableMyLocation() {
        if (getContext() != null && mMap != null) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            }
        }
    }
}
