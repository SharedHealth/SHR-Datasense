package org.sharedhealth.datasense.util;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {
    public static final String ISO_DATE_IN_MILLIS_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    public static final String ISO_DATE_IN_SECS_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";
    public static final String UTC_DATE_IN_MILLIS_FORMAT = "yyyy-MM-dd HH:mm:ss.SSSZ";
    public static final String UTC_DATE_IN_SECS_FORMAT = "yyyy-MM-dd HH:mm:ssZ";
    public static final String SIMPLE_DATE_WITH_SECS_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String SIMPLE_DATE_FORMAT = "yyyy-MM-dd";
    public static final String DATE_FMT_DD_MM_YYYY = "dd/MM/yyyy";

    public static final String FHIR_ISO_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXX";
    public static final String FHIR_ISO_DATE_IN_MILLIS_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    public static final String[] DATE_FORMATS = new String[]{
            ISO_DATE_IN_MILLIS_FORMAT, ISO_DATE_IN_SECS_FORMAT, FHIR_ISO_DATE_IN_MILLIS_FORMAT,
            UTC_DATE_IN_MILLIS_FORMAT, UTC_DATE_IN_SECS_FORMAT, FHIR_ISO_DATE_TIME_FORMAT,
            SIMPLE_DATE_WITH_SECS_FORMAT, SIMPLE_DATE_FORMAT, DATE_FMT_DD_MM_YYYY};
    private static final Logger logger = Logger.getLogger(DateUtil.class);

    public static String getCurrentTimeInUTCString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(UTC_DATE_IN_MILLIS_FORMAT);
        return dateFormat.format(new Date());
    }

    public static int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR);
    }

    public static int getYearOf(Date date) {
        Calendar instance = Calendar.getInstance();
        instance.setTime(date);
        return instance.get(Calendar.YEAR);
    }

    public static Date parseDate(String date, String... formats) throws ParseException {
        return org.apache.commons.lang3.time.DateUtils.parseDate(date, formats);
    }

    public static Date parseDate(String date) {
        try {
            return parseDate(date, DateUtil.DATE_FORMATS);
        } catch (ParseException e) {
            throw new RuntimeException("invalid date:" + date);
        }
    }

    public static String parseToString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(SIMPLE_DATE_WITH_SECS_FORMAT);
        return format.format(date);
    }

    public static String format(Date date) {
        SimpleDateFormat format = new SimpleDateFormat(SIMPLE_DATE_WITH_SECS_FORMAT);
        return format.format(date);
    }

    public static String toGivenFormatString(Date date, String format) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    public static String toUTCString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(UTC_DATE_IN_MILLIS_FORMAT);
        //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(date);
    }

    public static String toISOString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(ISO_DATE_IN_MILLIS_FORMAT);
        //dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return dateFormat.format(date);
    }

    public static String getCurrentTimeInISOString() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(ISO_DATE_IN_MILLIS_FORMAT);
        return dateFormat.format(new Date());
    }

    public static int getYears(Date startDate, Date endDate) {
        return new Period(new LocalDate(startDate.getTime()), new LocalDate(endDate.getTime()), PeriodType.years()).getYears();
    }

    public static int getMonths(Date startDate, Date endDate) {
        return new Period(new LocalDate(startDate.getTime()), new LocalDate(endDate.getTime()), PeriodType.months()).getMonths();
    }

    public static int getDays(Date startDate, Date endDate) {
        return new Period(new LocalDate(startDate.getTime()), new LocalDate(endDate.getTime()), PeriodType.days()).getDays();
    }

    public static boolean isSameDay(Date date1, Date date2) {
        return DateUtils.isSameDay(date1, date2);
    }
}