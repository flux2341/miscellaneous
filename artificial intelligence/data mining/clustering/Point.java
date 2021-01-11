package clustering;

public class Point
{
    public double X, Y, Z;
    public Point()
    {
        this(0.0f, 0.0f, 0.0f);
    }
    
    public Point(double x, double y, double z)
    {
        X = x;
        Y = y;
        Z = z;
    }
    
    public double distanceTo(Point p)
    {
        double x = X - p.X;
        double y = Y - p.Y;
        double z = Z - p.Z;
        return (Math.sqrt(x*x + y*y + z*z));
    }
}
