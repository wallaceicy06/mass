/*******************************************************************************
 * MASS (Metro Analytics Software System) by Sean Harger
 * is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0
 * United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/.
 ******************************************************************************/
package net.metro.systemobjects.periodobjects;

/**
 * Object to represent a clock time (in 24-hr format). Contains variables for
 * the number of hours, minutes, and seconds for a time.
 * 
 * @author Sean Harger
 * 
 */
public class Time implements Comparable<Time> {
	public int hrs;
	public int mins;
	public int secs;

	/**
	 * Constructs a {@code Time} object.
	 * 
	 * @param h
	 *            integer number of hours
	 * @param m
	 *            integer number of minutes
	 * @param s
	 *            integer number of seconds
	 */
	public Time(int h, int m, int s) {
		hrs = h;
		mins = m;
		secs = s;
	}

	/**
	 * Constructs a {@code Time} object.
	 * 
	 * @param h
	 *            integer number of hours
	 * @param m
	 *            integer number of minutes
	 */
	public Time(int h, int m) {
		this(h, m, 0);
	}

	/**
	 * Returns the hours of this {@code Time}.
	 * 
	 * @return the hours of this {@code Time}.
	 */
	public int getHours() {
		return hrs;
	}

	/**
	 * Returns the minutes of this {@code Time}.
	 * 
	 * @return the minutes of this {@code Time}.
	 */
	public int getMins() {
		return mins;
	}

	/**
	 * Returns the seconds of this {@code Time}.
	 * 
	 * @return the seconds of this {@code Time}.
	 */
	public int getSecs() {
		return secs;
	}

	/**
	 * Returns a {@code String} representation of this {@code Time} in the
	 * hh:mm:ss format.
	 * 
	 * @return a {@code String} representation fo this {@code Time}.
	 */
	public String toString() {
		String str = new String();
		str += String.format("%02d:%02d", hrs, mins);
		return str;
	}

	/**
	 * Determines if two {@code Time} objects are equal.
	 * 
	 * @param t
	 *            {@code Time} to compare this with
	 * @return {@code true} if the two {@code Time} objects are equal
	 */
	public boolean equals(Time t) {
		if (this.compareTo(t) == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Compares two {@code Time} objects.
	 * 
	 * @param t
	 *            {@code Time} to compare this with
	 * @return negative integer if this time is earlier than the specified
	 *         {@code Time} object <br>
	 *         {@code 0} if the two {@code Time} objects are equal <br>
	 *         positive integer if this time is later than the specified
	 *         {@code Time} object
	 */
	public int compareTo(Time t) {
		if (this.hrs < t.getHours()) {
			return -1;
		} else if (this.hrs > t.getHours()) {
			return 1;
		} else // which means hours are equal
		{
			if (this.mins < t.getMins()) {
				return -1;
			} else if (this.mins > t.getMins()) {
				return 1;
			} else // which means hours, mins are equal
			{
				if (this.secs < t.getSecs()) {
					return -1;
				} else if (this.secs > t.getSecs()) {
					return 1;
				} else // which means hours, mins, and secs are equal
				{
					return 0;
				}
			}
		}
	}
}
