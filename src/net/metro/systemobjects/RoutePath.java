/*******************************************************************************
 * MASS (Metro Analytics Software System) by Sean Harger
 * is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0
 * United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/.
 ******************************************************************************/
package net.metro.systemobjects;

import java.util.ArrayList;
import java.util.List;

import net.metro.systemobjects.dataobjects.DataType;

/**
 * Object that defines one path of a particular route using a series of
 * {@code WayPoint}s and {@code Stop}s.
 * 
 * @author Sean Harger
 * 
 */
public class RoutePath implements Comparable<RoutePath> {
	// private fields
	private Route myRoute;
	private String name;
	private int pathId;
	private ArrayList<Service> services;
	private ArrayList<WayPoint> wayPoints;

	/**
	 * Constructs a {@code RoutePath}.
	 * 
	 * @param rte
	 *            {@code Route} associated with this {@code RoutePath}
	 * @param nm
	 *            name associated with this {@code RoutePath}
	 * @param pthId
	 *            path id associated with this {@code RoutePath}
	 */
	public RoutePath(Route rte, String nm, int pthId) {
		myRoute = rte;
		name = new String(nm);
		pathId = pthId;
		services = new ArrayList<Service>();
		wayPoints = new ArrayList<WayPoint>();
	}

	/**
	 * Returns the {@code Route} associated with this {@code RoutePath}.
	 * 
	 * @return the {@code Route} associated with this {@code RoutePath}.
	 */
	public Route getRoute() {
		return myRoute;
	}

	/**
	 * Returns the name associated with this {@code RoutePath}.
	 * 
	 * @return the name associated with this {@code RoutePath}.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the route path id associated with this {@code RoutePath}.
	 * 
	 * @return the route path id associated with this {@code RoutePath}.
	 */
	public int getPathId() {
		return pathId;
	}

	/**
	 * Finds the {@code Service} associated with this {@code RoutePath} that
	 * matches the specified service id.
	 * 
	 * @param svcId
	 *            id of the service being looked for
	 * @return the matching service or {@code null} if none is found.
	 */
	public Service getService(int svcId) {
		for (Service svc : services) {
			if (svc.getServiceId() == svcId) {
				return svc;
			}
		}
		return null;
	}

	/**
	 * Finds the services associated with this {@code RoutePath} matching the
	 * specified {@code Period}.
	 * 
	 * @param pd
	 *            period constraint of the sought {@code Service}
	 * @return the matching service or {@code null} if none is found.
	 */
	public Service getServicesWithPeriod(Period pd) {
		for (Service svc : services) {
			if (svc.getPeriod().equals(pd)) {
				return svc;
			}
		}
		return null;
	}

	/**
	 * Finds the minimum data value across all services associated with this
	 * {@code RoutePath}.
	 * 
	 * @param dType
	 *            the {@code DataType} to compare
	 * @return the minimum data value or {@code Integer.MAX_VALUE} if none is
	 *         found
	 */
	public int getMinData(DataType dType) {
		int minValue = Integer.MAX_VALUE;
		for (Service svc : services) {
			int dtVal = svc.getMinData(dType);
			if (dtVal != -1) // constant value -1 indicates no data present
			{
				if (dtVal < minValue) {
					minValue = dtVal;
				}
			}
		}
		return minValue;
	}

	/**
	 * Finds the maximum data value across all services associated with this
	 * {@code RoutePath}.
	 * 
	 * @param dType
	 *            the {@code DataType} to compare
	 * @return the maximum data value or {@code -1} if none is found
	 */
	public int getMaxData(DataType dType) {
		int maxValue = -1;
		for (Service svc : services) {
			int dtVal = svc.getMaxData(dType);
			if (dtVal != -1) // constant value -1 indicates no data present
			{
				if (dtVal > maxValue) {
					maxValue = dtVal;
				}
			}
		}
		return maxValue;
	}

	/**
	 * Returns all {@code Service}s associated with this {@code RoutePath}.
	 * 
	 * @return all {@code Service}s associated with this {@code RoutePath}.
	 */
	public ArrayList<Service> getServices() {
		return services;
	}

	/**
	 * Returns all {@code WayPoints} in this {@code RoutePath}.
	 * 
	 * @return all {@code WayPoints} in this {@code RoutePath}.
	 */
	public ArrayList<WayPoint> getWayPoints() {
		return wayPoints;
	}

	/**
	 * Finds the stop contained in the waypoint list matching the specified stop
	 * id.
	 * 
	 * @param stopId
	 *            stop id to match Stops according to
	 * @return the matching stop or {@code null} if none is found.
	 */
	public Stop getStop(int stopId) {
		for (WayPoint wp : wayPoints) {
			if (wp instanceof Stop) {
				if (((Stop) wp).getStationId() == stopId) {
					return (Stop) wp;
				}
			}
		}
		return null;
	}

