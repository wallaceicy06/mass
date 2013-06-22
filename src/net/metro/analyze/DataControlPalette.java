/*******************************************************************************
 * MASS (Metro Analytics Software System) by Sean Harger
 * is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0
 * United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/.
 ******************************************************************************/
package net.metro.analyze;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

import javax.swing.AbstractButton;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import net.metro.systemobjects.Period;
import net.metro.systemobjects.RoutePath;
import net.metro.systemobjects.dataobjects.DataType;
import net.metro.systemobjects.periodobjects.Day;
import net.metro.systemobjects.periodobjects.TimePeriod;

/**
 * This internal frame is the controller for all data visualizations on the map.
 * It allows the user to specify the constraints as to which routes, route
 * paths, days, time periods, data types, and scale to be shown on the
 * {@code SystemMap}.
 * 
 * @author Sean Harger
 * 
 */
public class DataControlPalette extends JInternalFrame {
	private static final long serialVersionUID = -5962785107213511272L;

	private static final int TIMEPERIOD_EARLY = 1;
	private static final int TIMEPERIOD_MORNINGRUSH = 2;
	private static final int TIMEPERIOD_BASE = 3;
	private static final int TIMEPERIOD_EVENINGRUSH = 4;
	private static final int TIMEPERIOD_LATE = 5;

	private static final DataType[] DATATYPES = { DataType.BOARDINGS,
			DataType.ALIGHTINGS, DataType.LOAD };

	private SystemMap map;
	private MapFrame mapFrame;
	private Hashtable<Integer, JLabel> labelTable;

	private DefaultListModel<RoutePath> selectedRoutePathsModel;

	private final ButtonGroup dayButtonGroup = new ButtonGroup();
	private JSlider timePeriodSlider;

	private JButton btnAdd;
	private JButton btnRemove;

	private JRadioButton rdbtnWeekdays;
	private JRadioButton rdbtnSaturdays;
	private JRadioButton rdbtnSundays;

	private JComboBox<DataType> comboBox_dataType;
	private final ButtonGroup scaleTypeButtonGroup = new ButtonGroup();
	private AbstractButton rdbtnRelative;
	private JRadioButton rdbtnFixed;
	private JList<RoutePath> selectedRoutePathsList;
	private JButton btnCurrent;
	private JButton btnSet;
	private JSlider slider_maxVal;
	private JSlider slider_minVal;
	private JTextField textField_min;
	private JTextField textField_max;
	private JToggleButton tglbtnCustom;

	/**
	 * Constructs a {@code DataControlPalette} internal frame.
	 * 
	 * @param mpFrm
	 *            reference to {@code MapFrame} from which the
	 *            {@code DataControlPalette} was invoked.
	 * @param mp
	 *            reference to {@code SystemMap} which the
	 *            {@code DataControlPalette} is intended to control.
	 */
	public DataControlPalette(MapFrame mpFrm, SystemMap mp) {
		setFrameIcon(new ImageIcon(
				DataControlPalette.class.getResource("/res/controls.png")));
		map = mp;
		mapFrame = mpFrm;

		constructLabelTable();
		setUpGui();
		registerListeners();
		setInitialValues();
	}

	/**
	 * Disposes frame and de-selects the "Show Data Control Palette" option in
	 * the {@code MapFrame}.
	 */
	public void dispose() {
		mapFrame.getShowControlPaletteItem().setSelected(false);
		super.dispose();
	}

	/**
	 * Constructs a {@code Hashtable} for the time period slider and associates
	 * static integers with selectable time periods.
	 */
	private void constructLabelTable() {
		labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(TIMEPERIOD_EARLY, new JLabel("Early"));
		labelTable.put(TIMEPERIOD_MORNINGRUSH, new JLabel("Morning"));
		labelTable.put(TIMEPERIOD_BASE, new JLabel("Mid-day"));
		labelTable.put(TIMEPERIOD_EVENINGRUSH, new JLabel("Evening"));
		labelTable.put(TIMEPERIOD_LATE, new JLabel("Late"));
	}

