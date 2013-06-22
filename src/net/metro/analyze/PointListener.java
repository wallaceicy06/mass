/*******************************************************************************
 * MASS (Metro Analytics Software System) by Sean Harger
 * is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0
 * United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/.
 ******************************************************************************/
package net.metro.analyze;

import java.awt.Point;
import java.util.EventListener;

/**
 * Interface designed to inform other GUI elements of actions taken place within
 * a {@code SystemMap}. Every active listener can use this information to
 * determine when a point on the map has been selected.
 * 
 * @author Sean Harger
 * 
 */
public interface PointListener extends EventListener {
	/**
	 * Called when a point is selected on the map.
	 * 
	 * @param p
	 *            selected {@code Point}
	 */
	public void pointSelected(PointEvent p);

	/**
	 * Event object containing information regarding selection of a point on a
	 * {@code SystemMap}.
	 * 
	 * @author Sean Harger
	 * 
	 */
	public class PointEvent {
		private Point point;

		/**
		 * Constructs a {@code PointEvent}.
		 * 
		 * @param p
		 */
		public PointEvent(Point p) {
			point = p;
		}

		/**
		 * Returns the selected {@code Point}.
		 * 
		 * @return the selected {@code Point}.
		 */
		public Point getPoint() {
			return point;
		}
	}
}
