/*******************************************************************************
 * MASS (Metro Analytics Software System) by Sean Harger
 * is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0
 * United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/.
 ******************************************************************************/
package net.metro.analyze;

import java.awt.Point;

import net.metro.systemobjects.WayPoint;

/**
 * Interface designed to inform other GUI elements of actions taken place within
 * a {@code SystemMap}. Every active listener can use this information to
 * determine when a map marker is being hovered over, selected, moving, or not
 * selected at all.
 * 
 * @author Sean Harger
 * 
 */
public interface MarkerListener {
	/**
	 * Called when the user's mouse is hovering over a map marker.
	 * 
	 * @param e
	 *            {@code MarkerEvent} object indicating the hovering marker.
	 */
	public abstract void mouseHoveringOverMarker(MarkerEvent e);

	/**
	 * Called when the user's mouse is not hovering over a map marker.
	 */
	public abstract void mouseHoveringOverNothing();

	/**
	 * Called when the user selects a map marker by clicking it on the map.
	 * 
	 * @param e
	 *            {@code MarkerEvent} object indicating the selected marker.
	 */
	public abstract void markerSelected(MarkerEvent e);

	/**
	 * Called when the user moves a map marker from one location to another.
	 * 
	 * @param e
	 *            {@code MarkerEvent} object indicating the marker moved and the
	 *            location moved to.
	 */
	public abstract void markerMoved(MarkerEvent e);

	/**
	 * Called when the user is actively moving a map marker.
	 * 
	 * @param e
	 *            {@code MarkerEvent} object indicating the marker moving and
	 *            the current position of the user's mouse.
	 */
	public abstract void markerMoving(MarkerEvent e);

	/**
	 * Called when the user selects a space on the map not occupied by a map
	 * marker.
	 */
	public abstract void markerNotSelected();

	/**
	 * Event object containing information regarding changes to map
	 * {@code WayPoint}s and {@code Stop}s.
	 * 
	 * @author Sean Harger
	 * 
	 */
	public class MarkerEvent {
		private WayPoint marker;
		private Point pointMovedTo;

		/**
		 * Constructs a {@code MarkerEvent}.
		 * 
		 * @param mkr
		 *            {@code WayPoint} involved in this event.
		 */
		public MarkerEvent(WayPoint mkr) {
			marker = mkr;
			pointMovedTo = null;
		}

		/**
		 * Constructs a {@code MarkerEvent}.
		 * 
		 * @param mkr
		 *            {@code WayPoint} involved in this event.
		 * @param ptMv
		 *            {@code Point} that this marker was moved to or is
		 *            currently at
		 */
		public MarkerEvent(WayPoint mkr, Point ptMv) {
			marker = mkr;
			pointMovedTo = ptMv;
		}

		/**
		 * Returns the {@code MapMarker} involved with this event.
		 * 
		 * @return the {@code MapMarker} involved with this event.
		 */
		public WayPoint getMapMarker() {
			return marker;
		}

		/**
		 * Returns the {@code Point} that the involved {@code MapMarker} was
		 * moved to.
		 * 
		 * @return the {@code Point} that the involved {@code MapMarker} was
		 *         moved to.
		 */
		public Point getPointMovedTo() {
			return pointMovedTo;
		}

		/**
		 * Returns whether the marker was moved or not.
		 * 
		 * @return {@code true} if the marker was moved
		 */
		public boolean wasMarkerMoved() {
			if (pointMovedTo == null) {
				return false;
			} else {
				return true;
			}
		}
	}
}
