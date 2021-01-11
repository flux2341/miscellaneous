package com.example.flux.trailtracker;

import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;


public class MainActivity extends ActionBarActivity implements LocationListener
{
    private static final String TAG = "MainActivity";
    private LocationManager location_manager;

    private EditText edittext_username;
    private Button button_login;

    private Button button_record;
    private Button button_settings;
    private Button button_history;
    private Button button_logout;

    private TextView text_latitude, text_longitude;
    private TextView text_vertices, text_time, text_distance, text_speed;

    private boolean recording;

    private Path current_path;
    private long total_distance;
    private long start_time;
    private long total_time;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edittext_username = (EditText)(findViewById(R.id.edittext_username));
        button_login = (Button)(findViewById(R.id.button_login));

        button_record = (Button)(findViewById(R.id.button_record));
        button_settings = (Button)(findViewById(R.id.button_settings));
        button_history = (Button)(findViewById(R.id.button_history));
        button_logout = (Button)(findViewById(R.id.button_logout));

        text_latitude = (TextView)(findViewById(R.id.text_latitude));
        text_longitude = (TextView)(findViewById(R.id.text_longitude));
        text_vertices = (TextView)(findViewById(R.id.text_vertices));
        text_time = (TextView)(findViewById(R.id.text_time));
        text_distance = (TextView)(findViewById(R.id.text_distance));
        text_speed = (TextView)(findViewById(R.id.text_speed));

        recording = false;

        if (Database.VariableExists("username", this))
        {
            ShowMenuMode();
            Database.Initialize(this);
        }
        else
        {
            ShowLoginMode();
        }

