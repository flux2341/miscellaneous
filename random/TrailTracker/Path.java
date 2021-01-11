package com.example.flux.trailtracker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Path
{
    public String Name;
    public ArrayList<PathMarker> Markers;
    public ArrayList<PathVertex> Vertices;
    public Path()
    {
        SimpleDateFormat date_format = new SimpleDateFormat("yyyyMMdd-HHmmss");
        Name = date_format.format(new Date());
        Vertices = new ArrayList<PathVertex>();
        Markers = new ArrayList<PathMarker>();
    }
    public Path(String name)
    {
        Name = name;
        Vertices = new ArrayList<PathVertex>();
        Markers = new ArrayList<PathMarker>();
    }
}
