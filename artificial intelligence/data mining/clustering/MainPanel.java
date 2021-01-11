package clustering;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import javax.swing.JPanel;

public class MainPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener
{
    private int mx, my;
    
    private RotatorXZ view_yaw;
    private RotatorYZ view_pitch;
    private Translator view_location;
    
    private ArrayList<Point> points;
    
    private ArrayList<Point> outliers;
    private ArrayList<Point> non_outliers;
    
    private ArrayList<Cluster> clusters;
    
    public MainPanel()
    {
        view_yaw = new RotatorXZ(0.0);
        view_pitch = new RotatorYZ(0.0);
        view_location = new Translator(0.0f, 0.0f, -500.0);
        
        //points = Clustering.generatePoints(500, 100.0);
        //points = Clustering.generatePoints(10, 0.5, 500, 200.0);
        points = null;
        outliers = null;
        non_outliers = null;
        clusters = null;
        
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
    }
    
    
    public void generatePoints(int n_clusters, double spread, int n_points, double max)
    {
        points = Clustering.generatePoints(n_clusters, spread, n_points, max);
        
        outliers = null;
        non_outliers = null;
        clusters = null;
        
        repaint();
    }
    
    public void removeOutliers(double percentage)
    {
        if (points != null)
        {
            outliers = new ArrayList<Point>();
            non_outliers = new ArrayList<Point>();
            Clustering.removeOutliers(percentage, points, non_outliers, outliers);
        }
        
        repaint();
    }
    
    public double generateClusters(int n_clusters, Cluster.DistanceDefinition dd)
    {
        if (non_outliers != null)
        {
            Cluster.distance_definition = dd;
            clusters = Clustering.generateClusters(non_outliers, n_clusters);
            
            repaint();
            
            return Clustering.getAverageSilhouetteCoefficient(clusters);
        }
        
        return 0.0;
    }
    
    
    private Point worldToScreen(Point p)
    {
        p = view_yaw.invert(p);
        p = view_pitch.invert(p);
        p = view_location.invert(p);
        
        if (p.Z < 0.0f)
        {
            p.X = p.Y = p.Z = Double.NaN;
        }
        else
        {
            double focus = 0.001;
            p.X /= p.Z*focus;
            p.Y /= p.Z*focus;
        }
        
        return p;
    }
    
    
    private void drawPoint(Point p0, int width, int height, Graphics g)
    {
        int radius = 2;
        
        
        Point p = worldToScreen(p0);
        if (!Double.isNaN(p.X))
        {
            int px = width/2 + (int)(p.X);
            int py = height/2 - (int)(p.Y);

            if (radius == 1)
            {
                g.drawLine(px, py, px, py);
            }
            else
            {
                g.fillOval(px-radius, py-radius, 2*radius, 2*radius);
            }
        }
    }
    
    private static Color getAlphaColor(Color c, double alpha)
    {
        return new Color(c.getRed(), c.getGreen(), c.getBlue(), (int)(alpha*255));
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
        
        
        int width = getWidth(), height = getHeight();
        
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, width, height);
        
        
        double alpha = 0.7;
        if (clusters == null)
        {
            g.setColor(getAlphaColor(Color.WHITE, alpha));
            if (non_outliers == null)
            {
                if (points != null)
                {
                    for (int i=0; i<points.size(); ++i)
                    {
                        drawPoint(points.get(i), width, height, g);
                    }
                }
            }
            else
            {
                for (int i=0; i<non_outliers.size(); ++i)
                {
                    drawPoint(non_outliers.get(i), width, height, g);
                }
                
            }
        }
        else
        {
            Color[] colors = new Color[]{Color.ORANGE, Color.GREEN, Color.MAGENTA,
                                         Color.YELLOW, Color.CYAN};
            for (int i=0; i<clusters.size(); ++i)
            {
                Cluster cluster = clusters.get(i);
                g.setColor(getAlphaColor(colors[i], alpha));
                for (int j=0; j<cluster.points.size(); ++j)
                {
                    drawPoint(cluster.points.get(j), width, height, g);
                }
                
            }
        }
        
        if (outliers != null)
        {
            g.setColor(getAlphaColor(Color.RED, alpha));
            for (int i=0; i<outliers.size(); ++i)
            {
                drawPoint(outliers.get(i), width, height, g);
            }
        }
        
    }
    
    
    
    
    @Override
    public void mouseClicked(MouseEvent me) {}
    
    @Override
    public void mousePressed(MouseEvent me)
    {
        mx = me.getX();
        my = me.getY();
    }

    @Override
    public void mouseReleased(MouseEvent me) {}

    @Override
    public void mouseEntered(MouseEvent me) {}

    @Override
    public void mouseExited(MouseEvent me) {}

    @Override
    public void mouseDragged(MouseEvent me)
    {
        int x = me.getX();
        int y = me.getY();
        int dx = x - mx;
        int dy = y - my;
        
        double rs = 0.01f;
        
        view_yaw.setAngle(view_yaw.getAngle() + dx*rs);
        view_pitch.setAngle(view_pitch.getAngle() - dy*rs);

        mx = x;
        my = y;

        repaint();
    }

    @Override
    public void mouseMoved(MouseEvent me) {}

    @Override
    public void mouseWheelMoved(MouseWheelEvent mwe)
    {
        view_location.Z += 10f * mwe.getWheelRotation();
        repaint();
    }
}
