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
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import net.metro.systemobjects.RoutePath;
import net.metro.systemobjects.Stop;
import net.metro.systemobjects.WayPoint;

import org.openstreetmap.gui.jmapviewer.Coordinate;

/**
 * Dialog that assists in editing {@code WayPoint} information including the
 * type of waypoint (WayPoint or Stop), the location, and if a stop, the stop's
 * name and id.
 * 
 * @author Sean Harger
 * 
 */
public class WayPointEdit extends JDialog implements PointListener,
		MarkerListener {
	private static final long serialVersionUID = -1366360421074600533L;

	public static final int EDIT_NEW_WAYPOINT = 0;
	public static final int EDIT_EXISTING_WAYPOINT = 1;

	private static final int TYPE_WAYPOINT = 0;
	private static final int TYPE_STOP = 1;

	private int frameType;
	private WayPoint originalWayPoint;
	private WayPoint wayPoint;
	private RoutePath routePath;
	private LineEdit lineEditFrame;
	private MetroMapController controller;

	private JPanel contentPane;
	private JPanel stopInfoPanel;
	private JTextField textField_editLatitude;
	private JTextField textField_editLongitude;
	private JTextField textField_editStopName;
	private JTextField textField_editStationId;
	private JRadioButton rdbtnStop;
	private JRadioButton rdbtnWayPoint;

	/**
	 * Constructs a {@code WayPointEdit}.
	 * 
	 * @param wp
	 *            {@code WayPoint} to edit
	 * @param lnEdtFrm
	 *            {@code LineEditFrame} from which this dialog was invoked
	 * @param frmTyp
	 *            integer constant representing the frame type: <br>
	 *            {@code WayPointEdit.TYPE_WAYPOINT}: for editing waypoints
	 *            {@code WayPointEdit.TYPE_STOP}: for editing stops (includes
	 *            two extra text fields)
	 */
	public WayPointEdit(WayPoint wp, LineEdit lnEdtFrm, int frmTyp) {
		frameType = frmTyp;

		originalWayPoint = wayPoint = wp;
		lineEditFrame = lnEdtFrm;
		controller = lineEditFrame.getMainFrame().getMap().getController();
		routePath = wp.getRoutePath();

		setUpGui();
		setInitialState();
		setLocationRelativeTo(lineEditFrame);

		controller.selectWayPoint(wp);
		controller.addMarkerListener(this);
		controller.setMarkerMovingEnabled(true);
		controller.setMarkerSelectionEnabled(false);
	}

	/**
	 * Disposes this {@code WayPointEdit} frame normally, but removes all
	 * references to itself as a {@code MarkerListener} and
	 * {@code PointListener}, and turns off marker movement and turns on marker
	 * selection.
	 */
	public void dispose() {
		controller.removeMarkerListener(this);
		controller.removePointListener(this);
		controller.setMarkerMovingEnabled(false);
		controller.setMarkerSelectionEnabled(true);
		super.dispose();
	}

	/**
	 * Returns the type of waypoint indicated by the selected radio button.
	 * 
	 * @return {@code WayPointEdit.TYPE_STOP} if "Stop" is selected, or
	 *         {@code WayPointEdit.TYPE_WAYPOINT} if "WayPoint" is selected.
	 */
	private int getSelectedWayPointType() {
		if (rdbtnStop.isSelected()) {
			return TYPE_STOP;
		} else {
			return TYPE_WAYPOINT;
		}
	}

	/**
	 * Adjusts the visible items in the {@code WayPointEdit} dialog according to
	 * the selected waypoint type from the radio buttons. If "Stop" is selected,
	 * then a special frame is shown that allows the user to edit the stop name
	 * and id.
	 */
	private void registerWayPointTypeChange() {
		if (getSelectedWayPointType() == TYPE_STOP) {
			textField_editStationId.setText("0");
			textField_editStopName.setText("New Stop");
			stopInfoPanel.setVisible(true);
		} else {
			stopInfoPanel.setVisible(false);
		}
	}

	/**
	 * Attempts to commit the changes made by the user for the {@code WayPoint}
	 * this dialog was created to edit. If the user attempts to place a
	 * non-numerical character in the latitude and longitude fields, then an
	 * error message is shown.
	 */
	private void processChanges() {
		try {
			double latitude = Double.parseDouble(textField_editLatitude
					.getText());
			double longitude = Double.parseDouble(textField_editLongitude
					.getText());

			WayPoint newWayPoint;
			if (getSelectedWayPointType() == TYPE_STOP) {
				int stationId = Integer.parseInt(textField_editStationId
						.getText());
				newWayPoint = new Stop(routePath, latitude, longitude,
						textField_editStopName.getText(), stationId);
			} else {
				newWayPoint = new WayPoint(routePath, latitude, longitude);
			}

			routePath.replaceWayPoint(wayPoint, newWayPoint);
			wayPoint = newWayPoint;
			lineEditFrame.updateWayPointList();

			lineEditFrame.selectWayPoint(wayPoint);
		} catch (NumberFormatException ex) {
			ex.printStackTrace();
			JOptionPane
					.showMessageDialog(
							this,
							"Latitude, Longitude, and Station ID\nmay only contain numerical characters.",
							"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Processes the changes made by the user and closes the dialog.
	 */
	private void confirmChanges() {
		processChanges();
		dispose();
	}

	/**
	 * Cancels the changes made by the user. If the {@code WayPoint} being
	 * edited was created as a new waypoint, then it is removed from its
	 * {@code RoutePath} upon cancellation.
	 */
	private void cancelChanges() {
		if (frameType == WayPointEdit.EDIT_NEW_WAYPOINT) {
			routePath.deleteWayPoint(wayPoint);
			lineEditFrame.getMainFrame().getMap().setSelectedMapMarker(null);
			lineEditFrame.updateWayPointList();
		} else if (frameType == WayPointEdit.EDIT_EXISTING_WAYPOINT) {
			routePath.replaceWayPoint(wayPoint, originalWayPoint);
			lineEditFrame.getMainFrame().getMap()
					.setSelectedMapMarker(originalWayPoint);
		}
		lineEditFrame.getMainFrame().getMap().repaint();
		dispose();
	}

	private void setInitialState() {
		textField_editLatitude.setText(Double.toString(originalWayPoint
				.getLat()));
		textField_editLongitude.setText(Double.toString(originalWayPoint
				.getLon()));

		if (originalWayPoint instanceof Stop) {
			rdbtnStop.setSelected(true);

			textField_editStopName.setText(((Stop) originalWayPoint).getName());
			textField_editStationId.setText(Integer
					.toString(((Stop) originalWayPoint).getStationId()));

			stopInfoPanel.setVisible(true);
		} else {
			rdbtnWayPoint.setSelected(true);

			stopInfoPanel.setVisible(false);
		}
	}

	/**
	 * Initializes the GUI elements for this {@code WayPointEdit}.
	 */
	private void setUpGui() {
		setTitle("Edit WayPoint");
		setAlwaysOnTop(true);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		setBounds(100, 100, 243, 366);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 143, 0 };
		gbl_contentPane.rowHeights = new int[] { 126, 57, 0, 0, 0 };
		gbl_contentPane.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		JPanel locationPanel = new JPanel();
		locationPanel.setBorder(new TitledBorder(null, "Location",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_locationPanel = new GridBagConstraints();
		gbc_locationPanel.insets = new Insets(0, 0, 5, 0);
		gbc_locationPanel.fill = GridBagConstraints.BOTH;
		gbc_locationPanel.gridx = 0;
		gbc_locationPanel.gridy = 0;
		contentPane.add(locationPanel, gbc_locationPanel);
		GridBagLayout gbl_locationPanel = new GridBagLayout();
		gbl_locationPanel.columnWidths = new int[] { 0, 0 };
		gbl_locationPanel.rowHeights = new int[] { 0, 0, 0, 0 };
		gbl_locationPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_locationPanel.rowWeights = new double[] { 0.0, 0.0, 0.0,
				Double.MIN_VALUE };
		locationPanel.setLayout(gbl_locationPanel);

		JPanel latitudeEditPanel = new JPanel();
		GridBagConstraints gbc_latitudeEditPanel = new GridBagConstraints();
		gbc_latitudeEditPanel.insets = new Insets(0, 0, 5, 0);
		gbc_latitudeEditPanel.fill = GridBagConstraints.BOTH;
		gbc_latitudeEditPanel.gridx = 0;
		gbc_latitudeEditPanel.gridy = 0;
		locationPanel.add(latitudeEditPanel, gbc_latitudeEditPanel);

		JLabel lblLatitude = new JLabel("Latitude");
		latitudeEditPanel.add(lblLatitude);

		textField_editLatitude = new JTextField();
		textField_editLatitude.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				processChanges();
			}
		});
		latitudeEditPanel.add(textField_editLatitude);
		textField_editLatitude.setColumns(10);

		JPanel longitudeEditPanel = new JPanel();
		GridBagConstraints gbc_longitudeEditPanel = new GridBagConstraints();
		gbc_longitudeEditPanel.insets = new Insets(0, 0, 5, 0);
		gbc_longitudeEditPanel.fill = GridBagConstraints.BOTH;
		gbc_longitudeEditPanel.gridx = 0;
		gbc_longitudeEditPanel.gridy = 1;
		locationPanel.add(longitudeEditPanel, gbc_longitudeEditPanel);

		JLabel lblLongitude = new JLabel("Longitude");
		longitudeEditPanel.add(lblLongitude);

		textField_editLongitude = new JTextField();
		textField_editLongitude.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent arg0) {
				processChanges();
			}
		});
		longitudeEditPanel.add(textField_editLongitude);
		textField_editLongitude.setColumns(10);

		JPanel typePanel = new JPanel();
		typePanel.setBorder(new TitledBorder(null, "Type",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_typePanel = new GridBagConstraints();
		gbc_typePanel.insets = new Insets(0, 0, 5, 0);
		gbc_typePanel.fill = GridBagConstraints.BOTH;
		gbc_typePanel.gridx = 0;
		gbc_typePanel.gridy = 1;
		contentPane.add(typePanel, gbc_typePanel);
		typePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		rdbtnWayPoint = new JRadioButton("WayPoint");
		rdbtnWayPoint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				registerWayPointTypeChange();
			}
		});
		typePanel.add(rdbtnWayPoint);

		rdbtnStop = new JRadioButton("Stop");
		rdbtnStop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				registerWayPointTypeChange();
			}
		});
		typePanel.add(rdbtnStop);

		ButtonGroup wayPointTypes = new ButtonGroup();
		wayPointTypes.add(rdbtnWayPoint);
		wayPointTypes.add(rdbtnStop);

		stopInfoPanel = new JPanel();
		stopInfoPanel.setBorder(new TitledBorder(UIManager
				.getBorder("TitledBorder.border"), "Stop Information",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_stopInfoPanel = new GridBagConstraints();
		gbc_stopInfoPanel.insets = new Insets(0, 0, 5, 0);
		gbc_stopInfoPanel.fill = GridBagConstraints.BOTH;
		gbc_stopInfoPanel.gridx = 0;
		gbc_stopInfoPanel.gridy = 2;
		contentPane.add(stopInfoPanel, gbc_stopInfoPanel);
		GridBagLayout gbl_stopInfoPanel = new GridBagLayout();
		gbl_stopInfoPanel.columnWidths = new int[] { 0, 0 };
		gbl_stopInfoPanel.rowHeights = new int[] { 0, 0, 0 };
		gbl_stopInfoPanel.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_stopInfoPanel.rowWeights = new double[] { 1.0, 1.0,
				Double.MIN_VALUE };
		stopInfoPanel.setLayout(gbl_stopInfoPanel);

		JPanel stopNamePanel = new JPanel();
		GridBagConstraints gbc_stopNamePanel = new GridBagConstraints();
		gbc_stopNamePanel.insets = new Insets(0, 0, 5, 0);
		gbc_stopNamePanel.fill = GridBagConstraints.BOTH;
		gbc_stopNamePanel.gridx = 0;
		gbc_stopNamePanel.gridy = 0;
		stopInfoPanel.add(stopNamePanel, gbc_stopNamePanel);

		JLabel lblName = new JLabel("Name");
		stopNamePanel.add(lblName);

		textField_editStopName = new JTextField();
		stopNamePanel.add(textField_editStopName);
		textField_editStopName.setColumns(16);

		JPanel stopIdPanel = new JPanel();
		GridBagConstraints gbc_stopIdPanel = new GridBagConstraints();
		gbc_stopIdPanel.fill = GridBagConstraints.BOTH;
		gbc_stopIdPanel.gridx = 0;
		gbc_stopIdPanel.gridy = 1;
		stopInfoPanel.add(stopIdPanel, gbc_stopIdPanel);

		JLabel lblStationId = new JLabel("Station ID");
		stopIdPanel.add(lblStationId);

		textField_editStationId = new JTextField();
		stopIdPanel.add(textField_editStationId);
		textField_editStationId.setColumns(14);

		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 3;
		contentPane.add(panel, gbc_panel);

		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				confirmChanges();
			}
		});
		panel.add(btnOk);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelChanges();
			}
		});
		panel.add(btnCancel);
	}

	/**
	 * If a point is selected by the user in the {@code SystemMap}, then the
	 * coordinates of that point are set as the new coordinates for this
	 * {@code WayPoint}.
	 * 
	 * @param e
	 *            {@code PointEvent} object
	 */
	public void pointSelected(PointEvent e) {
		System.out.println("Point selected at " + e.getPoint().x + ","
				+ e.getPoint().y);
		SystemMap map = lineEditFrame.getMainFrame().getMap();
		Coordinate location = map.getPosition(e.getPoint());
		textField_editLatitude.setText(Double.toString(location.getLat()));
		textField_editLongitude.setText(Double.toString(location.getLon()));
		processChanges();
	}

	/**
	 * No implementation.
	 */
	public void markerSelected(MarkerEvent e) {
	}

	/**
	 * If a marker is moved by the user, then the coordinates of the final point
	 * are set as the new coordinates for this {@code WayPoint}.
	 */
	public void markerMoved(MarkerEvent e) {
		System.out.println("Marker " + e.getMapMarker().getStatusMessage()
				+ " moved to " + e.getPointMovedTo().x + ","
				+ e.getPointMovedTo().y);
		SystemMap map = lineEditFrame.getMainFrame().getMap();
		Coordinate location = map.getPosition(e.getPointMovedTo());
		textField_editLatitude.setText(Double.toString(location.getLat()));
		textField_editLongitude.setText(Double.toString(location.getLon()));
		processChanges();
	}

	/**
	 * No implementation.
	 */
	public void markerMoving(MarkerEvent e) {
	}

	/**
	 * No implementation.
	 */
	public void markerNotSelected() {
	}

	/**
	 * No implementation.
	 */
	public void mouseHoveringOverMarker(MarkerEvent e) {
	}

	/**
	 * No implementation.
	 */
	public void mouseHoveringOverNothing() {
	}

}
