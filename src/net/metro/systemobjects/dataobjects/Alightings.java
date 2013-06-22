/*******************************************************************************
 * MASS (Metro Analytics Software System) by Sean Harger
 * is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0
 * United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/.
 ******************************************************************************/
package net.metro.systemobjects.dataobjects;

/**
 * Data object designed to hold information regarding alightings at a particular
 * {@code ServiceStop}. Alightings are the number of people exiting a bus at
 * each stop.
 * 
 * @author Sean Harger
 * 
 */
public class Alightings extends Data {
	/**
	 * Constructs an {@code Alightings} data object.
	 * 
	 * @param val
	 *            integer value for number of alightings
	 */
	public Alightings(int val) {
		super(DataType.ALIGHTINGS, val);
	}

}
