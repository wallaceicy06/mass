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
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import net.metro.systemobjects.Route;
import net.metro.systemobjects.SystemObjects;

/**
 * Dialog that assists in editing {@code Route} information including name and
 * number.
 * 
 * @author Sean Harger
 * 
 */
public class RouteEdit extends JDialog {
	private static final long serialVersionUID = 1062553716990818535L;

	private Route route;
	private LineEdit lineEditFrame;
	private int editType;

	/**
	 * Specifies a {@code RouteEdit} frame to edit the route as if it were new.
	 * This sets the cancel button to remove the route from the database when
	 * pressed.
	 */
	public static final int EDIT_NEW_ROUTE = 0;
	/**
	 * Specifies a {@code RouteEdit} frame to edit the route as an existing
	 * route. This sets the cancel button to not remove the route from the
	 * database when pressed.
	 */
	public static final int EDIT_EXISTING_ROUTE = 1;

	private JPanel contentPane;
	private JTextField textField_routeNum;
	private JTextField textField_routeName;

	/**
	 * Constructs a {@code RouteEdit}.
	 * 
	 * @param rte
	 *            {@code Route} to edit
	 * @param lnEdtFrm
	 *            {@code LineEditFrame} from which this {@code RouteEdit} was
	 *            invoked.
	 * @param edTyp
	 *            integer specifying the function of this edit frame <br>
	 *            {@code RouteEdit.EDIT_NEW_ROUTE} for new routes <br>
	 *            {@code RouteEdit.EDIT_EXISTING_ROUTE} for existing routes
	 */
	public RouteEdit(Route rte, LineEdit lnEdtFrm, int edTyp) {
		setResizable(false);
		setTitle("Edit Route");
		route = rte;
		lineEditFrame = lnEdtFrm;
		editType = edTyp;

		setUpGui();
		setInitialState();
		setLocationRelativeTo(lineEditFrame);
	}

	/**
	 * Attempts to commit the specified changes to the route in question.
	 * Displays an error message if the user attempts to place a non-numerical
	 * character for the route id or if they attempt to add a duplicate route.
	 */
	private void processChanges() {
		try {
			int routeNum = Integer.parseInt(textField_routeNum.getText());
			String routeName = textField_routeName.getText();

			if (lineEditFrame.getMainFrame().getSystemObjects()
					.checkOtherRoutesForId(route) == true) {
				throw new Exception("Route " + routeNum + " already exists.");
			}

			route.setNumber(routeNum);
			route.setName(routeName);
			lineEditFrame.updateRouteComboBox();

			this.dispose();
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this,
					"Route Number\nmay only contain numerical characters.",
					"Error", JOptionPane.ERROR_MESSAGE);
		} catch (Exception ex) {
			ex.printStackTrace();
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Cancels all changes to the route and disposes the frame. If this Route
	 * was just created as new, it removes it from the database.
	 */
	private void cancelChanges() {
		if (editType == RouteEdit.EDIT_NEW_ROUTE) {
			SystemObjects objects = lineEditFrame.getMainFrame()
					.getSystemObjects();
			objects.removeRoute(route);
			lineEditFrame.updateRouteComboBox();
		} else if (editType == RouteEdit.EDIT_EXISTING_ROUTE) {

		}
		this.dispose();
	}

	/**
	 * Sets the initial values of the text fields according to the values within
	 * the passed {@code Route} object.
	 */
	private void setInitialState() {
		textField_routeNum.setText(Integer.toString(route.getRouteId()));
		textField_routeName.setText(route.getName());
	}

	/**
	 * Initializes the GUI elements of this {@code RouteEdit}.
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
		contentPane.setLayout(gbl_contentPane);

		JPanel panel_routeProperties = new JPanel();
		panel_routeProperties.setBorder(new TitledBorder(null,
				"Route Properties", TitledBorder.LEADING, TitledBorder.TOP,
				null, null));
		GridBagConstraints gbc_panel_routeProperties = new GridBagConstraints();
		gbc_panel_routeProperties.insets = new Insets(0, 0, 5, 0);
		gbc_panel_routeProperties.fill = GridBagConstraints.BOTH;
		gbc_panel_routeProperties.gridx = 0;
		gbc_panel_routeProperties.gridy = 0;
		contentPane.add(panel_routeProperties, gbc_panel_routeProperties);
		GridBagLayout gbl_panel_routeProperties = new GridBagLayout();
		gbl_panel_routeProperties.columnWidths = new int[] { 0, 0 };
		gbl_panel_routeProperties.rowHeights = new int[] { 0, 0, 0 };
		gbl_panel_routeProperties.columnWeights = new double[] { 1.0,
				Double.MIN_VALUE };
		gbl_panel_routeProperties.rowWeights = new double[] { 0.0, 0.0,
				Double.MIN_VALUE };
		panel_routeProperties.setLayout(gbl_panel_routeProperties);

		JPanel panel_routeNumber = new JPanel();
		FlowLayout fl_panel_routeNumber = (FlowLayout) panel_routeNumber
				.getLayout();
		fl_panel_routeNumber.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbc_panel_routeNumber = new GridBagConstraints();
		gbc_panel_routeNumber.insets = new Insets(0, 0, 5, 0);
		gbc_panel_routeNumber.fill = GridBagConstraints.BOTH;
		gbc_panel_routeNumber.gridx = 0;
		gbc_panel_routeNumber.gridy = 0;
		panel_routeProperties.add(panel_routeNumber, gbc_panel_routeNumber);

		JLabel lblNumber = new JLabel("Number");
		panel_routeNumber.add(lblNumber);

		textField_routeNum = new JTextField();
		panel_routeNumber.add(textField_routeNum);
		textField_routeNum.setColumns(10);

		JPanel panel_routeName = new JPanel();
		FlowLayout fl_panel_routeName = (FlowLayout) panel_routeName
				.getLayout();
		fl_panel_routeName.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbc_panel_routeName = new GridBagConstraints();
		gbc_panel_routeName.fill = GridBagConstraints.BOTH;
		gbc_panel_routeName.gridx = 0;
		gbc_panel_routeName.gridy = 1;
		panel_routeProperties.add(panel_routeName, gbc_panel_routeName);

		JLabel lblName = new JLabel("Name");
		panel_routeName.add(lblName);

		textField_routeName = new JTextField();
		panel_routeName.add(textField_routeName);
		textField_routeName.setColumns(20);

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