        // is location is off, ask user to turn it on
        location_manager = (LocationManager)getSystemService(LOCATION_SERVICE);
        /*if (!location_manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        }*/
    }


    private void HideAll()
    {
        edittext_username.setVisibility(View.GONE);
        button_login.setVisibility(View.GONE);
        button_record.setVisibility(View.GONE);
        button_settings.setVisibility(View.GONE);
        button_history.setVisibility(View.GONE);
        button_logout.setVisibility(View.GONE);
        text_latitude.setVisibility(View.GONE);
        text_longitude.setVisibility(View.GONE);
        text_vertices.setVisibility(View.GONE);
        text_time.setVisibility(View.GONE);
        text_distance.setVisibility(View.GONE);
        text_speed.setVisibility(View.GONE);
    }

    private void ShowLoginMode()
    {
        HideAll();
        edittext_username.setVisibility(View.VISIBLE);
        button_login.setVisibility(View.VISIBLE);
    }

    private void ShowMenuMode()
    {
        HideAll();
        button_record.setVisibility(View.VISIBLE);
        button_settings.setVisibility(View.VISIBLE);
        button_history.setVisibility(View.VISIBLE);
        button_logout.setVisibility(View.VISIBLE);
    }

    private void ShowRecordMode()
    {
        HideAll();
        button_record.setVisibility(View.VISIBLE);
        text_latitude.setVisibility(View.VISIBLE);
        text_longitude.setVisibility(View.VISIBLE);
        text_vertices.setVisibility(View.VISIBLE);
        text_time.setVisibility(View.VISIBLE);
        text_distance.setVisibility(View.VISIBLE);
        text_speed.setVisibility(View.VISIBLE);
    }


    public void loginButtonPressed(View view)
    {
        Database.SetVariable("username", edittext_username.getText().toString(), this);
        ShowMenuMode();
        Database.Initialize(this);
    }

    public void recordButtonPressed(View view)
    {
        if (recording)
        {
            stopRecording();
            ShowMenuMode();
        }
        else
        {
            startRecording();
            ShowRecordMode();
        }
    }

    public void settingsButtonPressed(View view)
    {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void historyButtonPressed(View view)
    {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
    }


    public void logoutButtonPressed(View view)
    {
        Database.RemoveVariable("username", this);
        ShowLoginMode();
    }



    private static String GetIndefiniteTime(long ms)
    {
        DecimalFormat df = new DecimalFormat("#.00");

        long seconds = ms/1000;
        if (seconds < 60.0)
        {
            return df.format(seconds) + " seconds";
        }
        else if (seconds < 3600.0)
        {
            return df.format(seconds / 60.0) + " minutes";
        }
        else if (seconds < 86400.0)
        {
            return df.format(seconds / 3600.0) + " hours";
        }
        else if (seconds < 31536000.0)
        {
            return df.format(seconds / 86400.0) + " days";
        }
        else
        {
            return df.format(seconds / 31536000.0) + " years";
        }
    }

    private static String GetIndefiniteDistance(float m)
    {
        DecimalFormat df = new DecimalFormat("#.00");
        if (m < 1000)
        {
            return m + " meters";
        }
        return df.format(m/1000.0) + " kilometers";
    }





    private void startRecording()
    {
        if (!recording)
        {
            recording = true;
            button_record.setText("Stop Recording");


            current_path = new Path();
            total_distance = 0;
            total_time = 0;
            start_time = System.currentTimeMillis();

            Criteria criteria = new Criteria();
            String provider = location_manager.getBestProvider(criteria, false);

            //Location last_location = location_manager.getLastKnownLocation(provider);
            //recordLocation(last_location);

            Log.d(TAG, "Recording");
            Log.d(TAG, "\tProvider: " + provider);

            int min_time = Integer.parseInt(Database.GetVariable("mintime", this));
            int min_distance = Integer.parseInt(Database.GetVariable("mindistance", this));

            location_manager.requestLocationUpdates(provider, min_time, min_distance, this);

        }
    }
    private void stopRecording()
    {
        if (recording)
        {
            recording = false;
            button_record.setText("Start Recording");
            text_vertices.setText("");
            text_distance.setText("");
            text_time.setText("");
            text_speed.setText("");

            location_manager.removeUpdates(this);
            Database.AddNewPath(current_path, this);
            current_path = null;

            Intent intent = new Intent(this, MapsActivity.class);
            startActivity(intent);
        }
    }

    private void recordLocation(Location location)
    {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        double altitude = location.getAltitude();
        float speed = location.getSpeed();
        float distance;
        if (current_path.Vertices.size() == 0)
        {
            distance = 0;
        }
        else
        {
            PathVertex last_vertex = current_path.Vertices.get(current_path.Vertices.size() - 1);
            distance = UtilityMethods.GetDistance(last_vertex, location);
        }
        long time = location.getTime();


        Log.d(TAG, "saving location");
        Log.d(TAG, "\tLatitude: " + latitude);
        Log.d(TAG, "\tLongitude: " + longitude);


        PathVertex vertex = new PathVertex(latitude,
                                            longitude,
                                            altitude,
                                            speed,
                                            distance,
                                            time);
        current_path.Vertices.add(vertex);

        total_distance += vertex.Distance;
        total_time = System.currentTimeMillis() - start_time;

        text_latitude.setText("Latitude: " + latitude);
        text_longitude.setText("Longitude: " + longitude);
        text_vertices.setText("Vertices: " + current_path.Vertices.size());
        text_distance.setText("Distance: " + GetIndefiniteDistance(total_distance));
        text_time.setText("Time: " + GetIndefiniteTime(total_time));
        text_speed.setText("Speed: " + speed + "m/s");
    }


    @Override
    protected void onPause()
    {
        super.onPause();
        stopRecording();
    }


    @Override
    protected void onResume()
    {
        super.onResume();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onLocationChanged(Location location)
    {
        recordLocation(location);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(String provider)
    {
        Toast.makeText(this, "Provider Enabled: " + provider,  Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        Toast.makeText(this, "Provider Disabled: " + provider, Toast.LENGTH_SHORT).show();
    }
}
