package com.example.flux.trailtracker;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class UtilityMethods
{
    private static final String TAG = "UtilityMethods";





    public static String GetFileNameWithoutExtension(File file)
    {
        String name = file.getName();
        int pos = name.lastIndexOf(".");
        if (pos > 0)
        {
            name = name.substring(0, pos);
        }
        return name;
    }



    public static float GetDistance(PathVertex a, Location b)
    {
        float[] temp = new float[1];
        Location.distanceBetween(a.Latitude, a.Longitude, b.getLatitude(), b.getLongitude(), temp);
        return temp[0];
    }
    public static float GetDistance(PathVertex a, PathVertex b)
    {
        float[] temp = new float[1];
        Location.distanceBetween(a.Latitude, a.Longitude, b.Latitude, b.Longitude, temp);
        return temp[0];
    }
    public static float GetDistance(PathVertex a, LatLng b)
    {
        float[] temp = new float[1];
        Location.distanceBetween(a.Latitude, a.Longitude, b.latitude, b.longitude, temp);
        return temp[0];
    }


    public static String ReadFile(String file_name, Context context)
    {
        StringBuilder r = new StringBuilder();

        try
        {
            BufferedReader br = new BufferedReader(new FileReader(new File(file_name)));
            String line;

            while ((line = br.readLine()) != null)
            {
                r.append(line);
                r.append('\n');
            }
            br.close();
        }
        catch (IOException e)
        {
            Log.d(TAG, "error reading file", e);
        }
        return r.toString();
    }

    public static void SaveFile(String file_name, String text, Context context)
    {
        try
        {
            FileOutputStream outputStream = context.openFileOutput(file_name, Context.MODE_PRIVATE);
            outputStream.write(text.getBytes());
            outputStream.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    public static String DownloadFile(String url, Context context)
    {
        Log.d(TAG, "downloading file: "+url);
        String file_name = null;
        for (int attempts=0; attempts<1; ++attempts)
        {
            try
            {
                Log.d(TAG, "    attempt "+attempts);
                URL u = new URL(url);
                HttpURLConnection c = (HttpURLConnection)(u.openConnection());
                c.setRequestMethod("GET");
                c.setDoOutput(false);
                c.setDoInput(true);
                InputStream input_stream = c.getInputStream();

                file_name = url.substring(url.lastIndexOf('/') + 1);
                Log.d(TAG, "    file_name: \""+file_name+"\"");
                FileOutputStream fos = context.openFileOutput(file_name, Context.MODE_PRIVATE);

                int total_size = 0;

                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = input_stream.read(buffer)) > 0)
                {
                    fos.write(buffer, 0, len);
                    total_size += len;
                }

                fos.close();
                c.disconnect();

                Log.d(TAG, "    success, file size: "+total_size);

                return file_name;
            }
            catch (Exception e)
            {
                Log.d(TAG, "error downloading file", e);
                file_name = null;
            }
        }
        return file_name;
    }

    public static boolean UploadFile(String s_url, String file_name, Context context)
    {
        Log.d(TAG, "uploading file \""+file_name+"\" to \""+s_url+"\"");
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary =  "*****";


        int maxBufferSize = 1*1024*1024;

        try
        {

            if (!context.getFileStreamPath(file_name).exists())
            {
                Log.d(TAG, "file doesn't exist, returning...");
                return false;
            }


            FileInputStream fileInputStream = context.openFileInput(file_name);

            Log.d(TAG, s_url);
            URL url = new URL(s_url);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");

            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);

            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.writeBytes(twoHyphens + boundary + lineEnd);
            outputStream.writeBytes("Content-Disposition: form-data; name=\"uploadedfile\";filename=\"" + file_name +"\"" + lineEnd);
            outputStream.writeBytes(lineEnd);

            int bytesAvailable = fileInputStream.available();
            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
            byte[] buffer = new byte[bufferSize];



            while ((fileInputStream.read(buffer, 0, bufferSize)) > 0)
            {
                outputStream.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
            }

            outputStream.writeBytes(lineEnd);
            outputStream.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            Log.d(TAG, "response code: "+connection.getResponseCode()+", message: "+connection.getResponseMessage());

            fileInputStream.close();
            outputStream.flush();
            outputStream.close();

            Log.d(TAG, "success!");
            return true;
        }
        catch (Exception e)
        {
            Log.d(TAG, "error posting file", e);
            return false;
        }
    }

    public static String DownloadText(String url, Context context)
    {
        try
        {
            DefaultHttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet(url);
            HttpResponse response = client.execute(request);
            BufferedHttpEntity buf = new BufferedHttpEntity(response.getEntity());
            BufferedReader br = new BufferedReader(new InputStreamReader(buf.getContent()));

            StringBuilder r = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null)
            {
                r.append(line + "\n");
            }
            return r.toString();
        }
        catch (Exception e)
        {
            Log.d(TAG, "error downloading text", e);
            return null;
        }
    }
}
