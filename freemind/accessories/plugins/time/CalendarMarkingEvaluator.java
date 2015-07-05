/*FreeMind - A Program for creating and viewing Mindmaps
 *Copyright (C) 2000-2015 Christian Foltin, Joerg Mueller, Daniel Polansky, Dimitri Polivaev and others.
 *
 *See COPYING for Details
 *
 *This program is free software; you can redistribute it and/or
 *modify it under the terms of the GNU General Public License
 *as published by the Free Software Foundation; either version 2
 *of the License, or (at your option) any later version.
 *
 *This program is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with this program; if not, write to the Free Software
 *Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package accessories.plugins.time;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;

import freemind.controller.actions.generated.instance.CalendarMarking;
import freemind.controller.actions.generated.instance.CalendarMarkings;

/**
 * @author foltin
 * @date 05.07.2015
 */
public class CalendarMarkingEvaluator implements ICalendarMarkingEvaluator {

	private CalendarMarkings mCalendarMarkings;
	private HashMap<Long, CalendarMarking> mCache = new HashMap<>();

	private static interface RepetitionHandler {

		Calendar getFirst(Calendar pStartDate, CalendarMarking pMarking);
		Calendar getNext(Calendar pDay, CalendarMarking pMarking);
	}
	private abstract static class BasicRepetitionHandler implements RepetitionHandler {
		public Calendar compareIfStillBefore(Calendar pDay, CalendarMarking pMarking) {
			if(pMarking.getEndDate()>0){
				Calendar cal2 = Calendar.getInstance();
				cal2.setTimeInMillis(pMarking.getEndDate());
				if(pDay.compareTo(cal2)<=0){
					return pDay;
				} else {
					return null;
				}
			}
			return pDay;
		}

		public Calendar shiftToBeAfterStartDate(Calendar pStartDate,
				CalendarMarking pMarking, Calendar clone) {
			while(pStartDate != null && clone.compareTo(pStartDate)>0){
				// first occurrence in this year is after the start date. we shift unless done.
				pStartDate = getNext(pStartDate, pMarking);
			}
			return pStartDate;
		}
		
	}		

	private abstract static class DirektBeginnerHandler extends BasicRepetitionHandler {
		
		@Override
		public Calendar getFirst(Calendar pStartDate, CalendarMarking pMarking) {
			return pStartDate;
		}
		
	}
	
	private static class NeverHandler extends DirektBeginnerHandler {

		@Override
		public Calendar getNext(Calendar pDay, CalendarMarking pMarking) {
			return null;
		}

	}
	private static class WeeklyHandler extends DirektBeginnerHandler {

		@Override
		public Calendar getNext(Calendar pDay, CalendarMarking pMarking) {
			pDay.add(Calendar.WEEK_OF_YEAR, pMarking.getRepeatEachNOccurence());
			return compareIfStillBefore(pDay, pMarking);
		}
		
	}

	private static class WeeklyEveryNthDayHandler extends DirektBeginnerHandler {
		
		@Override
		public Calendar getFirst(Calendar pStartDate,
				CalendarMarking pMarking) {
			Calendar clone = (Calendar) pStartDate.clone();
			pStartDate.set(Calendar.DAY_OF_WEEK, pMarking.getFirstOccurence());
			return shiftToBeAfterStartDate(pStartDate, pMarking, clone);
		}
		
		@Override
		public Calendar getNext(Calendar pDay, CalendarMarking pMarking) {
			Calendar clone = (Calendar) pDay.clone();
			pDay.add(Calendar.DAY_OF_WEEK, pMarking.getRepeatEachNOccurence());
			if(clone.get(Calendar.WEEK_OF_YEAR) != pDay.get(Calendar.WEEK_OF_YEAR)){
				// we switched the month. Let's start from new:
				pDay.set(Calendar.DAY_OF_WEEK, pMarking.getFirstOccurence());
			}
			return compareIfStillBefore(pDay, pMarking);
		}
		
	}
	
	private static class MonthlyHandler extends DirektBeginnerHandler {
		
		@Override
		public Calendar getNext(Calendar pDay, CalendarMarking pMarking) {
			pDay.add(Calendar.MONTH, pMarking.getRepeatEachNOccurence());
			return compareIfStillBefore(pDay, pMarking);
		}

	}
	
	private static class MonthlyEveryNthDayHandler extends BasicRepetitionHandler {
		
		@Override
		public Calendar getFirst(Calendar pStartDate,
				CalendarMarking pMarking) {
			Calendar clone = (Calendar) pStartDate.clone();
			pStartDate.set(Calendar.DAY_OF_MONTH, pMarking.getFirstOccurence());
			return shiftToBeAfterStartDate(pStartDate, pMarking, clone);
		}
		
