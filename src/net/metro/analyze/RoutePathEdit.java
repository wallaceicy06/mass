/*******************************************************************************
 * MASS (Metro Analytics Software System) by Sean Harger
 * is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0
 * United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/.
 ******************************************************************************/
package net.metro.analyze;

import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import net.metro.systemobjects.Route;
import net.metro.systemobjects.RoutePath;

/**
 * Dialog that assists in editing {@code RoutePath} information including route
 * path name and route path id.
 * 
 * @author Sean Harger
 */
public class RoutePathEdit extends JDialog {
	private static final long serialVersionUID = -1366360421074600533L;

	private RoutePath routePath;
	private LineEdit lineEditFrame;
	private int editType;

	public static final int EDIT_NEW_ROUTEPATH = 0;
	public static final int EDIT_EXISTING_ROUTEPATH = 1;

	private JPanel contentPane;
	private JTextField textField_routePathName;

	/**
	 * Constructs a {@code RoutePathEdit} dialog.
	 * 
	 * @param rtePth
	 *            {@code RoutePath} to be edited
	 * @param lnEdtFrm
	 *            {@code LineEdit} from which this dialog was invoked
	 * @param edTyp
	 *            edTyp integer specifying the function of this edit frame <br>
	 *            {@code RoutePathEdit.EDIT_NEW_ROUTEPATH} for new route paths <br>
	 *            {@code RoutePathEdit.EDIT_EXISTING_ROUTEPATH} for existing
	 *            route paths
	 */
	public RoutePathEdit(RoutePath rtePth, LineEdit lnEdtFrm, int edTyp) {
		setResizable(false);
		setTitle("Edit Route Path");
		routePath = rtePth;
		lineEditFrame = lnEdtFrm;
		editType = edTyp;

		setUpGui();
		setInitialState();
		setLocationRelativeTo(lineEditFrame);
	}

	/**
	 * Attempts to process changes made to the {@code RoutePath} in question.
	 */
	private void processChanges() {
		try {
			String routePathName = textField_routePathName.getText();

			routePath.setName(routePathName);
			lineEditFrame.updateRouteComboBox();

			this.dispose();
			System.out.println("got here");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Cancels all changes made to the {@code RoutePath} in question. If the
	 * waypoint was created new, then it is removed from the {@code Route} it
	 * was added to. Otherwise, the existing waypoint is returned to its
	 * original state.
	 */
	private void cancelChanges() {
		if (editType == RoutePathEdit.EDIT_NEW_ROUTEPATH) {
			Route rte = routePath.getRoute();
			rte.removePath(routePath);
			lineEditFrame.updatePathComboBox();
		} else if (editType == RoutePathEdit.EDIT_EXISTING_ROUTEPATH)
			;
		{
		}
		this.dispose();
	}

	/**
	 * Sets the initial value of the text field according to the existing name
	 * of the {@code RoutePath}.
	 */
	private void setInitialState() {
		textField_routePathName.setText(routePath.getName());
	}

	/**
	 * Initializes the GUI elements of this {@code RoutePathEdit}.
	 */
	private void setUpGui() {
		setAlwaysOnTop(true);
		setModal(true);
		setBounds(100, 100, 243, 177);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 143, 0 };
		gbl_contentPane.rowHeights = new int[] { 126, 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };

		JPanel panel_routePathProperties = new JPanel();
		panel_routePathProperties.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "Route Path Properties",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panel_routePathProperties = new GridBagConstraints();
		gbc_panel_routePathProperties.insets = new Insets(0, 0, 5, 0);
		gbc_panel_routePathProperties.fill = GridBagConstraints.BOTH;
		gbc_panel_routePathProperties.gridx = 0;
		gbc_panel_routePathProperties.gridy = 0;
		contentPane.add(panel_routePathProperties,
				gbc_panel_routePathProperties);
		GridBagLayout gbl_panel_routePathProperties = new GridBagLayout();
		gbl_panel_routePathProperties.columnWidths = new int[] { 0, 0 };
		gbl_panel_routePathProperties.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_panel_routePathProperties.columnWeights = new double[] { 1.0,
				Double.MIN_VALUE };
		gbl_panel_routePathProperties.rowWeights = new double[] { 0.0, 0.0,
				0.0, Double.MIN_VALUE };
		panel_routePathProperties.setLayout(gbl_panel_routePathProperties);

		JPanel panel_routePathName = new JPanel();
		FlowLayout fl_panel_routePathName = (FlowLayout) panel_routePathName
				.getLayout();
		fl_panel_routePathName.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbc_panel_routePathName = new GridBagConstraints();
		gbc_panel_routePathName.insets = new Insets(0, 0, 5, 0);
		gbc_panel_routePathName.fill = GridBagConstraints.BOTH;
		gbc_panel_routePathName.gridx = 0;
		gbc_panel_routePathName.gridy = 0;
		panel_routePathProperties.add(panel_routePathName,
				gbc_panel_routePathName);

		JLabel lblName = new JLabel("Name");
		panel_routePathName.add(lblName);

		textField_routePathName = new JTextField();
		panel_routePathName.add(textField_routePathName);
		textField_routePathName.setColumns(20);

		JLabel lblUseWaypointEditor = new JLabel("Use WayPoint Editor to");
		GridBagConstraints gbc_lblUseWaypointEditor = new GridBagConstraints();
		gbc_lblUseWaypointEditor.insets = new Insets(0, 0, 5, 0);
		gbc_lblUseWaypointEditor.gridx = 0;
		gbc_lblUseWaypointEditor.gridy = 1;
		panel_routePathProperties.add(lblUseWaypointEditor,
				gbc_lblUseWaypointEditor);

		JLabel lblAddeditremoveWaypoints = new JLabel(
				"add/edit/remove WayPoints");
		GridBagConstraints gbc_lblAddeditremoveWaypoints = new GridBagConstraints();
		gbc_lblAddeditremoveWaypoints.gridx = 0;
		gbc_lblAddeditremoveWaypoints.gridy = 2;
		panel_routePathProperties.add(lblAddeditremoveWaypoints,
				gbc_lblAddeditremoveWaypoints);

		JPanel panel_confirm = new JPanel();
		GridBagConstraints gbc_panel_confirm = new GridBagConstraints();
		gbc_panel_confirm.fill = GridBagConstraints.BOTH;
		gbc_panel_confirm.gridx = 0;
		gbc_panel_confirm.gridy = 1;
		contentPane.add(panel_confirm, gbc_panel_confirm);

		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				processChanges();
			}
		});
		panel_confirm.add(btnOk);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelChanges();
			}
		});
		panel_confirm.add(btnCancel);
	}
}
