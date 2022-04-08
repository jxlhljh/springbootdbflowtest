package cn.gzsendi.modules.framework.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {
	
    /*** 本地时区ID ***/
    public static final ZoneId LOCAL_ZONE_ID = ZoneId.systemDefault();

    /*** 缺省的日期表达式 ***/
    public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";

    /*** 缺省的日期表达式（中文年月日） ***/
    public static final String DEFAULT_DATE_PATTERN_ZH = "yyyy年MM月dd日";

    /*** 缺省的时间表达式 ***/
    public static final String DEFAULT_TIME_PATTERN = "HH:mm:ss";

    /*** 缺省的时间表达式（中文时分秒） ***/
    public static final String DEFAULT_TIME_PATTERN_ZH = "HH时mm分ss秒";

    /*** 缺省的日期时间表达式(yyyy-MM-dd HH:mm:ss) ***/
    public static final String DEFAULT_DATE_TIME_PATTERN = DEFAULT_DATE_PATTERN + ' ' + DEFAULT_TIME_PATTERN;

    /*** 缺省的时间戳表达式 ***/
    public static final String DEFAULT_TIMESTAMP_PATTERN = "yyyyMMddHHmmss";

    /*** 缺省的动态时间表达式 ***/
    public static final String[] DEFAULT_DYNAMIC_PATTERNS = {
            DEFAULT_DATE_TIME_PATTERN,
            DEFAULT_DATE_PATTERN,
            DEFAULT_TIMESTAMP_PATTERN,
            DEFAULT_DATE_PATTERN_ZH,
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd'T'HH:mm:ss.sss",
            "yyyy-MM-dd'T'HH:mm:ss.sssZ",
            DEFAULT_TIME_PATTERN,
            DEFAULT_TIME_PATTERN_ZH
    };

    /* 因为DateTimeFormatter是线程安全的，所以这里可以提供初始化好的DateTimeFormatter */

    /*** 缺省的日期时间格式器(yyyy-MM-dd) ***/
    public static final DateTimeFormatter DEFAULT_DATE_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_PATTERN);

    /*** 缺省的时间格式器(HH:mm:ss) ***/
    public static final DateTimeFormatter DEFAULT_TIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_TIME_PATTERN);

    /*** 缺省的日期时间格式器(yyyy-MM-dd HH:mm:ss) ***/
    public static final DateTimeFormatter DEFAULT_DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_PATTERN);
	
    /**
     * 将LocalDateTime格式化成指定格式的字符串
     *
     * @param localDateTime 本时区日期时间
     * @param pattern       格式字符串 (一定要包含日期段和时间段，
     *                      如果少了时间段如"yyyy-MM-dd"则报错)
     * @return String 格式化后的时间字符串
     */
    public static String format(LocalDateTime localDateTime, String pattern) {
        if (localDateTime == null) {
            return null;
        }
        if (DEFAULT_DATE_TIME_PATTERN.equals(pattern)) {
            return DEFAULT_DATE_TIME_FORMATTER.format(localDateTime);
        }
        return DateTimeFormatter.ofPattern(pattern).format(localDateTime);
    }

}
