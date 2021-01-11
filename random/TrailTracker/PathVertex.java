package com.example.flux.trailtracker;
public class PathVertex
{
    public double Longitude, Latitude, Altitude;
    public float Speed, Distance;
    public long Time;
    public PathVertex(double longitude, double latitude, double altitude, float speed, float distance, long time)
    {
        Longitude = longitude;
        Latitude = latitude;
        Altitude = altitude;
        Speed = speed;
        Distance = distance;
        Time = time;
    }

}
