/*******************************************************************************
 * MASS (Metro Analytics Software System) by Sean Harger
 * is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0
 * United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/.
 ******************************************************************************/
package net.metro.analyze;

import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import org.openstreetmap.gui.jmapviewer.DefaultMapController;

import net.metro.analyze.MarkerListener.MarkerEvent;
import net.metro.analyze.PointListener.PointEvent;
import net.metro.systemobjects.Stop;
import net.metro.systemobjects.WayPoint;

/**
 * Extends the functionality of the {@code DefaultMapController} class by adding
 * functionality specific to MASS. This includes the ability to select and move
 * {@code WayPoint}s and change the cursor icon according the mouse's location.
 * 
 * @author Sean Harger
 * 
 */
public class MetroMapController extends DefaultMapController {
	private SystemMap map;

	private boolean isMouseOverMarker;
	private boolean isMarkerMoving;
	private boolean markerMovingEnabled;
	private boolean markerSelectionEnabled;
	private WayPoint hoveringMarker;
	private ArrayList<PointListener> pointListeners;
	private ArrayList<MarkerListener> markerListeners;

	/**
	 * Constructs a {@code MetroMapController}.
	 * 
	 * @param sysMp
	 *            {@code SystemMap} that this controller is assigned to control.
	 */
	public MetroMapController(SystemMap sysMp) {
		super(sysMp);
		map = sysMp;
		isMouseOverMarker = false;
		isMarkerMoving = false;
		markerMovingEnabled = false;
		markerSelectionEnabled = true;
		pointListeners = new ArrayList<PointListener>();
		markerListeners = new ArrayList<MarkerListener>();
	}

	/**
	 * Adds a {@code PointListener} to the list of point listeners.
	 * 
	 * @param listener
	 *            {@code PointListener} to add.
	 */
	protected void addPointListener(PointListener listener) {
		pointListeners.add(listener);
	}

	/**
	 * Adds a {@code MarkerListener} to the list of marker listeners.
	 * 
	 * @param listener
	 *            {@code MarkerListener} to add.
	 */
	protected void addMarkerListener(MarkerListener listener) {
		markerListeners.add(listener);
	}

	/**
	 * Removes a {@code PointListener} from the list of point listeners.
	 * 
	 * @param listener
	 *            {@code PointListener} to remove.
	 */
	protected void removePointListener(PointListener listener) {
		pointListeners.remove(listener);
	}

	/**
	 * Removes a {@code MarkerListener} from the list of marker listeners.
	 * 
	 * @param listener
	 *            {@code MarkerListener} to remove.
	 */
	protected void removeMarkerListener(MarkerListener listener) {
		markerListeners.remove(listener);
	}

	/**
	 * Sends a {@code MarkerEvent} to each marker listener indicating that a
	 * {@code WayPoint} has been selected and the information regarding the
	 * selected marker.
	 * 
	 * @param wpt
	 */
	protected void selectWayPoint(WayPoint wpt) {
		map.setSelectedMapMarker(wpt);
		for (MarkerListener listener : markerListeners) {
			listener.markerSelected(new MarkerEvent(wpt));
		}
	}

	/**
	 * Sets the ability to move markers on the map.
	 * 
	 * @param enbld
	 *            {@code boolean} value indicating whether marker movement
	 *            should be enabled. or disabled.
	 */
	protected void setMarkerMovingEnabled(boolean enbld) {
		markerMovingEnabled = enbld;
	}

	/**
	 * Sets the ability to select markers on the map.
	 * 
	 * @param enbld
	 *            {@code boolean} value indicating whether marker selection
	 *            should be enabled.
	 */
	protected void setMarkerSelectionEnabled(boolean enbld) {
		markerSelectionEnabled = enbld;
		System.out.println("Just set marker selection to "
				+ markerSelectionEnabled);
	}

