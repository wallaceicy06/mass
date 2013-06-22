/*******************************************************************************
 * MASS (Metro Analytics Software System) by Sean Harger
 * is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0
 * United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/.
 ******************************************************************************/
/*
 * MASS (Metro Analytics Software System) by Sean Harger is licensed under 
 * the Creative Commons Attribution-NonCommercial-ShareAlike 3.0 United States License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-sa/3.0/us/.
 */

package net.metro.analyze;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.filechooser.FileNameExtensionFilter;

import net.metro.systemobjects.SystemObjects;

/**
 * Extends the functionality of the {@code MapFrame} class by adding the ability
 * to manipulate existing data from within the {@code SystemObjects} database.
 * This is accomplished primarily through the provision of a {@code LineEditor}
 * that facilitates the editing of routes, route paths, and waypoints. This is
 * the only {@code MapFrame} that allows the user to import & export data as
 * well as exit the program.
 * 
 * @author Sean Harger
 * 
 */
public class MainFrame extends MapFrame {
	private static final long serialVersionUID = 6314779640340884701L;

	public static final String EMPTY_MESSAGE = "";
	public static final String WELCOME_MESSAGE = "Welcome to MASS!";

	private ArrayList<MapFrame> mapFrames;

	private JMenuItem mntmLineEditor;
	private JMenuItem mntmNewMap;
	private JMenu mnImport;
	private JMenu mnExport;
	private JMenuItem mntmImportRoutes;
	private JMenuItem mntmImportServices;
	private JMenuItem mntmExportRoutes;
	private JMenuItem mntmExportServices;

	/**
	 * Constructs a {@code MainFrame}.
	 */
	public MainFrame() {
		super(new SystemObjects());
		mapFrames = new ArrayList<MapFrame>();
		mapFrames.add(this);
		setUpGui();
		registerListeners();
	}

	/**
	 * Returns all the secondary {@code MapFrame}s associated with this
	 * {@code MainFrame}.
	 * 
	 * @return
	 */
	protected ArrayList<MapFrame> getMapFrames() {
		return mapFrames;
	}

	/**
	 * Displays a {@code JFileChooser} and prompts the user to select a file
	 * from their file system.
	 * 
	 * @return selected file <br>
	 *         {@code null} if none selected
	 */
	private File selectFile() {
		final JFileChooser importFileChooser = new JFileChooser();
		FileNameExtensionFilter filter = new FileNameExtensionFilter(
				"CSV files", "csv");
		importFileChooser.setFileFilter(filter);

		int importChooserResult = importFileChooser.showDialog(this,
				"Select File");
		if (importChooserResult == JFileChooser.APPROVE_OPTION) {
			File importFile = importFileChooser.getSelectedFile();
			return importFile;
		}
		return null;
	}

	/**
	 * Prompts the user to select a file from their file system and then creates
	 * a {@code RouteImport} to attempt to import route data from that file.
	 * Route data includes route numbers, paths, stops, and waypoints.
	 */
	private void importRoutes() {
		File selectedRouteFile = selectFile();
		if (selectedRouteFile != null) {
			FileImport rteImport = new RouteImport(selectedRouteFile,
					super.getSystemObjects(), this);
			rteImport.go();
		}
	}

	/**
	 * Prompts the user to select a file from their file system and then creates
	 * a {@code ServiceImport} to attempt to import service data from that file.
	 * Service data includes periods, times, and all data including boardings,
	 * alightings, and load.
	 */
	private void importServices() {
		File selectedServiceFile = selectFile();
		if (selectedServiceFile != null) {
			ServiceImport svcImport = new ServiceImport(selectedServiceFile,
					super.getSystemObjects(), this);
			svcImport.go();
		}
	}

	/**
	 * Prompts the user to select a file from their file system and then creates
	 * a {@code RouteExport} to attempt to export modified route data from
	 * within MASS to that file. Route data includes route numbers, paths,
	 * stops, and waypoints.
	 */
	private void exportRoutes() {
		File selectedRouteFile = selectFile();
		if (selectedRouteFile != null) {
			new RouteExport(selectedRouteFile, super.getSystemObjects(), this);
		}
	}

	/**
	 * Creates a new secondary {@code MapFrame}. This is typically used to
	 * analyze data with different {@code RoutePath} and {@code Service}
	 * constraints than the current {@code MapFrame}.
	 */
	private void openNewMap() {
		MapFrame anotherMap = new MapFrame(getSystemObjects());
		anotherMap.getMap()
				.setViewableRoutes(this.getMap().getViewableRoutes());
		mapFrames.add(anotherMap);
		anotherMap.setVisible(true);
	}

	/**
	 * Creates a new {@code LineEdit } frame and opens it.
	 */
	private void openLineEditor() {
		new LineEdit(this);
	}

	/**
	 * Initializes the GUI elements of this {@code MainFrame}. Adds additional
	 * menu items not contained within a standard {@code MapFrame} and allows
	 * the closing of the {@code MainFrame} to terminate the program.
	 */
	private void setUpGui() {
		setTitle("MASS");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = super.getJMenuBar();
		menuBar.removeAll();

		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);

		mntmNewMap = new JMenuItem("Add map");
		mntmNewMap.setIcon(new ImageIcon(MainFrame.class
				.getResource("/res/add16.png")));
		mnFile.add(mntmNewMap);

		mnImport = new JMenu("Import");
		mnImport.setIcon(new ImageIcon(MainFrame.class
				.getResource("/res/import16.png")));
		mnFile.add(mnImport);

		mntmImportRoutes = new JMenuItem("Routes");
		mnImport.add(mntmImportRoutes);

		mntmImportServices = new JMenuItem("Services");
		mnImport.add(mntmImportServices);

		mnExport = new JMenu("Export");
		mnExport.setIcon(new ImageIcon(MainFrame.class
				.getResource("/res/export16.png")));
		mnFile.add(mnExport);

		mntmExportRoutes = new JMenuItem("Routes");
		mnExport.add(mntmExportRoutes);

		mntmExportServices = new JMenuItem("Services");
		mntmExportServices.setEnabled(false);
		mnExport.add(mntmExportServices);

		mnFile.add(super.getCloseMenuItem());

		JMenu mnView = new JMenu("View");
		menuBar.add(mnView);

		mnView.add(super.getShowBackgroundStopsItem());

		mnView.add(super.getShowControlPaletteItem());

		JMenu mnTools = new JMenu("Tools");
		menuBar.add(mnTools);

		mntmLineEditor = new JMenuItem("Line Editor");
		mnTools.add(mntmLineEditor);
	}

	/**
	 * Initializes the {@code EventListener}s for the GUI elements.
	 */
	private void registerListeners() {
		mntmNewMap.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				openNewMap();
			}
		});

		mntmImportRoutes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				importRoutes();
			}
		});

		mntmImportServices.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				importServices();
			}
		});

		mntmExportRoutes.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				exportRoutes();
			}
		});

		super.getCloseMenuItem().addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});

		mntmLineEditor.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				openLineEditor();
			}
		});
	}
}
