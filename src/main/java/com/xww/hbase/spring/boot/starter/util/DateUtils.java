package com.xww.hbase.spring.boot.starter.util;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

/**
 * @author xin.zhou [xinwowo@hotmail.com]
 */
public class DateUtils {

    public static final String[] WEEKS = new String[]{"日", "一", "二", "三", "四", "五", "六"};

    private static final long MILLISECONDS_OF_DAY = 86400000; //24 * 60 * 60 * 1000

    private DateUtils() {
    }

    // 按常用顺序排列
    public final static String Format_1 = "yyyy-MM-dd HH:mm:ss";
    public final static String Format_2 = "yyyy-MM-dd";
    public final static String Format_3 = "HH:mm:ss";
    public final static String Format_4 = "yyyy-MM-dd HH:mm:ss,SSS";
    public final static String Format_5 = "yyyy年MM月dd日 HH:mm:ss";
    public final static String Format_6 = "yyyy年MM月dd日";
    public final static String Format_7 = "yyyyMMdd";
    public final static String Format_8 = "yyyyMMddHHmmss";
    public final static String Format_9 = "yyyy.MM.dd HH:mm:ss";
    public final static String Format_10 = "yyyy.MM.dd";
    public final static String Format_11 = "M.d";
    public final static String Format_12 = "M月d日";
    public final static String Format_13 = "yyyy/MM/dd";
    public final static String Format_14 = "HH:mm";
    public final static String Format_15 = "yyyy-MM-dd HH:mm";
    public final static String Format_16 = "yyyy/MM/dd HH:mm:ss";
    public final static String Format_17 = "yyyyMMddHHmmssSSS";
    public final static String Format_18 = "yyyy-MM-dd HH:mm:ss.SSS";

    public static int getIntervalDays(Date oDate) {

        if (null == oDate) {
            return -1;

        }
        Date currentDate = new Date();
        if (currentDate.before(oDate)) {
            long intervalMilli = oDate.getTime() - currentDate.getTime();
            return (int) (intervalMilli / (24 * 60 * 60 * 1000));
        } else {
            return -1;
        }
    }

    /**
     * 判断day1是否比day2小自然天数,或天数相等
     *
     * @param day1
     * @param day2
     * @return
     * @throws Exception
     */
    public static boolean isLesserAndEqualsDay(Date day1, Date day2) throws Exception {
        if (null == day1 || null == day2) {
            throw new Exception(
                    "[DateUtils.isLargeDay]: day1 is null  or  day2 is null, day1=" + day1 + " ,  day2=" + day2);
        }
        Date startTimeForDay1 = getStartTimeForDay(day1);
        Date startTimeForDay2 = getStartTimeForDay(day2);
        return startTimeForDay1.getTime() <= startTimeForDay2.getTime();
    }

    /**
     * 判断day1是否比day2大自然天数
     *
     * @param day1
     * @param day2
     * @return
     * @throws Exception
     */
    public static boolean isLargeDay(Date day1, Date day2) throws Exception {
        if (null == day1 || null == day2) {
            throw new Exception(
                    "[DateUtils.isLargeDay]: day1 is null  or  day2 is null, day1=" + day1 + " ,  day2=" + day2);
        }
        Date startTimeForDay1 = getStartTimeForDay(day1);
        Date startTimeForDay2 = getStartTimeForDay(day2);
        return startTimeForDay1.getTime() > startTimeForDay2.getTime();
    }

    /**
     * 判断day1是否比day2大自然天数，或自然天数相等
     *
     * @param day1
     * @param day2
     * @return
     * @throws Exception
     */
    public static boolean isLargeAndEqualsDay(Date day1, Date day2) throws Exception {
        if (null == day1 || null == day2) {
            throw new Exception(
                    "[DateUtils.isLargeDay]: day1 is null  or  day2 is null, day1=" + day1 + " ,  day2=" + day2);
        }
        Date startTimeForDay1 = getStartTimeForDay(day1);
        Date startTimeForDay2 = getStartTimeForDay(day2);
        return startTimeForDay1.getTime() >= startTimeForDay2.getTime();
    }

