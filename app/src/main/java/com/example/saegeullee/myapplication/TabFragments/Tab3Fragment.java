package com.example.saegeullee.myapplication.TabFragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.saegeullee.myapplication.R;
import com.example.saegeullee.myapplication.models.Course;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

public class Tab3Fragment extends Fragment  {
    private static final String TAG = "Tab3Fragment";

//    private GoogleMap mMap;
//    private MapView mMapView;
    private View mView;
//
//    private static final LatLng PERTH = new LatLng(-31.952854, 115.857342);
//    private static final LatLng SYDNEY = new LatLng(-33.872854, 151.20689);
//    private static final LatLng BRISBANE = new LatLng(-27.47093, 153.0235);
//
//    private Marker mPerth;
//    private Marker mSydney;
//    private Marker mBrisbane;

    private TextView coursePlace, courseHour, courseDay;
    private Course course;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            course = getArguments().getParcelable(getString(R.string.bundle_course));
            Log.d(TAG, "onCreate: got course : " + course.toString());
        } else {
            Log.d(TAG, "onCreate: course is null");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragement3_layout, container, false);

        coursePlace = mView.findViewById(R.id.coursePlace);
        courseHour = mView.findViewById(R.id.courseHour);
        courseDay = mView.findViewById(R.id.courseDay);

        if(course != null) {
            coursePlace.setText(course.getCoursePlace());
            courseHour.setText(course.getCourseHour());
            courseDay.setText(course.getCourseDay());
        } else {
            Log.d(TAG, "onCreateView: course is null can not set course data to view");
        }

        return mView;
    }
//
//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//
//        mMapView =  view.findViewById(R.id.map);
//        if (mMapView != null) {
//            mMapView.onCreate(null);
//            mMapView.onResume();
//            mMapView.getMapAsync(this);
//        }
//    }

//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//
//
//        MapsInitializer.initialize(getContext());
//
//        mMap = googleMap;
//
//        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
//
//        LatLng SEOUL = new LatLng(37.56, 126.97);
//
//        MarkerOptions markerOptions = new MarkerOptions();
//
//        markerOptions.position(SEOUL);
//
//        markerOptions.title("서울");
//
//        markerOptions.snippet("수도");
//
//        mMap.addMarker(markerOptions);
//
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
//
//        mMap.animateCamera(CameraUpdateFactory.zoomTo(13));
//    }

//
//    @Override
//    public void onMapReady(GoogleMap googleMap) {
//        MapsInitializer.initialize(getContext());
//
//        mMap = googleMap;
//
//        List<Marker> markerList = new ArrayList<>();
//
//        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
//
//        mPerth = mMap.addMarker(new MarkerOptions()
//                .position(PERTH)
//                .title("Perth"));
//        mPerth.setTag(0);
//        markerList.add(mPerth);
//
//        mSydney = mMap.addMarker(new MarkerOptions()
//                .position(SYDNEY)
//                .title("Sydney")
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
//        mSydney.setTag(0);
//        markerList.add(mSydney);
//
//        mBrisbane = mMap.addMarker(new MarkerOptions()
//                .position(BRISBANE)
//                .title("Brisbane")
//                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN)));
//        mBrisbane.setTag(0);
//        markerList.add(mBrisbane);
//
//        for(Marker m : markerList) {
//            LatLng latLng = new LatLng(m.getPosition().latitude, m.getPosition().longitude);
//            mMap.addMarker(new MarkerOptions().position(latLng));
//            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
//            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 2));
//            Log.d(TAG, "onMapReady: " +  m.getTitle());
//        }
//
//
////        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
////        googleMap.addMarker(new MarkerOptions()
////                .position(new LatLng(40.689247, -74.044502))
////                .title("Statue of Liberty")
////                .snippet("I hope to go there soon"));
////
////        CameraPosition Liberty = CameraPosition.builder()
////                .target(new LatLng(40.689247, -74.044502))
////                .zoom(5)
////                .bearing(0)
////                .tilt(45)
////                .build();
////
////        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(Liberty));
//
//
//
//    }



}