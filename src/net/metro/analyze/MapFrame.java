/*******************************************************************************
 * MASS (Metro Analytics Software System) by Sean Harger
 * is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0
 * United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/.
 ******************************************************************************/
package net.metro.analyze;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.metro.systemobjects.Period;
import net.metro.systemobjects.RoutePath;
import net.metro.systemobjects.SystemObjects;

/**
 * Main controller for MASS. Contains the menu bar, map panel, and status bar.
 * The {@code MapFrame} is generally a read-only window in the sense that it can
 * only view existing data from the {@code SystemObjects} database but not edit
 * it.
 * 
 * @author Sean Harger
 * 
 */
public class MapFrame extends JFrame {
	private static final long serialVersionUID = 4369468733497955423L;

	public static final String WELCOME_MESSAGE = "Welcome to a new MASS frame!";
	public static final String EMPTY_MESSAGE = "";

	private SystemObjects objects;
	private ArrayList<RoutePath> routePathConstraint;
	private Period periodConstraint;

	private SystemMap map;
	private DataControlPalette dataControlPalette;

	private JPanel contentPane;
	private JLabel statusLabel;
	private JMenuItem mntmClose;
	private JCheckBoxMenuItem chckbxmntmShowDataControlPalette;
	private JCheckBoxMenuItem chckbxmntmShowBackgroundStops;

	/**
	 * Constructs a {@code MapFrame}.
	 * 
	 * @param objs
	 *            reference to the {@code SystemObjects} database.
	 */
	public MapFrame(SystemObjects objs) {
		this(objs, new ArrayList<RoutePath>(), Period.WEEKDAY_BASE);
	}

	/**
	 * Constructs a {@code MapFrame}.
	 * 
	 * @param objs
	 *            reference to the {@code SystemObjects} database.
	 * @param rtePthConst
	 *            reference to the {@code RoutePath} constraint.
	 * @param pdConst
	 *            reference to the {@code Period} constraint.
	 */
	public MapFrame(SystemObjects objs, ArrayList<RoutePath> rtePthConst,
			Period pdConst) {
		objects = objs;
		routePathConstraint = rtePthConst;
		periodConstraint = pdConst;

		setUpGui();
		registerListeners();
	}

	/**
	 * Returns the "Close" menu item.
	 * 
	 * @return the "Close" menu item.
	 */
	protected JMenuItem getCloseMenuItem() {
		return mntmClose;
	}

	/**
	 * Returns the "Show Data Control Palette" menu item.
	 * 
	 * @return the "Show Data Control Palette" menu item.
	 */
	protected JCheckBoxMenuItem getShowControlPaletteItem() {
		return chckbxmntmShowDataControlPalette;
	}

	/**
	 * Returns the "Show Background Stops" menu item.
	 * 
	 * @return the "Show Background Stops" menu item.
	 */
	protected JCheckBoxMenuItem getShowBackgroundStopsItem() {
		return chckbxmntmShowBackgroundStops;
	}

	/**
	 * Returns the {@code SystemMap} contained within this {@code MapFrame}.
	 * 
	 * @return the {@code SystemMap} contained within this {@code MapFrame}.
	 */
	protected SystemMap getMap() {
		return map;
	}

	/**
	 * Returns the {@code DataControlPalette} that controls the data shown on
	 * the map contained in this {@code MapFrame}.
	 * 
	 * @return the {@code DataControlPalette} that controls the data shown on
	 *         the map contained in this {@code MapFrame}.
	 */
	public DataControlPalette getDataController() {
		return dataControlPalette;
	}

	/**
	 * Updates the visibility of background stops on the map according to
	 * whether the menu item is checked or unchecked. Background stops are stops
	 * that are still able to be viewed, but are not a part of the currently
	 * specified {@code RoutePath}s in the {@code DataControlPalette} route path
	 * constraint list.
	 */
	private void updateBackgroundStopVisibility() {
		map.setBackgroundRoutesVisible(chckbxmntmShowBackgroundStops
				.isSelected());
		map.repaint();
	}

	/**
	 * Updates the visibility of the data control palette according to whether
	 * the menu item is checked or unchecked.
	 */
	private void updateDataControlPaletteVisibility() {
		boolean isVisible = chckbxmntmShowDataControlPalette.isSelected();
		dataControlPalette.setVisible(isVisible);
	}