		@Override
		public Calendar getNext(Calendar pDay, CalendarMarking pMarking) {
			Calendar clone = (Calendar) pDay.clone();
			pDay.add(Calendar.DAY_OF_MONTH, pMarking.getRepeatEachNOccurence());
			if(clone.get(Calendar.MONTH) != pDay.get(Calendar.MONTH)){
				// we switched the month. Let's start from new:
				pDay.set(Calendar.DAY_OF_MONTH, pMarking.getFirstOccurence());
			}
			return compareIfStillBefore(pDay, pMarking);
		}

	}
	
	private static class MonthlyEveryNthWeekHandler extends BasicRepetitionHandler {
		
		@Override
		public Calendar getFirst(Calendar pStartDate,
				CalendarMarking pMarking) {
			Calendar clone = (Calendar) pStartDate.clone();
			pStartDate.set(Calendar.WEEK_OF_MONTH, pMarking.getFirstOccurence());
			return shiftToBeAfterStartDate(pStartDate, pMarking, clone);
		}
		
		@Override
		public Calendar getNext(Calendar pDay, CalendarMarking pMarking) {
			Calendar clone = (Calendar) pDay.clone();
			pDay.add(Calendar.WEEK_OF_MONTH, pMarking.getRepeatEachNOccurence());
			if(clone.get(Calendar.MONTH) != pDay.get(Calendar.MONTH)){
				// we switched the month. Let's start from new:
				pDay.set(Calendar.WEEK_OF_MONTH, pMarking.getFirstOccurence());
			}
			return compareIfStillBefore(pDay, pMarking);
		}
		
	}
	

	
	private static class YearlyHandler extends DirektBeginnerHandler {
		
		@Override
		public Calendar getNext(Calendar pDay, CalendarMarking pMarking) {
			pDay.add(Calendar.YEAR, pMarking.getRepeatEachNOccurence());
			return compareIfStillBefore(pDay, pMarking);
		}
		
	}
	
	private static class YearlyEveryNthDayHandler extends BasicRepetitionHandler {
		
		@Override
		public Calendar getFirst(Calendar pStartDate,
				CalendarMarking pMarking) {
			Calendar clone = (Calendar) pStartDate.clone();
			pStartDate.set(Calendar.DAY_OF_YEAR, pMarking.getFirstOccurence());
			return shiftToBeAfterStartDate(pStartDate, pMarking, clone);
		}

		@Override
		public Calendar getNext(Calendar pDay, CalendarMarking pMarking) {
			Calendar clone = (Calendar) pDay.clone();
			pDay.add(Calendar.DAY_OF_YEAR, pMarking.getRepeatEachNOccurence());
			if(clone.get(Calendar.YEAR) != pDay.get(Calendar.YEAR)){
				// we switched the year. Let's start from new:
				pDay.set(Calendar.DAY_OF_YEAR, pMarking.getFirstOccurence());
			}
			return compareIfStillBefore(pDay, pMarking);
		}

	}
	
	private static class YearlyEveryNthWeekHandler extends BasicRepetitionHandler {
		
		@Override
		public Calendar getFirst(Calendar pStartDate,
				CalendarMarking pMarking) {
			Calendar clone = (Calendar) pStartDate.clone();
			pStartDate.set(Calendar.WEEK_OF_YEAR, pMarking.getFirstOccurence());
			return shiftToBeAfterStartDate(pStartDate, pMarking, clone);
		}
		
		@Override
		public Calendar getNext(Calendar pDay, CalendarMarking pMarking) {
			Calendar clone = (Calendar) pDay.clone();
			pDay.add(Calendar.WEEK_OF_YEAR, pMarking.getRepeatEachNOccurence());
			if(clone.get(Calendar.YEAR) != pDay.get(Calendar.YEAR)){
				// we switched the year. Let's start from new:
				pDay.set(Calendar.WEEK_OF_YEAR, pMarking.getFirstOccurence());
			}
			return compareIfStillBefore(pDay, pMarking);
		}
		
	}
	
	private static class YearlyEveryNthMonthHandler extends BasicRepetitionHandler {
		
		@Override
		public Calendar getFirst(Calendar pStartDate,
				CalendarMarking pMarking) {
			Calendar clone = (Calendar) pStartDate.clone();
			pStartDate.set(Calendar.MONTH, pMarking.getFirstOccurence());
			return shiftToBeAfterStartDate(pStartDate, pMarking, clone);
		}
		
		@Override
		public Calendar getNext(Calendar pDay, CalendarMarking pMarking) {
			Calendar clone = (Calendar) pDay.clone();
			pDay.add(Calendar.MONTH, pMarking.getRepeatEachNOccurence());
			if(clone.get(Calendar.YEAR) != pDay.get(Calendar.YEAR)){
				// we switched the year. Let's start from new:
				pDay.set(Calendar.MONTH, pMarking.getFirstOccurence());
			}
			return compareIfStillBefore(pDay, pMarking);
		}
		
	}
	
