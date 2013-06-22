/*******************************************************************************
 * MASS (Metro Analytics Software System) by Sean Harger
 * is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0
 * United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/.
 ******************************************************************************/
package net.metro.systemobjects;

import java.util.ArrayList;

import net.metro.systemobjects.dataobjects.Data;
import net.metro.systemobjects.dataobjects.DataType;
import net.metro.systemobjects.periodobjects.Day;

/**
 * Object that provides information about a particular path of a route during a
 * specific time of day and time of the week. It also contains a series of
 * {@code ServiceStop} objects that contain rider data for each stop on this
 * particular service during its scheduled time period.
 * 
 * @author Sean Harger
 * 
 */
public class Service {
	// parent references
	private RoutePath myPath;

	// private fields
	private int serviceId;
	private Stop originStop;
	private Stop destinationStop;
	private Period period;
	private ArrayList<ServiceStop> serviceStops;

	/*
	 * private ArrayList<Data> serviceData; (commented out due to no use at this
	 * time)
	 */

	/**
	 * Constructs a {@code Service}.
	 * 
	 * @param svcId
	 *            unique id representing this {@code Service}
	 * @param pd
	 *            period during which this service operates
	 * @param ognStop
	 *            the origin {@code Stop} of this service (must be a stop from
	 *            the {@code RoutePath} which owns this {@code Service}.
	 * @param dstStop
	 *            the destination {@code Stop} of this service (must be a stop
	 *            from the {@code RoutePath} which owns this {@code Service}.
	 * @param pth
	 *            {@code RoutePath} which operates this service
	 */
	public Service(int svcId, Period pd, Stop ognStop, Stop dstStop,
			RoutePath pth) {
		serviceId = svcId;
		period = pd;
		originStop = ognStop;
		destinationStop = dstStop;
		myPath = pth;

		serviceStops = new ArrayList<ServiceStop>();
		for (Stop st : myPath.getStopsInBetween(ognStop, dstStop)) {
			serviceStops.add(new ServiceStop(st));
		}

	}

	/**
	 * Returns the service id identifying this {@code Service}.
	 * 
	 * @return the service id identifying this {@code Service}.
	 */
	public int getServiceId() {
		return serviceId;
	}

	/**
	 * Returns the origin {@code Stop} of this particular service.
	 * 
	 * @return the origin {@code Stop} of this particular service.
	 */
	public Stop getOriginStop() {
		return originStop;
	}

	/**
	 * Returns the destination {@code Stop} of this particular service.
	 * 
	 * @return the destination {@code Stop} of this particular service.
	 */
	public Stop getDestinationStop() {
		return destinationStop;
	}

	/**
	 * Returns the period (days & time) during which this service operates.
	 * 
	 * @return the period during which this service operates.
	 */
	public Period getPeriod() {
		return period;
	}

	/**
	 * Returns the {@code ServiceStop} associated with the specified
	 * {@code Stop}.
	 * 
	 * @param st
	 *            the {@code Stop} to look for a corresponding
	 *            {@code ServiceStop}
	 * @return the matching {@code ServiceStop} or {@code null} if none is
	 *         found.
	 */
	public ServiceStop getServiceStop(Stop st) {
		return getServiceStop(st.getStationId());
	}

	/**
	 * Returns the {@code Service Stop} containing the specified stop id.
	 * 
	 * @param stopId
	 *            the stop id to look for a corresponding {@code ServiceStop}
	 * @return the matching {@code ServiceStop} or {@code null} if none is
	 *         found.
	 */
	public ServiceStop getServiceStop(int stopId) {
		for (ServiceStop st : serviceStops) {
			if (st.getStop().getStationId() == stopId) {
				return st;
			}
		}
		return null;
	}

	/**
	 * Returns all the {@code ServiceStop}s associated with this {@code Service}
	 * in order.
	 * 
	 * @return all the {@code ServiceStop}s associated with this {@code Service}
	 *         in order.
	 */
	public ArrayList<ServiceStop> getServiceStops() {
		return serviceStops;
	}

