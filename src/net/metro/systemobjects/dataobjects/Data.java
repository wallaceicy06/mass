/*******************************************************************************
 * MASS (Metro Analytics Software System) by Sean Harger
 * is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0
 * United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/.
 ******************************************************************************/
package net.metro.systemobjects.dataobjects;

/**
 * Abstract object to contain a specific data value.
 * 
 * @author Sean Harger
 * 
 */
public abstract class Data {
	private int value;
	private DataType myType;

	/**
	 * Constructs a {@code Data} object.
	 * 
	 * @param typ
	 *            unique {@code DataType} that this object contains
	 * @param val
	 *            the data value for the specified {@code DataType}
	 */
	public Data(DataType typ, int val) {
		myType = typ;
		value = val;
	}

	/**
	 * Returns the integer value for this {@code Data} object.
	 * 
	 * @return the integer value for this {@code Data} object.
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Returns the {@code DataType} of this {@code Data} object.
	 * 
	 * @return the {@code DataType} of this {@code Data} object.
	 */
	public DataType getDataType() {
		return myType;
	}

	/**
	 * Returns a {@code String} representation of this {@code Data} object
	 * including its data type and value.
	 * 
	 * @return a {@code String} representation of this {@code Data} object.
	 */
	public String toString() {
		return new String(myType.getName() + ": " + value);
	}
}
