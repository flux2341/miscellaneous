package com.example.flux.trailtracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.io.File;
import java.util.ArrayList;

public class Database
{
    private Database() {}


    private static ArrayList<Path> paths;
    private static int selected_path;

    public static void Initialize(Context context)
    {
        paths = new ArrayList<Path>();
        selected_path = -1;

        //SynchronizeWithServer(context);
        //LoadPathsFromFiles(context);

        if (!VariableExists("mintime", context))
        {
            SetVariable("mintime", "400", context);
        }

        if (!VariableExists("mindistance", context))
        {
            SetVariable("mindistance", "1", context);
        }
    }





    private static SharedPreferences shared_preferences;

    public static SharedPreferences getSharedPreferences(Context context)
    {
        if (shared_preferences == null)
        {
            shared_preferences = PreferenceManager.getDefaultSharedPreferences(context);
        }
        return shared_preferences;
    }


    public static String GetVariable(String name, Context context)
    {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.getString(name, null);
    }
    public static boolean SetVariable(String name, String value, Context context)
    {
        SharedPreferences preferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(name, value);
        return editor.commit();
    }
    public static boolean RemoveVariable(String name, Context context)
    {
        SharedPreferences preferences = getSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(name);
        return editor.commit();
    }
    public static boolean VariableExists(String name, Context context)
    {
        SharedPreferences preferences = getSharedPreferences(context);
        return preferences.contains(name);
    }

    public static ArrayList<Path> GetPaths()
    {
        return paths;
    }

    public static void AddNewPath(Path path, Context context)
    {
        AddPath(path);
        SavePath(path, context);
        UploadPath(path, context);
    }
    public static void AddPath(Path path)
    {
        paths.add(path);
        selected_path = paths.size() - 1;
    }

    public static Path GetSelectedPath()
    {
        return paths.get(selected_path);
    }

    public static void SetSelectedPath(int index)
    {
        selected_path = index;
    }



    private static final String server_url = "http://fluxdemo.nfshost.com/tracerserver.php";
    public static void SynchronizeWithServer(Context context)
    {
        String username = GetVariable("username", context);
        String str_files = UtilityMethods.DownloadText(server_url+"?username="+username+"&action=list", context);
        String[] files = str_files.split(",");
        for (int i=0; i<files.length; ++i)
        {
            File file = new File(files[i]);
            if (!file.exists())
            {
                UtilityMethods.DownloadFile(server_url + username + "/" + files[i], context);
            }
        }
    }

    public static void SavePath(Path path, Context context)
    {
        String file_name = path.Name + ".csv";
        StringBuilder sb = new StringBuilder();

        for (int i=0; i<path.Markers.size(); ++i)
        {
            PathMarker marker = path.Markers.get(i);
            sb.append(marker.Latitude).append(',');
            sb.append(marker.Longitude).append(',');
            if (i < path.Markers.size() - 1) {
                sb.append(marker.Name).append(',');
            }
        }
        sb.append('\n');

        for (int i=0; i<path.Vertices.size(); ++i)
        {
            PathVertex vertex = path.Vertices.get(i);
            sb.append(vertex.Latitude).append(',');
            sb.append(vertex.Longitude).append(',');
            sb.append(vertex.Altitude).append(',');
            sb.append(vertex.Distance).append(',');
            sb.append(vertex.Speed).append(',');
            sb.append(vertex.Time).append(',');
            sb.append('\n');
        }

        UtilityMethods.SaveFile(file_name, sb.toString(), context);

    }
    public static void LoadPathsFromFiles(Context context)
    {
        File root = new File("");
        File[] files = root.listFiles();
        for (File file : files)
        {
            if (file.isFile())
            {
                AddPath(LoadPathFromFile(file, context));
            }
        }
    }
    public static Path LoadPathFromFile(File file, Context context)
    {
        String path_name = UtilityMethods.GetFileNameWithoutExtension(file);
        Path path = new Path(path_name);
        String[] lines = UtilityMethods.ReadFile(file.getName(), context).split("\n");
        String[] str_markers = lines[0].split(",");
        for (int i=0; i<str_markers.length; i+=3)
        {
            double latitude = Double.parseDouble(str_markers[i]);
            double longitude = Double.parseDouble(str_markers[i+1]);
            String name = str_markers[i+2];
            path.Markers.add(new PathMarker(name, latitude, longitude));
        }
        for (int i=1; i<lines.length; ++i)
        {
            String[] line = lines[i].split(",");
            double latitude = Double.parseDouble(line[0]);
            double longitude = Double.parseDouble(line[1]);
            double altitude = Double.parseDouble(line[2]);
            float speed = Float.parseFloat(line[3]);
            float distance = Float.parseFloat(line[4]);
            long time = Long.parseLong(line[5]);
            path.Vertices.add(new PathVertex(latitude,
                                                longitude,
                                                altitude,
                                                speed,
                                                distance,
                                                time));
        }

        return path;
    }

    public static void UploadPath(Path path, Context context)
    {
        String username = GetVariable("username", context);
        UtilityMethods.UploadFile(server_url + "?username="+username+"&action=upload", path.Name + ".csv", context);
    }

}
