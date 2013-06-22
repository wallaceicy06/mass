/*******************************************************************************
 * MASS (Metro Analytics Software System) by Sean Harger
 * is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0
 * United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/.
 ******************************************************************************/
package net.metro.systemobjects.dataobjects;

/**
 * Data object designed to hold information regarding boardings at a particular
 * {@code ServiceStop}. Boardings are the number of people entering a bus at
 * each stop.
 * 
 * @author Sean Harger
 * 
 */
public class Boardings extends Data {
	/**
	 * Constructs a {@code Boardings} data object.
	 * 
	 * @param val
	 *            integer value for number of boardings.
	 */
	public Boardings(int val) {
		super(DataType.BOARDINGS, val);
	}

}
