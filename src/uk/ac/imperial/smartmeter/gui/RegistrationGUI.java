package uk.ac.imperial.smartmeter.gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import uk.ac.imperial.smartmeter.res.ElectricityGeneration;
import uk.ac.imperial.smartmeter.webcomms.LCClient;

public class RegistrationGUI {
	
	String result;
	
	public RegistrationGUI(final LCClient lc){
	
	final JFrame parent = new JFrame();
	JButton button = new JButton();
    button.setText("Click me to register as a user!");
	parent.add(button);
	parent.pack();
	parent.setVisible(true);
	button.addActionListener(new ActionListener(){
		public void actionPerformed(ActionEvent evt)
		{
			result = JOptionPane.showInputDialog(parent, "Please enter your password below.", null);
			lc.registerUser(0.,0.,0.);
			lc.setGeneration(new ElectricityGeneration(3.));
			parent.setVisible(false);
			lc.queryUserExists();
		}
	});
	}
	public String getResult()
	{
		return result;
	}
	
}
