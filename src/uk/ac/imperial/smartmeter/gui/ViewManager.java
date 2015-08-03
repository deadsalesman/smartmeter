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

import uk.ac.imperial.smartmeter.res.ArraySet;
import uk.ac.imperial.smartmeter.res.DateHelper;
import uk.ac.imperial.smartmeter.res.DecimalRating;
import uk.ac.imperial.smartmeter.res.DeviceType;
import uk.ac.imperial.smartmeter.res.ElectricityGeneration;
import uk.ac.imperial.smartmeter.res.ElectricityRequirement;
import uk.ac.imperial.smartmeter.res.ElectricityTicket;
import uk.ac.imperial.smartmeter.webcomms.LCClient;

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
	private JComboBox<Integer> endHour;
	private JComboBox<Integer> startDay;
	private JComboBox<Integer> endDay;
	private JComboBox<String> devices;
	private JButton submit;
	private LCClient client;
	private String userName;
	public ViewManager(String user, LCClient lc)
	{
		userName = user;
		client = lc;
		initialiseReqComponents();
		setupView();
	}
	private void initialiseReqComponents() {
		// TODO Auto-generated method stub
		startHour = new JComboBox<Integer>(new Integer[]{1,2,3});
		startDay = new JComboBox<Integer>(new Integer[]{1,2,3});
		endHour = new JComboBox<Integer>(new Integer[]{1,2,3});
		endDay = new JComboBox<Integer>(new Integer[]{1,2,3});
		devices = new JComboBox<String>(new String[]{"Light","Dishwasher","Laser"});
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
	private Boolean processRequirement() {
		//verify validity of requirement
		//generate requirement
		//dispatch to server
		System.out.println(devices.getSelectedItem());
		ElectricityRequirement er = new ElectricityRequirement(
				DateHelper.incrementDay((Integer)startDay.getSelectedItem()),
				DateHelper.incrementDay((Integer)endDay.getSelectedItem()),
				new DecimalRating((Integer)9),
				DeviceType.valueOf(devices.getSelectedItem().toString()).ordinal(),
				new Double(9),
				client.getId()
				);

		client.registerUser("ps", "lc");
		client.setRequirement(er);
		client.setGeneration(new ElectricityGeneration(10.));
		ArraySet<ElectricityTicket> tkt = client.getTickets();
		return (tkt.get(0).ownerID.toString().equals(client.getId()));
	}
}
