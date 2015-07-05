package tests.freemind;

import java.text.DateFormat;
import java.util.Calendar;

import accessories.plugins.time.CalendarMarkingEvaluator;
import freemind.common.XmlBindingTools;
import freemind.controller.actions.generated.instance.CalendarMarking;
import freemind.controller.actions.generated.instance.CalendarMarkings;

public class CalendarMarkingTests extends FreeMindTestBase {
	public void testCalendarMarkingEmpty() throws Exception {
		CalendarMarkings result = (CalendarMarkings) XmlBindingTools.getInstance().unMarshall("<calendar_markings/>");
		assertEquals(0, result.sizeCalendarMarkingList());
	}
	public void testCalendarMarkingSingle() throws Exception {
		CalendarMarkings result = (CalendarMarkings) XmlBindingTools.getInstance().unMarshall("<calendar_markings>" +
				"  <calendar_marking name='bla' color='#ff69b4' start_date='1437213300000' repeat_type='never'/>" +
				"</calendar_markings>");
		assertEquals(1, result.sizeCalendarMarkingList());
		CalendarMarkingEvaluator ev = new CalendarMarkingEvaluator(result);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(result.getCalendarMarking(0).getStartDate());
		assertNotNull(ev.isMarked(cal));
		cal.add(Calendar.DAY_OF_YEAR, 1);
		assertNull(ev.isMarked(cal));
	}

	public void testCalendarMarkingDouble() throws Exception {
		long otherTime = DateFormat.getDateInstance().parse("1.1.1970").getTime();
		CalendarMarkings result = (CalendarMarkings) XmlBindingTools.getInstance().unMarshall("<calendar_markings>" +
				"  <calendar_marking name='bla' color='#ff69b4' start_date='1437213300000' repeat_type='never'/>" +
				"  <calendar_marking name='bla2' color='#ff69b5' start_date='"+otherTime + "' repeat_type='never'/>" +
				"</calendar_markings>");
		assertEquals(2, result.sizeCalendarMarkingList());
		CalendarMarkingEvaluator ev = new CalendarMarkingEvaluator(result);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(result.getCalendarMarking(0).getStartDate());
		assertNotNull(ev.isMarked(cal));
		cal.add(Calendar.DAY_OF_YEAR, 1);
		assertNull(ev.isMarked(cal));
		cal.setTimeInMillis(otherTime);
		CalendarMarking marked = ev.isMarked(cal);
		assertNotNull(marked);
		assertEquals("bla2", marked.getName());
		cal.add(Calendar.DAY_OF_YEAR, 1);
		assertNull(ev.isMarked(cal));
	}

	public void testCalendarMarkingRepeatWeekly() throws Exception {
		long startTime = DateFormat.getDateInstance().parse("5.7.2015").getTime();
		long endTime = DateFormat.getDateInstance().parse("19.7.2015").getTime();
		CalendarMarkings result = (CalendarMarkings) XmlBindingTools.getInstance().unMarshall("<calendar_markings>" +
				"  <calendar_marking name='bla2' color='#ff69b5' start_date='"+startTime + "' end_date='"+endTime + "' " +
						"repeat_type='weekly' repeat_each_n_occurence='1' first_occurence='0'/>" +
				"</calendar_markings>");
		assertEquals(1, result.sizeCalendarMarkingList());
		CalendarMarkingEvaluator ev = new CalendarMarkingEvaluator(result);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(result.getCalendarMarking(0).getStartDate());
		assertNotNull(ev.isMarked(cal));
		cal.add(Calendar.DAY_OF_YEAR, 1);
		assertNull(ev.isMarked(cal));
		cal.add(Calendar.DAY_OF_YEAR, -1);
		cal.add(Calendar.WEEK_OF_YEAR, 1);
		assertNotNull(ev.isMarked(cal));
		cal.setTimeInMillis(endTime);
		assertNotNull(ev.isMarked(cal));
		cal.add(Calendar.WEEK_OF_YEAR, 1);
		assertNull(ev.isMarked(cal));
	}

