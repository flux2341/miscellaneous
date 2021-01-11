package clustering;

import java.util.ArrayList;

public class Clustering
{
    public static void main(String[] args)
    {
        MainWindow main_window = new MainWindow();
        main_window.setVisible(true);
    }
    
    
    public static ArrayList<Point> generatePoints(int n, double max)
    {
        
        ArrayList<Point> r = new ArrayList(n);
        

        for (int i=0; i<n; ++i)
        {
            double x = (2*Math.random()-1)*max;
            double y = (2*Math.random()-1)*max;
            double z = (2*Math.random()-1)*max;
            r.add(new Point(x, y, z));
        }
        
        return r;
    }
    
    
    public static ArrayList<Point> generatePoints(int clusters, double spread, int n, double max)
    {
        Point[] centers = new Point[clusters];
        for (int i=0; i<clusters; ++i)
        {
            centers[i] = new Point();
            centers[i].X = (2*Math.random()-1)*max;
            centers[i].Y = (2*Math.random()-1)*max;
            centers[i].Z = (2*Math.random()-1)*max;
        }
        
        ArrayList<Point> r = new ArrayList<Point>(n);
        for (int i=0; i<n; ++i)
        {
            Point center = centers[(int)(Math.random()*centers.length)];
            
            double phi = Math.random()*Math.PI*2.0f;
            double theta = Math.random()*Math.PI;
            double radius = Math.random()*spread*max;
            
            double x = center.X + radius*Math.sin(theta)*Math.cos(phi);
            double y = center.Y + radius*Math.sin(theta)*Math.sin(phi);
            double z = center.Z + radius*Math.cos(theta);
            
            r.add(new Point(x, y, z));
        }
        
        return r;
    }
    
    
    
    
    
    
    public static void removeOutliers(double percentage,
                                        ArrayList<Point> input,
                                        ArrayList<Point> output,
                                        ArrayList<Point> outliers)
    {
        output.clear();
        outliers.clear();
        
        int n_outliers = (int)(percentage*input.size());
        ArrayList<Double> outlier_distances = new ArrayList<Double>(n_outliers);
        for (int i=0; i<input.size(); ++i)
        {
            Point p = input.get(i);
            double average_distance = 0.0;
            for (int j=0; j<input.size(); ++j)
            {
                if (i != j)
                {
                    Point q = input.get(j);
                    average_distance += p.distanceTo(q);
                }
            }
            average_distance /= input.size()-1;
            
            if (outliers.size() < n_outliers)
            {
                outliers.add(p);
                outlier_distances.add(average_distance);
            }
            else
            {
                boolean was_swapped = false;
                for (int j=0; j<outliers.size(); ++j)
                {
                    if (average_distance > outlier_distances.get(j))
                    {
                        output.add(outliers.get(j));
                        outliers.remove(j);
                        outlier_distances.remove(j);
                        
                        outliers.add(p);
                        outlier_distances.add(average_distance);
                        
                        was_swapped = true;
                        break;
                    }
                }
                
                if (!was_swapped)
                {
                    output.add(p);
                }
            }
            
        }
    }
    
    
    
    
    
    public static ArrayList<Cluster> generateClusters(ArrayList<Point> points, int k)
    {
        ArrayList<Cluster> clusters = new ArrayList<Cluster>(points.size());
        for (int i=0; i<points.size(); ++i)
        {
            Cluster cluster = new Cluster(1);
            cluster.add(points.get(i));
            clusters.add(cluster);
        }
        
        
        while (clusters.size() > k)
        {
            int min_a = -1, min_b = -1;
            double min_d = Float.POSITIVE_INFINITY;
            
            for (int i=0; i<clusters.size(); ++i)
            {
                for (int j=i+1; j<clusters.size(); ++j)
                {
                    double d = clusters.get(i).distanceTo(clusters.get(j));
                    if (d < min_d)
                    {
                        min_d = d;
                        min_a = i;
                        min_b = j;
                    }
                }
            }
            
            Cluster merged = clusters.get(min_a).mergeWith(clusters.get(min_b));
            
            clusters.remove(min_b);
            clusters.remove(min_a);
            
            clusters.add(merged);
        }
        
        
        
        return clusters;
        
    }
    
    
    
    public static double getSilhouetteCoefficient(Point p, Cluster c, ArrayList<Cluster> clusters)
    {
        double a=0.0, b=Double.POSITIVE_INFINITY;
        for (int i=0; i<clusters.size(); ++i)
        {
            Cluster e = clusters.get(i);
            if (c == e)
            {
                for (int j=0; j<e.size(); ++j)
                {
                    Point q = e.get(j);
                    if (q != p)
                    {
                        a += q.distanceTo(p);
                    }
                }
                a /= (e.size()-1);
            }
            else
            {
                double bt = 0.0;
                for (int j=0; j<e.size(); ++j)
                {
                    Point q = e.get(j);
                    bt += p.distanceTo(q);
                }
                bt /= e.size();
                
                if (bt < b)
                {
                    b = bt;
                }
            }
        }
        return (b - a)/Math.max(a, b);
    }
    
    public static double getAverageSilhouetteCoefficient(ArrayList<Cluster> clusters)
    {
        double s = 0.0;
        int n = 0;
        for (int i=0; i<clusters.size(); ++i)
        {
            Cluster c = clusters.get(i);
            for (int j=0; j<c.size(); ++j)
            {
                Point p = c.get(j);
                s += getSilhouetteCoefficient(p, c, clusters);
                n++;
            }
        }
        return s/n;
    }
}
