package com.example.flux.trailtracker;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;


public class SettingsActivity extends ActionBarActivity
{

    private static EditText edittext_min_time, edittext_min_distance;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        edittext_min_time = (EditText)(findViewById(R.id.edittext_min_time));
        edittext_min_distance = (EditText)(findViewById(R.id.edittext_min_distance));

        edittext_min_time.setText(Database.GetVariable("mintime", this));
        edittext_min_distance.setText(Database.GetVariable("mindistance", this));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void saveClicked(View view)
    {
        String min_time = edittext_min_time.getText().toString();
        String min_distance = edittext_min_distance.getText().toString();

        Database.SetVariable("mintime", min_time, this);
        Database.SetVariable("mindistance", min_distance, this);

        finish();
    }

    public void cancelClicked(View view)
    {
        finish();
    }
}
