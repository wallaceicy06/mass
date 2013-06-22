/*******************************************************************************
 * MASS (Metro Analytics Software System) by Sean Harger
 * is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0
 * United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/.
 ******************************************************************************/
package net.metro.systemobjects.periodobjects;

/**
 * Object to describe a duration between two {@code Time} objects. It contains
 * several static {@code TimePeriod} objects which should be used in the
 * program, but it can also be instantiated in order to compare one
 * {@code TimePeriod} with another.
 * 
 * @author Sean Harger
 * 
 */
public class TimePeriod implements Comparable<TimePeriod> {
	/**
	 * {@code TimePeriod} object representing early hours (0:00 - 6:00)
	 */
	public static final TimePeriod EARLY = new TimePeriod(new Time(0, 0, 0),
			new Time(6, 0, 0));
	/**
	 * {@code TimePeriod} object representing morning rush hours (6:00 - 9:00)
	 */
	public static final TimePeriod MORNINGRUSH = new TimePeriod(new Time(6, 0,
			0), new Time(9, 0, 0));
	/**
	 * {@code TimePeriod} object representing mid-day hours (9:00 - 15:00)
	 */
	public static final TimePeriod BASE = new TimePeriod(new Time(9, 0, 0),
			new Time(15, 0, 0));
	/**
	 * {@code TimePeriod} object representing evening rush hours (15:00 - 18:00)
	 */
	public static final TimePeriod EVENINGRUSH = new TimePeriod(new Time(15, 0,
			0), new Time(18, 0, 0));
	/**
	 * {@code TimePeriod} object representing late hours (18:00 - 24:00)
	 */
	public static final TimePeriod LATE = new TimePeriod(new Time(18, 0, 0),
			new Time(24, 0, 0));

	public Time startTm;
	public Time endTm;

	/**
	 * Constructs a {@code TimePeriod} object.
	 * 
	 * @param strt
	 *            {@code Time} representing the beginning of this time period
	 * @param end
	 *            {@code Time} representing the end of this time period
	 */
	public TimePeriod(Time strt, Time end) {
		startTm = strt;
		endTm = end;
	}

	/**
	 * Returns the {@code Time} object representing the beginning of this
	 * {@code TimePeriod}.
	 * 
	 * @return the {@code Time} object representing the beginning of this
	 *         {@code TimePeriod}.
	 */
	public Time getStartTime() {
		return startTm;
	}

	/**
	 * Returns the {@code Time} object representing the end of this
	 * {@code TimePeriod}.
	 * 
	 * @return the {@code Time} object representing the end of this
	 *         {@code TimePeriod}.
	 */
	public Time getEndTime() {
		return endTm;
	}

	/**
	 * Determines whether the specified {@code TimePeriod} is equivalent to this
	 * {@code TimePeriod}
	 * 
	 * @param tmPd
	 *            {@code TimePeriod} to compare this with
	 * @return {@code true} if the two {@code TimePeriod} objects are
	 *         equivalent.
	 */
	public boolean equals(TimePeriod tmPd) {
		if (this.compareTo(tmPd) == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns a {@code String} representation of this {@code TimePeriod} in the
	 * form of its start and end times.
	 * 
	 * @return a {@code String} representation of this {@code TimePeriod}.
	 */
	public String toString() {
		String str = new String();
		str += startTm.toString() + " - " + endTm.toString();
		return str;
	}

	/**
	 * Compares two TimePeriod objects
	 * 
	 * @param tmPd
	 *            {@code TimePeriod} to compare this one with
	 * @return 0 if the TimePeriods are identical <br>
	 *         positive integer if the passed {@code TimePeriod} is within this
	 *         {@code TimePeriod} <br>
	 *         negative integer if the passed {@code TimePeriod} outside of this
	 *         {@code TimePeriod} <br>
	 */
	@Override
	public int compareTo(TimePeriod tmPd) {
		int startCompare = this.startTm.compareTo(tmPd.getStartTime());
		int endCompare = this.endTm.compareTo(tmPd.getEndTime());

		if (startCompare == 0 & endCompare == 0) {
			return 0;
		} else if (startCompare < 0 && endCompare > 0) {
			return 1;
		} else {
			return -1;
		}
	}
}
