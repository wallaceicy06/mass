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
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

import net.metro.systemobjects.Route;
import net.metro.systemobjects.RoutePath;

/**
 * Frame that allows the user to select a {@code RoutePath} to add to the route
 * path constraint. It is intended to be invoked by a {@code DataControlPalette}
 * , and it is from the user's selection that a {@code RoutePath} will be
 * identified as "selected" on the map.
 * 
 * @author Sean Harger
 */
public class AddRoutePath extends JDialog {
	private static final long serialVersionUID = 4547383301507605081L;

	private DataControlPalette controlPalette;
	private ArrayList<Route> routes;
	private DefaultListModel<RoutePath> selectedRoutePaths;

	private final JPanel contentPanel = new JPanel();
	private JComboBox<Route> comboBox_route;
	private JComboBox<RoutePath> comboBox_routePath;

	/**
	 * Constructs an {@code AddRoutePath} dialog.
	 * 
	 * @param ctrlPltte
	 *            the {@code DataControlPalette} from which the
	 *            {@code AddRoutePath} was invoked.
	 * @param rtes
	 *            the visible {@code Route}s available to add.
	 * @param slctdPths
	 *            the list model from the {@code DataControlPalette} to add
	 *            selected {@code RoutePath}s to.
	 */
	public AddRoutePath(DataControlPalette ctrlPltte, ArrayList<Route> rtes,
			DefaultListModel<RoutePath> slctdPths) {
		controlPalette = ctrlPltte;
		routes = rtes;
		selectedRoutePaths = slctdPths;

		setUpGui();
		addRouteComboBoxItems();
	}

	/**
	 * Attempts to commit the {@code RoutePath} selection made by the user.
	 * Displays error message if {@code RoutePath} is already in list.
	 */
	private void processSelection() {
		RoutePath rtePth = getSelectedRoutePath();
		if (rtePth != null) {
			if (controlPalette.isRoutePathInList(rtePth)) {
				JOptionPane.showMessageDialog(this,
						"RoutePath is already in list.", "Cannot Add",
						JOptionPane.INFORMATION_MESSAGE);
			} else {
				selectedRoutePaths.addElement(rtePth);
				this.dispose();
			}
		} else {
			JOptionPane.showMessageDialog(this, "No RoutePath is selected.",
					"Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Dismisses the user's selection and closes the dialog.
	 */
	private void cancelSelection() {
		this.dispose();
	}

	/**
	 * Returns the selected {@code Route} object in the routes combo box.
	 * 
	 * @return the selected {@code Route} object in the routes combo box.
	 */
	private Route getSelectedRoute() {
		return (Route) comboBox_route.getSelectedItem();
	}

	/**
	 * Returns the selected {@code RoutePath} in the route paths combo box.
	 * 
	 * @return the selected {@code RoutePath} in the route paths combo box.
	 */
	private RoutePath getSelectedRoutePath() {
		return (RoutePath) comboBox_routePath.getSelectedItem();
	}

	/**
	 * Adds all viewable {@code Route}s to the routes combo box.
	 */
	private void addRouteComboBoxItems() {
		for (Route rte : routes) {
			comboBox_route.addItem(rte);
		}
	}

	/**
	 * Refreshes the {@code RoutePath} options in the route paths combo box
	 * according to the selection made in the route combo box.
	 */
	private void updateRoutePathComboBox() {
		comboBox_routePath.removeAllItems();
		Route rte = getSelectedRoute();
		if (rte != null) {
			for (RoutePath rtePth : rte.getPaths()) {
				comboBox_routePath.addItem(rtePth);
			}
		}
	}

	/**
	 * Initializes the GUI elements of the {@code AddRoutePath} frame.
	 */
	private void setUpGui() {
		setTitle("Add");
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				AddRoutePath.class.getResource("/res/add128.png")));
		setModal(true);
		setBounds(100, 100, 200, 200);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		setLocationRelativeTo(controlPalette);

		JPanel panel_options = new JPanel();
		contentPanel.add(panel_options, BorderLayout.CENTER);
		GridBagLayout gbl_panel_options = new GridBagLayout();
		gbl_panel_options.columnWidths = new int[] { 0, 0 };
		gbl_panel_options.rowHeights = new int[] { 0, 0, 0 };
		gbl_panel_options.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gbl_panel_options.rowWeights = new double[] { 0.0, 0.0,
				Double.MIN_VALUE };
		panel_options.setLayout(gbl_panel_options);

		JPanel panel_route = new JPanel();
		FlowLayout fl_panel_route = (FlowLayout) panel_route.getLayout();
		fl_panel_route.setAlignment(FlowLayout.LEFT);
		panel_route.setBorder(new TitledBorder(null, "Route",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panel_route = new GridBagConstraints();
		gbc_panel_route.insets = new Insets(0, 0, 5, 0);
		gbc_panel_route.fill = GridBagConstraints.BOTH;
		gbc_panel_route.gridx = 0;
		gbc_panel_route.gridy = 0;
		panel_options.add(panel_route, gbc_panel_route);

		comboBox_route = new JComboBox<Route>();
		comboBox_route.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				updateRoutePathComboBox();
			}
		});
		comboBox_route.setPreferredSize(new Dimension(100, 20));
		panel_route.add(comboBox_route);

		JPanel panel_routePath = new JPanel();
		FlowLayout fl_panel_routePath = (FlowLayout) panel_routePath
				.getLayout();
		fl_panel_routePath.setAlignment(FlowLayout.LEFT);
		panel_routePath.setBorder(new TitledBorder(null, "Path",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panel_routePath = new GridBagConstraints();
		gbc_panel_routePath.fill = GridBagConstraints.BOTH;
		gbc_panel_routePath.gridx = 0;
		gbc_panel_routePath.gridy = 1;
		panel_options.add(panel_routePath, gbc_panel_routePath);

		comboBox_routePath = new JComboBox<RoutePath>();
		comboBox_routePath.setPreferredSize(new Dimension(150, 20));
		panel_routePath.add(comboBox_routePath);

		JPanel panel_confirm = new JPanel();
		contentPanel.add(panel_confirm, BorderLayout.SOUTH);

		JButton btnOk = new JButton("OK");
		btnOk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				processSelection();
			}
		});
		panel_confirm.add(btnOk);

		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelSelection();
			}
		});
		panel_confirm.add(btnCancel);
	}

}
