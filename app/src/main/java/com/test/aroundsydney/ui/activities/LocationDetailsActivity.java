package com.test.aroundsydney.ui.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.test.aroundsydney.R;
import com.test.aroundsydney.models.entitys.Location;
import com.test.aroundsydney.presenters.DetailsPresenter;
import com.test.aroundsydney.views.DetailsView;

import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.functions.Consumer;

public class LocationDetailsActivity extends MvpAppCompatActivity implements DetailsView {

    public static final String LOCATION_DETAILS_EXTRA_KEY = "LOCATION_DETAILS_EXTRA_KEY";

    @InjectPresenter
    DetailsPresenter detailsPresenter;

    private TextView locationNameView;
    private TextView latitudeView;
    private TextView longitudeView;
    private TextView distanceView;
    private EditText notesView;
    private TableRow tableRowDistance;
    private Location location;

    @SuppressLint("CheckResult")
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
            location = (Location) getIntent().getExtras().getSerializable(LOCATION_DETAILS_EXTRA_KEY);
            if (location != null) {
                Observable<Float> observer = detailsPresenter.getDistanceForLocation(location);
                if (observer != null) {
                    observer.subscribe(new Consumer<Float>() {
                        @Override
                        public void accept(Float aFloat) {
                            location.distance = aFloat;
                            insertDataToViews(location);
                        }
                    });
                } else {
                    insertDataToViews(location);
                }
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
            }
        }
    }
}