	/**
	 * Sets the initial values for the period, day, and time period constraints
	 * according to the initial constraints specified in the {@code MapFrame}.
	 */
	private void setInitialValues() {
		Period pdConstraint = mapFrame.getPeriodConstraint();
		List<RoutePath> rtePthConstraint = mapFrame.getRoutePathConstraint();

		List<Day> days = pdConstraint.getDays();
		if (Day.areDaysEqual(days, Arrays.asList(Day.SATURDAYS))) {
			rdbtnSaturdays.setSelected(true);
		} else if (Day.areDaysEqual(days, Arrays.asList(Day.SUNDAYS))) {
			rdbtnSundays.setSelected(true);
		} else if (Day.areDaysEqual(days, Arrays.asList(Day.WEEKDAYS))) {
			rdbtnWeekdays.setSelected(true);
		}

		TimePeriod tmPd = pdConstraint.getTimePeriod();
		if (tmPd.equals(TimePeriod.EARLY)) {
			timePeriodSlider.setValue(DataControlPalette.TIMEPERIOD_EARLY);
		} else if (tmPd.equals(TimePeriod.MORNINGRUSH)) {
			timePeriodSlider
					.setValue(DataControlPalette.TIMEPERIOD_MORNINGRUSH);
		} else if (tmPd.equals(TimePeriod.BASE)) {
			timePeriodSlider.setValue(DataControlPalette.TIMEPERIOD_BASE);
		} else if (tmPd.equals(TimePeriod.EVENINGRUSH)) {
			timePeriodSlider
					.setValue(DataControlPalette.TIMEPERIOD_EVENINGRUSH);
		} else if (tmPd.equals(TimePeriod.LATE)) {
			timePeriodSlider.setValue(DataControlPalette.TIMEPERIOD_LATE);
		}

		for (RoutePath rtePth : rtePthConstraint) {
			this.selectedRoutePathsModel.addElement(rtePth);
		}

		this.comboBox_dataType.setSelectedItem(map.getSelectedDataType());

	}

	/**
	 * Shows an {@code AddRoutePath} dialog requesting that the user specify a
	 * {@code RoutePath} to add to the constraint list.
	 */
	private void addRoutePath() {
		new AddRoutePath(this, map.getViewableRoutes(), selectedRoutePathsModel)
				.setVisible(true);
	}

	/**
	 * Removes the selected {@code RoutePath} from the constraint list.
	 */
	private void removeSelectedRoutePath() {
		for (RoutePath rtePth : selectedRoutePathsList.getSelectedValuesList()) {
			selectedRoutePathsModel.removeElement(rtePth);
		}
	}

