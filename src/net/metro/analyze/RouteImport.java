/*******************************************************************************
 * MASS (Metro Analytics Software System) by Sean Harger
 * is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0
 * United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/.
 ******************************************************************************/
package net.metro.analyze;

import java.io.File;
import java.util.ArrayList;

import net.metro.systemobjects.Route;
import net.metro.systemobjects.RoutePath;
import net.metro.systemobjects.Stop;
import net.metro.systemobjects.SystemObjects;
import net.metro.systemobjects.WayPoint;

/**
 * Extends the {@code FileImport} class designed to import Routes from an
 * external {@code .csv} file into MASS.
 * 
 * @author Sean Harger
 * 
 */
public class RouteImport extends FileImport {
	private static final long serialVersionUID = -36901587909585933L;

	protected static final String[] requiredFileHeaders = { "routeId",
			"routeName", "pathId", "pathSeq", "pathName", "wayPointType",
			"pointLat", "pointLon", "stopName", "stopId" };
	protected static final int WPTYPE_WAYPOINT = 0;
	protected static final int WPTYPE_STOP = 1;

	private ArrayList<WayPointSet> wayPointSets;
	private WayPointSet currentWayPointSet;

	/**
	 * Constructs a {@code RouteImport} object.
	 * 
	 * @param imptFl
	 *            {@code File} to import route data from
	 * @param objs
	 *            {@code SystemObjects} database to add route data to
	 * @param mnFrm
	 *            reference to {@code MainFrame} from which this
	 *            {@code RouteImport} frame was invoked
	 */
	public RouteImport(File imptFl, SystemObjects objs, MainFrame mnFrm) {
		super(imptFl, objs, requiredFileHeaders, mnFrm);
		setTitle("Route Import");
		currentWayPointSet = null;
		wayPointSets = new ArrayList<WayPointSet>();
	}

