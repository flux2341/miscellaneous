package apriori;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class MainWindow extends JFrame implements ActionListener
{
    private JFileChooser file_chooser;
    private JTextArea input_text, output_text;
    public MainWindow()
    {
        file_chooser = new JFileChooser();
        
        input_text = new JTextArea();
        input_text.setBorder(new EmptyBorder(10,10,10,10));
        output_text = new JTextArea();
        output_text.setBorder(new EmptyBorder(10,10,10,10));
        
        JScrollPane input_scrollpane = new JScrollPane(input_text);
        JScrollPane output_scrollpane = new JScrollPane(output_text);
        
        JSplitPane split_pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                                                input_scrollpane,
                                                output_scrollpane);
        split_pane.setResizeWeight(0.5);
        super.getContentPane().add(split_pane);
        
        
        JMenuBar menu_bar = new JMenuBar();
        
        JMenu menu_file = new JMenu("File");
        
        JMenuItem item_open = new JMenuItem("Open");
        item_open.addActionListener(this);
        menu_file.add(item_open);
        
        JMenuItem item_save = new JMenuItem("Save");
        item_save.addActionListener(this);
        menu_file.add(item_save);
        
        JMenuItem item_generate = new JMenuItem("Generate");
        item_generate.addActionListener(this);
        menu_file.add(item_generate);
        
        menu_file.addSeparator();
        
        JMenuItem item_exit = new JMenuItem("Exit");
        item_exit.addActionListener(this);
        menu_file.add(item_exit);
        
        menu_bar.add(menu_file);
        
        
        
        JMenu menu_run = new JMenu("Run");
        
        JMenuItem item_run = new JMenuItem("Run");
        item_run.addActionListener(this);
        menu_run.add(item_run);
        
        menu_bar.add(menu_run);
        
        super.setJMenuBar(menu_bar);
        
        
        
        
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    }
    
    
    @Override
    public void actionPerformed(ActionEvent ae)
    {
        String command = ae.getActionCommand();
        if (command.equals("Open"))
        {
            int r = file_chooser.showOpenDialog(this);
            if (r == JFileChooser.APPROVE_OPTION)
            {
                try
                {
                    File file = file_chooser.getSelectedFile();
                    BufferedReader b = new BufferedReader(new FileReader(file));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = b.readLine()) != null)
                    {
                        sb.append(line).append('\n');
                    }
                    b.close();
                    input_text.setText(sb.toString());
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        else if (command.equals("Save"))
        {
            int r = file_chooser.showSaveDialog(this);
            if (r == JFileChooser.APPROVE_OPTION)
            {
                try
                {
                    File file = file_chooser.getSelectedFile();
                    BufferedWriter b = new BufferedWriter(new FileWriter(file));
                    b.write(input_text.getText());
                    b.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        else if (command.equals("Generate"))
        {
            JTextField tf_items = new JTextField();
            JTextField tf_sets = new JTextField();
            JTextField tf_min = new JTextField();
            JTextField tf_max = new JTextField();
            JComboBox cb_style = new JComboBox(new String[]{"fruits","gems",
                                                            "generic"});
            
            Object[] message = {
                "number of items", tf_items,
                "number of transactions", tf_sets,
                "min items per transaction", tf_min,
                "max items per transaction", tf_max,
                "item style", cb_style
            };
            
            int option = JOptionPane.showConfirmDialog(null, message,
                                                       "Transaction Generation Parameters",
                                                       JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.OK_OPTION)
            {
                return;
            }
            
            int n_items = Integer.parseInt(tf_items.getText());
            int n_sets = Integer.parseInt(tf_sets.getText());
            int min_per_set = Integer.parseInt(tf_min.getText());
            int max_per_set = Integer.parseInt(tf_max.getText());
            String style = (String)(cb_style.getSelectedItem());
            
            String[] n;
            if (style.equals("fruits"))
            {
                n = Apriori.fruits;
            }
            else if (style.equals("gems"))
            {
                n = Apriori.gemstones;
            }
            else
            {
                n = Apriori.generic;
            }
            
            if (n_items > n.length)
            {
                n_items = n.length;
            }
            
            ArrayList<String> temp = new ArrayList<>(Arrays.asList(n));
            ArrayList<String> names = new ArrayList(temp.subList(0, n_items));
            AprioriInput input = new AprioriInput(names, n_sets, min_per_set, max_per_set, -1, -1);
            
            input_text.setText(input.toString());
        }
        else if (command.equals("Run"))
        {
            // open prompt for minimum support/confidence parameters
            
            JTextField tf_min_support = new JTextField();
            JTextField tf_min_confidence = new JTextField();
            
            Object[] message = {
                "minumum support (%)", tf_min_support,
                "minimum confidence (%)", tf_min_confidence
            };
            
            int option = JOptionPane.showConfirmDialog(null, message,
                                                       "Apriori Parameters",
                                                       JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.OK_OPTION)
            {
                return;
            }
            
            double minimum_support = Double.parseDouble(tf_min_support.getText())/100.0;
            double minimum_confidence = Double.parseDouble(tf_min_confidence.getText())/100.0;
            
            
            StringBuilder s = new StringBuilder();
            s.append("minimum support: ");
            s.append(Apriori.formatPercentage(minimum_support)).append('\n');
            s.append("minimum confidence: ");
            s.append(Apriori.formatPercentage(minimum_confidence)).append('\n');
            s.append('\n');
            
            output_text.setText(s.toString());
            
            AprioriInput input = new AprioriInput(input_text.getText(),
                                                    minimum_support,
                                                    minimum_confidence);
            
            s.append("frequent item sets ~~~~~~~").append('\n');
            ArrayList<AprioriItemSet> fis = Apriori.findFrequentItemSets(input);
            for (int i=0; i<fis.size(); ++i)
            {
                double support = input.calculateSupport(fis.get(i));
                s.append(fis.get(i).toString(input.names));
                s.append("  [").append(Apriori.formatPercentage(support)).append("]\n");
            }
            
            
            output_text.setText(s.toString());
            
            s.append('\n');
            s.append("association rules ~~~~~~~~").append('\n');
            
            ArrayList<AprioriAssociationRule> rules = Apriori.findAssociationRules(fis, input);
            for (int i=0; i<rules.size(); ++i)
            {
                s.append(rules.get(i).toString(input.names)).append('\n');
            }
            
            output_text.setText(s.toString());
            
        }
        else if (command.equals("Exit"))
        {
            System.exit(0);
        }
    }
}
