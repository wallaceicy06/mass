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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import net.metro.systemobjects.SystemObjects;

/**
 * Abstract class for importing data to be used within MASS. Imported files are
 * tabular, comma delimited text files with the {@code .csv} extension.
 * 
 * @author Sean Harger
 * 
 */
public abstract class FileImport extends JDialog {
	private static final long serialVersionUID = 8827699500005191847L;

	private File importFile;
	private MainFrame mainFrame;
	private ImportWorker importWorker;
	private JProgressBar progressBar;
	private String[] requiredFileHeaders;

	private SystemObjects objects;

	/**
	 * Constructs a {@code FileImport} object.
	 * 
	 * @param imptFl
	 *            the {@code File} to import the data from.
	 * @param objs
	 *            the {@code SystemObjects} database to add the data to.r
	 * @param reqFlHdrs
	 *            array of {@code String}s specifying the required column
	 *            headers expected in the file.
	 * @param mnFrm
	 *            reference to the {@code MainFrame} from which the
	 *            {@code FileExport} frame was invoked.
	 */
	public FileImport(File imptFl, SystemObjects objs, String[] reqFlHdrs,
			MainFrame mnFrm) {
		importFile = imptFl;
		objects = objs;
		requiredFileHeaders = reqFlHdrs;
		mainFrame = mnFrm;

		setUpGui();
		setLocationRelativeTo(mainFrame);
		setAlwaysOnTop(true);
		setVisible(true);

		importWorker = new ImportWorker();
		importWorker.addPropertyChangeListener(new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent evt) {
				if ("progress".equals(evt.getPropertyName())) {
					progressBar.setValue((Integer) evt.getNewValue());
				}
			}
		});
	}

	/**
	 * Returns the {@code SystemObjects} database reference.
	 * 
	 * @return the {@code SystemOjbects} database reference.
	 */
	protected SystemObjects getSystemObjects() {
		return objects;
	}

	/**
	 * Beins execution of the {@code ImportWorker} task.
	 */
	public void go() {
		importWorker.execute();
	}

	/**
	 * Counts the number of lines in the specified import file.
	 * 
	 * @param fl
	 *            {@code File} to count lines from.
	 * @return the number of lines within the specified file.
	 * @throws FileNotFoundException
	 */
	private int countLines(File fl) throws FileNotFoundException {
		Scanner lineCounter = new Scanner(fl);
		int numLines = 0;
		while (lineCounter.hasNextLine()) {
			lineCounter.nextLine();
			numLines++;
		}

		if (numLines > 0) {
			numLines--; // done to prevent counting of header line
		}

		lineCounter.close();

		return numLines;
	}

	/**
	 * Reads the file headers from the imported file. If the headers do not
	 * match the expected file headers given in the constructor, it displays an
	 * error message.
	 * 
	 * @param firstLn
	 */
	private void processHeaders(String firstLn) {
		Scanner firstLineScanner = new Scanner(firstLn);
		firstLineScanner.useDelimiter(",");

		ArrayList<String> scannedHeaders = new ArrayList<String>();
		while (firstLineScanner.hasNext()) {
			scannedHeaders.add(firstLineScanner.next());
		}

		for (int index = 0; index < requiredFileHeaders.length; index++) {
			System.out.print(scannedHeaders.get(index) + "\n"
					+ requiredFileHeaders[index]);
			System.out.println(scannedHeaders.get(index).equals(
					requiredFileHeaders[index]));

			if (!scannedHeaders.get(index).equals(requiredFileHeaders[index])) {
				JOptionPane.showMessageDialog(this,
						"Files are not formatted correctly.",
						"File Format Error", JOptionPane.ERROR_MESSAGE);
			}
		}

		firstLineScanner.close();
	}

	/**
	 * Method intended to be overridden which is run after reading of the entire
	 * file. If there are any last operations that must be performed before
	 * control is returned to the {@code MainFrame}, they should be specified
	 * from within this method.
	 */
	protected void processDataSets() {
		// intended to be overridden
	}

	/**
	 * Reads an individual line from the import file.
	 * 
	 * @param ln
	 *            one line from the import file.
	 * @return array of {@code String} objects for each column value.
	 */
	private String[] readLine(String ln) {
		String[] lineTokens = new String[requiredFileHeaders.length];
		Scanner lineScanner = new Scanner(ln);
		lineScanner.useDelimiter(",");

		for (int index = 0; index < requiredFileHeaders.length; index++) {
			lineTokens[index] = new String(lineScanner.next());
		}
		return lineTokens;
	}

	/**
	 * Interprets the data specified from the array of {@code String} objects
	 * read by the {@code readLine()} method and converts it to data objects to
	 * be stored in the {@code SystemObjects} database.
	 * 
	 * @param lineTokens
	 *            array of {@code String} objects for each data value.
	 */
	protected abstract void processLineData(String[] lineTokens);

	/**
	 * Initializes the GUI for this {@code FileImport}.
	 */
	private void setUpGui() {
		setTitle("Import");
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				FileImport.class.getResource("/res/import128.png")));
		setBounds(100, 100, 437, 90);
		getContentPane().setLayout(new BorderLayout());

		JPanel progressPanel = new JPanel();
		FlowLayout flowLayout = (FlowLayout) progressPanel.getLayout();
		flowLayout.setVgap(10);
		getContentPane().add(progressPanel, BorderLayout.CENTER);

		progressBar = new JProgressBar();
		progressBar.setMaximum(100);
		progressBar.setPreferredSize(new Dimension(400, 30));
		progressBar.setMinimumSize(new Dimension(10, 30));
		progressBar.setMaximumSize(new Dimension(32767, 30));
		progressPanel.add(progressBar);
	}

	/**
	 * Updates the viewable routes in the MapViewer by adding all data from the
	 * {@code SystemObjects} database. Run after the completion of an import
	 * sequence.
	 */
	private void updateViewableData() {
		for (MapFrame mpFrm : mainFrame.getMapFrames()) {
			if (mpFrm != null) {
				mpFrm.getMap().setViewableRoutes(objects.getAllRoutes());
			}
		}
	}

	/**
	 * Background task that manages the reading of all data from the imported
	 * file.
	 * 
	 * @author Sean Harger
	 * 
	 */
	class ImportWorker extends SwingWorker<Integer, Object> {
		/**
		 * The background loop that continually reads data from the import file
		 * and writes it to the {@code SystemObjects} database. Calculates and
		 * sets progress according to the current line and the estimated number
		 * of total lines calculated by the {@code countLines()} method. Sleeps
		 * the thread for 1 millisecond so that the progress bar has time to
		 * update.
		 */
		protected Integer doInBackground() throws Exception {
			mainFrame.setEnabled(false);

			int numLines = countLines(importFile);
			Scanner mainScanner = new Scanner(importFile);
			System.out.println("processHeaders");
			processHeaders(mainScanner.nextLine());

			for (int lineCtr = 0; mainScanner.hasNextLine(); lineCtr++) {
				processLineData(readLine(mainScanner.nextLine()));
				double progress = (double) (lineCtr + 1) / numLines * 100;
				setProgress((int) progress);
				Thread.sleep(1);
			}

			mainScanner.close();

			return 0;
		}

		/**
		 * Finishes the import task by processing all data sets, setting the
		 * progress to complete, refreshing the viewable items from the
		 * {@code SystemObjects} database, and re-enabling control of the
		 * {@code MainFrame};
		 */
		protected void done() {
			processDataSets();
			mainFrame.setEnabled(true);
			setProgress(100);
			updateViewableData();
			dispose();
		}
	}
}
