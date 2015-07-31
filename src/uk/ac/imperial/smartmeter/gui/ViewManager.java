package uk.ac.imperial.smartmeter.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class ViewManager extends JPanel implements ActionListener{

	//private HelpView hv;
	//private DiagView dv;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String help = "HELP";
	private static final String req  = "REQ";
	private static final String diag = "DIAG";
	private JTextArea text;
	private JComboBox<Integer> startHour;
	private JComboBox endHour;
	private JComboBox startDay;
	private JComboBox endDay;
	private JButton submit;
	private JComboBox devices;
	
	public ViewManager()
	{
		initialiseReqComponents();
		setupView();
	}
	private void initialiseReqComponents() {
		// TODO Auto-generated method stub
		startHour = new JComboBox<Integer>(new Integer[]{1,2,3});
		startDay = new JComboBox(new String[]{"M","T","W"});
		endHour = new JComboBox(new String[]{"1","2","3"});
		endDay = new JComboBox(new String[]{"M","T","W"});
		devices = new JComboBox(new String[]{"Light","Dishwasher","Laser"});
		String imgLocation = "../images/tick.png";

		URL imageURL = MainGUIClass.class.getResource(imgLocation);

		// Create and initialize the button.

		submit = new JButton();

		submit.setActionCommand("SUBMIT");

		submit.setToolTipText("SUBMIT");

		submit.addActionListener(this);

		
		if (imageURL != null) { // image found

			submit.setIcon(new ImageIcon(imageURL, "Submit"));

		} else { // no image found

			submit.setText("Submit");

			System.err.println("Resource not found: "

			+ imgLocation);

		}
		startHour.getSelectedIndex();
	}
	private void setupLayout(JComponent... arg)
	{
		GroupLayout gl = new GroupLayout(this);
		this.setLayout(gl);
		
		gl.setAutoCreateContainerGaps(true);
		gl.setHorizontalGroup(gl.createSequentialGroup().addComponent(arg[0]));
		gl.setVerticalGroup(gl.createParallelGroup().addComponent(arg[0]));
	}
	public void setupView()
	{
		text = new JTextArea();
		text.setText("HELP");

		this.setSize(100, 100);
		text.setSize(50, 50);
		setupLayout(text);
	}
	private void setupReq()
	{
		GroupLayout gl = new GroupLayout(this);
		this.setLayout(gl);
		text.setText("req");
		gl.setAutoCreateContainerGaps(true);
		gl.setHorizontalGroup(gl.createSequentialGroup()
				.addGroup(gl.createParallelGroup().addComponent(startDay).addComponent(startHour))
				.addGroup(gl.createParallelGroup().addComponent(devices).addComponent(submit))
				.addGroup(gl.createParallelGroup().addComponent(endDay).addComponent(endHour))
				);
		
		gl.setVerticalGroup(gl.createParallelGroup().addComponent(startDay).addComponent(endDay));
		gl.setVerticalGroup(gl.createParallelGroup().addComponent(startHour).addComponent(endHour));
		gl.setVerticalGroup(gl.createSequentialGroup()
				.addComponent(devices)
				.addGroup(gl.createParallelGroup().addComponent(startDay).addComponent(endDay))
				.addGroup(gl.createParallelGroup().addComponent(startHour).addComponent(endHour))
				.addComponent(submit)
				);
		
	}
	private void setupHelp()
	{
		GroupLayout gl = new GroupLayout(this);
		this.setLayout(gl);
		text.setText("help");
		gl.setAutoCreateContainerGaps(true);
		gl.setHorizontalGroup(gl.createSequentialGroup().addComponent(text));
		gl.setVerticalGroup(gl.createParallelGroup().addComponent(text));
	}
	private void  setupDiag()
	{
		GroupLayout gl = new GroupLayout(this);
		this.setLayout(gl);
		text.setText("diag");
		gl.setAutoCreateContainerGaps(true);
		gl.setHorizontalGroup(gl.createSequentialGroup().addComponent(text));
		gl.setVerticalGroup(gl.createParallelGroup().addComponent(text));
	}
	@Override
	public void actionPerformed(ActionEvent e) {

		String cmd = e.getActionCommand();

		// Handle each button.
		this.removeAll();
		this.updateUI();
		
		if (help.equals(cmd)) { // first button clicked

			setupHelp();

		} else if (req.equals(cmd)) { // second button clicked

			setupReq();

		} else if (diag.equals(cmd)) { // third button clicked

			setupDiag();

		} else if ("SUBMIT".equals(cmd)) { //req has been submitted
			processRequirement();
		}


	}
	private void processRequirement() {
		//verify validity of requirement
		//generate requirement
		//dispatch to server
		System.out.println(devices.getItemAt(devices.getSelectedIndex()));
	}
}
