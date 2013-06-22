/*******************************************************************************
 * MASS (Metro Analytics Software System) by Sean Harger
 * is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0
 * United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/.
 ******************************************************************************/
package net.metro.systemobjects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.metro.systemobjects.periodobjects.Day;
import net.metro.systemobjects.periodobjects.TimePeriod;

/**
 * Object that defines a time period within which a {@code Service} operates.
 * 
 * @author Sean Harger
 * 
 */
public final class Period implements Comparable<Period> {
	/**
	 * Constant {@code Period} for Weekdays during 0:00 - 6:00.
	 */
	public static final Period WEEKDAY_EARLY = new Period(Day.WEEKDAYS,
			TimePeriod.EARLY);

	/**
	 * Constant {@code Period} for Weekdays during 6:00 - 9:00.
	 */
	public static final Period WEEKDAY_MORNINGRUSH = new Period(Day.WEEKDAYS,
			TimePeriod.MORNINGRUSH);

	/**
	 * Constant {@code Period} for Weekdays during 9:00 - 15:00.
	 */
	public static final Period WEEKDAY_BASE = new Period(Day.WEEKDAYS,
			TimePeriod.BASE);

	/**
	 * Constant {@code Period} for Weekdays during 15:00 - 18:00.
	 */
	public static final Period WEEKDAY_EVENINGRUSH = new Period(Day.WEEKDAYS,
			TimePeriod.EVENINGRUSH);

	/**
	 * Constant {@code Period} for Weekdays during 18:00 - 24:00.
	 */
	public static final Period WEEKDAY_LATE = new Period(Day.WEEKDAYS,
			TimePeriod.EVENINGRUSH);

	/**
	 * Constant {@code Period} for Saturdays during 0:00 - 6:00.
	 */
	public static final Period SATURDAY = new Period(Day.SATURDAY,
			TimePeriod.EARLY);

	/**
	 * Constant {@code Period} for Saturdays during 6:00 - 9:00.
	 */
	public static final Period SATURDAY_MORNINGRUSH = new Period(Day.SATURDAY,
			TimePeriod.MORNINGRUSH);

	/**
	 * Constant {@code Period} for Saturdays during 9:00 - 15:00.
	 */
	public static final Period SATURDAY_BASE = new Period(Day.SATURDAY,
			TimePeriod.BASE);

	/**
	 * Constant {@code Period} for Saturdays during 15:00 - 18:00.
	 */
	public static final Period SATURDAY_EVENINGRUSH = new Period(Day.SATURDAY,
			TimePeriod.EVENINGRUSH);

	/**
	 * Constant {@code Period} for Saturdays during 18:00 - 24:00.
	 */
	public static final Period SATURDAY_LATE = new Period(Day.SATURDAY,
			TimePeriod.EVENINGRUSH);

	/**
	 * Constant {@code Period} for Sundays during 0:00 - 6:00.
	 */
	public static final Period SUNDAY = new Period(Day.SUNDAY, TimePeriod.EARLY);

	/**
	 * Constant {@code Period} for Sundays during 6:00 - 9:00.
	 */
	public static final Period SUNDAY_MORNINGRUSH = new Period(Day.SUNDAY,
			TimePeriod.MORNINGRUSH);

	/**
	 * Constant {@code Period} for Sundays during 9:00 - 15:00.
	 */
	public static final Period SUNDAY_BASE = new Period(Day.SUNDAY,
			TimePeriod.BASE);

	/**
	 * Constant {@code Period} for Sundays during 15:00 - 18:00.
	 */
	public static final Period SUNDAY_EVENINGRUSH = new Period(Day.SUNDAY,
			TimePeriod.EVENINGRUSH);

	/**
	 * Constant {@code Period} for Sundays during 18:00 - 24:00.
	 */
	public static final Period SUNDAY_LATE = new Period(Day.SUNDAY,
			TimePeriod.EVENINGRUSH);

	private ArrayList<Day> days;
	private TimePeriod timePd;

	/**
	 * Constructs a {@code Period}.
	 * 
	 * @param dy
	 *            array of {@code Day}s to identify the period
	 * @param pd
	 *            {@code TimePeriod} to identify the period
	 */
	public Period(Day[] dy, TimePeriod pd) {
		days = new ArrayList<Day>(Arrays.asList(dy));
		timePd = pd;
	}

	/**
	 * Constructs a {@code Period}
	 * 
	 * @param dy
	 *            one {@code Day} to identify the period
	 * @param pd
	 *            {@code TimePeriod} to identify the period
	 */
	public Period(Day dy, TimePeriod pd) {
		days = new ArrayList<Day>();
		days.add(dy);
		timePd = pd;
	}

	/**
	 * Returns the {@code Day}s that identify this {@code Period}.
	 * 
	 * @return
	 */
	public ArrayList<Day> getDays() {
		return days;
	}

	/**
	 * Returns the minimum sequence value in a specified list of {@code Day}s.
	 * 
	 * @param dys
	 *            list of {@code Day}s to search for minimum sequence value
	 * @return minimum day sequence value or {@code -1} if none found
	 */
	private static int getMaxDaySeq(List<Day> dys) {
		int maxDaySeq = -1;
		for (Day d : dys) {
			int dySeq = d.getSequence();
			if (dySeq > maxDaySeq) {
				maxDaySeq = dySeq;
			}
		}
		return maxDaySeq;
	}

	/**
	 * Returns the maximum sequence value in a specified list of {@code Day}s.
	 * 
	 * @param dys
	 *            list of {@code Day}s to search for maximum sequence value
	 * @return minimum day sequence value or {@code Integer.MAX_VALUE} if none
	 *         found
	 */
	private static int getMinDaySeq(List<Day> dys) {
		int minDaySeq = Integer.MAX_VALUE;
		for (Day d : dys) {
			int dySeq = d.getSequence();
			if (dySeq < minDaySeq) {
				minDaySeq = dySeq;
			}
		}
		return minDaySeq;
	}

	/**
	 * Returns the {@code TimePeriod} associated with this {@code Period}.
	 * 
	 * @return the {@code TimePeriod} associated with this {@code Period}.
	 */
	public TimePeriod getTimePeriod() {
		return timePd;
	}

	/**
	 * Determines whether two Periods are equal using the {@code compareTo()}
	 * method.
	 * 
	 * @param pd
	 *            {@code Period} to compare this one with
	 * @return {@code true} if the two {@code Period}s are equal.
	 */
	public boolean equals(Period pd) {
		if (this.compareTo(pd) == 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Compares two {@code Period} objects according to their {@code Day}s and
	 * {@code TimePeriod}s.
	 * 
	 * @param pd
	 *            {@code Period} to compare with this one
	 * @return 0 if the two Periods are equal <br>
	 *         positive integer if the passed period is contained within this
	 *         period <br>
	 *         negative integer if the passed period lies outside of this period
	 */
	public int compareTo(Period pd) {
		int thisMinDay = getMinDaySeq(this.days);
		int thisMaxDay = getMaxDaySeq(this.days);
		int sentMinDay = getMinDaySeq(pd.getDays());
		int sentMaxDay = getMaxDaySeq(pd.getDays());

		if (thisMinDay == sentMinDay && thisMaxDay == sentMaxDay) {
			TimePeriod sentTimePd = pd.getTimePeriod();
			return this.timePd.compareTo(sentTimePd);
		} else if (sentMinDay < thisMinDay && sentMaxDay > thisMaxDay) {
			return 1;
		} else {
			return -1;
		}
	}
}
