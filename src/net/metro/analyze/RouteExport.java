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
 * Extension of the {@code FileExport} class designed to export existing route
 * data from within MASS to an external {@code .csv} file.
 * 
 * @author Sean Harger
 * 
 */
public class RouteExport extends FileExport {
	private static final long serialVersionUID = 7882929032171016869L;

	protected static final String[] fileHeaders = { "routeId", "routeName",
			"pathId", "pathSeq", "pathName", "wayPointType", "pointLat",
			"pointLon", "stopName", "stopId" };
	protected static final int WPTYPE_WAYPOINT = 0;
	protected static final int WPTYPE_STOP = 1;
	private ArrayList<RouteFileLineSet> routeFileLines;

	/**
	 * Constructs a {@code RouteExport}.
	 * 
	 * @param exptFl
	 *            {@code File} to export data to.
	 * @param objs
	 *            {@code SystemObjects} database to extract data from.
	 * @param mnFrm
	 *            {@code MainFrame} from which this {@code RouteExport} was
	 *            invoked.
	 */
	public RouteExport(File exptFl, SystemObjects objs, MainFrame mnFrm) {
		super(exptFl, objs, RouteExport.fileHeaders, mnFrm);
		super.setTitle("Route Export");
		routeFileLines = new ArrayList<RouteFileLineSet>();

		go();
	}

	/**
	 * Calculates the number of lines to be printed in the exported file and
	 * prepares a {@code RouteFileLineSet} for each line. These sets will be
	 * used to print individual lines into the export file.
	 * 
	 * @return number of lines to be printed
	 */
	protected int calculateLines() {
		for (Route rte : getSystemObjects().getAllRoutes()) {
			for (RoutePath rtePth : rte.getPaths()) {
				for (WayPoint wp : rtePth.getWayPoints()) {
					routeFileLines.add(new RouteFileLineSet(rte, rtePth, wp));
				}
			}
		}

		return routeFileLines.size();
	}

	/**
	 * Processes the route data for the specified line of the export file for
	 * final printing.
	 * 
	 * @param lnNum
	 *            line number to process data for.
	 * @return {@code String} array of values for each column in the exported
	 *         file.
	 */
	protected String[] processLineData(int lnNum) {
		RouteFileLineSet set = routeFileLines.get(lnNum);
		Route rte = set.getRoute();
		RoutePath rtePth = set.getRoutePath();
		WayPoint wp = set.getWayPoint();

		String routeId = Integer.toString(rte.getRouteId());
		String routeName = rte.getName();
		String pathId = Integer.toString(rtePth.getPathId());
		String pathSeq = Integer
				.toString(rtePth.getWayPoints().indexOf(wp) + 1);
		String pathName = rtePth.getName();
		String wayPointType = "-1";
		String pointLat = Double.toString(wp.getLat());
		String pointLon = Double.toString(wp.getLon());
		String stopName = "";
		String stopId = "";
		if (wp instanceof Stop) {
			System.out.println("stop ");
			wayPointType = Integer.toString(RouteExport.WPTYPE_STOP);
			stopName = ((Stop) wp).getName();
			stopId = Integer.toString(((Stop) wp).getStationId());
		} else if (wp instanceof WayPoint) {
			System.out.println("waypoint");
			wayPointType = Integer.toString(RouteExport.WPTYPE_WAYPOINT);
			stopName = "WAYPOINT";
			stopId = "WAYPOINT";
		}

		return new String[] { routeId, routeName, pathId, pathSeq, pathName,
				wayPointType, pointLat, pointLon, stopName, stopId };
	}

	/**
	 * Wrapper class for individual route data to be exported. Contains
	 * information on the route, route path, and waypoint for each line.
	 * 
	 * @author Sean Harger
	 * 
	 */
	private class RouteFileLineSet {
		private Route route;
		private RoutePath routePath;
		private WayPoint wayPoint;

		/**
		 * Constructs a {@code RouteFileLineSet}.
		 * 
		 * @param rte
		 *            {@code Route} to be added to the set
		 * @param rtePth
		 *            {@code RoutePath} to be added to the set
		 * @param wp
		 *            {@code WayPoint} to be added to the set
		 */
		public RouteFileLineSet(Route rte, RoutePath rtePth, WayPoint wp) {
			route = rte;
			routePath = rtePth;
			wayPoint = wp;
		}

		/**
		 * Returns the {@code Route} contained within this set.
		 * 
		 * @return the {@code Route} contained within this set.
		 */
		public Route getRoute() {
			return route;
		}

		/**
		 * Returns the {@code RoutePath} contained within this set.
		 * 
		 * @return the {@code RoutePath} contained within this set.
		 */
		public RoutePath getRoutePath() {
			return routePath;
		}

		/**
		 * Returns the {@code WayPoint} contained within this set.
		 * 
		 * @return the {@code WayPoint} contained within this set.
		 */
		public WayPoint getWayPoint() {
			return wayPoint;
		}
	}
}