	/**
	 * Returns whether a {@code RoutePath} is in the constraint list.
	 * 
	 * @param rtePth
	 *            {@code RoutePath} to look for in the list.
	 * @return {@code true} if the {@code RoutePath} is in the list.
	 */
	protected boolean isRoutePathInList(RoutePath rtePth) {
		for (RoutePath pth : getRoutePathsInList()) {
			if (rtePth == pth) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns each {@code RoutePath} added to the constraint list.
	 * 
	 * @return list of {@code RoutePath}s added to the constraint list.
	 */
	private ArrayList<RoutePath> getRoutePathsInList() {
		ArrayList<RoutePath> allRoutePaths = new ArrayList<RoutePath>();
		for (int index = 0; index < selectedRoutePathsModel.getSize(); index++) {
			allRoutePaths.add(selectedRoutePathsModel.get(index));
		}
		return allRoutePaths;
	}

	/**
	 * Updates the {@code RoutePath} constraint in the {@code MapFrame}
	 * according to the selected {@code RoutePath}s in the list. Also selects
	 * those same {@code RoutePath}s in the {@code SystemMap} in case the
	 * selected {@code RoutePath}s do not have data. In this way, they are still
	 * shown as selected on the map but are in a different color indicating that
	 * they do not have data.
	 */
	private void updateRoutePathConstraint() {
		mapFrame.setRoutePathConstraint(getRoutePathsInList());
		map.setSelectedRoutePaths(getRoutePathsInList());
	}

	/**
	 * Updates the {@code Period} constraint as specified according to the
	 * selection of day radio buttons and the position of the time period
	 * slider.
	 */
	private void updatePeriodConstraint() {
		mapFrame.setPeriodConstraint(getSelectedPeriod());
	}

	/**
	 * Interprets the selected radio buttons and slider position into a
	 * {@code Period} object
	 * 
	 * @return {@code Period} object representing the currently selected
	 *         day/time period values.
	 */
	private Period getSelectedPeriod() {
		Day[] dys = getSelectedDays();
		TimePeriod tmPd = getSelectedTimePeriod();

		return new Period(dys, tmPd);
	}

	/**
	 * Returns the selected data type in the combo box.
	 * 
	 * @return the selected data type in the combo box.
	 */
	private DataType getSelectedDataType() {
		return (DataType) comboBox_dataType.getSelectedItem();
	}

	/**
	 * Updates the selected {@code DataType} in the {@code SystemMap} according
	 * to the value specified in the {@code combobox_DataType}
	 */
	private void updateDataSelection() {
		DataType typ = getSelectedDataType();
		if (typ != null) {
			map.setSelectedDataType(typ);
			map.repaint();
		}
	}

	/**
	 * Attempts to set the specified minimum and maximum scale values to the
	 * {@code SystemMap}. Displays an error message if the minimum value is
	 * greater than the maximum value or if the typed integer value is not
	 * formatted properly.
	 */
	private void attemptToCommitScaleValues() {
		try {
			int min = Integer.parseInt(textField_min.getText());
			int max = Integer.parseInt(textField_max.getText());

			if (min > max) {
				throw new Exception("Minimum cannot be greater than maximum.");
			}

			map.overrideScaleMin(min);
			map.overrideScaleMax(max);
			slider_minVal.setValue(min);
			slider_maxVal.setValue(max);
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(this, ex.getMessage(), "Error",
					JOptionPane.ERROR_MESSAGE);
			textField_min.setText(Integer.toString(map.getScaleMin()));
			textField_max.setText(Integer.toString(map.getScaleMax()));
		}
	}

	/**
	 * Returns an array of the selected {@code Day} objects from the radio
	 * buttons.
	 * 
	 * @return an array of the selected {@code Day} objects from the radio
	 *         buttons.
	 */
	private Day[] getSelectedDays() {
		if (rdbtnWeekdays.isSelected()) {
			return Day.WEEKDAYS;
		} else if (rdbtnSaturdays.isSelected()) {
			return Day.SATURDAYS;
		} else {
			return Day.SUNDAYS;
		}
	}

	/**
	 * Returns the selected {@code TimePeriod} from the slider.
	 * 
	 * @return the selected {@code TimePeriod} from the slider.
	 */
	private TimePeriod getSelectedTimePeriod() {
		int val = timePeriodSlider.getValue();

		if (val == TIMEPERIOD_EARLY) {
			return TimePeriod.EARLY;
		} else if (val == TIMEPERIOD_MORNINGRUSH) {
			return TimePeriod.MORNINGRUSH;
		} else if (val == TIMEPERIOD_BASE) {
			return TimePeriod.BASE;
		} else if (val == TIMEPERIOD_EVENINGRUSH) {
			return TimePeriod.EVENINGRUSH;
		} else if (val == TIMEPERIOD_LATE) {
			return TimePeriod.LATE;
		} else {
			return null;
		}
	}

	/**
	 * Initializes the GUI for this {@code DataControlPalette}.
	 */
	private void setUpGui() {
		setTitle("Data Control");
		getContentPane().setLayout(new BorderLayout(0, 0));

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		getContentPane().add(tabbedPane);
		tabbedPane.setFocusable(false);

		JPanel panel_filter = new JPanel();
		tabbedPane.addTab("Service Filter", null, panel_filter, null);
		panel_filter.setLayout(new BorderLayout(0, 0));

		JPanel confirmPanel = new JPanel();
		panel_filter.add(confirmPanel, BorderLayout.SOUTH);

		btnAdd = new JButton("Add");
		confirmPanel.add(btnAdd);

		btnRemove = new JButton("Remove");
		confirmPanel.add(btnRemove);

		Panel panel = new Panel();
		panel_filter.add(panel, BorderLayout.CENTER);
		panel.setLayout(new BorderLayout(0, 0));

		JScrollPane scrollPane = new JScrollPane();
		panel.add(scrollPane);

		selectedRoutePathsModel = new DefaultListModel<RoutePath>();
		selectedRoutePathsList = new JList<RoutePath>(selectedRoutePathsModel);
		selectedRoutePathsList
				.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		scrollPane.add(selectedRoutePathsList);
		scrollPane.setViewportView(selectedRoutePathsList);

		JPanel panel_timePeriod = new JPanel();
		panel_timePeriod.setBorder(new LineBorder(new Color(255, 255, 255), 5));
		panel_timePeriod.setBackground(Color.WHITE);
		tabbedPane.addTab("Time Period", null, panel_timePeriod, null);
		GridBagLayout gbl_panel_timePeriod = new GridBagLayout();
		gbl_panel_timePeriod.columnWidths = new int[] { 0, 0 };
		gbl_panel_timePeriod.rowHeights = new int[] { 0, 0, 0 };
		gbl_panel_timePeriod.columnWeights = new double[] { 1.0,
				Double.MIN_VALUE };
		gbl_panel_timePeriod.rowWeights = new double[] { 0.0, 1.0,
				Double.MIN_VALUE };
		panel_timePeriod.setLayout(gbl_panel_timePeriod);

		JPanel panel_days = new JPanel();
		panel_days.setBorder(new TitledBorder(null, "Days",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_days.setBackground(Color.WHITE);
		GridBagConstraints gbc_panel_days = new GridBagConstraints();
		gbc_panel_days.insets = new Insets(0, 0, 5, 0);
		gbc_panel_days.fill = GridBagConstraints.BOTH;
		gbc_panel_days.gridx = 0;
		gbc_panel_days.gridy = 0;
		panel_timePeriod.add(panel_days, gbc_panel_days);

		rdbtnWeekdays = new JRadioButton("Weekdays");
		dayButtonGroup.add(rdbtnWeekdays);
		rdbtnWeekdays.setFocusable(false);
		rdbtnWeekdays.setBackground(Color.WHITE);
		rdbtnWeekdays.setSelected(true);
		panel_days.add(rdbtnWeekdays);

		rdbtnSaturdays = new JRadioButton("Saturdays");
		dayButtonGroup.add(rdbtnSaturdays);
		rdbtnSaturdays.setFocusable(false);
		rdbtnSaturdays.setBackground(Color.WHITE);
		panel_days.add(rdbtnSaturdays);

		rdbtnSundays = new JRadioButton("Sundays");
		dayButtonGroup.add(rdbtnSundays);
		rdbtnSundays.setFocusable(false);
		rdbtnSundays.setBackground(Color.WHITE);
		panel_days.add(rdbtnSundays);

		JPanel panel_timeOfDay = new JPanel();
		panel_timeOfDay.setBorder(new TitledBorder(null, "Time of day",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_timeOfDay.setBackground(Color.WHITE);
		GridBagConstraints gbc_panel_timeOfDay = new GridBagConstraints();
		gbc_panel_timeOfDay.fill = GridBagConstraints.BOTH;
		gbc_panel_timeOfDay.gridx = 0;
		gbc_panel_timeOfDay.gridy = 1;
		panel_timePeriod.add(panel_timeOfDay, gbc_panel_timeOfDay);

		timePeriodSlider = new JSlider();
		timePeriodSlider.setFocusable(false);
		timePeriodSlider.setBackground(Color.WHITE);
		panel_timeOfDay.add(timePeriodSlider);

		timePeriodSlider.setPreferredSize(new Dimension(300, 50));
		timePeriodSlider.setPaintLabels(true);
		timePeriodSlider.setPaintTicks(true);
		timePeriodSlider.setSnapToTicks(true);
		timePeriodSlider.setValue(TIMEPERIOD_BASE);
		timePeriodSlider.setMaximum(TIMEPERIOD_LATE);
		timePeriodSlider.setMinimum(TIMEPERIOD_EARLY);
		timePeriodSlider.setLabelTable(labelTable);

		JPanel panel_dataTypes = new JPanel();

		panel_dataTypes.setBorder(new LineBorder(Color.WHITE, 5));
		panel_dataTypes.setFocusable(false);
		panel_dataTypes.setBackground(Color.WHITE);
		tabbedPane.addTab("Data Types", null, panel_dataTypes, null);
		GridBagLayout gbl_panel_dataTypes = new GridBagLayout();
		gbl_panel_dataTypes.columnWidths = new int[] { 0, 0 };
		gbl_panel_dataTypes.rowHeights = new int[] { 0, 0, 0 };
		gbl_panel_dataTypes.columnWeights = new double[] { 1.0,
				Double.MIN_VALUE };
		gbl_panel_dataTypes.rowWeights = new double[] { 0.0, 0.0,
				Double.MIN_VALUE };
		panel_dataTypes.setLayout(gbl_panel_dataTypes);

		JPanel panel_dataTypeComboBox = new JPanel();
		panel_dataTypeComboBox.setBorder(new TitledBorder(null, "Data Type",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_dataTypeComboBox.setBackground(Color.WHITE);
		FlowLayout fl_panel_dataTypeComboBox = (FlowLayout) panel_dataTypeComboBox
				.getLayout();
		fl_panel_dataTypeComboBox.setAlignment(FlowLayout.LEFT);
		GridBagConstraints gbc_panel_dataTypeComboBox = new GridBagConstraints();
		gbc_panel_dataTypeComboBox.insets = new Insets(0, 0, 5, 0);
		gbc_panel_dataTypeComboBox.fill = GridBagConstraints.BOTH;
		gbc_panel_dataTypeComboBox.gridx = 0;
		gbc_panel_dataTypeComboBox.gridy = 0;
		panel_dataTypes.add(panel_dataTypeComboBox, gbc_panel_dataTypeComboBox);

		// $hide>>$
		comboBox_dataType = new JComboBox<DataType>(DATATYPES);
		panel_dataTypeComboBox.add(comboBox_dataType);
		// $hide<<$

		JPanel panel_scale = new JPanel();
		panel_scale.setBackground(Color.WHITE);
		panel_scale.setBorder(new TitledBorder(null, "Scale",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_panel_scale = new GridBagConstraints();
		gbc_panel_scale.fill = GridBagConstraints.BOTH;
		gbc_panel_scale.gridx = 0;
		gbc_panel_scale.gridy = 1;
		panel_dataTypes.add(panel_scale, gbc_panel_scale);
		panel_scale.setLayout(new BoxLayout(panel_scale, BoxLayout.Y_AXIS));

		JPanel panel_scaleOption = new JPanel();
		panel_scale.add(panel_scaleOption);
		panel_scaleOption.setBackground(Color.WHITE);
		FlowLayout fl_panel_scaleOption = (FlowLayout) panel_scaleOption
				.getLayout();
		fl_panel_scaleOption.setAlignment(FlowLayout.LEFT);

		rdbtnRelative = new JRadioButton("Relative");
		rdbtnRelative.setSelected(true);
		rdbtnRelative.setFocusable(false);
		rdbtnRelative.setBackground(Color.WHITE);
		scaleTypeButtonGroup.add(rdbtnRelative);
		panel_scaleOption.add(rdbtnRelative);

		rdbtnFixed = new JRadioButton("Fixed");
		panel_scaleOption.add(rdbtnFixed);
		rdbtnFixed.setFocusable(false);
		rdbtnFixed.setBackground(Color.WHITE);
		scaleTypeButtonGroup.add(rdbtnFixed);

		btnCurrent = new JButton("Current");
		panel_scaleOption.add(btnCurrent);
		btnCurrent.setEnabled(false);

		tglbtnCustom = new JToggleButton("Custom");
		tglbtnCustom.setEnabled(false);
		panel_scaleOption.add(tglbtnCustom);

		btnSet = new JButton("Set");
		panel_scaleOption.add(btnSet);
		btnSet.setEnabled(false);

		JPanel panel_minControl = new JPanel();
		panel_minControl.setBackground(Color.WHITE);
		FlowLayout flowLayout = (FlowLayout) panel_minControl.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel_scale.add(panel_minControl);

		JLabel lblMin = new JLabel("min");
		lblMin.setPreferredSize(new Dimension(25, 14));
		panel_minControl.add(lblMin);

		slider_minVal = new JSlider();
		slider_minVal.setMinimum(0);
		slider_minVal.setMaximum(500);
		slider_minVal.setValue(0);
		slider_minVal.setPreferredSize(new Dimension(180, 23));
		slider_minVal.setEnabled(false);
		slider_minVal.setBackground(Color.WHITE);
		panel_minControl.add(slider_minVal);

		textField_min = new JTextField();
		textField_min.setEnabled(false);
		textField_min.setEditable(false);
		textField_min.setText(Integer.toString(slider_minVal.getValue()));
		panel_minControl.add(textField_min);
		textField_min.setColumns(5);

		JPanel panel_maxControl = new JPanel();
		panel_maxControl.setBackground(Color.WHITE);
		FlowLayout flowLayout_1 = (FlowLayout) panel_maxControl.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		panel_scale.add(panel_maxControl);

		JLabel lblMax = new JLabel("max");
		lblMax.setPreferredSize(new Dimension(25, 14));
		panel_maxControl.add(lblMax);

		slider_maxVal = new JSlider();
		slider_maxVal.setMinimum(0);
		slider_maxVal.setMaximum(500);
		slider_maxVal.setValue(0);
		slider_maxVal.setPreferredSize(new Dimension(180, 23));
		slider_maxVal.setEnabled(false);
		slider_maxVal.setBackground(Color.WHITE);
		panel_maxControl.add(slider_maxVal);

		textField_max = new JTextField();
		textField_max.setEnabled(false);
		textField_max.setEditable(false);
		textField_max.setText(Integer.toString(slider_maxVal.getValue()));
		panel_maxControl.add(textField_max);
		textField_max.setColumns(5);

		setBounds(100, 100, 337, 244);
		setClosable(true);
		setMaximizable(false);
		setLocationBottomRight();
	}

	/**
	 * Initializes the {@code EventListener}s for the GUI elements.
	 */
	private void registerListeners() {
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addRoutePath();
			}
		});

		btnRemove.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				removeSelectedRoutePath();
			}
		});

		selectedRoutePathsModel.addListDataListener(new ListDataListener() {
			public void contentsChanged(ListDataEvent arg0) {
			}

			public void intervalAdded(ListDataEvent arg0) {
				updateRoutePathConstraint();
			}

			public void intervalRemoved(ListDataEvent arg0) {
				updateRoutePathConstraint();
			}

		});

		rdbtnWeekdays.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				updatePeriodConstraint();
			}
		});

		rdbtnSaturdays.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				updatePeriodConstraint();
			}
		});

		rdbtnSundays.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				updatePeriodConstraint();
			}
		});

		timePeriodSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				updatePeriodConstraint();
			}
		});

		map.addComponentListener(new ComponentListener() {
			public void componentHidden(ComponentEvent arg0) {
			}

			public void componentMoved(ComponentEvent arg0) {
			}

			public void componentResized(ComponentEvent arg0) {
				setLocationBottomRight();
			}

			public void componentShown(ComponentEvent arg0) {
			}
		});

		rdbtnRelative.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (rdbtnRelative.isSelected()) {
					map.setScaleFixed(false);
					slider_minVal.setEnabled(false);
					slider_maxVal.setEnabled(false);
					btnCurrent.setEnabled(false);
					btnSet.setEnabled(false);
					tglbtnCustom.setEnabled(false);
					textField_min.setEnabled(false);
					textField_max.setEnabled(false);
					map.repaint();
				}
			}
		});

		rdbtnFixed.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if (rdbtnFixed.isSelected()) {
					map.setScaleFixed(true);
					int relativeMax = map.getRelativeMax();
					// relative min removed due to default setting of minimum to
					// 0
					slider_minVal.setEnabled(true);
					slider_minVal.setMaximum(relativeMax);

					slider_maxVal.setEnabled(true);
					slider_maxVal.setMaximum(relativeMax);

					btnCurrent.setEnabled(true);
					btnSet.setEnabled(true);
					tglbtnCustom.setEnabled(true);
					textField_min.setEnabled(true);
					textField_max.setEnabled(true);

					map.repaint();
				}
			}
		});

		comboBox_dataType.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent arg0) {
				updateDataSelection();
			}
		});

		btnSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			}
		});

		slider_minVal.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				map.overrideScaleMin(slider_minVal.getValue());
				textField_min.setText(Integer.toString(slider_minVal.getValue()));
			}
		});

		slider_maxVal.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				map.overrideScaleMax(slider_maxVal.getValue());
				textField_max.setText(Integer.toString(slider_maxVal.getValue()));
			}
		});

		tglbtnCustom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (tglbtnCustom.isSelected()) {
					tglbtnCustom.setText("Set");
					textField_min.setEditable(true);
					textField_max.setEditable(true);
				} else {
					tglbtnCustom.setText("Custom");
					textField_min.setEditable(false);
					textField_max.setEditable(false);
					attemptToCommitScaleValues();
				}
			}
		});

		btnCurrent.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int min = map.getViewableMin();
				int max = map.getViewableMax();
				slider_minVal.setValue(min);
				slider_maxVal.setValue(max);
			}
		});
	}

	/**
	 * Sets the location of this {@code DataControlPalette} to the bottom right
	 * hand corner of the {@code MapFrame}.
	 */
	private void setLocationBottomRight() {
		setLocation(map.getWidth() - this.getWidth(),
				map.getHeight() - this.getHeight());
	}

}
