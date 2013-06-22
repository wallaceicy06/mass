/*******************************************************************************
 * MASS (Metro Analytics Software System) by Sean Harger
 * is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0
 * United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/.
 ******************************************************************************/
package net.metro.systemobjects;

import org.openstreetmap.gui.jmapviewer.Coordinate;

/**
 * Object that defines a stop of a route on a particular route path. It extends
 * the general {@code WayPoint} class and adds the functionality of bearing a
 * stop id and a stop name. {@code Stop} objects should not be confused with
 * {@code ServiceObjects} since a {@code Stop} is simply a designation of a
 * place where a bus on a particular route path could make a stop, while a
 * {@code ServiceStop} is a data wrapper which identifies the numerical values
 * relating to the stopping of a bus at a particular stop during a particular
 * time.
 * 
 * @author Sean Harger
 * 
 */
public class Stop extends WayPoint {
	private String name;
	private int stationId;

	/**
	 * Constructs a {@code Stop}.
	 * 
	 * @param rtePth
	 *            {@code RoutePath} associated with the {@code Stop}
	 * @param loc
	 *            location of the {@code Stop}
	 * @param nm
	 *            name of the {@code Stop}
	 * @param stnId
	 *            unique id for the {@code Stop}
	 */
	public Stop(RoutePath rtePth, Coordinate loc, String nm, int stnId) {
		super(rtePth, loc);
		name = new String(nm);
		stationId = stnId;
	}

	/**
	 * Constructs a {@code Stop}.
	 * 
	 * @param rtePth
	 *            {@code RoutePath} associated with the {@code Stop}
	 * @param lat
	 *            decimal latitude location of the {@code Stop}
	 * @param lon
	 *            decimal longitude location of the {@code Stop}
	 * @param nm
	 *            name of the {@code Stop}
	 * @param stnId
	 *            unique id for the {@code Stop}
	 */
	public Stop(RoutePath rtePth, double lat, double lon, String nm, int stId) {
		this(rtePth, new Coordinate(lat, lon), nm, stId);
	}

	/**
	 * Returns the name of this {@code Stop}.
	 * 
	 * @return the name of this {@code Stop}.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the unique stop id for this {@code Stop}.
	 * 
	 * @return the unique stop id for this {@code Stop}.
	 */
	public int getStationId() {
		return stationId;
	}

	/**
	 * Returns a {@code String} representation of this {@code Stop} in the form
	 * of its name.
	 * 
	 * @return a {@code String} representation of this {@code Stop}.
	 */
	public String toString() {
		return new String("Stop          " + name);
	}

	/**
	 * Returns a {@code String} status message to be placed in the status bar of
	 * the {@code MapFrame}.
	 * 
	 * @return a status bar message representing this {@code Stop}
	 */
	public String getStatusMessage() {
		return new String(getStationId() + "   " + getName() + "   "
				+ getRoutePath().getRoute() + "-"
				+ getRoutePath().toString().substring(0, 1));
	}
}
