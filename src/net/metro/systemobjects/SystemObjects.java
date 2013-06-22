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
 * This is a container database of all objects relating to routes, route paths,
 * stops, waypoints, services, and data. Each encapsulated object is a container
 * for a lower level system object until it reaches the lowest levels (
 * {@code WayPoint}s, {@code Stop}s, and {@code Data} objects.
 * 
 * @author Sean Harger
 * 
 */
public class SystemObjects {
	private ArrayList<Route> routes;

	/**
	 * Constructs an empty {@code SystemObjects} database.
	 */
	public SystemObjects() {
		routes = new ArrayList<Route>();
	}

	/**
	 * Determines whether a route exists in the database with the specified
	 * route id.
	 * 
	 * @param routeNum
	 *            route id to search for
	 * @return {@code true} if a route with the specified route id exists in the
	 *         database
	 */
	public boolean routeExists(int routeNum) {
		if (getRoute(routeNum) != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Determines whether the specified {@code Route} is duplicated in the
	 * database. This is especially helpful when the user is attempting to add a
	 * duplicate {@code Route} using the {@code LineEditor}.
	 * 
	 * @param rteToExclude
	 *            {@code Route} to check for duplication
	 * @return {@code true} if the database contains a duplicate.
	 */
	public boolean checkOtherRoutesForId(Route rteToExclude) {
		for (Route rte : routes) {
			if (rte != rteToExclude
					&& rte.getRouteId() == rteToExclude.getRouteId()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Finds the {@code Route} with the specified route id and returns it.
	 * 
	 * @param routeNum
	 *            route id of the {@code Route} desired
	 * @return the matching {@code Route} or {@code null} if none is found.
	 */
	public Route getRoute(int routeNum) {
		for (Route r : routes) {
			if (r.getRouteId() == routeNum) {
				return r;
			}
		}
		return null;
	}

	/**
	 * Returns a list of all the {@code Route}s in the database.
	 * 
	 * @return a list of all the {@code Route}s in the database.
	 */
	public ArrayList<Route> getAllRoutes() {
		return routes;
	}

	/**
	 * Returns a list of all the {@code Service}s in the database. Returns a
	 * list of all the {@code Service}s in the database.
	 * 
	 * @return
	 */
	public ArrayList<Service> getAllServices() {
		ArrayList<Service> svcs = new ArrayList<Service>();
		for (Route rte : routes) {
			for (RoutePath rtePth : rte.getPaths()) {
				svcs.addAll(rtePth.getServices());
			}
		}
		return svcs;

	}

	/**
	 * Finds all {@code Service}s in the database matching the specified
	 * {@code RoutePath} and {@code Period} constraints.
	 * 
	 * @param rtePthConstraint
	 *            desired {@code RoutePath} for the returned services to be a
	 *            part of
	 * @param pdConstraint
	 *            desired {@code Period} for the returned services to operate
	 *            during
	 * @return list of {@code Service} objects matching the {@code RoutePath}
	 *         and {@code Period} constraints.
	 */
	public ArrayList<Service> getServicesWithConstraint(
			ArrayList<RoutePath> rtePthConstraint, Period pdConstraint) {
		ArrayList<Service> matchingServices = new ArrayList<Service>();

		for (Route rte : routes) {
			for (RoutePath rtePth : rte.getPaths()) {
				if (rtePthConstraint != null
						&& rtePthConstraint.contains(rtePth)) {
					for (Service svc : rtePth.getServices()) {
						if (pdConstraint != null
								&& svc.getPeriod().equals(pdConstraint)) {
							matchingServices.add(svc);
						}
					}
				}
			}
		}

		return matchingServices;
	}

	/**
	 * Adds the specified {@code Route} to the database and sorts the routes by
	 * their route id.
	 * 
	 * @param rte
	 *            {@code Route} to add
	 */
	public void addRoute(Route rte) {
		routes.add(rte);
		Collections.sort(routes);
	}

	/**
	 * Removes the specified {@code Route} from the database.
	 * 
	 * @param rte
	 *            {@code Route} to remove
	 */
	public void removeRoute(Route rte) {
		routes.remove(rte);
	}
}
