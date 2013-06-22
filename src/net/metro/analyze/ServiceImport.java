/*******************************************************************************
 * MASS (Metro Analytics Software System) by Sean Harger
 * is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0
 * United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/.
 ******************************************************************************/
package net.metro.analyze;

import java.io.File;

import net.metro.systemobjects.Period;
import net.metro.systemobjects.Route;
import net.metro.systemobjects.RoutePath;
import net.metro.systemobjects.Service;
import net.metro.systemobjects.ServiceStop;
import net.metro.systemobjects.Stop;
import net.metro.systemobjects.SystemObjects;
import net.metro.systemobjects.dataobjects.Alightings;
import net.metro.systemobjects.dataobjects.Boardings;
import net.metro.systemobjects.dataobjects.Load;
import net.metro.systemobjects.periodobjects.Day;
import net.metro.systemobjects.periodobjects.TimePeriod;

/**
 * Extends the {@code FileImport} class designed to import Services from an
 * external {@code .csv} file into MASS.
 * 
 * @author Sean Harger
 * 
 */
public class ServiceImport extends FileImport {
	private static final long serialVersionUID = -3040048634866135038L;

	protected static final String[] requiredFileHeaders = { "routeId",
			"pathId", "serviceId", "origin", "destination", "days", "time",
			"stopId", "boardings", "alightings", "load" };

	private Route currentRoute;
	private RoutePath currentRoutePath;
	private Service currentService;

	/**
	 * Constructs a {@code ServiceImport}.
	 * 
	 * @param imptFl
	 *            {@code File} from which service data is to be imported from
	 * @param objs
	 *            {@code SystemObjects} to which the service data will be
	 *            written
	 * @param mnFrm
	 *            reference to the {@code MainFrame} from which this
	 *            {@code ServiceImport} was invoked
	 */
	public ServiceImport(File imptFl, SystemObjects objs, MainFrame mnFrm) {
		super(imptFl, objs, requiredFileHeaders, mnFrm);
		setTitle("Service Import");
		currentRoute = null;
		currentRoutePath = null;
		currentService = null;
	}

	/**
	 * Processes an individual series of data values from a line imported from a
	 * {@code File} and adds them to the {@code SystemObjects} database. Because
	 * this is service data, individual services for existing routes will be
	 * created along with information about their days of operation, time
	 * periods, and path. A series of {@code ServiceStops} will be placed in
	 * each service which are the containers for all service-related data.
	 */
	protected void processLineData(String[] lineTokens) {
		try {
			int routeId = Integer.parseInt(lineTokens[0]);
			int pathId = Integer.parseInt(lineTokens[1]);
			int serviceId = Integer.parseInt(lineTokens[2]);
			int originId = Integer.parseInt(lineTokens[3]);
			int destinationId = Integer.parseInt(lineTokens[4]);
			String days = new String(lineTokens[5]);
			String time = new String(lineTokens[6]);
			int stopId = Integer.parseInt(lineTokens[7]);
			int board = Integer.parseInt(lineTokens[8]);
			int alight = Integer.parseInt(lineTokens[9]);
			int load = Integer.parseInt(lineTokens[10]);

			if (currentRoute == null || currentRoutePath == null
					|| currentRoute.getRouteId() != routeId
					|| currentRoutePath.getPathId() != pathId) {
				currentRoute = getSystemObjects().getRoute(routeId);
				currentRoutePath = currentRoute.getRoutePath(pathId);
			}

			if (currentService == null
					|| currentService.getServiceId() != serviceId) {
				Period pd = interpretPeriod(days, time);
				Stop ognStop = currentRoutePath.getStop(originId);
				Stop dstStop = currentRoutePath.getStop(destinationId);
				currentService = new Service(serviceId, pd, ognStop, dstStop,
						currentRoutePath);
				currentRoutePath.addService(currentService);
			}
			ServiceStop currentServiceStop = currentService
					.getServiceStop(stopId);
			System.out.println(currentServiceStop + "bd:" + board + "al:"
					+ alight + "ld:" + load);
			currentServiceStop.addData(new Boardings(board));
			currentServiceStop.addData(new Alightings(alight));
			currentServiceStop.addData(new Load(load));

			System.out.println("Stop: " + stopId);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * Interprets the period identifiers from an import file into a
	 * {@code Period} object. The default strings are <br>
	 * "WK" for weekdays, "SA" for Saturdays, "SU" FOR Sundays;<br>
	 * "EA" for early (0:00 - 6:00), "AM" for morning rush (6:00 - 9:00), "BS"
	 * for mid-day (9:00 - 15:00), "PM" for evening rush (15:00 - 18:00), and
	 * "NI' for late hours (18:00 - 24:00 ).
	 * 
	 * @param dys
	 *            one of the default {@code String} specifiers for days, listed
	 *            in the description
	 * @param tm
	 *            one of the default {@code String} specifiers for time periods,
	 *            listed in the description
	 * @return
	 */
	private Period interpretPeriod(String dys, String tm) {
		Day[] days = null;

		if (dys.equals("WK")) {
			days = Day.WEEKDAYS;
		} else if (dys.equals("SA")) {
			days = Day.SATURDAYS;
		} else if (dys.equals("SU")) {
			days = Day.SUNDAYS;
		}

		TimePeriod tmPd = null;

		if (tm.equals("EA")) {
			tmPd = TimePeriod.EARLY;
		} else if (tm.equals("AM")) {
			tmPd = TimePeriod.MORNINGRUSH;
		} else if (tm.equals("BS")) {
			tmPd = TimePeriod.BASE;
		} else if (tm.equals("PM")) {
			tmPd = TimePeriod.EVENINGRUSH;
		} else if (tm.equals("NI")) {
			tmPd = TimePeriod.LATE;
		}

		return new Period(days, tmPd);
	}
}
