/*******************************************************************************
 * MASS (Metro Analytics Software System) by Sean Harger
 * is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 3.0
 * United States License. To view a copy of this license, visit
 * http://creativecommons.org/licenses/by-nc-sa/3.0/us/.
 ******************************************************************************/
package net.metro.systemobjects.periodobjects;

import java.util.List;

/**
 * Object to represent a day of the week. The only way to access a member of
 * this class is through one of the static fields, since the days of the week
 * cannot change.
 * 
 * @author Sean Harger
 * 
 */
public class Day {
	/**
	 * {@code Day} object representing Monday.
	 */
	public static final Day MONDAY = new Day(1, "Monday");
	/**
	 * {@code Day} object representing Tuesday.
	 */
	public static final Day TUESDAY = new Day(2, "Tuesday");
	/**
	 * {@code Day} object representing Wednesday.
	 */
	public static final Day WEDNESDAY = new Day(3, "Wednesday");
	/**
	 * {@code Day} object representing Thursday.
	 */
	public static final Day THURSDAY = new Day(4, "Thursday");
	/**
	 * {@code Day} object representing Friday.
	 */
	public static final Day FRIDAY = new Day(5, "Friday");
	/**
	 * {@code Day} object representing Saturday.
	 */
	public static final Day SATURDAY = new Day(6, "Saturday");
	/**
	 * {@code Day} object representing Sunday.
	 */
	public static final Day SUNDAY = new Day(7, "Sunday ");

	/**
	 * array of {@code Day} objects representing weekdays (Monday - Friday).
	 */
	public static final Day[] WEEKDAYS = new Day[] { Day.MONDAY, Day.TUESDAY,
			Day.WEDNESDAY, Day.THURSDAY, Day.FRIDAY };
	/**
	 * array of {@code Day} objects representing Saturdays.
	 */
	public static final Day[] SATURDAYS = new Day[] { Day.SATURDAY };
	/**
	 * array of {@code Day} objects representing Sundays.
	 */
	public static final Day[] SUNDAYS = new Day[] { Day.SUNDAY };
	/**
	 * array of {@code Day} objects representing weekends (Saturday - Sunday).
	 */
	public static final Day[] WEEKENDS = new Day[] { Day.SATURDAY, Day.SUNDAY };

	public int seq;
	public String name;

	/**
	 * Constructs a {@code Day} object. Since this is a private constructor, the
	 * only day objects that are accessible are those that are members of the
	 * static fields of the {@code Day} class.
	 * 
	 * @param sq
	 *            integer sequence of this day during the week (Monday is 1,
	 *            Tuesday is 2, etc.)
	 * @param nm
	 *            name of the day
	 */
	private Day(int sq, String nm) {
		seq = sq;
		name = nm;
	}

	/**
	 * Returns the name of this {@code Day}.
	 * 
	 * @return the name of this {@code Day}.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns a {@code String} representation of this day in a concactenated
	 * form (only the first two letters).
	 * 
	 * @return a {@code String} representation of this day.
	 */
	public String toString() {
		return name.substring(0, 2);
	}

	/**
	 * Returns the integer sequence of this day during the week.
	 * 
	 * @return the integer sequence of this day during the week.
	 */
	public int getSequence() {
		return seq;
	}

	/**
	 * Determines whether two lists of days are equal. The lists do not have to
	 * be in order.
	 * 
	 * @param listOne
	 *            first list of days
	 * @param listTwo
	 *            second list of days
	 * @return {@code true} if the lists contain the same, but no additional
	 *         days
	 */
	public static boolean areDaysEqual(List<Day> listOne, List<Day> listTwo) {
		if (listOne.size() != listTwo.size()) {
			return false;
		} else {
			for (Day dy : listOne) {
				if (!listTwo.contains(dy)) {
					return false;
				}
			}
			return true;
		}
	}
}
