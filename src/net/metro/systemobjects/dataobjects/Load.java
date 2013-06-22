/*******************************************************************************
 * MASS (Metro Analytics Software System) by Sean Harger
 * is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0
 * United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/.
 ******************************************************************************/
package net.metro.systemobjects.dataobjects;

/**
 * Data object designed to hold information regarding load at a particular
 * {@code ServiceStop}. Load is the number of people present on a bus after
 * boardings and alightings have occurred at a stop.
 * 
 */
public class Load extends Data {
	/**
	 * Constructs a {@code Load} data object.
	 * 
	 * @param val
	 *            integer value representing the load
	 */
	public Load(int val) {
		super(DataType.LOAD, val);
	}

}