	/**
	 * Called if the user clicks their mouse. If the user clicks once, the
	 * controller tries to select a nearby map marker if one exists, and then
	 * sends a {@code MarkerEvent} to all marker listeners indicating that a
	 * marker was selected. Otherwise, this method proceeds according to the
	 * implementation within the {@code DefaultMapController} class.
	 */
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 1) {
			if (markerSelectionEnabled) {
				if (isMouseOverMarker) {
					ArrayList<Stop> closeStops = map
							.getCloseStops(e.getPoint());
					Stop selectedStop;
					if (closeStops.size() > 1) {
						SelectStop stopSelector = new SelectStop(
								map.getMapFrame(), closeStops);
						selectedStop = stopSelector.showDialog();
					} else {
						selectedStop = closeStops.get(0);
					}

					if (selectedStop != null) {
						map.setSelectedMapMarker(selectedStop);
						map.setSelectedRoutePath(selectedStop.getRoutePath());
						for (MarkerListener listener : markerListeners) {
							listener.markerSelected(new MarkerEvent(map
									.getSelectedMarker()));
						}
					} else {
						map.clearSelectedRoutePaths();
					}
				} else {
					map.setSelectedMapMarker(null);
					for (MarkerListener listener : markerListeners) {
						listener.markerNotSelected();
					}
				}
			}

			for (PointListener listener : pointListeners) {
				listener.pointSelected(new PointEvent(e.getPoint()));
			}
		}
		super.mouseClicked(e);
	}

	/**
	 * Called when a user presses their mouse button. If a user selects their
	 * primary mouse button while over a map marker and marker movement is
	 * enabled, then the marker is set to moving and the controller sends
	 * appropriate {@code MarkerEvent}s according to its movement. Otherwise,
	 * this method proceeds according to the implementation within the
	 * {@code DefaultMapController} class.
	 */
	public void mousePressed(MouseEvent e) {
		if (markerMovingEnabled && e.getButton() == MouseEvent.BUTTON1 /*
																		 * &&
																		 * hoveringMarker
																		 * ==
																		 * map.
																		 * getSelectedMarker
																		 * ()
																		 */) {
			System.out.println("marker moving");
			isMarkerMoving = true;
		}
		super.mousePressed(e);
	}

	/**
	 * Called when a user releases their mouse button. If marker movement is
	 * enabled and a marker was moving, then movement is disabled and the
	 * marker's final position is recorded and sent as a {@code MarkerEvent} to
	 * all marker listeners. Otherwise, this method proceeds according to the
	 * implementation within the {@code DefaultMapController class.
	 */
	public void mouseReleased(MouseEvent e) {
		if (markerMovingEnabled && e.getButton() == MouseEvent.BUTTON1 /*
																		 * &&
																		 * hoveringMarker
																		 * ==
																		 * map.
																		 * getSelectedMarker
																		 * ()
																		 */) {
			System.out.println("marker not moving");
			isMarkerMoving = false;
			for (MarkerListener listener : markerListeners) {
				listener.markerMoved(new MarkerEvent(map.getSelectedMarker(), e
						.getPoint()));
			}
		}
		super.mousePressed(e);

	}

	/**
	 * Called when a user moves their mouse from one location to another. If a
	 * user hovers their mouse over a Stop marker, then the controller changes
	 * the mouse icon to a hand indicating that they are hovering over a marker.
	 * If not, it sets the icon to the default pointer. Otherwise, this method
	 * proceeds to the implementation within the {@code DefaultMapController}
	 * class.
	 */
	public void mouseMoved(MouseEvent e) {
		if (map.isStop(e.getPoint())) {
			hoveringMarker = map.getClosestStop(e.getPoint());
			map.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			for (MarkerListener listener : markerListeners) {
				listener.mouseHoveringOverMarker(new MarkerEvent(hoveringMarker));
			}
			isMouseOverMarker = true;
		} else {
			hoveringMarker = null;
			map.setCursor(Cursor.getDefaultCursor());
			for (MarkerListener listener : markerListeners) {
				listener.mouseHoveringOverNothing();
			}
			isMouseOverMarker = false;
		}
		super.mouseMoved(e);
	}

	/**
	 * Called when a user drags their mouse from one point to another. If this
	 * occurs, the controller sends a {@code MarkerEvent} indicating the current
	 * position of the marker. Otherwise, this method proceeds according to the
	 * implementation within the {@code DefaultMapController} class.
	 */
	public void mouseDragged(MouseEvent e) {
		if (isMarkerMoving) {
			for (MarkerListener listener : markerListeners) {
				listener.markerMoving(new MarkerEvent(map.getSelectedMarker(),
						e.getPoint()));
			}
		}
		super.mouseDragged(e);
	}
}