	/**
	 * Processes individual line data into the necessary formats to be stored in
	 * the {@code SystemObjects} database. Creates {@code WayPointSet}s for
	 * large amounts of waypoints belonging to a particular route path. These
	 * will be added later during the {@code processDataSets()} method.
	 */
	protected void processLineData(String[] lineTokens) {
		try {
			int routeId = Integer.parseInt(lineTokens[0]);
			String routeName = new String(lineTokens[1]);
			int pathId = Integer.parseInt(lineTokens[2]);
			/*
			 * int pathSeq = Integer.parseInt( lineTokens[ 3 ] ); (commented out
			 * due to no use of column at this time)
			 */
			String pathName = new String(lineTokens[4]);
			int wayPointType = Integer.parseInt(lineTokens[5]);
			double pointLat = Double.parseDouble(lineTokens[6]);
			double pointLon = Double.parseDouble(lineTokens[7]);

			if (currentWayPointSet == null
					|| currentWayPointSet.getRouteId() != routeId
					|| currentWayPointSet.getPathId() != pathId) {
				currentWayPointSet = new WayPointSet(routeId, routeName,
						pathId, pathName);
				wayPointSets.add(currentWayPointSet);
			}

			if (wayPointType == RouteImport.WPTYPE_WAYPOINT) {
				currentWayPointSet.addWayPoint(new ImportWayPoint(pointLat,
						pointLon));
			} else if (wayPointType == RouteImport.WPTYPE_STOP) {
				String stopName = new String(lineTokens[8]);
				int stopId = Integer.parseInt(lineTokens[9]);

				currentWayPointSet.addWayPoint(new ImportStop(pointLat,
						pointLon, stopName, stopId));
			} else {
				System.out
						.println("The specified WayPoint type is not valid. You specified \""
								+ wayPointType + "\".");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Traverses the {@code WayPointSet} objects and adds routes, route paths,
	 * stops, and waypoints to the {@code SystemObjects} database.
	 */
	protected void processDataSets() {
		for (WayPointSet set : wayPointSets) {
			Route rte = getSystemObjects().getRoute(set.getRouteId());
			if (rte == null) {
				rte = new Route(set.getRouteId(), set.getRouteName());
				getSystemObjects().addRoute(rte);
			}

			RoutePath rtePth = rte.getRoutePath(set.getPathId());
			if (rtePth == null) {
				rtePth = new RoutePath(rte, set.getPathName(), set.getPathId());
				rte.addPath(rtePth);
			}

			rtePth.deleteAllWayPoints();
			for (ImportWayPoint imptWp : set.getWayPoints()) {
				if (imptWp instanceof ImportStop) {
					ImportStop imptSt = (ImportStop) imptWp;
					rtePth.addWayPoint(new Stop(rtePth, imptSt.getLat(), imptSt
							.getLon(), imptSt.getName(), imptSt.getStopId()));
				} else {
					rtePth.addWayPoint(new WayPoint(rtePth, imptWp.getLat(),
							imptWp.getLon()));
				}
			}
		}
	}

	/**
	 * Wrapper class for individual waypoints that contain an {@code ArrayList}
	 * of {@code WayPoint}s and their corresponding route id, route name, route
	 * path id, and route path name. These are later added to the
	 * {@code SystemObjects} database via the {@code processDataSets()} method.
	 * 
	 * @author Sean
	 * 
	 */
	private class WayPointSet {
		private int routeId;
		private String routeName;
		private int pathId;
		private String pathName;
		private ArrayList<ImportWayPoint> wayPoints;

		/**
		 * Constructs a {@code WayPointSet}.
		 * 
		 * @param rteId
		 *            route id for this set
		 * @param rteNm
		 *            route name for this set
		 * @param pthId
		 *            path id for this set
		 * @param pthNm
		 *            path name for this set
		 */
		public WayPointSet(int rteId, String rteNm, int pthId, String pthNm) {
			routeId = rteId;
			routeName = rteNm;
			pathId = pthId;
			pathName = pthNm;
			wayPoints = new ArrayList<ImportWayPoint>();
		}

		/**
		 * Returns route id for this set.
		 * 
		 * @return route id for this set.
		 */
		public int getRouteId() {
			return routeId;
		}

		/**
		 * Returns route name for this set.
		 * 
		 * @return route name for this set.
		 */
		public String getRouteName() {
			return routeName;
		}

		/**
		 * Returns route path id for this set.
		 * 
		 * @return route path id for this set.
		 */
		public int getPathId() {
			return pathId;
		}

		/**
		 * Returns route path name for this set.
		 * 
		 * @return route path name for this set.
		 */
		public String getPathName() {
			return pathName;
		}

		/**
		 * Adds a {@code ImportWayPoint} to this {@code WayPointSet}.
		 * 
		 * @param wp
		 *            {@code ImportWayPoint} to add.
		 */
		public void addWayPoint(ImportWayPoint wp) {
			wayPoints.add(wp);
		}

		/**
		 * Returns the list of {@code ImportWayPoint}s contained in this set.
		 * 
		 * @return the list of {@code ImportWayPoint}s contained in this set.
		 */
		public ArrayList<ImportWayPoint> getWayPoints() {
			return wayPoints;
		}
	}

	/**
	 * Wrapper class for a waypoint to be imported. The reason the standard
	 * {@code WayPoint} cannot be used for this is because it requires a
	 * {@code RoutePath} reference. This cannot be done during the scan of the
	 * file because there are no existing {@code RoutePath} objects to
	 * reference. This object does not require a {@code RoutePath} to be
	 * instantiated.
	 * 
	 * @author Sean Harger
	 * 
	 */
	private class ImportWayPoint {
		private double latitude;
		private double longitude;

		/**
		 * Constructs an {@code ImportWayPoint}.
		 * 
		 * @param lat
		 * @param lon
		 */
		public ImportWayPoint(double lat, double lon) {
			latitude = lat;
			longitude = lon;
		}

		/**
		 * Returns this waypoint's latitude.
		 * 
		 * @return this waypoint's latitude.
		 */
		public double getLat() {
			return latitude;
		}

		/**
		 * Returns this waypoint's longitude.
		 * 
		 * @return this waypoint's longitude.
		 */
		public double getLon() {
			return longitude;
		}
	}

	/**
	 * Wrapper class for a stop to be imported. The reason the standard
	 * {@code Stop} cannot be used for this is because it requires a
	 * {@code RoutePath} reference. This cannot be done during the scan of the
	 * file because there are no existing {@code RoutePath} objects to
	 * reference. This object does not require a {@code RoutePath} to be
	 * instantiated.
	 * 
	 * @author Sean Harger
	 * 
	 */
	private class ImportStop extends ImportWayPoint {
		private int stopId;
		private String name;

		/**
		 * Constructs an {@code ImportStop}.
		 * 
		 * @param lat
		 *            latitude for this stop
		 * @param lon
		 *            longitude for this stop
		 * @param stopNm
		 *            name of this stop
		 * @param stId
		 *            stop id for this stop
		 */
		public ImportStop(double lat, double lon, String stopNm, int stId) {
			super(lat, lon);
			stopId = stId;
			name = stopNm;
		}

		/**
		 * Returns this stop's id.
		 * 
		 * @return this stop's id.
		 */
		public int getStopId() {
			return stopId;
		}

		/**
		 * Returns this stop's name.
		 * 
		 * @return this stop's name.
		 */
		public String getName() {
			return name;
		}
	}
}
