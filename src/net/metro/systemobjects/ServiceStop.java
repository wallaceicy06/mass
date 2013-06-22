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

/**
 * Object that stores data information regarding occurrences at a particular
 * along a route path. This is usually data like boardings, alightings, and
 * load. A {@code ServiceStop} should not be confused with a {@code Stop} in
 * that a {@code Stop} is a route-type object in that it only is intended to
 * represent a place where a route path DOES stop. Conversely, a
 * {@code ServiceStop} is a collection of data values that represents numerical
 * values regarding that stop on a particular route path during a particular
 * time period.
 * 
 * @author Sean Harger
 * 
 */
public class ServiceStop {
	private Stop stop;
	private ArrayList<Data> data;

	/**
	 * Constructs a {@code ServiceStop}.
	 * 
	 * @param st
	 *            {@code Stop} associated with this {@code ServiceStop}
	 */
	public ServiceStop(Stop st) {
		stop = st;
		data = new ArrayList<Data>();
	}

	/**
	 * Returns the {@code Stop} associated with this {@code ServiceStop}.
	 * 
	 * @return the {@code Stop} associated with this {@code ServiceStop}.
	 */
	public Stop getStop() {
		return stop;
	}

	/**
	 * Looks for the specified {@code DataType} for this {@code ServiceStop} and
	 * returns it if found.
	 * 
	 * @param dTyp
	 *            {@code DataType} to look for
	 * @return the matching {@code Data} object or {@code null} if none was
	 *         found.
	 */
	public Data getData(DataType dTyp) {
		for (Data dt : data) {
			if (dt.getDataType() == dTyp) {
				return dt;
			}
		}
		return null;
	}

	/**
	 * Adds the specified {@code Data} object to the collection of data objects
	 * for this {@code ServiceStop}.
	 * 
	 * @param dt
	 *            {@code Data} object to add
	 */
	public void addData(Data dt) {
		data.add(dt);
	}

	/**
	 * Finds the minimum data value for the specified data type across the
	 * specified list of {@code ServiceStop}s.
	 * 
	 * @param dType
	 *            {@code DataType} to compare
	 * @param svcStops
	 *            list of {@code ServiceStop}s to search through
	 * @return the minimum data value or {@code Integer.MAX_VALUE} if none was
	 *         found.
	 */
	public static int findMinData(DataType dType,
			ArrayList<ServiceStop> svcStops) {
		int minVal = Integer.MAX_VALUE;

		for (ServiceStop st : svcStops) {
			Data dt = st.getData(dType);
			if (dt != null) {
				int dtVal = dt.getValue();
				if (dtVal < minVal) {
					minVal = dtVal;
				}
			}
		}
		return minVal;
	}

	/**
	 * Finds the maximum data value for the specified data type across the
	 * specified list of {@code ServiceStop}s.
	 * 
	 * @param dType
	 *            {@code DataType} to compare
	 * @param svcStops
	 *            list of {@code ServiceStop}s to search through
	 * @return the maximum data value or {@code -1} if none was found.
	 */
	public static int findMaxData(DataType dType,
			ArrayList<ServiceStop> svcStops) {
		int maxVal = -1;

		for (ServiceStop st : svcStops) {
			Data dt = st.getData(dType);
			if (dt != null) {
				int dtVal = dt.getValue();
				if (dtVal > maxVal) {
					maxVal = dtVal;
				}
			}
		}
		return maxVal;
	}
}
