package clustering;

public interface Transformer
{
    public abstract Point transform(Point p);
    public abstract Point invert(Point p);
}



class Translator implements Transformer
{
    public double X, Y, Z;
    public Translator(double x, double y, double z)
    {
        X = x;
        Y = y;
        Z = z;
    }
    @Override
    public Point transform(Point p)
    {
        return new Point(p.X+X, p.Y+Y, p.Z+Z);
    }
    
    @Override
    public Point invert(Point p)
    {
        return new Point(p.X-X, p.Y-Y, p.Z-Z);
    }
}






class Scaler implements Transformer
{
    public double X, Y, Z;
    public Scaler(double x, double y, double z)
    {
        X = x;
        Y = y;
        Z = z;
    }
    
    @Override
    public Point transform(Point p)
    {
        return new Point(p.X*X, p.Y*Y, p.Z*Z);
    }

    @Override
    public Point invert(Point p)
    {
        return new Point(p.X/X, p.Y/Y, p.Z/Z);
    }
    
}




class RotatorXY implements Transformer
{
    private double angle, cosa, sina;
    public RotatorXY(double angle)
    {
        setAngle(angle);
    }
    
    public double getAngle()
    {
        return angle;
    }
    public void setAngle(double angle)
    {
        this.angle = angle;
        cosa = Math.cos(angle);
        sina = Math.sin(angle);
    }
        public Point transform(Point p)
        {
            return new Point(p.X*cosa + p.Y*sina,
                -p.X*sina + p.Y*cosa,
                p.Z);
        }
        public Point invert(Point p)
        {
            return new Point(p.X*cosa - p.Y*sina,
                p.X*sina + p.Y*cosa,
                p.Z);
        }
}


class RotatorXZ implements Transformer
{
    private double angle, cosa, sina;
    public RotatorXZ(double angle)
    {
        setAngle(angle);
    }
    
    public double getAngle()
    {
        return angle;
    }
    public void setAngle(double angle)
    {
        this.angle = angle;
        cosa =  Math.cos(angle);
        sina =  Math.sin(angle);
    }
    
    public Point transform(Point p)
    {
        return new Point(p.X*cosa + p.Z*sina,
            p.Y,
            -p.X*sina + p.Z*cosa);
    }
    public Point invert(Point p)
    {
        return new Point(p.X*cosa - p.Z*sina,
            p.Y,
            p.X*sina + p.Z*cosa);
    }
}

class RotatorYZ implements Transformer
{
    private double angle, cosa, sina;
    public RotatorYZ(double angle)
    {
        setAngle(angle);
    }
    
    public double getAngle()
    {
        return angle;
    }
    public void setAngle(double angle)
    {
        this.angle = angle;
        cosa =  Math.cos(angle);
        sina =  Math.sin(angle);
    }
    
    public Point transform(Point p)
    {
        return new Point(p.X,
            p.Y*cosa + p.Z*sina,
            -p.Y*sina + p.Z*cosa);
    }
    public Point invert(Point p)
    {
        return new Point(p.X,
            p.Y*cosa - p.Z*sina,
            p.Y*sina + p.Z*cosa);
    }
    
}