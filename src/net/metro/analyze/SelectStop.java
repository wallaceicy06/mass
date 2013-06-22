/*******************************************************************************
 * MASS (Metro Analytics Software System) by Sean Harger
 * is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0
 * United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/.
 ******************************************************************************/
package net.metro.analyze;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import net.metro.systemobjects.Stop;

/**
 * Dialog that appears when multiple stops are found near the clicked point. It
 * allows the user to select a stop from a list to ensure that the correct one
 * is highlighted on the map.
 * 
 * @author Sean Harger
 * 
 */
public class SelectStop extends JDialog {
	private static final long serialVersionUID = -6405022790395786239L;

	private MapFrame mapFrame;
	private Stop desiredStop;
	private ArrayList<Stop> possibleStops;
	private String[] stopInfo;

	private final JPanel contentPanel = new JPanel();
	private JButton okButton;
	private JButton cancelButton;
	private JComboBox<String> stopList;

	/**
	 * Constructs a {@code SelectStop}.
	 * 
	 * @param mpFrm
	 *            {@code MapFrame} from which this dialog was invoked
	 * @param stps
	 *            {@code ArrayList} of stops to choose from
	 */
	public SelectStop(MapFrame mpFrm, ArrayList<Stop> stps) {
		mapFrame = mpFrm;
		possibleStops = stps;
		setUpWayPointInfo();
		setUpGui();
		setLocationRelativeTo(mapFrame);
		setUpListeners();
	}

	/**
	 * Shows the {@code SelectStop} dialog and halts the previous thread until a
	 * selection is made.
	 * 
	 * @return the selected {@code Stop} or {@code null} if the dialog was
	 *         closed with no selection.
	 */
	public Stop showDialog() {
		this.setVisible(true);
		setLocationRelativeTo(mapFrame);
		return desiredStop;
	}

	/**
	 * Prepares the {@code String} values that describe each stop and stores
	 * them into an array of {@code String}s which are placed in the combo box
	 * for selection. Since stops do not print their route path name by default,
	 * this method is needed in order to provide the stop name along with the
	 * route path information.
	 */
	private void setUpWayPointInfo() {
		stopInfo = new String[possibleStops.size()];
		for (int index = 0; index < stopInfo.length; index++) {
			Stop st = possibleStops.get(index);
			stopInfo[index] = new String(st.getRoutePath().getRoute() + "-"
					+ st.getRoutePath().toString().substring(0, 1) + " "
					+ st.getName());
		}
	}

	/**
	 * Initializes the GUI for this {@code SelectStop}.
	 */
	private void setUpGui() {
		setTitle("Select Stop");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				SelectStop.class.getResource("/res/pinpoint128.png")));
		setModal(true);
		setBounds(100, 100, 239, 110);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		// $hide>>$
		stopList = new JComboBox<String>(stopInfo);
		stopList.setPreferredSize(new Dimension(190, 20));
		contentPanel.add(stopList);
		// $hide<<$

		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);

		okButton = new JButton("OK");
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);

		cancelButton = new JButton("Cancel");
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
	}

	/**
	 * Initializes the {@code EventListener}s for this {@code SelectStop}.
	 */
	private void setUpListeners() {
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				desiredStop = possibleStops.get(stopList.getSelectedIndex());
				setVisible(false);
				dispose();
			}
		});

		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				desiredStop = null;
				setVisible(false);
				dispose();
			}
		});
	}
}
