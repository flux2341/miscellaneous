package clustering;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class MainWindow extends JFrame implements ActionListener
{
    private MainPanel main_panel;
    
    public MainWindow()
    {
        main_panel = new MainPanel();
        setLayout(new BorderLayout());
        getContentPane().add(main_panel, BorderLayout.CENTER);
        
        
        
        JMenuBar menu_bar = new JMenuBar();
        
        JMenu menu_steps = new JMenu("Steps");
        
        JMenuItem item_open = new JMenuItem("1) Generate Data");
        item_open.addActionListener(this);
        menu_steps.add(item_open);
        
        JMenuItem item_save = new JMenuItem("2) Remove Outliers");
        item_save.addActionListener(this);
        menu_steps.add(item_save);
        
        JMenuItem item_generate = new JMenuItem("3) Create Clusters");
        item_generate.addActionListener(this);
        menu_steps.add(item_generate);
        
        menu_steps.addSeparator();
        
        JMenuItem item_exit = new JMenuItem("Exit");
        item_exit.addActionListener(this);
        menu_steps.add(item_exit);
        
        menu_bar.add(menu_steps);
        
        super.setJMenuBar(menu_bar);
        
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent ae)
    {
        String command = ae.getActionCommand();
        if (command.equals("1) Generate Data"))
        {
            JTextField tf_clusters = new JTextField();
            tf_clusters.setText("20");
            JTextField tf_spread = new JTextField();
            tf_spread.setText("0.5");
            JTextField tf_points = new JTextField();
            tf_points.setText("500");
            JTextField tf_max = new JTextField();
            tf_max.setText("50");
            
            
            Object[] message = {
                "# of clusters", tf_clusters,
                "spread [0.0,1.0]", tf_spread,
                "# of points", tf_points,
                "scale", tf_max
            };
            
            int option = JOptionPane.showConfirmDialog(null, message,
                                                       "Data Generation Parameters",
                                                       JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.OK_OPTION)
            {
                return;
            }
            
            int n_clusters = Integer.parseInt(tf_clusters.getText());
            double spread = Double.parseDouble(tf_spread.getText());
            int n_points = Integer.parseInt(tf_points.getText());
            double max = Double.parseDouble(tf_max.getText());
            
            main_panel.generatePoints(n_clusters, spread, n_points, max);
        }
        else if (command.equals("2) Remove Outliers"))
        {
            JTextField tf_percentage = new JTextField();
            tf_percentage.setText("0.1");
            Object[] message = {"percentage to remove [0.0,1.0]", tf_percentage};
            int option = JOptionPane.showConfirmDialog(null, message,
                                                       "Outlier Removal Parameters",
                                                       JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.OK_OPTION)
            {
                return;
            }
            
            double percentage = Double.parseDouble(tf_percentage.getText());
            main_panel.removeOutliers(percentage);
            
        }
        else if (command.equals("3) Create Clusters"))
        {
            JTextField tf_clusters = new JTextField();
            tf_clusters.setText("3");
            JComboBox cb_dd = new JComboBox(new String[]{"Nearest", "Farthest", "Average", "Centers"});
            
            
            
            Object[] message = {"# of clusters", tf_clusters, "distance definition", cb_dd};
            
            
            int option = JOptionPane.showConfirmDialog(null, message,
                                                       "Cluster Creation Parameters",
                                                       JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.OK_OPTION)
            {
                return;
            }
            
            int n_clusters = Integer.parseInt(tf_clusters.getText());
            String s_dd = (String)(cb_dd.getSelectedItem());
            Cluster.DistanceDefinition dd = Cluster.DistanceDefinition.Average;
            if (s_dd.equals("Nearest"))
            {
                dd = Cluster.DistanceDefinition.Nearest;
            }
            else if (s_dd.equals("Farthest"))
            {
                dd = Cluster.DistanceDefinition.Farthest;
            }
            else if (s_dd.equals("Average"))
            {
                dd = Cluster.DistanceDefinition.Average;
            }
            else if (s_dd.equals("Centers"))
            {
                dd = Cluster.DistanceDefinition.Centers;
            }
            
            double s = main_panel.generateClusters(n_clusters, dd);
            JOptionPane.showMessageDialog(this, "Silhouette Coefficient: "+s);
        }
        else if (command.equals("Exit"))
        {
            System.exit(0);
        }
    }
}
