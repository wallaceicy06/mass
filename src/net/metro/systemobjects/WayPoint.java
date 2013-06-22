/*******************************************************************************
 * MASS (Metro Analytics Software System) by Sean Harger
 * is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0
 * United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/.
 ******************************************************************************/
package net.metro.systemobjects;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.interfaces.MapMarker;

/**
 * Object describing a point visited by a route along its route path. A general
 * {@code WayPoint} object is usually a break in a straight line path along a
 * route, but is not considered to be a stop along that path. It is simply a way
 * of designating the path which a route takes, since it is not always
 * necessarily a straight-line path from stop to stop.
 * 
 * @author Sean Harger
 * 
 */
public class WayPoint implements MapMarker {
	private RoutePath myPath;
	private Coordinate location;

	/**
	 * Constructs a {@code WayPoint}.
	 * 
	 * @param rtePth
	 *            {@code RoutePath} that this {@code WayPoint} belongs to
	 * @param loc
	 *            location of the {@code WayPoint}
	 */
	public WayPoint(RoutePath rtePth, Coordinate loc) {
		myPath = rtePth;
		location = loc;
	}

	/**
	 * Constructs a {@code WayPoint}.
	 * 
	 * @param rtePth
	 *            {@code RoutePath} that this {@code WayPoint} belongs to
	 * @param lat
	 *            decimal latitude value for this {@code WayPoint}
	 * @param lon
	 *            decimal longitude value for this {@code WayPoint}
	 */
	public WayPoint(RoutePath rtePth, double lat, double lon) {
		this(rtePth, new Coordinate(lat, lon));
	}

	/**
	 * Returns the {@code RoutePath} associated with this {@code WayPoint}.
	 * 
	 * @return the {@code RoutePath} associated with this {@code WayPoint}.
	 */
	public RoutePath getRoutePath() {
		return myPath;
	}

	/**
	 * Returns the {@code Coordinate} location of this {@code WayPoint}.
	 * 
	 * @return the {@code Coordinate} location of this {@code WayPoint}.
	 */
	public Coordinate getLocation() {
		return location;
	}

	/**
	 * Returns the decimal latitude value for this {@code WayPoint}.
	 * 
	 * @return the decimal latitude value for this {@code WayPoint}
	 */
	public double getLat() {
		return location.getLat();
	}

	/**
	 * Returns the decimal longitude value for this {@code WayPoint}.
	 * 
	 * @return the decimal longitude value for this {@code WayPoint}
	 */
	public double getLon() {
		return location.getLon();
	}

	/**
	 * Overrides the paint method of the {@code MapMarker} interface. There is
	 * no implementation since {@code WayPoint}s are invisible by default.
	 */
	public void paint(Graphics g, Point position) {
	}

	/**
	 * Paints this {@code WayPoint} according to the specified position, size,
	 * and fill color.
	 * 
	 * @param g
	 *            {@code Graphics} variable
	 * @param position
	 *            {@code Point} in component to be painted
	 * @param sz
	 *            integer size of the marker
	 * @param fillCol
	 *            the fill color of the marker
	 */
	public void paint(Graphics g, Point position, int sz, Color fillCol) {
		paint(g, position, sz, fillCol, Color.BLACK);
	}

	/**
	 * Paints this {@code WayPoint} according to the specified position, size,
	 * fill color, and stroke color
	 * 
	 * @param g
	 *            ({@code Graphics} variable
	 * @param position
	 *            {@code Point} in component to be painted
	 * @param sz
	 *            integer size of the marker
	 * @param fillCol
	 *            the fill color of the marker
	 * @param strokeCol
	 *            the stroke color of the marker
	 */
	public void paint(Graphics g, Point position, int sz, Color fillCol,
			Color strokeCol) {
		int size_h = sz;
		int size = size_h * 2;
		g.setColor(fillCol);
		g.fillOval(position.x - size_h, position.y - size_h, size, size);
		g.setColor(strokeCol);
		g.drawOval(position.x - size_h, position.y - size_h, size, size);
	}

	public String toString() {
		return new String("WayPoint");
	}

	public String getStatusMessage() {
		return new String("WayPoint");
	}
}
