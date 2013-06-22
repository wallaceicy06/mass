/*******************************************************************************
 * MASS (Metro Analytics Software System) by Sean Harger
 * is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0
 * United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/.
 ******************************************************************************/
package net.metro.systemobjects.dataobjects;

/**
 * Object to define the class of data that a specific {@code Data} object is. It
 * also defines the type of data that it is: <br>
 * {@code DataType.POINT} for data types that are intended to be painted at
 * stops only (like boardings and alightings) <br>
 * {@code DataType.SEGMENT} for data types that are intended to be painted at
 * and in between stops (like load)
 * 
 * @author Sean
 * 
 */
public class DataType {
	public static final int POINT = 0;
	public static final int SEGEMENT = 1;

	/**
	 * {@code DataType} object for load, the amount of people on a bus between
	 * two stops.
	 */
	public static final DataType LOAD = new DataType("Load", DataType.SEGEMENT);
	/**
	 * {@code DataType} object for boardings, the amount of people entering a
	 * bus at a specific stop.
	 */
	public static final DataType BOARDINGS = new DataType("Boardings",
			DataType.POINT);
	/**
	 * {@code DataType} object for alightings, the amount of people exiting a
	 * bus at a specific stop.
	 */
	public static final DataType ALIGHTINGS = new DataType("Alightings",
			DataType.POINT);

	private String name;
	private int type;

	/**
	 * Constructs a {@code DataType} object. This is a private constructor and
	 * thus, no additional data types can be created without modifying the code
	 * of the program.
	 * 
	 * @param nm
	 * @param typ
	 */
	private DataType(String nm, int typ) {
		name = nm;
		type = typ;
	}

	/**
	 * Returns the name of this {@code DataType}.
	 * 
	 * @return the name of this {@code DataType}.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the type of data, point or segment.
	 * 
	 * @return {@code DataType.POINT} for point data types <br>
	 *         {@code DataType.SEGMENT} for segment data types
	 */
	public int getType() {
		return type;
	}

	/**
	 * Returns a {@code String} representation of this {@code DataType} in the
	 * form of its name.
	 * 
	 * @return a {@code String} representation of this {@code DataType}.
	 */
	public String toString() {
		return getName();
	}
}
