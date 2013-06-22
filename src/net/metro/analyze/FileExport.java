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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import net.metro.systemobjects.SystemObjects;
import java.awt.Toolkit;

/**
 * Abstract class for exporting data edited from within MASS. Exported files are
 * tabular, comma delimited text files with the {@code .csv} extension.
 * 
 * @author Sean Harger
 * 
 */
public abstract class FileExport extends JDialog {
	private static final long serialVersionUID = 7185476550329303764L;

	private int numLines;

	private File exportFile;
	private MainFrame mainFrame;
	private ExportWorker exportWorker;
	private JProgressBar progressBar;
	private String[] fileHeaders;
	private BufferedWriter fileWriter;

	private SystemObjects objects;

	/**
	 * Constructs a {@code FileExport} object.
	 * 
	 * @param exptFile
	 *            the {@code File} to write the data to.
	 * @param objs
	 *            the {@code SystemObjects} database from which to aggregate
	 *            data.
	 * @param flHdrs
	 *            array of {@code String}s specifying the column headers of the
	 *            file.
	 * @param mnFrm
	 *            reference to the {@code MainFrame} from which the
	 *            {@code FileExport} frame was invoked.
	 */
	public FileExport(File exptFile, SystemObjects objs, String[] flHdrs,
			MainFrame mnFrm) {
		exportFile = exptFile;
		objects = objs;
		fileHeaders = flHdrs;
		mainFrame = mnFrm;

		try {
			fileWriter = new BufferedWriter(new FileWriter(exportFile));
		} catch (IOException e) {
			e.printStackTrace();
		}

		setUpGui();
		setLocationRelativeTo(mainFrame);
		setAlwaysOnTop(true);
		setVisible(true);

		exportWorker = new ExportWorker();
		exportWorker.addPropertyChangeListener(new PropertyChangeListener() {
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
	 * Calculates the number of lines in the file to estimate the current
	 * progress and then begins execution of the {@code ExportWorker} task.
	 */
	protected void go() {
		numLines = calculateLines();
		exportWorker.execute();
	}

	/**
	 * Calculates the number of lines to be written in the file. This is used to
	 * determine the value of the progress bar.
	 * 
	 * @return the number of lines to be written in the file.
	 */
	protected abstract int calculateLines();

	/**
	 * Writes the headers specified by the {@code String} array in teh
	 * constructor to the first line in the exported file.
	 * 
	 * @throws IOException
	 */
	private void writeHeaders() throws IOException {
		for (String hdr : fileHeaders) {
			fileWriter.write(hdr);
			fileWriter.append(",");
		}
		fileWriter.newLine();
	}

	/**
	 * Writes the specified line values to the next line in the file. It
	 * separtes each value with a comma.
	 * 
	 * @param lineTokens
	 *            array of {@code String} values to write to the line.
	 * @throws IOException
	 */
	private void writeLine(String[] lineTokens) throws IOException {
		for (String token : lineTokens) {
			fileWriter.write(token);
			fileWriter.append(",");
		}
		fileWriter.newLine();
	}

	/**
	 * Generates the {@code String} array specifying the values to be written
	 * for each line.
	 * 
	 * @param lnNum
	 *            the line number of the desired data.
	 * @return
	 */
	protected abstract String[] processLineData(int lnNum);

	/**
	 * Initializes the GUI for this {@code FileExport}.
	 */
	private void setUpGui() {
		setTitle("Export");
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(
				FileExport.class.getResource("/res/export128.png")));
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
	 * Background task that manages the writing of all data to the exported
	 * file.
	 * 
	 * @author Sean Harger
	 * 
	 */
	class ExportWorker extends SwingWorker<Integer, Object> {
		/**
		 * The background loop that continually writes data to the file until
		 * complete. Calculates and sets progress according to the current line
		 * and the estimated number of total lines calculated by the
		 * {@code calculateLines()} method. Sleeps the thread for 1 millisecond
		 * so that the progress bar has time to update.
		 */
		protected Integer doInBackground() throws IOException,
				InterruptedException {
			mainFrame.setEnabled(false);
			writeHeaders();

			for (int lineCtr = 0; lineCtr < numLines; lineCtr++) {
				writeLine(processLineData(lineCtr));
				double progress = (double) (lineCtr + 1) / numLines * 100;
				setProgress((int) progress);
				Thread.sleep(1);
			}

			return 0;
		}

		/**
		 * Finishes the export task by setting the progress bar to complete,
		 * closing the file, and re-enabling control of the {@code MainFrame}.
		 */
		protected void done() {
			setProgress(100);
			try {
				fileWriter.close();
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			mainFrame.setEnabled(true);
			dispose();
		}
	}

}
