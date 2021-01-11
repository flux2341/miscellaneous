package com.example.flux.trailtracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.widget.EditText;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapsActivity extends FragmentActivity implements GoogleMap.OnMapClickListener
{

    private GoogleMap map; // Might be null if Google Play services APK is not available.
    private Path path;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #map} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded()
    {
        // Do a null check to confirm that we have not already instantiated the map.
        if (map == null) {
            // Try to obtain the map from the SupportMapFragment.
            map = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (map != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #map} is not null.
     */
    private void setUpMap()
    {
        //map.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

        map.setOnMapClickListener(this);

        //map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);

        path = Database.GetSelectedPath();
        PolylineOptions path_options = new PolylineOptions();//.geodesic(true);
        for (int i=0; i<path.Vertices.size(); ++i)
        {
            PathVertex vertex = path.Vertices.get(i);
            path_options.add(new LatLng(vertex.Latitude, vertex.Longitude));
        }
        map.addPolyline(path_options);

        /*map.addPolyline(new PolylineOptions().geodesic(true)
                .add(new LatLng(-33.866, 151.195))  // Sydney
                .add(new LatLng(-18.142, 178.431))  // Fiji
                .add(new LatLng(21.291, -157.821))  // Hawaii
                .add(new LatLng(37.423, -122.091))  // Mountain View
        );*/


        // map.addMarker(new MarkerOptions().position(ll).title(value.toString()));
    }

    @Override
    public void onMapClick(LatLng latLng)
    {
        /*double min_distance = Double.POSITIVE_INFINITY;
        PathVertex chosen_vertex = null;
        for (int i=0; i<path.Vertices.size(); ++i)
        {
            PathVertex vertex = path.Vertices.get(i);
            double distance = UtilityMethods.GetDistance(vertex, latLng);
            if (distance < min_distance)
            {
                min_distance = distance;
                chosen_vertex = vertex;
            }
        }*/

        final LatLng ll = latLng;
        final EditText input = new EditText(this);
        new AlertDialog.Builder(MapsActivity.this)
                .setTitle("Enter Marker Text")
                //.setMessage("Enter Marker Text")
                .setView(input)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        Editable value = input.getText();
                        map.addMarker(new MarkerOptions().position(ll).title(value.toString()));
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        }).show();





    }
}