	/**
	 * Returns all the {@code Stop}s associated with this {@code Service} in
	 * order.
	 * 
	 * @return all the {@code Stop}s associated with this {@code Service} in
	 *         order.
	 */
	public ArrayList<Stop> getStops() {
		ArrayList<Stop> stps = new ArrayList<Stop>();
		for (ServiceStop st : serviceStops) {
			stps.add(st.getStop());
		}
		return stps;
	}

	/**
	 * Finds the maximum data value of the specified data type across all
	 * {@code ServiceStop}s associated with this {@code Service}.
	 * 
	 * @param dType
	 *            data type to compare
	 * @return the maximum data value across all {@code ServiceStop}s associated
	 *         with this {@code Service}.
	 */
	public int getMaxData(DataType dType) {
		int maxValue = -1;
		for (ServiceStop st : serviceStops) {
			Data dt = st.getData(dType);
			if (dt != null) {
				int dtVal = dt.getValue();
				if (dtVal > maxValue) {
					maxValue = dtVal;
				}
			}
		}
		return maxValue;
	}

	/**
	 * Finds the minimum data value of the specified data type across all
	 * {@code ServiceStop}s associated with this {@code Service}.
	 * 
	 * @param dType
	 *            data type to compare
	 * @return the minimum data value across all {@code ServiceStop}s associated
	 *         with this {@code Service}.
	 */
	public int getMinData(DataType dType) {
		int minValue = Integer.MAX_VALUE;
		for (ServiceStop st : serviceStops) {
			Data dt = st.getData(dType);
			if (dt != null) {
				int dtVal = st.getData(dType).getValue();
				if (dtVal < minValue) {
					minValue = dtVal;
				}
			}
		}
		return minValue;
	}

	/**
	 * Returns the portion of the parent route path on which this service
	 * operates. This includes all {@code Stop}s and {@code WayPoint}s in
	 * between the origin stop and destination stop.
	 * 
	 * @return the portion of the parent route path on which this service
	 *         operates.
	 */
	public ArrayList<WayPoint> getServicePath() {
		return myPath.getSubPath(originStop, destinationStop);
	}

	/**
	 * Returns the parent {@code RoutePath} object which contains this service.
	 * 
	 * @return the parent {@code RoutePath} object which contains this service.
	 */
	public RoutePath getRoutePath() {
		return myPath;
	}

	/**
	 * Returns a {@code String} representation of this {@code Service} in the
	 * form of its days of operation and time period of operation.
	 * 
	 * @return {@code String} representation of this {@code Service}.
	 */
	public String toString() {
		String str = new String();
		for (Day dy : period.getDays()) {
			str += dy.toString() + " ";
		}
		str += "  " + period.getTimePeriod().toString();
		return str;
	}

	/**
	 * Finds the minimum data value of the specified {@code DataType} within the
	 * specified list of services.
	 * 
	 * @param dType
	 *            data type to compare
	 * @param svcScope
	 *            services to search through
	 * @return the minimum data value across the specified services, or
	 *         {@code Integer.MAX_VALUE} if none was found.
	 */
	public static int findMinData(DataType dType, ArrayList<Service> svcScope) {
		int minValue = Integer.MAX_VALUE;

		for (Service svc : svcScope) {
			int dtVal = svc.getMinData(dType);
			if (dtVal != -1) {
				if (dtVal < minValue) {
					minValue = dtVal;
				}
			}
		}
		return minValue;
	}

	/**
	 * Finds the maximum data value of the specified {@code DataType} within the
	 * specified list of services.
	 * 
	 * @param dType
	 *            data type to compare
	 * @param svcScope
	 *            services to search through
	 * @return the maximum data value across the specified services, or
	 *         {@code -1} if none was found.
	 */
	public static int findMaxData(DataType dType, ArrayList<Service> svcScope) {
		int maxValue = -1;

		for (Service svc : svcScope) {
			int dtVal = svc.getMaxData(dType);
			if (dtVal != -1) {
				if (dtVal > maxValue) {
					maxValue = dtVal;
				}
			}
		}
		return maxValue;
	}
}