	private static class DailyHandler extends DirektBeginnerHandler {
		
		@Override
		public Calendar getNext(Calendar pDay, CalendarMarking pMarking) {
			pDay.add(Calendar.DAY_OF_YEAR, pMarking.getRepeatEachNOccurence());
			return compareIfStillBefore(pDay, pMarking);
		}
		
	}
	
	
	private static HashMap<String, RepetitionHandler> sHandlerMap;

	public CalendarMarkingEvaluator(CalendarMarkings pCalendarMarkings) {
		mCalendarMarkings = pCalendarMarkings;
		if (sHandlerMap == null) {
			sHandlerMap = new HashMap<>();
			sHandlerMap.put(CalendarMarking.NEVER, new NeverHandler());
			sHandlerMap.put(CalendarMarking.DAILY, new DailyHandler());
			sHandlerMap.put(CalendarMarking.WEEKLY, new WeeklyHandler());
			sHandlerMap.put(CalendarMarking.WEEKLY_EVERY_NTH_DAY, new WeeklyEveryNthDayHandler());
			sHandlerMap.put(CalendarMarking.MONTHLY, new MonthlyHandler());
			sHandlerMap.put(CalendarMarking.MONTHLY_EVERY_NTH_DAY, new MonthlyEveryNthDayHandler());
			sHandlerMap.put(CalendarMarking.MONTHLY_EVERY_NTH_WEEK, new MonthlyEveryNthWeekHandler());
			sHandlerMap.put(CalendarMarking.YEARLY, new YearlyHandler());
			sHandlerMap.put(CalendarMarking.YEARLY_EVERY_NTH_DAY, new YearlyEveryNthDayHandler());
			sHandlerMap.put(CalendarMarking.YEARLY_EVERY_NTH_WEEK, new YearlyEveryNthWeekHandler());
			sHandlerMap.put(CalendarMarking.YEARLY_EVERY_NTH_MONTH, new YearlyEveryNthMonthHandler());
		}
	}

	/* (non-Javadoc)
	 * @see accessories.plugins.time.ICalenderMarkingEvaluator#isMarked(java.util.Calendar)
	 */
	@Override
	public CalendarMarking isMarked(Calendar pCalendar) {
		long millies = pCalendar.getTimeInMillis();
		if(mCache.containsKey(millies)) {
			return mCache.get(millies);
		}
		pCalendar = (Calendar) pCalendar.clone();
		for (int i = 0; i < mCalendarMarkings.sizeCalendarMarkingList(); i++) {
			CalendarMarking marking = mCalendarMarkings.getCalendarMarking(i);
			// get first occurrence:
			Calendar firstDay = Calendar.getInstance();
			firstDay.setTimeInMillis(marking.getStartDate());
			RepetitionHandler handler = sHandlerMap
					.get(marking.getRepeatType());
			firstDay = handler.getFirst(firstDay, marking);
			if(firstDay == null){
				continue;
			}
			if (equal(pCalendar, firstDay)) {
				mCache.put(millies, marking);
				return marking;
			}
			while(pCalendar.compareTo(firstDay)>=0){
				firstDay = handler.getNext(firstDay, marking);
				if(firstDay == null){
					break;
				}
				if (equal(pCalendar, firstDay)) {
					mCache.put(millies, marking);
					return marking;
				}
			}
		}
		mCache.put(millies, null);
		return null;
	}

	private boolean equal(Calendar pCalendar, Calendar pOtherDay) {
		return pCalendar.get(Calendar.YEAR) == pOtherDay.get(Calendar.YEAR)
				&& pCalendar.get(Calendar.MONTH) == pOtherDay
						.get(Calendar.MONTH)
				&& pCalendar.get(Calendar.DAY_OF_MONTH) == pOtherDay
						.get(Calendar.DAY_OF_MONTH);
	}
	
	/**
	 * Don't use for endless repetitions!
	 */
	public void print(){
		for (int i = 0; i < mCalendarMarkings.sizeCalendarMarkingList(); i++) {
			CalendarMarking marking = mCalendarMarkings.getCalendarMarking(i);
			// get first occurrence:
			Calendar firstDay = Calendar.getInstance();
			firstDay.setTimeInMillis(marking.getStartDate());
			RepetitionHandler handler = sHandlerMap
					.get(marking.getRepeatType());
			firstDay = handler.getFirst(firstDay, marking);
			printDate(firstDay);
			while(firstDay != null){
				firstDay = handler.getNext(firstDay, marking);
				printDate(firstDay);
			}
		}
	}

	public void printDate(Calendar firstDay) {
		if (firstDay != null) {
			System.out.println(DateFormat.getDateInstance().format(
					firstDay.getTime()));
		}
	}
}
