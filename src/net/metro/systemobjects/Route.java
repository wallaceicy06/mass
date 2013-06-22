/*******************************************************************************
 * MASS (Metro Analytics Software System) by Sean Harger
 * is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0
 * United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/.
 ******************************************************************************/
package net.metro.systemobjects;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Object that defines a {@code Route} and its paths and waypoints, as well as
 * services and data.
 * 
 * @author Sean Harger
 * 
 */
public class Route implements Comparable<Route> {
	private int routeId;
	private String name;
	private ArrayList<RoutePath> routePaths;

	/**
	 * Constructs a {@code Route}.
	 * 
	 * @param rteNum
	 *            route id
	 * @param rteName
	 *            route name
	 * @param rtePths
	 *            {@code ArrayList} of {@code RoutePaths} that belong to this
	 *            {@code Route}
	 */
	public Route(int rteNum, String rteName, ArrayList<RoutePath> rtePths) {
		routeId = rteNum;
		name = new String(rteName);
		routePaths = rtePths;
	}

	/**
	 * Constructs a {@code Route}
	 * 
	 * @param rteNum
	 *            route id
	 * @param rteName
	 *            route name
	 */
	public Route(int rteNum, String rteName) {
		this(rteNum, rteName, new ArrayList<RoutePath>());
	}

	/**
	 * Returns the route id associated with this {@code Route}.
	 * 
	 * @return the route id associated with this {@code Route}.
	 */
	public int getRouteId() {
		return routeId;
	}

	/**
	 * Returns the name associated with this {@code Route}.
	 * 
	 * @return the name associated with this {@code Route}.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the RoutePath with the specified route path id.
	 * 
	 * @param pathId
	 *            the path id to search for a match
	 * @return route path corresponding to the specified path id or {@code null}
	 *         if none found
	 */
	public RoutePath getRoutePath(int pathId) {
		for (RoutePath pth : routePaths) {
			if (pth.getPathId() == pathId) {
				return pth;
			}
		}
		return null;
	}

	/**
	 * Returns all route paths associated with this {@code Route}.
	 * 
	 * @return all route paths associated with this {@code Route}.
	 */
	public ArrayList<RoutePath> getPaths() {
		return routePaths;
	}

	/**
	 * Sets the number of this route to the specified id.
	 * 
	 * @param rteNum
	 *            the id desired to be set for this {@code Route}
	 */
	public void setNumber(int rteNum) {
		routeId = rteNum;
	}

	/**
	 * Sets the name of this route according to the specified {@code String}.
	 * 
	 * @param nm
	 *            the name desired to be set for this {@code Route}
	 */
	public void setName(String nm) {
		name = nm;
	}

	/**
	 * Adds the specified {@code RoutePath} to this {@code Route}.
	 * 
	 * @param pth
	 *            route path to add to this route.
	 */
	public void addPath(RoutePath pth) {
		routePaths.add(pth);
		Collections.sort(routePaths);
	}

	/**
	 * Removes the specified {@code RoutePath} from this {@code Route}.
	 * 
	 * @param pth
	 *            route path to remove from this route.
	 */
	public void removePath(RoutePath pth) {
		routePaths.remove(pth);
	}

	/**
	 * Returns {@code String} representation of this {@code Route} in the form
	 * of its route id.
	 * 
	 * @return {@code String} representation of this {@code Route} in the form
	 *         of its route id.
	 */
	public String toString() {
		return new String(Integer.toString(routeId));
	}

	/**
	 * Compares two routes according to their route ids.
	 * 
	 * @return negative integer if this route is numerically before the
	 *         specified {@code Route} <br>
	 *         {@code 0} if this route is numerically equal to the specified
	 *         {@code Route} <br>
	 *         positive integer if this route is numerically after the specified
	 *         {@code Route}
	 */
	public int compareTo(Route rte) {
		return Integer.compare(this.routeId, rte.getRouteId());
	}

}
