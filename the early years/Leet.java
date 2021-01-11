/* Hello
 * This is my code
 * Don't steal it
 * I'm  a hustlah
 * */
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ButtonGroup;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;

public class Leet extends JApplet implements ActionListener
{
	private JLabel title, text1, text2;
	private JTextArea enter, result;
	private JButton click;
	private JPanel[] panels;
	private JCheckBox noobboxx, wordbox;
	private JRadioButton numbers, slashes, altcaps;
	private String[] normalwordz = {"you","the","fear","elite","that"};
	private String[] wordz = {"joo","teh","pheer","leet","taht"};
	private String normal = "abcdefghijklmnopqrstuvqxyz";
	private String[] lslashes = {"/-\\","|3","(","|)","3","|=","9","|-|","|","_|","|<","|","|\\/|","|\\|","()","|?",".()","|2","s","+","|_|","\\/","\\/\\/",")(","y","-/_"};
	private String[] lnumbers = {"4","8","(","1)","3","1=","9","1-1","1","_1","1<","1","1^^1","n","0","|?","q","|2","5","7","u","v","9","x","y","2"};
	private boolean ty = true;
	public void init()
	{
		setSize(200,400);
		panels = new JPanel[7];
		/*Just out of spite:*/ 
		for(int i=0; i<7; i++)
		{
			panels[i] = new JPanel();
			if (i==6)
			{
				panels[i] = new JPanel(new GridLayout(6,1));
				setContentPane(panels[i]);
				for(int o=0;o<6;o++)
				{
					panels[i].add(panels[o]);
					if (o==0)
					{
						title = new JLabel();
						panels[o].add(title);
					}
					else if (o==1)
					{
						text1 = new JLabel();
						enter = new JTextArea(5,20);
						panels[o].add(text1);
						panels[o].add(enter);
					}
					else if (o==2)
					{
						slashes = new JRadioButton("",true);
						numbers = new JRadioButton("",false);
						altcaps = new JRadioButton("",false);
						ButtonGroup omgbutton = new ButtonGroup();
						slashes.addActionListener(this);
						numbers.addActionListener(this);
						altcaps.addActionListener(this);
						omgbutton.add(numbers);
						omgbutton.add(slashes);
						omgbutton.add(altcaps);
						panels[o].add(slashes);
						panels[o].add(numbers);
						panels[o].add(altcaps);
					}
					else if (o==3)
					{
						click = new JButton();
						click.addActionListener(this);
						panels[o].add(click);
					}
					else if (o==4)
					{
						text2 = new JLabel();
						result = new JTextArea(5,20);
						panels[o].add(text2);
						panels[o].add(result);
					}
					else if (o==5)
					{
						wordbox = new JCheckBox();
						wordbox.addActionListener(this);
						noobboxx = new JCheckBox("I are teh noob!");
						noobboxx.addActionListener(this);
						panels[o].add(noobboxx);
						panels[o].add(wordbox);
					}
				}
			}
		}
		slashes.doClick();
	}
	public void actionPerformed(ActionEvent e)
	{
		ty = true;
		String start = enter.getText();
		String hold1 = "", hold2 = "", holdfinal = "";
		int f = start.length();
		/*Switches words out, for awesomeness:*/
		if (wordbox.isSelected())
		{
			for (int o = 0; o<normalwordz.length; o++)
			{
				if (start.indexOf(normalwordz[o])!=-1)
				{
					int u = start.indexOf(normalwordz[o]);
					start = start.substring(0,u) + wordz[o] + start.substring(u+normalwordz[o].length());
				}
			}
		}
		/*For teh noobs!*/
		if (noobboxx.isSelected())
		{
			title.setText("1337 Converter // by Flux");
			text1.setText("Enter Text:");
			text2.setText("Holla Back:");
			click.setText("Click");
			slashes.setText("Slashes");
			numbers.setText("Numbers");
			altcaps.setText("Alternating caps");
			wordbox.setText("Change Words");
		}
		else 
		{
			if (slashes.isSelected())
			{
				title.setText("|33-| (()|\\|\\/3|2-|3|2  // |3|/ |=|_|_|)(");
				text1.setText("|\\|-| |2 -|)(-|:");
				text2.setText("-|)-(  |2 _/-|-|:");
				click.setText("(|_|(|(");
				slashes.setText("_/-|/-\\_/-3_/-");
				numbers.setText("|\\||_|/\\/\\|33|2_/-");
				altcaps.setText("/-\\-|3|2|\\|/-\\-|||\\|()/ (/-\\|?_/-");
				wordbox.setText("(|-|/-\\|\\|( 3 \\-|-/()|2|)_/-");
			}
			else if (e.getSource() == numbers)
			{
				title.setText("1337 (01\\1\\/3|273|2 // |31/ 1=1_1_1)(");
				text1.setText("31\\173|2 73}{7:");
				text2.setText("71-13 |2351_11_7:");
				click.setText("(1_1(1<");
				slashes.setText("51451-135");
				numbers.setText("1\\11_11^^183|25");
				altcaps.setText("4173|21\\14711\\19 (4|?5");
				wordbox.setText("(1-141\\193 \\/\\/0|21)5");
			}
			else if (e.getSource() == altcaps)
			{
				title.setText("LeEt CoNvErTeR // By FlUx");
				text1.setText("EnTeR TeXt:");
				text2.setText("ThE ReSuLt:");
				click.setText("ClIcK");
				slashes.setText("SlAsHeS");
				numbers.setText("NuMbErS");
				altcaps.setText("AlTeRnAtInG CaPs");
				wordbox.setText("ChAnGe WoRdS");
			}
		}
		/*Actually converts the text:*/
		if (e.getSource() == click)
		{
			if (slashes.isSelected())
			{
				for(int i=0; f!=i; i++)
				{
					hold1 = start.substring(i,i+1);
					hold1 = hold1.toLowerCase();
					int o = normal.indexOf(hold1);
					if (o!=-1)
					{
						hold2 = lslashes[o];
					}
					else
					{
						hold2 = hold1;
		  			}
		  			holdfinal = (holdfinal+hold2);
		  		}
		  	}
		  	if (numbers.isSelected())
		  	{
		  		for(int i=0; i!=f; i++)
		  		{
		  			hold1 = start.substring(i,i+1);
		  			hold1 = hold1.toLowerCase();
		  			int o = normal.indexOf(hold1);
		  			if (o!=-1)
		  			{
		  				hold2 = lnumbers[o];
		  			}
		  			else
		  			{
		  				hold2 = hold1;
		  			}
		  			holdfinal = (holdfinal+hold2);
		  		}
		  	}
		  	if (altcaps.isSelected())
		  	{
		  		ty = true;
		  		for(int i=0; i<f; i++)
		  		{
		  			hold1 = start.substring(i,i+1);
		  			if (hold1.equals(" "))
		  			{
		  				ty = false;
		  			}
					if (ty == true)
					{
						hold2 = hold1.toUpperCase();
						ty = false;
					}
					else if (ty == false) 
					{
						hold2 = hold1.toLowerCase();
						ty = true;
					}
					holdfinal = holdfinal+hold2;
		  		}
		  	}
			result.setText("");
			result.setText(holdfinal);
			showStatus("OMG TRANSLATED LOL");   //OMG LOL!
			//by Flux
		  }
	}
}