    /**
     * 判断time 是否在 baseDay所在的天内
     *
     * @param baseDay
     * @param time
     * @return
     * @throws Exception
     */
    public static boolean isInDay(Date baseDay, Date time) throws Exception {
        if (null == baseDay || null == time) {
            return false;
        }
        Date startTime = getStartTimeForDay(baseDay);
        Date endTime = getEndTimeForDay(baseDay);
        return (startTime.getTime() <= time.getTime() && endTime.getTime() >= time.getTime());
    }

    /**
     * 判断是否是非工作时间晚21:00-早8:00区间
     *
     * @param baseDay
     * @param time
     * @return
     * @throws Exception
     */
    public static boolean isINRestTime(Date baseDay, Date time) throws Exception {
        if (null == baseDay || null == time) {
            return false;
        }
        Date startTime = getStartTimeForRest(baseDay);
        Date endTime = getEndTimeForRest(baseDay);
        return (startTime.getTime() <= time.getTime() && endTime.getTime() >= time.getTime());
    }

    /**
     * 判断两个时间的日期是否相同
     *
     * @param day1
     * @param day2
     * @return
     */
    public static boolean equalsDay(Date day1, Date day2) {
        if (null == day1 || null == day2) {
            return false;
        }
        return dateToString(day1, Format_7).equals(dateToString(day2, Format_7));
    }

    /**
     * 当前日期和时间,"yyyy-MM-dd HH:mm:ss"
     */
    public static String getNowDateTimeStr() {
        return dateToString(new Date(), Format_1);
    }

    public static String getNowDateTimeStr(String format) {
        return dateToString(new Date(), format);
    }

    /**
     * 当前日期,"yyyy-MM-dd"
     */
    public static String getNowDateStr() {
        return dateToString(new Date(), Format_2);
    }

    /**
     * 当前时间,"HH:mm:ss"
     */
    public static String getNowTimeStr() {
        return dateToString(new Date(), Format_3);
    }

    /**
     * 日期时间到字符串
     *
     * @param date
     * @param format
     * @return
     */
    public static String dateToString(Date date, String format) {
        if (null == date) {
            return null;
        }
        if (null == format) {
            format = Format_1;
        }
        SimpleDateFormat form = new SimpleDateFormat(format);
        return form.format(date);
    }

