package com.test.aroundsydney.ui.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.test.aroundsydney.R;
import com.test.aroundsydney.models.entitys.Location;
import com.test.aroundsydney.presenters.ListPresenter;
import com.test.aroundsydney.ui.activities.LocationDetailsActivity;
import com.test.aroundsydney.ui.activities.MainActivity;
import com.test.aroundsydney.ui.adapters.LocationListAdapter;
import com.test.aroundsydney.views.LocationListView;

import java.util.List;

public class LocationListFragment extends MvpAppCompatFragment implements LocationListView {

    @InjectPresenter
    ListPresenter listPresenter;

    private LocationListAdapter adapter;
    IntentFilter intentFilter = new IntentFilter(MainActivity.LOCATION_PERMISSION_GRANTED_EVENT);

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            listPresenter.initMyLocationProvider();
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getContext() != null) {
            LocalBroadcastManager.getInstance(getContext()).registerReceiver(broadcastReceiver, intentFilter);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new LocationListAdapter();
        adapter.setListener(new LocationListAdapter.LocationListAdapterItemClickListener() {
            @Override
            public void onItemClick(Location location) {
                Intent intent = new Intent(getActivity(), LocationDetailsActivity.class);
                intent.putExtra(LocationDetailsActivity.LOCATION_DETAILS_EXTRA_KEY, location);
                startActivity(intent);
            }
        });
        recyclerView.setAdapter(adapter);
        return rootView;
    }

    @Override
    public void showListData(List<Location> locations) {
        if (adapter != null) {
            adapter.addLocations(locations);
        }
    }

    @Override
    public void clearList() {
        if (adapter != null) {
            adapter.clearAdapter();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getContext() != null) {
            LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(broadcastReceiver);
        }
    }
}
