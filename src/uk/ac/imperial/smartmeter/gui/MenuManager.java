package uk.ac.imperial.smartmeter.gui;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class MenuManager extends JPanel{

	
	private static final String help = "HELP";
	private static final String req  = "REQ";
	private static final String diag = "DIAG";
	private final ViewManager view;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public MenuManager(ViewManager v)
	{

		view = v;
		setupMenu();
	}
	private void setupLayout(JComponent... arg)
	{
		GroupLayout gl = new GroupLayout(this);
		this.setLayout(gl);
		
		gl.setAutoCreateContainerGaps(true);

		gl.setHorizontalGroup(gl.createSequentialGroup().addComponent(arg[0]).addGap(200).addComponent(arg[1]).addGap(200)
				.addComponent(arg[2]).addGap(200));

		gl.setVerticalGroup(gl.createParallelGroup().addComponent(arg[0]).addComponent(arg[1]).addComponent(arg[2]));
	}
	public void setupMenu()
	{
		JTextArea text = new JTextArea();
		text.setText("BUTTON");
		text.setSize(50, 50);

		this.setSize(100, 100);
		setupLayout(getButtons());
	}
	public JButton[] getButtons() {
		JButton button1 = null;
		JButton button2 = null;
		JButton button3 = null;

		// first button

		button1 = makeNavigationButton("chart", diag,

		"Back to previous something-or-other",

		"Previous");

		// second button

		button2 = makeNavigationButton("energy", req,

		"Up to something-or-other",

		"Up");

		// third button

		button3 = makeNavigationButton("help", help,

		"Forward to something-or-other",

		"Next");
		return new JButton[] { button1, button2, button3 };
	}

	protected JButton makeNavigationButton(String imageName,

			String actionCommand,

			String toolTipText,

			String altText) {

				// Look for the image.

				String imgLocation = "../images/"

				+ imageName

				+ ".png";

				URL imageURL = MainGUIClass.class.getResource(imgLocation);

				// Create and initialize the button.

				JButton button = new JButton();

				button.setActionCommand(actionCommand);

				button.setToolTipText(toolTipText);

				button.addActionListener(view);

				Path currentRelativePath = Paths.get("");

				String s = currentRelativePath.toAbsolutePath().toString();

				System.out.println("Current relative path is: " + s);

				if (imageURL != null) { // image found

					button.setIcon(new ImageIcon(imageURL, altText));

				} else { // no image found

					button.setText(altText);

					System.err.println("Resource not found: "

					+ imgLocation);

				}

				return button;

			}
}
