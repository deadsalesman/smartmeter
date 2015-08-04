package uk.ac.imperial.smartmeter.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import uk.ac.imperial.smartmeter.webcomms.LCClient;

public class MainGUIClass extends JPanel

implements ActionListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	protected JTextArea textArea;

	protected String newline = "\n";

	static final private String PREVIOUS = "previous";

	static final private String UP = "up";

	static final private String NEXT = "next";

	private ViewManager view;
	private MenuManager menu;

	public MainGUIClass(String[] args) {

		super(new BorderLayout());

		// Create the toolbar.
		LCClient lc = new LCClient(args[0], Integer.parseInt(args[1]), args[2], Integer.parseInt(args[3]), args[4]);
		view = new ViewManager(args[4],lc);
		menu = new MenuManager(view);
		//JToolBar toolBar = new JToolBar("Still draggable");

		//addButtons(toolBar);

		// Create the text area used for output. Request

		// enough space for 5 rows and 30 columns.

		// Lay out the main panel.

		setPreferredSize(new Dimension(450, 530));

		//add(toolBar, BorderLayout.PAGE_START);

	}

	protected void addButtons(JToolBar toolBar) {

		JButton button = null;

		// first button

		button = makeNavigationButton("chart", PREVIOUS,

		"Back to previous something-or-other",

		"Previous");

		toolBar.add(button);

		// second button

		button = makeNavigationButton("energy", UP,

		"Up to something-or-other",

		"Up");

		toolBar.add(button);

		// third button

		button = makeNavigationButton("help", NEXT,

		"Forward to something-or-other",

		"Next");

		toolBar.add(button);

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

		button.addActionListener(this);

		
		if (imageURL != null) { // image found

			button.setIcon(new ImageIcon(imageURL, altText));

		} else { // no image found

			button.setText(altText);

			System.err.println("Resource not found: "

			+ imgLocation);

		}

		return button;

	}

	public void actionPerformed(ActionEvent e) {

		String cmd = e.getActionCommand();

		String description = null;

		// Handle each button.

		if (PREVIOUS.equals(cmd)) { // first button clicked

			description = "taken you to the previous <something>.";

		} else if (UP.equals(cmd)) { // second button clicked

			description = "taken you up one level to <something>.";

		} else if (NEXT.equals(cmd)) { // third button clicked

			description = "taken you to the next <something>.";

		}

		displayResult("If this were a real app, it would have "

		+ description);

	}

	protected void displayResult(String actionDescription) {

		textArea.append(actionDescription + newline);

		textArea.setCaretPosition(textArea.getDocument().getLength());

	}

	/**
	 * 
	 * Create the GUI and show it. For thread safety,
	 * 
	 * this method should be invoked from the
	 * 
	 * event dispatch thread.
	 */
	public JButton[] getButtons() {
		JButton button1 = null;
		JButton button2 = null;
		JButton button3 = null;

		// first button

		button1 = makeNavigationButton("chart", PREVIOUS,

		"Back to previous something-or-other",

		"Previous");

		// second button

		button2 = makeNavigationButton("energy", UP,

		"Up to something-or-other",

		"Up");

		// third button

		button3 = makeNavigationButton("help", NEXT,

		"Forward to something-or-other",

		"Next");
		return new JButton[] { button1, button2, button3 };
	}

	public void createLayout(JFrame fram, JComponent... arg) {
		JPanel pane = (JPanel) fram.getContentPane();
		GroupLayout gl = new GroupLayout(pane);
		pane.setLayout(gl);
		pane.setToolTipText("Content pane");

		gl.setAutoCreateContainerGaps(true);

		gl.setHorizontalGroup(gl.createParallelGroup().addComponent(menu).addComponent(view));
		gl.setVerticalGroup(gl.createSequentialGroup().addComponent(menu).addComponent(view));
		/*gl.setHorizontalGroup(gl.createSequentialGroup().addComponent(arg[0]).addGap(200).addComponent(arg[1]).addGap(200)
				.addComponent(arg[2]).addGap(200));

		gl.setVerticalGroup(gl.createParallelGroup().addComponent(arg[0]).addComponent(arg[1]).addComponent(arg[2]));
*/
		fram.pack();
	}

	private static void createAndShowGUI(String[] args) {

		// Create and set up the window.

		JFrame frame = new JFrame("ToolBarDemo");
		frame.getContentPane().setSize(800, 800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		MainGUIClass swinger = new MainGUIClass(args);
		swinger.createLayout(frame, swinger.getButtons());
		// Add content to the window.

		// frame.add(swinger);

		// Display the window.
		frame.setPreferredSize(new Dimension(900,600));
		frame.pack();

		frame.setVisible(true);

	}

	public static void main(final String[] args) {

		// Schedule a job for the event dispatch thread:

		// creating and showing this application's GUI.
		if (args.length != 5) {
			System.err.println("Usage: java LContNode <host name> <port number> <host name> <port number> <username>");
			System.exit(1);
		}
		SwingUtilities.invokeLater(new Runnable() {

			public void run() {

				// Turn off metal's use of bold fonts

				UIManager.put("swing.boldMetal", Boolean.FALSE);

				createAndShowGUI(args);

			}

		});

	}

}