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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.metro.systemobjects.Route;
import net.metro.systemobjects.RoutePath;
import net.metro.systemobjects.SystemObjects;
import net.metro.systemobjects.WayPoint;

import org.openstreetmap.gui.jmapviewer.Coordinate;

/**
 * Frame that allows for the editing of route data including the route numbers,
 * route paths, and route waypoints and stops. It cannot edit data on the
 * service level, and any changes in data for these data types must be made from
 * outside MASS and re-imported.
 * 
 * @author Sean Harger
 * 
 */
public class LineEdit extends JFrame implements MarkerListener {
	private static final long serialVersionUID = -5715189080178226452L;

	private MainFrame mainFrame;

	private JPanel contentPane;

	private JComboBox<Route> comboBox_route;
	private JComboBox<RoutePath> comboBox_path;

	private JList<WayPoint> list_wayPoints;

	/**
	 * Constructs a {@code LineEdit} frame.
	 * 
	 * @param mnFrm
	 */
	public LineEdit(MainFrame mnFrm) {
		setMinimumSize(new Dimension(600, 300));
		setSize(new Dimension(600, 300));
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				LineEdit.class.getResource("/res/metroIcon.png")));
		mainFrame = mnFrm;

		mainFrame.getMap().getController().addMarkerListener(this);

		setUpGui();
		updateRouteComboBox();
		setLocationRelativeTo(mainFrame);
		setVisible(true);
		list_wayPoints.requestFocusInWindow();
	}

	/**
	 * Disposes the {@code LineEditFrame} by clearing all selected RoutePaths
	 * that may have been selected from the editor and removing the
	 * {@code MarkerListener} placed on the {@code SystemMap} used to listen to
	 * user marker selection events.
	 */
	public void dispose() {
		SystemMap map = mainFrame.getMap();
		map.clearSelectedRoutePaths();

		map.getController().removeMarkerListener(this);
		super.dispose();
	}

	/**
	 * Returns the reference to the {@code MainFrame} object.
	 * 
	 * @return the reference to the {@code MainFrame} object.
	 */
	protected MainFrame getMainFrame() {
		return mainFrame;
	}

	/**
	 * Returns the selected {@code Route} from the route combo box.
	 * 
	 * @return the selected {@code Route} from the route combo box.
	 */
	private Route getSelectedRoute() {
		return (Route) comboBox_route.getSelectedItem();
	}

	/**
	 * Returns the selected {@code RoutePath} from the route path combo box.
	 * 
	 * @return the selected {@code RoutePath} from the route path combo box.
	 */
	protected RoutePath getSelectedRoutePath() {
		return (RoutePath) comboBox_path.getSelectedItem();
	}

	/**
	 * Returns the selected {@code WayPoint} from the waypoints list.
	 * 
	 * @return the selected {@code WayPoint} from the waypoints list.
	 */
	private WayPoint getSelectedWayPoint() {
		return list_wayPoints.getSelectedValue();
	}

	/**
	 * Selects the specified {@code WayPoint} in the waypoints list. If the
	 * specified waypoint is not a member of the currently selected
	 * {@code Route} or {@code RoutePath}, then those combo box selections are
	 * changed to the appropriate values for th specified waypoint.
	 * 
	 * @param wpt
	 *            {@code WayPoint} desired to be selected.
	 */
	protected void selectWayPoint(WayPoint wpt) {
		RoutePath rtePth = wpt.getRoutePath();
		Route rte = rtePth.getRoute();

		comboBox_route.setSelectedItem(rte);
		comboBox_path.setSelectedItem(rtePth);
		list_wayPoints.setSelectedValue(wpt, true);
	}

	/**
	 * Updates the routes combo box according to the routes within the
	 * {@code SystemObjects} database. It is typically called when the
	 * {@code LineEdit} frame is invoked and when the user adds a route to the
	 * database manually.
	 */
	protected void updateRouteComboBox() {
		SystemObjects objects = mainFrame.getSystemObjects();
		comboBox_route.removeAllItems();
		for (Route rte : objects.getAllRoutes()) {
			comboBox_route.addItem(rte);
		}
	}

	/**
	 * Updates the route paths combo box according to the route paths associated
	 * with the selected route in the route combo box. If no route is selected,
	 * then the list remains empty. This is typically called after a user makes
	 * a selection in the routes combo box or when a user adds a route path to
	 * the database manually.
	 */
	protected void updatePathComboBox() {
		comboBox_path.removeAllItems();
		Route rte = getSelectedRoute();
		if (rte != null) {
			for (RoutePath pth : getSelectedRoute().getPaths()) {
				comboBox_path.addItem(pth);
			}
		}
	}

	/**
	 * Updates the waypoint list according to the selected {@code RoutePath} in
	 * the route path combo box. It is typically called after a user selects a
	 * route path from the combo box or adds a waypoint to the list manually.
	 */
	protected void updateWayPointList() {
		int selectedIndex = list_wayPoints.getSelectedIndex();
		list_wayPoints.setListData(getSelectedRoutePath().getWayPoints()
				.toArray(new WayPoint[1]));
		list_wayPoints.setSelectedIndex(selectedIndex);
	}

	/**
	 * Gets the selected {@code RoutePath} from the route path combo box and
	 * highlights that route in the {@code SystemMap};
	 */
	private void showSelectedPathOnMap() {
		RoutePath slctdRtePth = getSelectedRoutePath();
		mainFrame.getMap().setSelectedRoutePath(slctdRtePth);
	}

	/**
	 * Adds a new {@code RoutePath} for the selected route and shows a
	 * corresponding {@code RoutePathEdit} dialog to edit it.
	 */
	private void newRoutePath() {
		Route slctdRte = getSelectedRoute();
		if (slctdRte != null) {
			RoutePath newRtePth = new RoutePath(slctdRte, "New RoutePath", 0);
			slctdRte.addPath(newRtePth);
			new RoutePathEdit(newRtePth, this, RoutePathEdit.EDIT_NEW_ROUTEPATH)
					.setVisible(true);
		} else {
			JOptionPane
					.showMessageDialog(
							this,
							"Please select a route that you want to add this RoutePath to.",
							"Add RoutePath", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Creates a {@code RoutePathEdit} dialog to edit the currently selected
	 * route path.
	 */
	private void editRoutePath() {
		RoutePath rtePth = getSelectedRoutePath();
		if (rtePth != null) {
			new RoutePathEdit(rtePth, this,
					RoutePathEdit.EDIT_EXISTING_ROUTEPATH).setVisible(true);
		} else {
			JOptionPane.showMessageDialog(this,
					"Please select a RoutePath to edit.", "Edit RoutePath",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Deletes the selected route path from the selected route in the
	 * {@code SystemObjects} database.
	 */
	private void deleteRoutePath() {
		Route slctdRte = getSelectedRoute();
		RoutePath slctdRtePth = getSelectedRoutePath();
		if (slctdRtePth != null) {
			slctdRte.removePath(slctdRtePth);
		}
		updatePathComboBox();
	}

	/**
	 * Creates a new {@code Route} in the {@code SystemObjects} database and
	 * shows a corresponding {@code RouteEdit} dialog to edit it.
	 */
	private void newRoute() {
		Route newRoute = new Route(0, "New Route");
		mainFrame.getSystemObjects().addRoute(newRoute);
		new RouteEdit(newRoute, this, RouteEdit.EDIT_NEW_ROUTE)
				.setVisible(true);
		updateRouteComboBox();
	}

	/**
	 * Creates a {@code RouteEdit} dialog to edit the selected route.
	 */
	private void editRoute() {
		Route selectedRoute = getSelectedRoute();
		if (selectedRoute != null) {
			new RouteEdit(selectedRoute, this, RouteEdit.EDIT_EXISTING_ROUTE)
					.setVisible(true);
		} else {
			JOptionPane.showMessageDialog(this,
					"Please select a route that you want to edit.",
					"Edit Route", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Deletes the currently selected route from the {@code SystemObjects}
	 * database.
	 */
	private void deleteRoute() {
		Route selectedRoute = getSelectedRoute();
		if (selectedRoute != null) {
			mainFrame.getSystemObjects().removeRoute(selectedRoute);
			updateRouteComboBox();
			updatePathComboBox();
		} else {
			JOptionPane.showMessageDialog(this,
					"Please select a route that you want to delete.",
					"Delete Route", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Creates a {@code WayPointEdit} dialog to edit the currently selected
	 * waypoint.
	 */
	private void editWayPoint() {
		WayPoint slctdWp = getSelectedWayPoint();
		if (slctdWp != null) {
			new WayPointEdit(slctdWp, this, WayPointEdit.EDIT_EXISTING_WAYPOINT)
					.setVisible(true);
		} else {
			JOptionPane.showMessageDialog(this,
					"Please select WayPoint to edit.", "Edit WayPoint",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Adds a {@code WayPoint} to the currently selected route path and shows a
	 * corresponding {@code WayPointEdit} dialog to edit it.
	 */
	private void addWayPoint() {
		WayPoint slctdWp = getSelectedWayPoint();
		RoutePath slctdRtePth = getSelectedRoutePath();
		System.out.println("SELECTED WAYPOINT: " + slctdWp);

		Coordinate defaultLoc = mainFrame.getMap().getPosition();
		WayPoint newWp = new WayPoint(slctdRtePth, defaultLoc.getLat(),
				defaultLoc.getLon());

		if (slctdRtePth != null) {
			if (slctdWp != null) {
				slctdRtePth.insertNewWayPoint(newWp, slctdWp);
			} else {
				slctdRtePth.addWayPoint(newWp);
			}
			new WayPointEdit(newWp, this, WayPointEdit.EDIT_NEW_WAYPOINT)
					.setVisible(true);
		} else {
			JOptionPane.showMessageDialog(this, "Please select a RoutePath.",
					"Add WayPoint", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Deletes the currently selected waypoint from the selected route path.
	 */
	private void deleteWayPoint() {
		WayPoint slctdWp = getSelectedWayPoint();

		if (slctdWp != null) {
			int response = JOptionPane.showConfirmDialog(this,
					"Are you sure you want to delete this WayPoint?",
					"Delete WayPoint", JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE);
			if (response == JOptionPane.YES_OPTION) {
				RoutePath slctdRtePth = getSelectedRoutePath();
				slctdRtePth.deleteWayPoint(slctdWp);
			}
			updateWayPointList();
		} else {
			JOptionPane.showMessageDialog(this,
					"Please select a WayPoint to delete.", "Delete WayPoint",
					JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * Initializes the GUI for this {@code LineEdit}.
	 */
	private void setUpGui() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setTitle("Line Editor");
		setAlwaysOnTop(true);

		setBounds(100, 100, 598, 302);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] { 236, 254, 0 };
		gbl_contentPane.rowHeights = new int[] { 313, 0 };
		gbl_contentPane.columnWeights = new double[] { 0.0, 1.0,
				Double.MIN_VALUE };
		gbl_contentPane.rowWeights = new double[] { 1.0, Double.MIN_VALUE };
		contentPane.setLayout(gbl_contentPane);

		JPanel panel_routeOptions = new JPanel();
		panel_routeOptions.setBorder(new TitledBorder(null, "Route Options",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panel_routeOptions = new GridBagConstraints();
		gbc_panel_routeOptions.insets = new Insets(0, 0, 0, 5);
		gbc_panel_routeOptions.fill = GridBagConstraints.BOTH;
		gbc_panel_routeOptions.gridx = 0;
		gbc_panel_routeOptions.gridy = 0;
		contentPane.add(panel_routeOptions, gbc_panel_routeOptions);
		GridBagLayout gbl_panel_routeOptions = new GridBagLayout();
		gbl_panel_routeOptions.columnWidths = new int[] { 78, 140, 0 };
		gbl_panel_routeOptions.rowHeights = new int[] { 0, 0, 0, 0, 0, 0 };
		gbl_panel_routeOptions.columnWeights = new double[] { 1.0, 0.0,
				Double.MIN_VALUE };
		gbl_panel_routeOptions.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0,
				0.0, Double.MIN_VALUE };
		panel_routeOptions.setLayout(gbl_panel_routeOptions);

		JLabel lblSelectRoute = new JLabel("Select Route");
		GridBagConstraints gbc_lblSelectRoute = new GridBagConstraints();
		gbc_lblSelectRoute.anchor = GridBagConstraints.EAST;
		gbc_lblSelectRoute.insets = new Insets(0, 0, 5, 5);
		gbc_lblSelectRoute.gridx = 0;
		gbc_lblSelectRoute.gridy = 0;
		panel_routeOptions.add(lblSelectRoute, gbc_lblSelectRoute);

		comboBox_route = new JComboBox<Route>();
		comboBox_route.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (getSelectedRoute() != null) {
					updatePathComboBox();
				}
			}
		});
		GridBagConstraints gbc_comboBox_route = new GridBagConstraints();
		gbc_comboBox_route.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox_route.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_route.gridx = 1;
		gbc_comboBox_route.gridy = 0;
		panel_routeOptions.add(comboBox_route, gbc_comboBox_route);

		JPanel panel_RouteOptions = new JPanel();
		GridBagConstraints gbc_panel_RouteOptions = new GridBagConstraints();
		gbc_panel_RouteOptions.insets = new Insets(0, 0, 5, 0);
		gbc_panel_RouteOptions.fill = GridBagConstraints.BOTH;
		gbc_panel_RouteOptions.gridx = 1;
		gbc_panel_RouteOptions.gridy = 1;
		panel_routeOptions.add(panel_RouteOptions, gbc_panel_RouteOptions);

		JButton btn_newRoute = new JButton("New");
		panel_RouteOptions.add(btn_newRoute);

		JButton btn_editRoute = new JButton("Edit");
		btn_editRoute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				editRoute();
			}
		});
		panel_RouteOptions.add(btn_editRoute);

		JButton btn_deleteRoute = new JButton("Delete");
		btn_deleteRoute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteRoute();
			}
		});
		panel_RouteOptions.add(btn_deleteRoute);
		btn_newRoute.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				newRoute();
			}
		});

		JLabel lblSelectPath = new JLabel("Select Path");
		GridBagConstraints gbc_lblSelectPath = new GridBagConstraints();
		gbc_lblSelectPath.insets = new Insets(0, 0, 5, 5);
		gbc_lblSelectPath.anchor = GridBagConstraints.EAST;
		gbc_lblSelectPath.gridx = 0;
		gbc_lblSelectPath.gridy = 3;
		panel_routeOptions.add(lblSelectPath, gbc_lblSelectPath);

		comboBox_path = new JComboBox<RoutePath>();
		comboBox_path.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				if (getSelectedRoutePath() != null) {
					updateWayPointList();
					showSelectedPathOnMap();
				}
			}
		});
		GridBagConstraints gbc_comboBox_path = new GridBagConstraints();
		gbc_comboBox_path.insets = new Insets(0, 0, 5, 0);
		gbc_comboBox_path.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox_path.gridx = 1;
		gbc_comboBox_path.gridy = 3;
		panel_routeOptions.add(comboBox_path, gbc_comboBox_path);

		JPanel panel_routePathOptions = new JPanel();
		GridBagConstraints gbc_panel_routePathOptions = new GridBagConstraints();
		gbc_panel_routePathOptions.fill = GridBagConstraints.BOTH;
		gbc_panel_routePathOptions.gridx = 1;
		gbc_panel_routePathOptions.gridy = 4;
		panel_routeOptions.add(panel_routePathOptions,
				gbc_panel_routePathOptions);

		JButton btn_newRoutePath = new JButton("New");
		panel_routePathOptions.add(btn_newRoutePath);
		btn_newRoutePath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				newRoutePath();
			}
		});

		JButton btn_editRoutePath = new JButton("Edit");
		btn_editRoutePath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				editRoutePath();
			}
		});
		panel_routePathOptions.add(btn_editRoutePath);

		JButton btn_deleteRoutePath = new JButton("Delete");
		btn_deleteRoutePath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteRoutePath();
			}
		});
		panel_routePathOptions.add(btn_deleteRoutePath);

		JPanel wayPointPanel = new JPanel();
		wayPointPanel.setName("WayPoints");
		wayPointPanel.setBorder(new TitledBorder(null, "WayPoints",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_wayPointPanel = new GridBagConstraints();
		gbc_wayPointPanel.fill = GridBagConstraints.BOTH;
		gbc_wayPointPanel.gridx = 1;
		gbc_wayPointPanel.gridy = 0;
		contentPane.add(wayPointPanel, gbc_wayPointPanel);
		wayPointPanel.setLayout(new BorderLayout(0, 0));

		JPanel pathOptionsPanel = new JPanel();
		wayPointPanel.add(pathOptionsPanel, BorderLayout.SOUTH);
		pathOptionsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton btn_addWayPoint = new JButton("Add");
		btn_addWayPoint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addWayPoint();
			}
		});
		btn_addWayPoint.setActionCommand("Add");
		pathOptionsPanel.add(btn_addWayPoint);

		JButton btn_editWayPoint = new JButton("Edit");
		btn_editWayPoint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editWayPoint();
			}
		});
		pathOptionsPanel.add(btn_editWayPoint);

		JButton btn_deleteWayPoint = new JButton("Delete");
		btn_deleteWayPoint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteWayPoint();
			}
		});
		pathOptionsPanel.add(btn_deleteWayPoint);

		JScrollPane scrollPane = new JScrollPane();
		wayPointPanel.add(scrollPane);

		list_wayPoints = new JList<WayPoint>();
		list_wayPoints.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		list_wayPoints.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent e) {
				SystemMap map = mainFrame.getMap();
				if (!map.isHighlightedMarkerMoving()) {
					map.setSelectedMapMarker(getSelectedWayPoint());
				}
			}
		});
		scrollPane.setViewportView(list_wayPoints);
	}

	/**
	 * If a {@code WayPoint} is selected on the map, then that waypoint is
	 * selected in the waypoints list.
	 */
	public void markerSelected(MarkerEvent e) {
		selectWayPoint((WayPoint) e.getMapMarker());
	}

	/**
	 * No implementation.
	 */
	public void markerMoved(MarkerEvent e) {
		// do nothing
	}

	/**
	 * No implementation.
	 */
	public void markerMoving(MarkerEvent e) {
		// do nothing
	}

	/**
	 * If empty space is selected on the map, then the selected WayPoint is
	 * de-selected.
	 */
	public void markerNotSelected() {
		list_wayPoints.clearSelection();
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
