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

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import freemind.controller.actions.generated.instance.CalendarMarking;
import freemind.controller.actions.generated.instance.CalendarMarkings;

/**
 * @author foltin
 * @date 05.07.2015
 */
public class CalendarMarkingEvaluator {

	private CalendarMarkings mCalendarMarkings;
	private HashMap<Long, CalendarMarking> mCache = new HashMap<>();

	private static interface RepetitionHandler {

		Calendar getFirst(Calendar pStartCalendar, CalendarMarking pMarking);
		Calendar getNext(Calendar pFirstDay, CalendarMarking pMarking);
	}

	private abstract static class DirektBeginnerHandler implements RepetitionHandler {
		
		@Override
		public Calendar getFirst(Calendar pStartCalendar, CalendarMarking pMarking) {
			return pStartCalendar;
		}
		
		public Calendar compareIt(Calendar pFirstDay, CalendarMarking pMarking) {
			if(pMarking.getEndDate()>0){
				Calendar cal2 = Calendar.getInstance();
				cal2.setTimeInMillis(pMarking.getEndDate());
				if(pFirstDay.compareTo(cal2)<=0){
					return pFirstDay;
				} else {
					return null;
				}
			}
			return pFirstDay;
		}
		
	}
	
	private static class NeverHandler extends DirektBeginnerHandler {

		@Override
		public Calendar getNext(Calendar pFirstDay, CalendarMarking pMarking) {
			return null;
		}

	}
	private static class WeeklyHandler extends DirektBeginnerHandler {

		@Override
		public Calendar getNext(Calendar pFirstDay, CalendarMarking pMarking) {
			pFirstDay.add(Calendar.WEEK_OF_YEAR, pMarking.getRepeatEachNOccurence());
			return compareIt(pFirstDay, pMarking);
		}
		
	}

	private static class MonthlyHandler extends DirektBeginnerHandler {
		
		@Override
		public Calendar getNext(Calendar pFirstDay, CalendarMarking pMarking) {
			pFirstDay.add(Calendar.MONTH, pMarking.getRepeatEachNOccurence());
			return compareIt(pFirstDay, pMarking);
		}

	}
	
	private static class YearlyHandler extends DirektBeginnerHandler {
		
		@Override
		public Calendar getNext(Calendar pFirstDay, CalendarMarking pMarking) {
			pFirstDay.add(Calendar.YEAR, pMarking.getRepeatEachNOccurence());
			return compareIt(pFirstDay, pMarking);
		}
		
	}
	
	private static class DailyHandler extends DirektBeginnerHandler {
		
		@Override
		public Calendar getNext(Calendar pFirstDay, CalendarMarking pMarking) {
			pFirstDay.add(Calendar.DAY_OF_YEAR, pMarking.getRepeatEachNOccurence());
			return compareIt(pFirstDay, pMarking);
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
			sHandlerMap.put(CalendarMarking.MONTHLY, new MonthlyHandler());
			sHandlerMap.put(CalendarMarking.YEARLY, new YearlyHandler());
		}
	}

	public CalendarMarking isMarked(Calendar pCalendar) {
		long millies = pCalendar.getTimeInMillis();
		if(mCache.containsKey(millies)) {
			return mCache.get(millies);
		}
		for (int i = 0; i < mCalendarMarkings.sizeCalendarMarkingList(); i++) {
			CalendarMarking marking = mCalendarMarkings.getCalendarMarking(i);
			// get first occurrence:
			Calendar firstDay = Calendar.getInstance();
			firstDay.setTimeInMillis(marking.getStartDate());
			RepetitionHandler handler = sHandlerMap
					.get(marking.getRepeatType());
			firstDay = handler.getFirst(firstDay, marking);
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
}
