package com.test.aroundsydney.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.test.aroundsydney.R;
import com.test.aroundsydney.common.Constant;
import com.test.aroundsydney.models.entitys.Location;
import com.test.aroundsydney.presenters.DetailsPresenter;
import com.test.aroundsydney.views.DetailsView;

import java.util.Locale;

public class LocationDetailsActivity extends MvpAppCompatActivity implements DetailsView {


    @InjectPresenter
    DetailsPresenter detailsPresenter;

    private TextView locationNameView;
    private TextView latitudeView;
    private TextView longitudeView;
    private TextView distanceView;
    private EditText notesView;
    private TableRow tableRowDistance;
    private Location location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_details);

        locationNameView = findViewById(R.id.text_view_location_name);
        latitudeView = findViewById(R.id.text_view_latitude);
        longitudeView = findViewById(R.id.text_view_longitude);
        distanceView = findViewById(R.id.text_view_distance);
        notesView = findViewById(R.id.edit_text_notes);
        tableRowDistance = findViewById(R.id.table_row_distance);

        if (getIntent().getExtras() != null) {
            location = (Location) getIntent().getExtras().getSerializable(Constant.LOCATION_DETAILS_EXTRA_KEY);
            if (location != null) {
                if (location.distance == 0) {
                    location.distance = detailsPresenter.getDistanceForLocation(location);
                }
                insertDataToViews(location);
            }
        }
    }

    private void insertDataToViews(Location location) {
        locationNameView.setText(location.name);
        latitudeView.setText(String.valueOf(location.latitude));
        longitudeView.setText(String.valueOf(location.longitude));
        tableRowDistance.setVisibility(location.distance != 0 ? View.VISIBLE : View.GONE);
        distanceView.setText(String.format(Locale.getDefault(), "%.3f km", location.distance));
        notesView.setText(location.note);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (location.note == null || !notesView.getText().toString().equals(location.note)) {
            location.note = notesView.getText().toString();
            if (location != null) {
                detailsPresenter.updateLocationData(location);
                LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(Constant.LOCATIONS_DATA_UPDATE_EVENT));
            }
        }
    }
}