	/**
	 * Returns the {@code SystemObjects} database referenced by this
	 * {@code MapFrame}.
	 * 
	 * @return the {@code SystemObjects} database referenced by this
	 *         {@code MapFrame}.
	 */
	protected SystemObjects getSystemObjects() {
		return objects;
	}

	/**
	 * Sets the status bar message to the specified string.
	 * 
	 * @param msg
	 *            {@code String} with the desired message.
	 */
	public void setStatusMessage(String msg) {
		statusLabel.setText(msg);
	}

	/**
	 * Returns the {@code Period} constraint currently active in this
	 * {@code MapFrame}.
	 * 
	 * @return the {@code Period} constraint currently active in this
	 *         {@code MapFrame}.
	 */
	protected Period getPeriodConstraint() {
		return periodConstraint;
	}

	/**
	 * Returns the {@code RoutePath} constraint currently active in this
	 * {@code mapFrame}.
	 * 
	 * @return the {@code RoutePath} constraint currently active in this
	 *         {@code mapFrame}.
	 */
	protected ArrayList<RoutePath> getRoutePathConstraint() {
		return routePathConstraint;
	}

	/**
	 * Sets the {@code RoutePath} constraint for this {@code MapFrame}.
	 * 
	 * @param rtePths
	 *            list of route paths to be included in data visualization.
	 */
	protected void setRoutePathConstraint(ArrayList<RoutePath> rtePths) {
		if (this.routePathConstraint == null
				|| !routePathConstraint.equals(rtePths)) {
			routePathConstraint = rtePths;
			updateSelectedServices();
		}
	}

	/**
	 * Sets the {@code Period} constraint for this {@code MapFrame}.
	 * 
	 * @param pd
	 *            period to be included in data visualization.
	 */
	protected void setPeriodConstraint(Period pd) {
		if (this.periodConstraint == null || !pd.equals(this.periodConstraint)) {
			periodConstraint = pd;
			updateSelectedServices();
		}
	}

	/**
	 * Sets the selected services on the {@code SystemMap} to services that
	 * match the {@code RoutePath} and {@code Period} constraints within the
	 * {@code SystemObjects} database.
	 */
	private void updateSelectedServices() {
		if (routePathConstraint != null && periodConstraint != null) {
			map.setSelectedServices(objects.getServicesWithConstraint(
					routePathConstraint, periodConstraint));
		}
	}

	/**
	 * Initializes the GUI for this {@code MapFrame}.
	 */
	private void setUpGui() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
		}

		setTitle("MASS Additional Frame");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				MapFrame.class.getResource("/res/metroIcon.png")));

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		mntmClose = new JMenuItem("Close");
		mntmClose.setIcon(new ImageIcon(MapFrame.class
				.getResource("/res/close16.png")));

		mnFile.add(mntmClose);

		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);

		chckbxmntmShowBackgroundStops = new JCheckBoxMenuItem(
				"Show Background Stops");
		chckbxmntmShowBackgroundStops.setSelected(true);
		mnView.add(chckbxmntmShowBackgroundStops);

		chckbxmntmShowDataControlPalette = new JCheckBoxMenuItem(
				"Show Data Control Palette");
		mnView.add(chckbxmntmShowDataControlPalette);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JPanel centerPanel = new JPanel();
		contentPane.add(centerPanel, BorderLayout.CENTER);
		centerPanel.setLayout(new BorderLayout(0, 0));

		map = new SystemMap(this);
		centerPanel.add(map);
		dataControlPalette = new DataControlPalette(this, map);
		map.add(dataControlPalette);

		JPanel statusPanel = new JPanel();
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
				null, null));
		statusPanel.setPreferredSize(new Dimension(this.getWidth(), 20));
		statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.X_AXIS));
		statusLabel = new JLabel(MapFrame.WELCOME_MESSAGE);
		statusLabel.setHorizontalAlignment(SwingConstants.LEFT);
		statusPanel.add(statusLabel);
		contentPane.add(statusPanel, BorderLayout.SOUTH);
	}

	/**
	 * Initializes the {@code EventListener}s for the GUI elements.
	 */
	private void registerListeners() {
		mntmClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});

		chckbxmntmShowDataControlPalette
				.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent e) {
						updateDataControlPaletteVisibility();
					}
				});

		chckbxmntmShowBackgroundStops.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				updateBackgroundStopVisibility();
			}
		});
	}
}