    /**
     * 字符串到日期时间
     *
     * @param dateString
     * @param format
     * @return
     * @throws ParseException
     */
    public static Date stringToDate(String dateString, String format) {
        if (StringUtils.isEmpty(dateString)) {
            return null;
        }
        SimpleDateFormat form = new SimpleDateFormat(null == format ? Format_2 : format);
        Date date = null;
        try {
            date = form.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * 获取某个月的最大天数，比如1月是31、4月是30.<br>
     * 注意在月份（month）是2月的时候，year不能为null，因为需要根据年份来确定是闰年还是平年.<br>
     *
     * @param year
     * @param month
     * @return
     */
    public static Integer getMaxDayInMonth(Integer year, Integer month) {
        if (null == month || (2 == month && null == year)) {
            return null;
        }
        if (1 == month || 3 == month || 5 == month || 7 == month || 8 == month || 10 == month || 12 == month) {
            return 31;
        }
        if (4 == month || 6 == month || 9 == month || 11 == month) {
            return 30;
        }
        if (2 == month) {
            if (year % 400 == 0 || (year % 4 == 0 && year % 100 != 0)) {
                return 29;
            } else {
                return 28;
            }
        }
        return null;
    }

    public static List<String> sortListDesc(Set<String> set) {
        List<String> retStr = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<Long, String> map = new TreeMap<Long, String>();
        for (String value : set) {
            try {
                map.put(sdf.parse(value).getTime(), value);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Collection<String> coll = map.values();
        retStr.addAll(coll);
        Collections.reverse(retStr);
        return retStr;
    }

    public static List<String> sortListDesc1(Set<String> set) {
        List<String> retStr = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Map<Long, String> map = new TreeMap<Long, String>();
        for (String value : set) {
            try {
                map.put(sdf.parse(value).getTime(), value);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        Collection<String> coll = map.values();
        retStr.addAll(coll);
        Collections.reverse(retStr);
        return retStr;
    }

    public static int getNowDayInWeek() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    public static int getDayInWeek(Date time) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 判断time是否在工作日
     *
     * @param time
     * @return
     * @throws Exception
     */
    public static boolean isCommonWorkDay(Date time) throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(time);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        return 2 <= day && day <= 6;
    }

    /**
     * 获取date所在天的起始时间，比如 date为2012-12-12 12:12:12 ，则返回的结果是2012-12-12 00:00:00
     *
     * @return
     * @throws Exception
     */
    public static Date getStartTimeForDay(Date date) throws Exception {
        if (null == date) {
            return null;
        }
        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
        String timeStr = form.format(date);
        timeStr += " 00:00:00";
        form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return form.parse(timeStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取date所在天的中间时间，比如 date为2012-12-12 01:22:02 ，则返回的结果是2012-12-12 12:00:00
     *
     * @return
     * @throws Exception
     */
    public static Date getMiddleTimeForDay(Date date) throws Exception {
        if (null == date) {
            return null;
        }
        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
        String timeStr = form.format(date);
        timeStr += " 12:00:00";
        form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return form.parse(timeStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取date所在天的非工作起始时间，比如2012-12-12 21:00:00
     *
     * @return
     * @throws Exception
     */
    public static Date getStartTimeForRest(Date date) throws Exception {
        if (null == date) {
            return null;
        }
        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
        String timeStr = form.format(date);
        timeStr += " 21:00:00";
        form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return form.parse(timeStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 日期格式化
     *
     * @return
     * @throws Exception
     */
    public static String formatDate(Date date) throws Exception {
        if (null == date) {
            return null;
        }
        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeStr = form.format(date);
        return timeStr;
    }

    /**
     * 获取date所在天的结束时间，比如 date为2012-12-12 12:12:12 ，则返回的结果是2012-12-12 23:59:59
     *
     * @param date
     * @return
     * @throws Exception
     */
    public static Date getEndTimeForDay(Date date) {
        if (null == date) {
            return null;
        }
        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
        String timeStr = form.format(date);
        timeStr += " 23:59:59";
        form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return form.parse(timeStr);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取date所在天非工作时间的结束时间，比如 date为2012-12-12 08:12:12
     *
     * @param date
     * @return
     * @throws Exception
     */
    public static Date getEndTimeForRest(Date date) throws Exception {
        if (null == date) {
            return null;
        }
        SimpleDateFormat form = new SimpleDateFormat("yyyy-MM-dd");
        String timeStr = form.format(date);
        // 通过日历获取下一天日期
        Calendar cal = Calendar.getInstance();
        cal.setTime(form.parse(timeStr));
        cal.add(Calendar.DAY_OF_YEAR, +1);
        String nextDate = form.format(cal.getTime());
        nextDate += " 08:00:00";
        form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            return form.parse(nextDate);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 得到date的下一天的时间，比如date是2012-12-12 12:12:12 ，则返回的结果是2012-12-13 12:12:12
     *
     * @param date
     * @return
     * @throws Exception
     */
    public static Date getNextDay(Date date) throws Exception {
        if (null == date) {
            return null;
        }
        long nextTimeL = date.getTime() + 1000 * 60 * 60 * 24;
        return new Date(nextTimeL);
    }

    /**
     * 得到date的前一天的时间，比如date是2012-12-12 12:12:12 ，则返回的结果是2012-12-11 12:12:12
     *
     * @param date
     * @return
     * @throws Exception
     */
    public static Date getPreDay(Date date) throws Exception {
        if (null == date) {
            return null;
        }
        long nextTimeL = date.getTime() - 1000 * 60 * 60 * 24;
        return new Date(nextTimeL);
    }

    /**
     * 得到date的前几天的时间，比如date是2012-12-12 12:12:12 ，days是2，则返回的结果是2012-12-10
     * 12:12:12
     *
     * @param date
     * @param days
     * @return
     * @throws Exception
     */
    public static Date getPreDays(Date date, int days) throws Exception {
        if (null == date) {
            return null;
        }
        if (days <= 0) {
            return date;
        }
        long nextTimeL = date.getTime() - 1000 * 60 * 60 * 24 * days;
        return new Date(nextTimeL);
    }

    public static LocalDate toLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static Date asDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static String getWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return WEEKS[calendar.get(Calendar.DAY_OF_WEEK) - 1];
    }

    /**
     * 计算两个日期之间的小时数
     * 超时标签显示：超时0-59分钟都显示为：超时0.5小时。1小时以后每半小时更新，例：超时1.5小时，超时2小时，超时2.5小时。
     *
     * @param startTime
     * @param endTime
     * @return
     */
    public static float offsetHours(Date startTime, Date endTime) {
        long diff = endTime.getTime() - startTime.getTime();
        if (diff < 0) {
            return 0;
        }
        long min = diff / (1000 * 60);
        float resHour = 0;
        if (min >= 0 && min <= 59) {
            resHour = 0.5f;
        } else if (min % 60 < 30) {
            resHour = min / 60;
        } else if (min % 60 >= 30) {
            resHour = min / 60 + 0.5f;
        }
        return resHour;
    }

    /**
     * 获取当前时间之前或之后几分钟 minute
     */
    public static String getTimeByMinute(int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, minute);
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(calendar.getTime());

    }

    /**
     * 计算两个日期之间相差的天数 daysBetween("2012-09-08 10:10:10","2012-09-09 00:10:00");
     * //1
     *
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    public static int daysBetween(Date smdate, Date bdate) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        smdate = sdf.parse(sdf.format(smdate));
        bdate = sdf.parse(sdf.format(bdate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = (time2 - time1) / (1000 * 3600 * 24);

        return Integer.parseInt(String.valueOf(between_days));
    }

    /**
     * @param curTime    ：今天的时间
     * @param targetTime ： 目标天的时间
     * @return
     * @throws Exception
     */
    public static String getSlangTime(Date curTime, Date targetTime, boolean todayIsEmpty) throws Exception {
        if (null == curTime || null == targetTime) {
            throw new Exception("curTime is null or targetTime is null.");
        }
        String dayStr = null;
        if (getMiddleTimeForDay(curTime).getTime() <= getMiddleTimeForDay(targetTime).getTime()) {
            int days = daysBetween(curTime, targetTime);
            if (0 == days) {
                dayStr = todayIsEmpty ? "" : "今天 ";
                return dayStr + dateToString(targetTime, DateUtils.Format_14);
            } else if (1 == days) {
                dayStr = "明天";
            } else {
                dayStr = dateToString(targetTime, DateUtils.Format_15);
            }
        } else {
            int days = daysBetween(targetTime, curTime);
            if (1 == days) {
                dayStr = "昨天";
            } else {
                dayStr = dateToString(targetTime, DateUtils.Format_15);
            }
        }
        return dayStr;
    }

    /**
     * @param curTime    ：今天的时间
     * @param targetTime ： 目标天的时间
     * @return
     * @throws Exception
     */
    public static String getSlangTime2(Date curTime, Date targetTime, boolean todayIsEmpty) throws Exception {
        if (null == curTime || null == targetTime) {
            throw new Exception("curTime is null or targetTime is null.");
        }
        String dayStr = null;
        if (getMiddleTimeForDay(curTime).getTime() <= getMiddleTimeForDay(targetTime).getTime()) {
            int days = daysBetween(curTime, targetTime);
            if (0 == days) {
                dayStr = todayIsEmpty ? "" : "今天 ";
                return dayStr + dateToString(targetTime, DateUtils.Format_14);
            } else if (1 == days) {
                dayStr = "明天";
            } else {
                dayStr = dateToString(targetTime, DateUtils.Format_2);
            }
        } else {
            int days = daysBetween(targetTime, curTime);
            if (1 == days) {
                dayStr = "昨天";
            } else {
                dayStr = dateToString(targetTime, DateUtils.Format_2);
            }
        }
        return dayStr;
    }

    public static Date getWeekBegin(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return getDayBegin(calendar);
    }

    public static Date getWeekEnd(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setFirstDayOfWeek(Calendar.MONDAY);
        calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return getEndTimeForDay(calendar.getTime());
    }

    public static Date getDayBegin(Date date) {
        Calendar calendarBefore = Calendar.getInstance();
        calendarBefore.setTime(date);
        return getDayBegin(calendarBefore);
    }

    public static Date getDayEnd(Date date) {
        Calendar calendarBefore = Calendar.getInstance();
        calendarBefore.setTime(date);
        return getDayEnd(calendarBefore);
    }

    public static Date addDay(Date date, int days) {
        if (date == null) {
            return null;
        }
        return new Date(date.getTime() + MILLISECONDS_OF_DAY * days);
    }

    public static Date reduceDay(Date date, int days) {
        if (date == null) {
            return null;
        }
        return new Date(date.getTime() - MILLISECONDS_OF_DAY * days);
    }

    public static Date getDayBegin(Calendar calendarBefore) {
        Calendar calendarAfter = Calendar.getInstance();
        calendarAfter.set(calendarBefore.get(Calendar.YEAR), calendarBefore.get(Calendar.MONTH),
                calendarBefore.get(Calendar.DATE));
        calendarAfter.set(Calendar.HOUR_OF_DAY, 0);
        calendarAfter.set(Calendar.MINUTE, 0);
        calendarAfter.set(Calendar.SECOND, 0);
        calendarAfter.set(Calendar.MILLISECOND, 0);
        return calendarAfter.getTime();
    }

    public static Date getDayEnd(Calendar calendarBefore) {
        Calendar calendarAfter = Calendar.getInstance();
        calendarAfter.set(calendarBefore.get(Calendar.YEAR), calendarBefore.get(Calendar.MONTH),
                calendarBefore.get(Calendar.DATE));
        calendarAfter.set(Calendar.HOUR_OF_DAY, 23);
        calendarAfter.set(Calendar.MINUTE, 59);
        calendarAfter.set(Calendar.SECOND, 59);
        calendarAfter.set(Calendar.MILLISECOND, 999);
        return calendarAfter.getTime();
    }

    public static int compare(Date date, Date anotherDate) {
        if (date == null && anotherDate == null) {
            return 0;
        }
        if (anotherDate == null) {
            return 1;
        }
        if (date == null) {
            return -1;
        }
        return date.compareTo(anotherDate);
    }

    public static boolean isTimeYMDHMS(String time) throws Exception {
        if (StringUtils.isEmpty(time)) {
            throw new Exception("[DateUtils.isTimeYMDHMS]:time is null.");
        }
        String regex = "\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}";
        return time.trim().matches(regex);
    }

    public static boolean isTimeYMD(String time) throws Exception {
        if (StringUtils.isEmpty(time)) {
            throw new Exception("[DateUtils.isTimeYMD]:time is null.");
        }
        String regex = "\\d{4}-\\d{2}-\\d{2}";
        return time.trim().matches(regex);
    }

}