	public void testCalendarMarkingRepeatBeWeekly() throws Exception {
		long startTime = DateFormat.getDateInstance().parse("5.7.2015").getTime();
		long endTime = DateFormat.getDateInstance().parse("19.7.2015").getTime();
		CalendarMarkings result = (CalendarMarkings) XmlBindingTools.getInstance().unMarshall("<calendar_markings>" +
				"  <calendar_marking name='bla2' color='#ff69b5' start_date='"+startTime + "' end_date='"+endTime + "' " +
				"repeat_type='weekly' repeat_each_n_occurence='2' first_occurence='0'/>" +
				"</calendar_markings>");
		assertEquals(1, result.sizeCalendarMarkingList());
		CalendarMarkingEvaluator ev = new CalendarMarkingEvaluator(result);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(result.getCalendarMarking(0).getStartDate());
		assertNotNull(ev.isMarked(cal));
		cal.add(Calendar.DAY_OF_YEAR, 1);
		assertNull(ev.isMarked(cal));
		cal.add(Calendar.DAY_OF_YEAR, -1);
		cal.add(Calendar.WEEK_OF_YEAR, 1);
		assertNull(ev.isMarked(cal));
		cal.add(Calendar.WEEK_OF_YEAR, 1);
		assertNotNull(ev.isMarked(cal));
		cal.add(Calendar.WEEK_OF_YEAR, 1);
		assertNull(ev.isMarked(cal));
	}
	
	public void testCalendarMarkingRepeatDaily() throws Exception {
		long startTime = DateFormat.getDateInstance().parse("5.7.2015").getTime();
		long endTime = DateFormat.getDateInstance().parse("19.7.2015").getTime();
		CalendarMarkings result = (CalendarMarkings) XmlBindingTools.getInstance().unMarshall("<calendar_markings>" +
				"  <calendar_marking name='bla2' color='#ff69b5' start_date='"+startTime + "' end_date='"+endTime + "' " +
				"repeat_type='daily' repeat_each_n_occurence='1' first_occurence='0'/>" +
				"</calendar_markings>");
		assertEquals(1, result.sizeCalendarMarkingList());
		CalendarMarkingEvaluator ev = new CalendarMarkingEvaluator(result);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(result.getCalendarMarking(0).getStartDate());
		assertNotNull(ev.isMarked(cal));
		cal.add(Calendar.DAY_OF_YEAR, 1);
		assertNotNull(ev.isMarked(cal));
	}
	public void testCalendarMarkingRepeatMonthly() throws Exception {
		long startTime = DateFormat.getDateInstance().parse("5.7.2015").getTime();
		long endTime = DateFormat.getDateInstance().parse("5.8.2015").getTime();
		CalendarMarkings result = (CalendarMarkings) XmlBindingTools.getInstance().unMarshall("<calendar_markings>" +
				"  <calendar_marking name='bla2' color='#ff69b5' start_date='"+startTime + "' end_date='"+endTime + "' " +
				"repeat_type='monthly' repeat_each_n_occurence='1' first_occurence='0'/>" +
				"</calendar_markings>");
		assertEquals(1, result.sizeCalendarMarkingList());
		CalendarMarkingEvaluator ev = new CalendarMarkingEvaluator(result);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(result.getCalendarMarking(0).getStartDate());
		assertNotNull(ev.isMarked(cal));
		cal.add(Calendar.MONTH, 1);
		assertNotNull(ev.isMarked(cal));
		cal.add(Calendar.MONTH, 1);
		assertNull(ev.isMarked(cal));
	}
	public void testCalendarMarkingRepeatYearly() throws Exception {
		long startTime = DateFormat.getDateInstance().parse("5.7.2015").getTime();
		long endTime = DateFormat.getDateInstance().parse("5.7.2016").getTime();
		CalendarMarkings result = (CalendarMarkings) XmlBindingTools.getInstance().unMarshall("<calendar_markings>" +
				"  <calendar_marking name='bla2' color='#ff69b5' start_date='"+startTime + "' end_date='"+endTime + "' " +
				"repeat_type='yearly' repeat_each_n_occurence='1' first_occurence='0'/>" +
				"</calendar_markings>");
		assertEquals(1, result.sizeCalendarMarkingList());
		CalendarMarkingEvaluator ev = new CalendarMarkingEvaluator(result);
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(result.getCalendarMarking(0).getStartDate());
		assertNotNull(ev.isMarked(cal));
		cal.add(Calendar.YEAR, 1);
		assertNotNull(ev.isMarked(cal));
		cal.add(Calendar.YEAR, 1);
		assertNull(ev.isMarked(cal));
	}
}

