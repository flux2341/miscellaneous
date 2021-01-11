package clustering;

import java.util.ArrayList;

public class Cluster
{
    public enum DistanceDefinition {Nearest, Farthest, Average, Centers};
    
    public static DistanceDefinition distance_definition = DistanceDefinition.Average;
    
    public ArrayList<Point> points;
    
    public Cluster()
    {
        points = new ArrayList<Point>();
    }
    public Cluster(int size)
    {
        points = new ArrayList<Point>(size);
    }
    
    public void add(Point p)
    {
        points.add(p);
    }
    
    public void add(ArrayList<Point> p)
    {
        points.addAll(p);
    }
    
    public Point get(int i)
    {
        return points.get(i);
    }
    
    public int size()
    {
        return points.size();
    }
    
    public Cluster mergeWith(Cluster c)
    {
        Cluster r = new Cluster(points.size()+c.points.size());
        r.add(points);
        r.add(c.points);
        return r;
    }
    
    public Point getCenter()
    {
        Point r = new Point(0.0f, 0.0f, 0.0f);
        
        for (int i=0; i<points.size(); ++i)
        {
            Point p = points.get(i);
            r.X += p.X;
            r.Y += p.Y;
            r.Z += p.Z;
        }
        
        r.X /= points.size();
        r.Y /= points.size();
        r.Z /= points.size();
        
        return r;
    }
    
    public double nearestDistanceTo(Cluster c)
    {
        double r = Float.POSITIVE_INFINITY;
        for (int i=0; i<points.size(); ++i)
        {
            Point p = points.get(i);
            for (int j=0; j<c.points.size(); ++j)
            {
                Point q = c.points.get(j);
                double distance = p.distanceTo(q);
                if (distance < r)
                {
                    r = distance;
                }
            }
        }
        return r;
    }
    
    public double farthestDistanceTo(Cluster c)
    {
        double r = Float.NEGATIVE_INFINITY;
        for (int i=0; i<points.size(); ++i)
        {
            Point p = points.get(i);
            for (int j=0; j<c.points.size(); ++j)
            {
                Point q = c.points.get(j);
                double distance = p.distanceTo(q);
                if (distance > r)
                {
                    r = distance;
                }
            }
        }
        return r;
    }
    
    public double averageDistanceTo(Cluster c)
    {
        double r = 0.0f;
        for (int i=0; i<points.size(); ++i)
        {
            Point p = points.get(i);
            for (int j=0; j<c.points.size(); ++j)
            {
                Point q = c.points.get(j);
                r += p.distanceTo(q);
            }
        }
        return r/(points.size()*c.points.size());
    }
    
    public double centerDistanceTo(Cluster c)
    {
        Point p = this.getCenter();
        Point q = c.getCenter();
        
        return p.distanceTo(q);
    }
    
    
    public double distanceTo(Cluster c)
    {
        if (distance_definition == DistanceDefinition.Nearest)
        {
            return nearestDistanceTo(c);
        }
        else if (distance_definition == DistanceDefinition.Farthest)
        {
            return farthestDistanceTo(c);
        }
        else if (distance_definition == DistanceDefinition.Average)
        {
            return averageDistanceTo(c);
        }
        else if (distance_definition == DistanceDefinition.Centers)
        {
            return centerDistanceTo(c);
        }
        return 0.0f;
    }
}