	/**
	 * Returns all {@code Stop}s contained within this {@code RoutePath}'s
	 * waypoint list.
	 * 
	 * @return all {@code Stop}s in this {@code RoutePath}.
	 */
	public ArrayList<Stop> getStops() {
		ArrayList<Stop> allStops = new ArrayList<Stop>();

		for (WayPoint wp : wayPoints) {
			if (wp instanceof Stop) {
				allStops.add((Stop) wp);
			}
		}

		return allStops;
	}

	/**
	 * Finds the waypoints in between the specified start and end points.
	 * 
	 * @param start
	 *            start {@code WayPoint}
	 * @param end
	 *            end {@code WayPoint}
	 * @return list of {@code WayPoint}s in between the start and end points.
	 */
	public ArrayList<WayPoint> getSubPath(WayPoint start, WayPoint end) {
		System.out.println("Getting Sub-Path: ");
		System.out.println("From wayPoint: " + start + " to " + end);
		int startIndex = wayPoints.indexOf(start);
		int endIndex = wayPoints.indexOf(end);
		System.out.println("Start index: " + startIndex + " End Index: "
				+ endIndex);

		return new ArrayList<WayPoint>(wayPoints.subList(startIndex,
				endIndex + 1));
	}

	/**
	 * Finds the stops in between the specified start and end stops.
	 * 
	 * @param start
	 *            start {@code Stop}
	 * @param end
	 *            end {@code Stop}
	 * @return list of {@code Stop}s in between the start and end stops.
	 */
	public List<Stop> getStopsInBetween(Stop start, Stop end) {
		List<WayPoint> wayPointsInBetween = getSubPath(start, end);
		ArrayList<Stop> stopsInBetween = new ArrayList<Stop>();
		for (WayPoint wp : wayPointsInBetween) {
			if (wp instanceof Stop) {
				stopsInBetween.add((Stop) wp);
			}
		}
		return stopsInBetween;
	}

	/**
	 * Sets the name of this {@code RoutePath} to the specified {@code String}.
	 * 
	 * @param nm
	 *            desired name for this {@code RoutePath}
	 */
	public void setName(String nm) {
		name = nm;
	}

	/**
	 * Adds the specified {@code Service} to this {@code RoutePath}.
	 * 
	 * @param svc
	 *            {@code Service} to add
	 */
	public void addService(Service svc) {
		services.add(svc);
	}

	/**
	 * Adds the specified {@code WayPoint} to the end of the list of waypoints.
	 * 
	 * @param wpt
	 *            {@code WayPoint} to add
	 */
	public void addWayPoint(WayPoint wpt) {
		wayPoints.add(wpt);
	}

	/**
	 * Inserts a new {@code WayPoint} after the specified point
	 * 
	 * @param wpt
	 *            {@code WayPoint} to add
	 * @param wptToInsertAfter
	 *            {@code WayPoint} to insert after
	 */
	public void insertNewWayPoint(WayPoint wpt, WayPoint wptToInsertAfter) {
		wayPoints.add(wayPoints.indexOf(wptToInsertAfter) + 1, wpt); // added +1
																		// to
																		// shift
																		// to
																		// right
																		// instead
																		// of
																		// left
	}

	/**
	 * Replaces the specified old {@code WayPoint} with the new {@code WayPoint}
	 * .
	 * 
	 * @param oldWpt
	 *            old {@code WayPoint}
	 * @param newWpt
	 *            new {@code WayPoint}
	 */
	public void replaceWayPoint(WayPoint oldWpt, WayPoint newWpt) {
		int index = wayPoints.indexOf(oldWpt);
		wayPoints.set(index, newWpt);
	}

	/**
	 * Deletes the specified {@code WayPoint} from the list of waypoints.
	 * 
	 * @param wpt
	 *            {@code WayPoint} to delete
	 */
	public void deleteWayPoint(WayPoint wpt) {
		wayPoints.remove(wpt);
	}

	/**
	 * Deltes all {@code WayPoints} from the list of waypoints.
	 */
	public void deleteAllWayPoints() {
		wayPoints.clear();
	}

	/**
	 * Returns a {@code String} representation for this {@code Routepath} in the
	 * form of its name
	 * 
	 * @return a {@code String} representation for this {@code Routepath} in the
	 *         form of its name
	 */
	public String toString() {
		return new String(name);
	}

	/**
	 * Compares two {@code RoutePath}s according to their names.
	 * 
	 * @return negative integer if this is alphabetically before the specified
	 *         {@code RoutePath} <br>
	 *         {@code 0} if this is alphabetically equal to the specified
	 *         {@code RoutePath} <br>
	 *         positive integer if this is alphabetically after the specified
	 *         {@code RoutePath}
	 */
	public int compareTo(RoutePath rtePth) {
		return this.name.compareTo(rtePth.getName());
	}

}
